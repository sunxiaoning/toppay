package cc.williamsun.rabbitservice.toppay.enums;

import org.apache.commons.lang3.StringUtils;

import cc.williamsun.focuscr.exception.PlatformRuntimeException;

/**
 * 功能：支付渠道
 *
 * @author sunxiaoning
 * @date 2018/08/11
 */
public enum PayChannelEnum {

    ALIPAY("支付宝支付"),
    WECHATPAY("微信支付");

    protected final String chineseName;

    PayChannelEnum(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getChineseName() {
        return chineseName;
    }

    public static final PayChannelEnum parseByName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new PlatformRuntimeException("PayChannelEnum value 值不能为空！");
        }
        for (PayChannelEnum PayChannelEnum : PayChannelEnum.values()) {
            if (name.equals(PayChannelEnum.name())) {
                return PayChannelEnum;
            }
        }
        throw new PlatformRuntimeException("PayChannelEnum parseByName 解析出错！");
    }
}
