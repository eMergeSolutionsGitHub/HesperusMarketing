package com.HesperusMarketing.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Himanshu on 6/8/2016.
 */
public class CollectionNoteInvoice {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_COLLECTIONNOTE_NO = "collevtion_note_no";
    private static final String KEY_INVOICE_NO = "invoice_no";
    private static final String KEY_PAYMENT_TYPE = "type";
    private static final String KEY_CREDIT_AMOUNT = "credit_amount";
    private static final String KEY_CASH_AMOUNT = "cash_amount";
    private static final String KEY_CHEQES_AMOUNT = "cheqes_amount";
    private static final String KEY_UPLOAD_STATUS = "upload_status";


    String[] columns = new String[]{KEY_ROW_ID, KEY_COLLECTIONNOTE_NO, KEY_INVOICE_NO, KEY_PAYMENT_TYPE,KEY_CREDIT_AMOUNT, KEY_CASH_AMOUNT,KEY_CHEQES_AMOUNT};


    private static final String TABLE_NAME = "CollectionNote_Invoice";

    private static final String COLLECTION_NOTE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_COLLECTIONNOTE_NO + " TEXT NOT NULL,"
            + KEY_INVOICE_NO + " TEXT ,"
            + KEY_PAYMENT_TYPE + " TEXT ,"
            + KEY_CREDIT_AMOUNT + " TEXT ,"
            + KEY_CASH_AMOUNT + " TEXT ,"
            + KEY_CHEQES_AMOUNT + " TEXT ,"
            + KEY_UPLOAD_STATUS + " TEXT " + " );";
    public final Context customerContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    public CollectionNoteInvoice(Context c) {
        customerContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(COLLECTION_NOTE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public CollectionNoteInvoice openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public CollectionNoteInvoice openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insert_CollectionInvoice(String CollectionNo, String invoiceno,String type,String credit,String cash,String cheque) throws SQLException {

        ContentValues cv = new ContentValues();
        cv.put(KEY_COLLECTIONNOTE_NO, CollectionNo);
        cv.put(KEY_INVOICE_NO ,invoiceno);
        cv.put(KEY_PAYMENT_TYPE,type);
        cv.put(KEY_CREDIT_AMOUNT,credit);
        cv.put(KEY_CASH_AMOUNT, cash);
        cv.put(KEY_CHEQES_AMOUNT, cheque);
        cv.put(KEY_UPLOAD_STATUS, 0);

        return database.insert(TABLE_NAME, null, cv);

    }

    public List<String[]> getCollectionNoteInvoiceByUploadStatus() {
        List<String[]> invoice = new ArrayList<String[]>();

        String countQuery = "SELECT collection_note_number,invoice_no,type,credit_amount,cash_amount,cheqes_amount,row_id FROM CollectionNote_Invoice WHERE upload_status = '0' ";

        Cursor cursor =  database.rawQuery(countQuery, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoiceData = new String[7];
            invoiceData[0] = cursor.getString(0);//KEY_COLLECTIONNOTE_NO
            invoiceData[1] = cursor.getString(1);//KEY_INVOICE_NO
            invoiceData[2] = cursor.getString(2);// KEY_PAYMENT_TYPE
            invoiceData[3] = cursor.getString(3);//KEY_CREDIT_AMOUNT
            invoiceData[4] = cursor.getString(4);//KEY_CASH_AMOUNT
            invoiceData[5] = cursor.getString(5);//KEY_CHEQES_AMOUNT
            invoiceData[6] = cursor.getString(6);//Row ID


            invoice.add(invoiceData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoice size", "inside : " + invoice.size());

        return invoice;
    }
    public void setCellectionNoteInvoiceUpdatedStatus(String rowid, String status) {

        String updateQuery = "UPDATE " + TABLE_NAME
                + " SET "
                + KEY_UPLOAD_STATUS
                + " = '"
                + status
                + "' WHERE "
                + KEY_ROW_ID
                + " = '"
                + rowid
                + "'";

        database.execSQL(updateQuery);

    }


}
