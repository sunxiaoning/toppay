package cc.williamsun.rabbitservice.toppay.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cc.williamsun.rabbitservice.toppay.message.PayeeMsg;

import static cc.williamsun.rabbitservice.toppay.db.DbSchema.PayeeMsgTable;

/**
 * 功能：收款消息DbSchemas
 * @author sunxiaoning
 * @date 2018/08/26
 */
public class PayeeMsgDao {
    private PayeeMsgDbHelper payeeMsgDbHelper;

    public PayeeMsgDao(Context context) {
        payeeMsgDbHelper = new PayeeMsgDbHelper(context);
    }

    /**
     * 记录收款消息
     * @param payeeMsg
     */
    public void savePayeeMsg(PayeeMsg payeeMsg) {
        SQLiteDatabase payeeDb = payeeMsgDbHelper.getWritableDatabase();
        payeeDb.insert(PayeeMsgTable.NAME, null, getContentValues(payeeMsg));
        payeeDb.close();
    }

    /**
     * 删除收款消息
     * @param payeeMsg
     */
    public void removeCrime(PayeeMsg payeeMsg) {
        SQLiteDatabase payeeDb = payeeMsgDbHelper.getWritableDatabase();
        payeeDb.delete(PayeeMsgTable.NAME,
                PayeeMsgTable.Cols.UUID + " = ?",
                new String[] {payeeMsg.getUuid().toString()});
        payeeDb.close();
    }

    public List<PayeeMsg> getPayeeMsgs() {
        List<PayeeMsg> payeeMsgList = new ArrayList<>();
        PayeeMsgCursorWrapper cursor = queryPayeeMsg(null, null);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                payeeMsgList.add(cursor.getPayeeMsg());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return payeeMsgList;
    }

    private PayeeMsgCursorWrapper queryPayeeMsg(String whereClause, String[] whereArgs) {
        SQLiteDatabase payeeDb = payeeMsgDbHelper.getReadableDatabase();

        // query接口将返回一个Cursor对象
        Cursor cursor = payeeDb.query(
                PayeeMsgTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);

        // 从Cursor对象中，再进一步解析出数据
        // 由于Cursor解析数据的过程比较繁琐，因此一般定义一个CursorWrapper来封装对应的操作
        return new PayeeMsgCursorWrapper(cursor);
    }

    /**
     * ContentValues以键值对的方式写入信息其中键就是数据库中的列
     * @param payeeMsg
     * @return
     */
    private ContentValues getContentValues(PayeeMsg payeeMsg) {
        ContentValues values = new ContentValues();
        values.put(PayeeMsgTable.Cols.UUID, payeeMsg.getUuid());
        values.put(PayeeMsgTable.Cols.PAYER, payeeMsg.getPayer());
        values.put(PayeeMsgTable.Cols.PAYEE_AMOUNT, payeeMsg.getPayeeAmount());
        values.put(PayeeMsgTable.Cols.PAYEE_DATE_TIME, payeeMsg.getPayeeDateTime());
        values.put(PayeeMsgTable.Cols.PAY_CHANNEL, payeeMsg.getPayChannel());
        return values;
    }
}