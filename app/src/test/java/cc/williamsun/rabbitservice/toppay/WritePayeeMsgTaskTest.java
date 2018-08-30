package cc.williamsun.rabbitservice.toppay;

import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import cc.williamsun.focuscr.util.DateUtil;
import cc.williamsun.rabbitservice.toppay.message.PayeeMsg;
import cc.williamsun.rabbitservice.toppay.task.WritePayeeMsgTask;

import static org.junit.Assert.assertEquals;

/**
 * 功能：收款消息写入测试
 * @author sunxiaoning
 * @date 2018/08/30
 */
public class WritePayeeMsgTaskTest {

    @Test
    public void payeeMsgNotifyTest() {
        PayeeMsg payeeMsg = new PayeeMsg();
        payeeMsg.setUuid(UUID.randomUUID().toString().replaceAll("-",""));
        payeeMsg.setPayer("test");
        payeeMsg.setPayeeAmount("1.00");
        payeeMsg.setPayeeDateTime(DateUtil.formatDate(new Date()));
        payeeMsg.setPayChannel("WECHATPAY");
        WritePayeeMsgTask writePayeeMsgTask = new WritePayeeMsgTask(null, payeeMsg);
        writePayeeMsgTask.execute();
    }
}