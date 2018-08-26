package cc.williamsun.rabbitservice.toppay.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import cc.williamsun.rabbitservice.toppay.message.PayeeMsg;
import static cc.williamsun.rabbitservice.toppay.db.DbSchema.*;

/**
 * 功能：收款消息CursorWrapper
 * @author sunxiaoning
 * @date 2018/08/26
 */
public class PayeeMsgCursorWrapper extends CursorWrapper {
    public PayeeMsgCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * 获取PayeeMsg
     * @return
     */
    public PayeeMsg getPayeeMsg() {
        PayeeMsg payeeMsg = new PayeeMsg();
        payeeMsg.setUuid(getString(getColumnIndex(PayeeMsgTable.Cols.UUID)));
        payeeMsg.setPayer(getString(getColumnIndex(PayeeMsgTable.Cols.PAYER)));
        payeeMsg.setPayeeAmount(getString(getColumnIndex(PayeeMsgTable.Cols.PAYEE_AMOUNT)));
        payeeMsg.setPayeeDateTime(getString(getColumnIndex(PayeeMsgTable.Cols.PAYEE_DATE_TIME)));
        payeeMsg.setUuid(getString(getColumnIndex(PayeeMsgTable.Cols.PAY_CHANNEL)));
        return payeeMsg;
    }


}