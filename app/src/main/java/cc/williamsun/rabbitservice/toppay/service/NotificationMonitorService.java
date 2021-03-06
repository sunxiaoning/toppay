package cc.williamsun.rabbitservice.toppay.service;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import cc.williamsun.focuscr.util.DateUtil;
import cc.williamsun.rabbitservice.toppay.message.PayeeMsg;
import cc.williamsun.rabbitservice.toppay.task.WritePayeeMsgTask;

/**
 * 功能：收款通知监听
 * @author sunxiaoning
 * @date 2018/08/26
 */
public class NotificationMonitorService extends NotificationListenerService {

    public static final String WECHAT_NOTIFY_PKG = "com.tencent.mm";
    public static final String WECHAT_NOTIFY_TITLE = "微信支付";

    public static final String WECHAT_NOTIFY_PREFIX = "微信支付收款";
    public static final String WECHAT_NOTIFY_SUFFIX = "元";

    public static final String ALI_NOTIFY_PKG = "com.eg.android.AlipayGphone";
    public static final String ALI_NOTIFY_TITLE = "支付宝通知";

    public static final String ALI_NOTIFY_PREFIX = "通过扫码向你付款";
    public static final String ALI_NOTIFY_SUFFIX = "元";

    public static final String MIN_AMOUNT = "0.01";



    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        if(StringUtils.isBlank(notificationPkg) || !Arrays.asList(WECHAT_NOTIFY_PKG,ALI_NOTIFY_PKG).contains(notificationPkg)){
            return;
        }
        Bundle extras = sbn.getNotification().extras;

        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);

        // 通知标题校验
        if(StringUtils.isBlank(notificationTitle) || !Arrays.asList(WECHAT_NOTIFY_TITLE,ALI_NOTIFY_TITLE).contains(notificationTitle)){
            return;
        }
        Log.i("通知标题",notificationTitle);
        if(notificationPkg.equals(WECHAT_NOTIFY_PKG) && !notificationTitle.equals(WECHAT_NOTIFY_TITLE)){
            return;
        }
        if(notificationPkg.equals(ALI_NOTIFY_PKG) && !notificationTitle.equals(ALI_NOTIFY_TITLE)){
            return;
        }

        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        if(StringUtils.isBlank(notificationText)){
            return;
        }
        Log.i("通知内容",notificationText);
        String payeeAmountText = null;
        String payer = null;
        if(notificationPkg.equals(WECHAT_NOTIFY_PKG) && (notificationText.lastIndexOf(WECHAT_NOTIFY_PREFIX) < 0 || notificationText.lastIndexOf(WECHAT_NOTIFY_SUFFIX) < 0)){
            return;
        }
        if(notificationPkg.equals(ALI_NOTIFY_PKG) && (notificationText.lastIndexOf(ALI_NOTIFY_PREFIX) < 0 || notificationText.lastIndexOf(ALI_NOTIFY_SUFFIX) < 0)){
            return;
        }
        String payChannel = null;
        if(notificationPkg.equals(WECHAT_NOTIFY_PKG)){
            payChannel = "WECHATPAY";
            payeeAmountText = notificationText.substring(notificationText.lastIndexOf(WECHAT_NOTIFY_PREFIX) + WECHAT_NOTIFY_PREFIX.length(),notificationText.lastIndexOf(WECHAT_NOTIFY_SUFFIX));
        } else if(notificationPkg.equals(ALI_NOTIFY_PKG)){
            payChannel = "ALIPAY";
            if(notificationText.lastIndexOf(WECHAT_NOTIFY_PREFIX) < 0 || notificationText.lastIndexOf(WECHAT_NOTIFY_SUFFIX) < 0){
                return;
            }
            payer = notificationText.substring(0,notificationText.lastIndexOf(ALI_NOTIFY_PREFIX));
            payeeAmountText = notificationText.substring(notificationText.lastIndexOf(ALI_NOTIFY_PREFIX) + ALI_NOTIFY_PREFIX.length(),notificationText.lastIndexOf(ALI_NOTIFY_SUFFIX));
        }
        if(StringUtils.isBlank(payeeAmountText)){
            return;
        }
        if(!StringUtils.isBlank(payer)){
            Log.i("处理后付款人：",payer);
        }
        Log.i("处理后金额文本：",payeeAmountText);
        BigDecimal payeeAmount = new BigDecimal(payeeAmountText);
        if(payeeAmount.compareTo(new BigDecimal(MIN_AMOUNT)) < 0){
            return;
        }
        PayeeMsg payeeMsg = new PayeeMsg();
        payeeMsg.setUuid(UUID.randomUUID().toString().replaceAll("-",""));
        payeeMsg.setPayer(payer);
        payeeMsg.setPayeeAmount(payeeAmount.toString());
        payeeMsg.setPayeeDateTime(DateUtil.formatDate(new Date()));
        if(org.apache.commons.lang3.StringUtils.isNotBlank(payChannel)){
            payeeMsg.setPayChannel(payChannel);
        }
        new WritePayeeMsgTask(getApplicationContext(),payeeMsg).execute();
    }
}