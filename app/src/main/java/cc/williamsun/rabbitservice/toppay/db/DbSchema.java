package cc.williamsun.rabbitservice.toppay.db;

/**
 * 功能：收款消息DbSchemas
 * @author sunxiaoning
 * @date 2018/08/26
 */
public class DbSchema {
    public static final class PayeeMsgTable {
        public static final String NAME = "payee_msg";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String PAYER = "payee";
            public static final String PAYEE_AMOUNT = "payee_amount";
            public static final String PAYEE_DATE_TIME = "payee_date_time";
            public static final String PAY_CHANNEL = "pay_channel";
        }
    }
}