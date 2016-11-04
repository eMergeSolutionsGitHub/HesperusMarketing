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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class CollectionNoteSendToApprovel {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_COLLECTION_NOTE_NO = "collection_note_number";
    private static final String KEY_REP_NO = "rep_number";
    private static final String KEY_CUSTOMER_CODE = "customer_code";
    private static final String KEY_CURRENT_OUTSTANDING = "current_outstanding";
    private static final String KEY_CHEQUEAMOUNT= "cheque_ammount";
    private static final String KEY_CASHAMOUNT= "cash_ammount";
    private static final String KEY_DATE = "collected_date";
    private static final String KEY_UPLOAD_STATUS = "upload_status";



    String[] columns = new String[]{KEY_ROW_ID, KEY_COLLECTION_NOTE_NO, KEY_REP_NO, KEY_CUSTOMER_CODE, KEY_CURRENT_OUTSTANDING,KEY_CHEQUEAMOUNT,KEY_CASHAMOUNT,KEY_DATE,KEY_UPLOAD_STATUS};
    private static final String TABLE_NAME = "collection_note_send_approval";
    private static final String COLLECTION_NOTE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_COLLECTION_NOTE_NO + " TEXT NOT NULL,"
            + KEY_REP_NO + " TEXT ,"
            + KEY_CUSTOMER_CODE + " TEXT ,"
            + KEY_CURRENT_OUTSTANDING + " TEXT ,"
            + KEY_CHEQUEAMOUNT + " TEXT ,"
            + KEY_CASHAMOUNT + " TEXT ,"
            + KEY_DATE + " TEXT ,"
            + KEY_UPLOAD_STATUS + " TEXT "
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


    public long insertCollectionNoteSendToApprovel(String KEY_COLLECTION_NOTE_NO1, String KEY_REP_NO1, String KEY_CUSTOMER_CODE1, String KEY_CURRENT_OUTSTANDING1,String cash,String cheque ) throws SQLException {

        ContentValues cv = new ContentValues();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");

        cv.put(KEY_COLLECTION_NOTE_NO, KEY_COLLECTION_NOTE_NO1);
        cv.put(KEY_REP_NO, KEY_REP_NO1);
        cv.put(KEY_CUSTOMER_CODE, KEY_CUSTOMER_CODE1);
        cv.put(KEY_CURRENT_OUTSTANDING, KEY_CURRENT_OUTSTANDING1);
        cv.put(KEY_CHEQUEAMOUNT, cheque);
        cv.put(KEY_CASHAMOUNT, cash);
        cv.put(KEY_DATE, format.format(new Date()));
        cv.put(KEY_UPLOAD_STATUS, "false");


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

            Random ra = new Random();
            int num = ra.nextInt(1000);

            Number = splitDevicedID[0] + "/" + "HES" + "/" + count+num;
            closeDatabase();
        } catch (Exception e) {
        }
        return Number;
    }

    public void setCellectionNoteUpdatedStatus(String rowid, String status) {

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

    public List<String[]> getCollectionChqesNoteByUploadStatus() {
        List<String[]> invoice = new ArrayList<String[]>();

        Cursor cursor = database.rawQuery("SELECT cnsa.row_id,cnsa.collection_note_number,cnsa.rep_number,cnsa.customer_code,cnsa.current_outstanding,\n" +
                "ci.invoice_no,ci.credit_amount,ci.type,ci.cash_amount,ci.cheqes_amount,ci.balance_amount,\n" +
                "cc.Cheque_number,cc.bank,cc.branch,cc.collect_date,cc.realized_date,cc.cheque_image\n" +
                "FROM collection_note_send_approval cnsa\n" +
                "INNER JOIN CollectionNote_Cheque_invoice cci on cnsa.collection_note_number = cci.collevtion_note_no\n" +
                "INNER JOIN CollectionNote_Cheque cc on cci.collevtion_note_no = cc.collevtion_note_no and cci.Cheque_number = cc.Cheque_number\n" +
                "INNER JOIN CollectionNote_Invoice ci on cci.collevtion_note_no = ci.collevtion_note_no and cci.invoices_number = ci.invoice_no WHERE cnsa.upload_status ='false'", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoiceData = new String[20];

            invoiceData[0] = cursor.getString(0);//KEY_ROW_ID
            invoiceData[1] = cursor.getString(1);//KEY_COLLECTION_NOTE_NO
            invoiceData[2] = cursor.getString(2);//KEY_REP_NO
            invoiceData[3] = cursor.getString(3);//customer_code
            invoiceData[4] = cursor.getString(4);//KEY_CURRENT_OUTSTANDING
            invoiceData[5] = cursor.getString(5);//Inv no
            invoiceData[6] = cursor.getString(6);//credit
            invoiceData[7] = cursor.getString(7);//type


            invoiceData[8] = cursor.getString(8);//KEY_CASHAMOUNT
            invoiceData[9] = cursor.getString(9);//KEY_CHEQUEAMOUNT
            invoiceData[10] = cursor.getString(10);//bal

            if(cursor.getString(11)==null){
                invoiceData[11] ="";
            }else {
                invoiceData[11] = cursor.getString(11);//CHEQUE no
            }
            if(cursor.getString(12)==null){
                invoiceData[12] ="";
            }else {
                invoiceData[12] = cursor.getString(12);//BANKJ no
            }

            if(cursor.getString(13)==null){
                invoiceData[13] ="";
            }else {
                invoiceData[13] = cursor.getString(13);//BRANCH
            }
            if(cursor.getString(14)==null){
                invoiceData[14] ="";
            }else {
                invoiceData[14] = cursor.getString(14);//cal
            }

            if(cursor.getString(15)==null){
                invoiceData[15] ="";
            }else {
                invoiceData[15] = cursor.getString(15);//rel date
            }

            if(cursor.getBlob(16)==null){
                invoiceData[16]="";
            }else {
                byte[] bb = cursor.getBlob(16);
                invoiceData[16] = ConvertByteArryTobase64String(bb);
            }


            invoice.add(invoiceData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoice size", "inside : " + invoice.size());

        return invoice;
    }

    public List<String[]> getCollectionCashNoteByUploadStatus() {
        List<String[]> invoice = new ArrayList<String[]>();

        Cursor cursor = database.rawQuery("SELECT cnsa.row_id,cnsa.collection_note_number,cnsa.rep_number,cnsa.customer_code,cnsa.current_outstanding,\n" +
                "ci.invoice_no,ci.credit_amount,ci.type,ci.cash_amount,ci.cheqes_amount,ci.balance_amount\n" +
                "FROM collection_note_send_approval cnsa\n" +
                "INNER JOIN CollectionNote_Invoice ci on cnsa.collection_note_number = ci.collevtion_note_no \n" +
                "WHERE  NOT EXISTS (SELECT *\n" +
                "                   FROM   CollectionNote_Cheque_invoice od\n" +
                "                   WHERE  cnsa.collection_note_number = od.collevtion_note_no) AND cnsa.upload_status ='false'", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoiceData = new String[20];

            invoiceData[0] = cursor.getString(0);//KEY_ROW_ID
            invoiceData[1] = cursor.getString(1);//KEY_COLLECTION_NOTE_NO
            invoiceData[2] = cursor.getString(2);//KEY_REP_NO
            invoiceData[3] = cursor.getString(3);//customer_code
            invoiceData[4] = cursor.getString(4);//KEY_CURRENT_OUTSTANDING
            invoiceData[5] = cursor.getString(5);//Inv no
            invoiceData[6] = cursor.getString(6);//credit
            invoiceData[7] = cursor.getString(7);//type
            invoiceData[8] = cursor.getString(8);//KEY_CASHAMOUNT
            invoiceData[9] = cursor.getString(9);//KEY_CHEQUEAMOUNT
            invoiceData[10] = cursor.getString(10);//bal
            invoiceData[11] ="";
            invoiceData[12] ="";
            invoiceData[13] ="";
            invoiceData[14] ="";
            invoiceData[15] ="";
            invoiceData[16]="";

            invoice.add(invoiceData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoice size", "inside : " + invoice.size());

        return invoice;
    }


    public ArrayList<ReportList> getDataForCollection(String customer, String town, String fromDate, String toDate, int searchStatus) {
        ArrayList<ReportList> collection = new ArrayList<>();

        Cursor cursor = null;

        if (searchStatus == 0) {
            cursor = database.rawQuery("SELECT col.collected_date, col.collection_note_number,inv.invoice_no,inv.cash_amount,inv.cheqes_amount FROM collection_note_send_approval col INNER JOIN customers cus on col.customer_code = cus.pharmacy_code INNER JOIN CollectionNote_Invoice inv on col.collection_note_number = inv.collevtion_note_no  where cus.customer_name = '" + customer + "' ORDER BY collection_note_number", null);

        } else if (searchStatus == 1) {
            cursor = database.rawQuery("SELECT col.collected_date, col.collection_note_number,inv.invoice_no,inv.cash_amount,inv.cheqes_amount FROM collection_note_send_approval col INNER JOIN customers cus on col.customer_code = cus.pharmacy_code INNER JOIN CollectionNote_Invoice inv on col.collection_note_number = inv.collevtion_note_no  where cus.customer_name = '" + customer + "' AND col.collected_date BETWEEN'" + fromDate + "' AND '" + toDate + "'  ORDER BY collection_note_number", null);

        } else if (searchStatus == 3) {
            cursor = database.rawQuery("SELECT col.collected_date, col.collection_note_number,inv.invoice_no,inv.cash_amount,inv.cheqes_amount FROM collection_note_send_approval col INNER JOIN customers cus on col.customer_code = cus.pharmacy_code INNER JOIN CollectionNote_Invoice inv on col.collection_note_number = inv.collevtion_note_no where cus.town = '" + town + "' ORDER BY collection_note_number", null);
        } else if (searchStatus == 4) {
            cursor = database.rawQuery("SELECT col.collected_date, col.collection_note_number,inv.invoice_no,inv.cash_amount,inv.cheqes_amount FROM collection_note_send_approval col INNER JOIN customers cus on col.customer_code = cus.pharmacy_code INNER JOIN CollectionNote_Invoice inv on col.collection_note_number = inv.collevtion_note_no where cus.town = '" + town + "'  AND col.collected_date BETWEEN '" + fromDate + "' AND '" + toDate + "' ORDER BY collection_note_number", null);
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
    public String ConvertByteArryTobase64String(byte[] data1) {
        String strFile;
        byte[] data = data1;//Convert any file, image or video into byte array
        strFile = Base64.encodeToString(data1, Base64.NO_WRAP);//Convert byte array into string
        return strFile;

    }

}
