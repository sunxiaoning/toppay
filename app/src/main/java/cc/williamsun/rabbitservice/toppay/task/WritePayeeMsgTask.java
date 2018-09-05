package cc.williamsun.rabbitservice.toppay.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import cc.williamsun.focuscr.constant.PlatformStatusCode;
import cc.williamsun.focuscr.exception.PlatformRuntimeException;
import cc.williamsun.focuscr.util.Base64Utils;
import cc.williamsun.focuscr.util.DateUtil;
import cc.williamsun.focuscr.util.IdGenerator;
import cc.williamsun.focuscr.util.JacksonUtil;
import cc.williamsun.focuscr.util.RSAUtils;
import cc.williamsun.rabbitservice.toppay.consts.EncryptKeyConst;
import cc.williamsun.rabbitservice.toppay.consts.GatewayPostRequestConst;
import cc.williamsun.rabbitservice.toppay.db.PayeeMsgDao;
import cc.williamsun.rabbitservice.toppay.enums.PayChannelEnum;
import cc.williamsun.rabbitservice.toppay.message.PayeeMsg;
import cc.williamsun.rabbitservice.toppay.util.NetWorkUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    public static final ImmutableMap<PayChannelEnum,String> PAYEE_ACCOUNT = ImmutableMap.<PayChannelEnum,String>builder()
            .put(PayChannelEnum.ALIPAY,"212994454636703745")
            .put(PayChannelEnum.WECHATPAY,"212994454636703744")
            .build();

    /**
     * 加密密钥配置
     */
    public static final ImmutableMap<String,String> ENCRYPT_KEY_CONFIG = ImmutableMap.<String,String>builder()
            .put(EncryptKeyConst.CLIENT_PRIVATE_KEY,"MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOgle2UoDM9HR9RfqH8xgnUtWcAcOGeFu/rsIWZnFMYVkMyyXjKd4fNVOt6D0VjDdOrl9lLGngDQFyHYAc6ZLEhdXF86NZZFw0uff1Vp2yzJCXiUiCtllfDrD3ewVvhCLK6DbgYOkqQ1z7FoyyDtKe7RE2OwWIRqgXyoBgq8GGANAgMBAAECgYBiyChEuBETVoKDJR0+2mNn1x9ctuVtb6O4tyfTOkPFskGGSNP6d+JAt+Cv9KltOuaSWb5CJM/xCpr9RLwoHFeRt+hhbQVrbutjebdILqN1OrTvCYfAr8VrM165p4L2kx7vHU4wKURjw+8L7C4cO2qGnRWyUG1I7eZc9vrb57zR4QJBAPP1XQft0HirgSlH2Y7qXoMoBe5PLNZz/5+o8HMTvevelaoBauQvNabgjZ1+qVBPUh5fUPxmA/3HJknxAwgpaskCQQDzmtzgXD1ba3ij7PsLJcsauXQPCmw3ql2JZvpVs/mEXXr9TkMiorC3BkZFkyFfMduOcvmFdGmTAWHEC4L3E+klAkEAuzxW4W4BcYvXvtIZRNnWHf2Kx8NWm2U+DNEBcqD1Q+F6ppcd7fHZ0LFJn1YGJtAqK4tmQcEeURkW5usmM45uGQJBAL8goumZFT7T7FxZA4J0jY0TMj7Ww59NGIREemahhURX/7YcIEpdcN64movd6xIAUS3LgYA2nNqM/ALPsVpfkc0CQFOSHqTVROU+vW2u3OyqUEPZuwbU63ZwMEHQ276z6kdsNxeHxDeSIQ7MNTD91ZXTGhzxyv37xXDOe8rXUDxufRw=")
            .put(EncryptKeyConst.CLIENT_PUBLIC_KEY,"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDoJXtlKAzPR0fUX6h/MYJ1LVnAHDhnhbv67CFmZxTGFZDMsl4yneHzVTreg9FYw3Tq5fZSxp4A0Bch2AHOmSxIXVxfOjWWRcNLn39VadssyQl4lIgrZZXw6w93sFb4Qiyug24GDpKkNc+xaMsg7Snu0RNjsFiEaoF8qAYKvBhgDQIDAQAB")
            .put(EncryptKeyConst.PLATFORM_PUBLIC_KEY,"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDoJXtlKAzPR0fUX6h/MYJ1LVnAHDhnhbv67CFmZxTGFZDMsl4yneHzVTreg9FYw3Tq5fZSxp4A0Bch2AHOmSxIXVxfOjWWRcNLn39VadssyQl4lIgrZZXw6w93sFb4Qiyug24GDpKkNc+xaMsg7Snu0RNjsFiEaoF8qAYKvBhgDQIDAQAB")
            .build();

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
            Map<String, String> responseMap = payeeMsgNotify();
            if(MapUtils.isNotEmpty(responseMap) && responseMap.get("responseCode").equals(PlatformStatusCode.SUCCESS) && responseMap.get("bizStatus").equals("S")){
                payeeMsgDao.removeCrime(payeeMsg);
            }
        }
        return null;
    }

    @Nullable
    public Map<String, String> payeeMsgNotify() {
        Map<String,Object> requestParam = new TreeMap<>();
        requestParam.put(GatewayPostRequestConst.REQUEST_NO,String.valueOf(idGenerator.nextId()));
        requestParam.put(GatewayPostRequestConst.SERVICE_URL,PAYEE_NOTIFY_URL);
        requestParam.put(GatewayPostRequestConst.TIMESTAMP, DateUtil.formatDate(new Date(),DateUtil.longFormat));
        requestParam.put(GatewayPostRequestConst.MERCHANT_NO,MERCHANT_NO);
        Map<String,String> bizContent = new TreeMap<>();
        bizContent.put("payChannel",payeeMsg.getPayChannel());
        bizContent.put("payeeAccountNo",PAYEE_ACCOUNT.get(PayChannelEnum.parseByName(payeeMsg.getPayChannel())));
        bizContent.put("payer", StringUtils.isNotBlank(payeeMsg.getPayer())?payeeMsg.getPayer():"");
        bizContent.put("successAmount",payeeMsg.getPayeeAmount());
        bizContent.put("successTime",DateUtil.formatDate(new Date()));
        byte [] digestBizContent;
        try {
            digestBizContent = RSAUtils.encryptByPublicKey(JacksonUtil.toJson(bizContent).getBytes("UTF-8"),ENCRYPT_KEY_CONFIG.get(EncryptKeyConst.PLATFORM_PUBLIC_KEY));
        } catch (Exception e){
            throw new PlatformRuntimeException("收款通知请求参数加密出错！",e);
        }
        requestParam.put(GatewayPostRequestConst.BIZ_CONTENT, Base64Utils.encode(digestBizContent));
        String sign;
        try {
            sign = RSAUtils.sign(JacksonUtil.toJson(requestParam).getBytes("UTF-8"),ENCRYPT_KEY_CONFIG.get(EncryptKeyConst.CLIENT_PRIVATE_KEY));
        } catch (Exception e){
            throw new PlatformRuntimeException("收款通知请求参数加签名出错！",e);
        }
        requestParam.put(GatewayPostRequestConst.SIGN,sign);
        String responseJson = post(PAYEE_NOTIFY_URL,JacksonUtil.toJson(requestParam));
        Map<String,String> responseMap = null;
        if(StringUtils.isNotBlank(responseJson)){
            responseMap = JacksonUtil.jsonToMap(responseJson, new TypeReference<Map<String, String>>() {
            });
        }
        return responseMap;
    }

    /**
     * post json
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    private String post(String url, String json) {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new PlatformRuntimeException("post "+ url +"出错！");
    }

}
