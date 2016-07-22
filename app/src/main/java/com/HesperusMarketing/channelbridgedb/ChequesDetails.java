package com.HesperusMarketing.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.HesperusMarketing.channelbridgeaddapters.ReportList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Himanshu on 6/8/2016.
 */
public class ChequesDetails {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_ID = "ID";
    private static final String KEY_CUSTOMER_NO = "Customer_no";
    private static final String KEY_COLLECTIONNOTE_NO = "Collevtion_note_no";
    private static final String KEY_INVOICENO = "Invoice_no";
    private static final String KEY_PAYMENTTYPE = "Payment_type";
    private static final String KEY_CHEQUENUMBER = "Cheque_number";
    private static final String KEY_CHEQUEAMOUNT = "Cheque_ammount";
    private static final String KEY_COLLECT_DATE = "Collect_Date";
    private static final String KEY_IS_APPROVED = "is_Approved";
    private static final String KEY_ISREJECTED = "is_Reject";
    private static final String KEY_Is_REALIZED = "is_Realized";
    private static final String KEY_IsBOUNXED = "is_Bounzed";


    String[] columns = new String[]{KEY_ROW_ID, KEY_ID, KEY_CUSTOMER_NO, KEY_COLLECTIONNOTE_NO, KEY_INVOICENO, KEY_PAYMENTTYPE, KEY_CHEQUENUMBER, KEY_CHEQUEAMOUNT, KEY_CHEQUEAMOUNT, KEY_COLLECT_DATE
            , KEY_IS_APPROVED, KEY_ISREJECTED, KEY_Is_REALIZED, KEY_IsBOUNXED};


    private static final String TABLE_NAME = "Cheque_Detals";

    private static final String COLLECTION_NOTE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_ID + " TEXT NOT NULL,"
            + KEY_CUSTOMER_NO + " TEXT ,"
            + KEY_COLLECTIONNOTE_NO + " TEXT ,"
            + KEY_INVOICENO + " TEXT ,"
            + KEY_PAYMENTTYPE + " TEXT ,"
            + KEY_CHEQUENUMBER + " TEXT ,"
            + KEY_CHEQUEAMOUNT + " TEXT ,"
            + KEY_COLLECT_DATE + " TEXT ,"
            + KEY_IS_APPROVED + " TEXT ,"
            + KEY_ISREJECTED + " TEXT ,"
            + KEY_Is_REALIZED + " TEXT ,"
            + KEY_IsBOUNXED + " TEXT " + " );";
    public final Context customerContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    public ChequesDetails(Context c) {
        customerContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(COLLECTION_NOTE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public ChequesDetails openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public ChequesDetails openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public void insert_Cheqes(String ID, String cusname, String collevtionnoteno, String invoiceno, String paymenttype, String chequenumber, String cheqamount,
                              String collectdate, String isreject, String isrealized, String isbounzed, String isapproved) throws SQLException {

        ContentValues cv = new ContentValues();

        Cursor cursorMain = null;
        cursorMain = database.rawQuery("select ID from Cheque_Detals where ID = '" + ID + "' ", null);

        if(cursorMain.getCount()>0){

        }else {
            cv.put(KEY_ID, ID);
            cv.put(KEY_CUSTOMER_NO, cusname);
            cv.put(KEY_COLLECTIONNOTE_NO, collevtionnoteno);
            cv.put(KEY_INVOICENO, invoiceno);
            cv.put(KEY_PAYMENTTYPE, paymenttype);
            cv.put(KEY_CHEQUENUMBER, chequenumber);
            cv.put(KEY_CHEQUEAMOUNT, cheqamount);
            cv.put(KEY_COLLECT_DATE, collectdate);
            cv.put(KEY_IS_APPROVED, isapproved);
            cv.put(KEY_ISREJECTED, isreject);
            cv.put(KEY_Is_REALIZED, isrealized);
            cv.put(KEY_IsBOUNXED, isbounzed);


            database.insert(TABLE_NAME, null, cv);
        }




    }

    public ArrayList<ReportList> getDataForDealerSales(String customer, String town, String fromDate, String toDate, int searchStatus) {

        Cursor cursorMain = null;

        Calendar c = Calendar.getInstance();
        String status = null, returnVal;


        ArrayList<ReportList> sales = new ArrayList<>();
        try {
            if (searchStatus == 0) {
                cursorMain = database.rawQuery("select cus.customer_name, ch.Collect_Date,ch.Collevtion_note_no,ch.Cheque_number,ch.Invoice_no,ch.Cheque_ammount from Cheque_Detals ch INNER JOIN customers  cus on cus.pharmacy_id = ch.Customer_no where cus.customer_name = '" + customer + "' ", null);

            } else if (searchStatus == 1) {
                cursorMain = database.rawQuery("select cus.customer_name, ch.Collect_Date,ch.Collevtion_note_no,ch.Cheque_number,ch.Invoice_no,ch.Cheque_ammount from Cheque_Detals ch INNER JOIN customers  cus on cus.pharmacy_id = ch.Customer_no where cus.customer_name = '" + customer + "' AND ch.Collect_Date BETWEEN '" + fromDate + "' AND '" + toDate + "' ", null);

            } else if (searchStatus == 3) {
                cursorMain = database.rawQuery("select cus.customer_name, ch.Collect_Date,ch.Collevtion_note_no,ch.Cheque_number,ch.Invoice_no,ch.Cheque_ammount from Cheque_Detals ch INNER JOIN customers  cus on cus.pharmacy_id = ch.Customer_no where cus.town = '" + town + "' ", null);
            } else if (searchStatus == 4) {
                cursorMain = database.rawQuery("select cus.customer_name, ch.Collect_Date,ch.Collevtion_note_no,ch.Cheque_number,ch.Invoice_no,ch.Cheque_ammount from Cheque_Detals ch INNER JOIN customers  cus on cus.pharmacy_id = ch.Customer_no where cus.town = '" + town + "'  AND ch.Collect_Date BETWEEN '" + fromDate + "' AND '" + toDate + "' ", null);
            }

            cursorMain.moveToFirst();

            while (!cursorMain.isAfterLast()) {
                ReportList reportList = new ReportList();
                reportList.setDelName(cursorMain.getString(0));
                reportList.setCollectedDate(cursorMain.getString(1));
                reportList.setCollectionNo(cursorMain.getString(2));
                reportList.setChequeNum(cursorMain.getString(3));
                reportList.setInvoiceNo(cursorMain.getString(5));

                sales.add(reportList);
                cursorMain.moveToNext();
            }

        } catch (Exception ex) {
        }

        return sales;
    }

}
