package com.HesperusMarketing.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.HesperusMarketing.channelbridgeaddapters.ReportList;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


public class CollectionNoteSendToApprovel {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_COLLECTION_NOTE_NO = "COLLECTION_NOTE_NO";
    private static final String KEY_REP_NO = "REP_NO";
    private static final String KEY_CUSTOMER_CODE = "customer_code";
    private static final String KEY_CURRENT_OUTSTANDING = "CURRENT_OUTSTANDING";
    private static final String KEY_INVOICE_NO = "INVOICE_NO";
    private static final String KEY_CREDIT_AMOUNT = "CREDIT_AMOUNT";
    private static final String KEY_PAYMENT_TYPE = "PAYMENT_TYPE";
    private static final String KEY_CASH_AMOUNT = "CASH_AMOUNT";
    private static final String KEY_CHEQES_AMOUNT = "CHEQES_AMOUNT";
    private static final String KEY_UPLOAD_STATUS = "upload_status";
    private static final String KEY_INVOICEBALNCE = "INV_Balance";


    String[] columns = new String[]{KEY_ROW_ID, KEY_COLLECTION_NOTE_NO, KEY_REP_NO, KEY_CUSTOMER_CODE, KEY_CURRENT_OUTSTANDING, KEY_INVOICE_NO,
            KEY_CREDIT_AMOUNT, KEY_PAYMENT_TYPE, KEY_CASH_AMOUNT,KEY_CHEQES_AMOUNT,KEY_UPLOAD_STATUS, KEY_INVOICEBALNCE};
    private static final String TABLE_NAME = "collection_note_send_approval";
    private static final String COLLECTION_NOTE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_COLLECTION_NOTE_NO + " TEXT NOT NULL,"
            + KEY_REP_NO + " TEXT ,"
            + KEY_CUSTOMER_CODE + " TEXT ,"
            + KEY_CURRENT_OUTSTANDING + " TEXT ,"
            + KEY_INVOICE_NO + " TEXT ,"
            + KEY_CREDIT_AMOUNT + " TEXT ,"
            + KEY_PAYMENT_TYPE + " TEXT ,"
            + KEY_CASH_AMOUNT + " TEXT ,"
            + KEY_CHEQES_AMOUNT + " TEXT ,"
            + KEY_UPLOAD_STATUS + " TEXT, "
            + KEY_INVOICEBALNCE + " TEXT "
            + " );";
    public Context customerContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    public CollectionNoteSendToApprovel(Context c) {
        customerContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(COLLECTION_NOTE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public CollectionNoteSendToApprovel openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public CollectionNoteSendToApprovel openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }


    public long insertCollectionNoteSendToApprovel(String KEY_COLLECTION_NOTE_NO1, String KEY_REP_NO1, String KEY_CUSTOMER_CODE1, String KEY_CURRENT_OUTSTANDING1, String KEY_INVOICE_NO1,
                                                   String KEY_CREDIT_AMOUNT1, String KEY_PAYMENT_TYPE1, String KEY_CASH_AMOUNT1,String KEY_CHEQUE_AMOUNT1,String InvBalnce ) throws SQLException {

        ContentValues cv = new ContentValues();

        cv.put(KEY_COLLECTION_NOTE_NO, KEY_COLLECTION_NOTE_NO1);
        cv.put(KEY_REP_NO, KEY_REP_NO1);
        cv.put(KEY_CUSTOMER_CODE, KEY_CUSTOMER_CODE1);
        cv.put(KEY_CURRENT_OUTSTANDING, KEY_CURRENT_OUTSTANDING1);
        cv.put(KEY_INVOICE_NO, KEY_INVOICE_NO1);
        cv.put(KEY_CREDIT_AMOUNT, KEY_CREDIT_AMOUNT1);
        cv.put(KEY_PAYMENT_TYPE, KEY_PAYMENT_TYPE1);
        cv.put(KEY_CASH_AMOUNT, KEY_CASH_AMOUNT1);
        cv.put(KEY_CHEQES_AMOUNT, KEY_CHEQUE_AMOUNT1);
        cv.put(KEY_UPLOAD_STATUS, "false");
        cv.put(KEY_INVOICEBALNCE, InvBalnce);


        return database.insert(TABLE_NAME, null, cv);

    }

    public String GenareCollectionNoteNumber() {
        String Number = null;
        int count = 1;
        String finalCount;

        try {
            openReadableDatabase();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(customerContext);
            String deviceId = sharedPreferences.getString("DeviceId", "-1");


            String countQuery = "SELECT  row_id FROM " + TABLE_NAME + "";
            Cursor cur = database.rawQuery(countQuery, null);
            count = cur.getCount();
            cur.close();

            if (count == 0) {
                count = 1;
            }

            finalCount = String.valueOf(count);
            if (finalCount.length() == 1) {
                finalCount = "00" + finalCount;
            } else if (finalCount.length() == 1) {
                finalCount = "0" + finalCount;
            }

            String splitDevicedID[] = deviceId.split("@");

            Number = splitDevicedID[0] + "/" + "HES" + "/" + count;
            closeDatabase();
        } catch (Exception e) {


        }

        return Number;

    }

    public void setCellectionNoteUpdatedStatus(String invoiceId, String status) {

        String updateQuery = "UPDATE " + TABLE_NAME
                + " SET "
                + KEY_UPLOAD_STATUS
                + " = '"
                + status
                + "' WHERE "
                + KEY_ROW_ID
                + " = '"
                + invoiceId
                + "'";

        database.execSQL(updateQuery);
        Log.w("Upload service", "<Invoice> Set invoice uploaded status to :" + status + " of id : " + invoiceId + "");
    }

    public List<String[]> getCollectionNoteByUploadStatus(String status) {
        List<String[]> invoice = new ArrayList<String[]>();


        Log.w("CustomersPendingApproval", "status : " + status);

//		Cursor cursor = database.query(TABLE_NAME,
//				columns, KEY_UPLOADED_STATUS+" = ?", new String[]{status}, null, null, null);

        Cursor cursor = database.query(TABLE_NAME, columns, KEY_UPLOAD_STATUS + " = '" + status + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoiceData = new String[12];
            invoiceData[0] = cursor.getString(0);//KEY_ROW_ID
            invoiceData[1] = cursor.getString(1);//KEY_COLLECTION_NOTE_NO
            invoiceData[2] = cursor.getString(2);// KEY_REP_NO
            invoiceData[3] = cursor.getString(3);//customer_code
            invoiceData[4] = cursor.getString(4);//KEY_CURRENT_OUTSTANDING
            invoiceData[5] = cursor.getString(5);//KEY_INVOICE_NO
            invoiceData[6] = cursor.getString(6);//KEY_CREDIT_AMOUNT
            invoiceData[7] = cursor.getString(7);//KEY_PAYMENT_TYPE
            invoiceData[8] = cursor.getString(8);//KEY_CASH_AMOUNT
            invoiceData[9] = cursor.getString(9);//KEY_CHEQUE_AMOUNT
            invoiceData[10] = cursor.getString(10);//KEY_INVOICEBALNCE



            invoice.add(invoiceData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoice size", "inside : " + invoice.size());

        return invoice;
    }


    public String ConvertByteArryTobase64String(byte[] data1) {
        String strFile;
        byte[] data = data1;//Convert any file, image or video into byte array
        strFile = Base64.encodeToString(data1, Base64.NO_WRAP);//Convert byte array into string
        return strFile;

    }

    public ArrayList<ReportList> getDataForCollection(String customer, String town, String fromDate, String toDate, int searchStatus) {
        ArrayList<ReportList> collection = new ArrayList<>();

        Cursor cursor = null;

        if (searchStatus == 0) {
            cursor = database.rawQuery("SELECT COLLECT_DATE, COLLECTION_NOTE_NO,INVOICE_NO,CASH_AMOUNT, CHEQUE_AMOUNT FROM collection_note_send_approval col INNER JOIN customers cus on col.CUSTOMER_NAME = cus.customer_name where col.CUSTOMER_NAME = '" + customer + "' ORDER BY COLLECTION_NOTE_NO", null);

        } else if (searchStatus == 1) {
            cursor = database.rawQuery("SELECT COLLECT_DATE, COLLECTION_NOTE_NO,INVOICE_NO,CASH_AMOUNT, CHEQUE_AMOUNT FROM collection_note_send_approval col INNER JOIN customers cus on col.CUSTOMER_NAME = cus.customer_name where col.CUSTOMER_NAME = '" + customer + "' AND COLLECT_DATE BETWEEN '" + fromDate + "' AND '" + toDate + "'  ORDER BY COLLECTION_NOTE_NO", null);

        } else if (searchStatus == 3) {
            cursor = database.rawQuery("SELECT COLLECT_DATE, COLLECTION_NOTE_NO,INVOICE_NO,CASH_AMOUNT, CHEQUE_AMOUNT,cus.customer_name  FROM collection_note_send_approval col INNER JOIN customers cus on col.CUSTOMER_NAME = cus.customer_name where cus.town = '" + town + "' ORDER BY COLLECTION_NOTE_NO", null);
        } else if (searchStatus == 4) {
            cursor = database.rawQuery("SELECT COLLECT_DATE, COLLECTION_NOTE_NO,INVOICE_NO,CASH_AMOUNT, CHEQUE_AMOUNT,cus.customer_name  FROM collection_note_send_approval col INNER JOIN customers cus on col.CUSTOMER_NAME = cus.customer_name where cus.town = '" + town + "'  AND COLLECT_DATE BETWEEN '" + fromDate + "' AND '" + toDate + "' ORDER BY COLLECTION_NOTE_NO", null);
        }


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ReportList dealerSaleEntity = new ReportList();
            dealerSaleEntity.setCollectionDate(cursor.getString(0));//Collection Date
            dealerSaleEntity.setCollectionNo(cursor.getString(1));//Collection Note No
            dealerSaleEntity.setInvoiceNo(cursor.getString(2));//Invoice No
            dealerSaleEntity.setCashAmmount(cursor.getString(3));//Cash
            dealerSaleEntity.setChequsAmmount(cursor.getString(4));//Check


            if (searchStatus == 3 || searchStatus == 4) {
                dealerSaleEntity.setDelName(cursor.getString(5));
            } else {

            }

            collection.add(dealerSaleEntity);
            cursor.moveToNext();
        }
        return collection;
    }

}
