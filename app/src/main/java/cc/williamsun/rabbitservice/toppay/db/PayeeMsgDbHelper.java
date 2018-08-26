package cc.williamsun.rabbitservice.toppay.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static cc.williamsun.rabbitservice.toppay.db.DbSchema.PayeeMsgTable;

/**
 * 功能：收款消息DbSchemas
 * @author sunxiaoning
 * @date 2018/08/26
 */
public class PayeeMsgDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "payee_msg.db";

    public PayeeMsgDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //调用execSQL创建数据库
        //指定表的名称及其中的列
        sqLiteDatabase.execSQL("create table " + PayeeMsgTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                PayeeMsgTable.Cols.UUID + ", " +
                PayeeMsgTable.Cols.PAYER + ", " +
                PayeeMsgTable.Cols.PAYEE_AMOUNT + ", " +
                PayeeMsgTable.Cols.PAYEE_DATE_TIME + ", " +
                PayeeMsgTable.Cols.PAY_CHANNEL+ ")");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}