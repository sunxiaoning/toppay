package cc.williamsun.rabbitservice.toppay.message;

/**
 * 功能：收款消息
 * @author sunxiaoning
 * @date 2018/08/26
 */
public class PayeeMsg {

    /**
     * uuid
     */
    private String uuid;

    /**
     * 付款人
     */
    private String payer;

    /**
     * 收款金额
     */
    private String payeeAmount;

    /**
     * 收款时间
     */
    private String payeeDateTime;

    /**
     * 收款渠道
     */
    private String payChannel;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getPayeeAmount() {
        return payeeAmount;
    }

    public void setPayeeAmount(String payeeAmount) {
        this.payeeAmount = payeeAmount;
    }

    public String getPayeeDateTime() {
        return payeeDateTime;
    }

    public void setPayeeDateTime(String payeeDateTime) {
        this.payeeDateTime = payeeDateTime;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }
}
