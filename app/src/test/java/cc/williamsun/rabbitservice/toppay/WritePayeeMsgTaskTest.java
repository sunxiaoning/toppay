package cc.williamsun.rabbitservice.toppay;

import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import cc.williamsun.focuscr.exception.PlatformRuntimeException;
import cc.williamsun.focuscr.util.DateUtil;
import cc.williamsun.focuscr.util.JacksonUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 功能：收款消息写入测试
 * @author sunxiaoning
 * @date 2018/08/30
 */
public class WritePayeeMsgTaskTest {

    @Test
    public void payeeMsgNotifyTest() {
        Map<String,String> bizContent = new TreeMap<>();
        bizContent.put("payChannel","123");
        bizContent.put("payeeAccountNo","123345");
        bizContent.put("payer", "123");
        bizContent.put("successAmount","100");
        bizContent.put("successTime",DateUtil.formatDate(new Date()));
        post("http://192.168.191.2:7301/platformPersonalPay/platformPersonalPayResultNotify", JacksonUtil.toJson(bizContent));
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
                    .addHeader("accept","*/*")
                    .addHeader("Content-Type","application/json")
                    .addHeader("User-Agent:","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
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