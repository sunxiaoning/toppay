package cc.williamsun.rabbitservice.toppay.task;

import android.content.Context;
import android.os.AsyncTask;

import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import cc.williamsun.focuscr.exception.PlatformRuntimeException;
import cc.williamsun.focuscr.util.Base64Utils;
import cc.williamsun.focuscr.util.DateUtil;
import cc.williamsun.focuscr.util.IdGenerator;
import cc.williamsun.focuscr.util.JacksonUtil;
import cc.williamsun.focuscr.util.RSAUtils;
import cc.williamsun.rabbitservice.toppay.consts.GatewayPostRequestConst;
import cc.williamsun.rabbitservice.toppay.db.PayeeMsgDao;
import cc.williamsun.rabbitservice.toppay.enums.PayChannelEnum;
import cc.williamsun.rabbitservice.toppay.message.PayeeMsg;
import cc.williamsun.rabbitservice.toppay.util.NetWorkUtils;
import cn.trinea.android.common.util.HttpUtils;

/**
 * 功能：收款消息内容写入
 * @author sunxiaoning
 * @date 2018/08/26
 */
public class WritePayeeMsgTask extends AsyncTask<Void,Void,Void>{

    /**
     * Id 生成器
     */
    private static final IdGenerator idGenerator = new IdGenerator(1018);

    /**
     * 收款通知URL
     */
    public static final String PAYEE_NOTIFY_URL = "http://gateway.williamsun.cc:7101/pay/platformPersonalPayCashier/platformPersonalPayCashier";

    /**
     * 商户编号
     */
    public static final String MERCHANT_NO = "215781770136645632";

    /**
     * 收款账户配置
     */
    public static final ImmutableMap<PayChannelEnum,String> PAY_ACCOUNT = ImmutableMap.<PayChannelEnum,String>builder()
            .put(PayChannelEnum.ALIPAY,"212994454636703745")
            .put(PayChannelEnum.WECHATPAY,"212994454636703744")
            .build();

    /**
     * 平台RSA公钥
     */
    public static final String PLATFORM_RSA_PUB_KEY = "";

    /**
     * 账户RSA私钥
     */
    public static final String CLIENT_RSA_PRI_KEY = "";

    /**
     * 执行Context
     */
    private Context context;

    /**
     * 收款消息内容
     */
    private PayeeMsg payeeMsg;

    public WritePayeeMsgTask(Context context, PayeeMsg payeeMsg) {
        this.context = context;
        this.payeeMsg = payeeMsg;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        // 写入收款消息
        PayeeMsgDao payeeMsgDao = new PayeeMsgDao(context);
        payeeMsgDao.savePayeeMsg(payeeMsg);
        if(NetWorkUtils.isNetworkConnected(context)){
            Map<String,String> requestParam = new TreeMap<>();
            requestParam.put(GatewayPostRequestConst.REQUEST_NO,String.valueOf(idGenerator.nextId()));
            requestParam.put(GatewayPostRequestConst.SERVICE_URL,PAYEE_NOTIFY_URL);
            requestParam.put(GatewayPostRequestConst.TIMESTAMP, DateUtil.formatDate(new Date(),DateUtil.longFormat));
            requestParam.put(GatewayPostRequestConst.MERCHANT_NO,MERCHANT_NO);
            Map<String,Object> bizContent = new TreeMap<>();
            bizContent.put("payChannel",payeeMsg.getPayChannel());
            bizContent.put("payeeAccountNo",PAY_ACCOUNT.get(payeeMsg.getPayChannel()));
            bizContent.put("payer", StringUtils.isNotBlank(payeeMsg.getPayer())?payeeMsg.getPayer():"");
            bizContent.put("successAmount",payeeMsg.getPayeeAmount());
            bizContent.put("successTime",DateUtil.formatDate(new Date()));
            byte [] digestBizContent;
            try {
                digestBizContent = RSAUtils.encryptByPublicKey(JacksonUtil.toJson(bizContent).getBytes("UTF-8"),PLATFORM_RSA_PUB_KEY);
            } catch (Exception e){
                throw new PlatformRuntimeException("收款通知请求参数加密出错！",e);
            }
            requestParam.put(GatewayPostRequestConst.BIZ_CONTENT, Base64Utils.encode(digestBizContent));
            String sign;
            try {
                sign = RSAUtils.sign(JacksonUtil.toJson(requestParam).getBytes("UTF-8"),CLIENT_RSA_PRI_KEY);
            } catch (Exception e){
                throw new PlatformRuntimeException("收银台请求参数加签名出错！",e);
            }
            requestParam.put(GatewayPostRequestConst.SIGN,sign);
            for(String key : requestParam.keySet()){
                try {
                    requestParam.put(key,URLEncoder.encode(requestParam.get(key).toString(), "UTF-8"));
                } catch (Exception e){
                    e.printStackTrace();
                    throw new PlatformRuntimeException("请求参数编码出错！");
                }
            }
            HttpUtils.httpPostString(PAYEE_NOTIFY_URL,requestParam);
        }
        return null;
    }
}
