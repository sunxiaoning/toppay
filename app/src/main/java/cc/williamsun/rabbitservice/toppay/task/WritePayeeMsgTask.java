package cc.williamsun.rabbitservice.toppay.task;

import android.content.Context;
import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import cc.williamsun.rabbitservice.toppay.db.PayeeMsgDao;
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
     * 收款通知URL
     */
    public static final String PAYEE_NOTIFY_URL = "http://gateway.williamsun.cc:7101/pay/platformPersonalPayCashier/platformPersonalPayCashier";

    /**
     * 执行Context
     */
    private Context context;

    /**
     * 收款消息内容
     */
    private PayeeMsg payeeMsg;

    public WritePayeeMsgTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        // 写入收款消息
        PayeeMsgDao payeeMsgDao = new PayeeMsgDao(context);
        payeeMsgDao.savePayeeMsg(payeeMsg);
        if(NetWorkUtils.isNetworkConnected(context)){
            /* Map<String,Object> requestParam = new TreeMap<>();
            requestParam.put(GatewayPost.,"");
            requestParam.put("serviceUrl","");
            requestParam.put("timestamp","");
            requestParam.put("merchantNo","");
            Map<String,Object> bizContent = new TreeMap<>();
            bizContent.put("payChannel",request.getPayOrderNo());
            bizContent.put("payeeAccountNo",request.getPayChannel());
            bizContent.put("payer",request.getOrderAmount());
            bizContent.put("successAmount",request.getPayAmount());
            bizContent.put("successTime",request.getPayReason());
            byte [] digestBizContent;
            try {
                digestBizContent = RSAUtils.encryptByPublicKey(JacksonUtil.toJson(bizContent).getBytes("UTF-8"),encryptKeyGeneratorService.getPublicKey());
            } catch (Exception e){
                throw new PlatformRuntimeException("收银台请求参数加密出错！",e);
            }
            requestParam.put(GatewayPostRequestConst.BIZ_CONTENT, Base64Utils.encode(digestBizContent));
            String sign;
            try {
                sign = RSAUtils.sign(JacksonUtil.toJson(requestParam).getBytes("UTF-8"),encryptKeyGeneratorService.getPrivateKey());
            } catch (Exception e){
                throw new PlatformRuntimeException("收银台请求参数加签名出错！",e);
            }
            requestParam.put(GatewayPostRequestConst.SIGN,sign);
            requestParam.entrySet().stream().forEach(entry -> {
                try {
                    entry.setValue(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
            HttpUtils.httpPostString(PAYEE_NOTIFY_URL,);*/
        }
        return null;
    }
}
