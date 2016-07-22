package com.HesperusMarketing.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.HesperusMarketing.channelbridgeaddapters.ReportList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Himanshu on 6/8/2016.
 */
public class CollectionNoteCheques {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_COLLECTIONNOTE_NO = "collevtion_note_no";
    private static final String KEY_CHEQUENUMBER= "Cheque_number";
    private static final String KEY_CHEQUEAMOUNT= "Cheque_ammount";
    private static final String KEY_COLLECT_DATE = "collect_date";
    private static final String KEY_BANK = "bank";
    private static final String KEY_BRANCH= "branch";
    private static final String KEY_REALIZED_DATE= "realized_date";
    private static final String KEY_CHEQUE_IMAGE = "cheque_image";
    private static final String KEY_UPLOAD_STATUS = "upload_status";


    String[] columns = new String[]{KEY_ROW_ID, KEY_COLLECTIONNOTE_NO, KEY_CHEQUENUMBER, KEY_CHEQUEAMOUNT, KEY_COLLECT_DATE, KEY_BANK,KEY_BRANCH, KEY_REALIZED_DATE,KEY_CHEQUE_IMAGE,KEY_UPLOAD_STATUS};


    private static final String TABLE_NAME = "CollectionNote_Cheque";

    private static final String COLLECTION_NOTE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_COLLECTIONNOTE_NO + " TEXT NOT NULL,"
            + KEY_CHEQUENUMBER + " TEXT ,"
            + KEY_CHEQUEAMOUNT + " TEXT ,"
            + KEY_COLLECT_DATE + " TEXT ,"
            + KEY_BANK + " TEXT ,"
            + KEY_BRANCH + " TEXT ,"
            + KEY_REALIZED_DATE + " TEXT ,"
            + KEY_CHEQUE_IMAGE + " BLOB ,"
            + KEY_UPLOAD_STATUS + " TEXT " + " );";
    public final Context customerContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    public CollectionNoteCheques(Context c) {
        customerContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(COLLECTION_NOTE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public CollectionNoteCheques openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public CollectionNoteCheques openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insert_CollectionCheqes(String CollectionNo, String chequenumber,String cheqamount,String bankcode,String branchcode,String realizedDate,byte[] image) throws SQLException {

        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        ContentValues cv = new ContentValues();
        cv.put(KEY_COLLECTIONNOTE_NO, CollectionNo);
        cv.put(KEY_CHEQUENUMBER , chequenumber);
        cv.put(KEY_CHEQUEAMOUNT, cheqamount);
        cv.put(KEY_COLLECT_DATE , format.format(new Date()));
        cv.put(KEY_BANK, bankcode);
        cv.put(KEY_BRANCH, branchcode);
        cv.put(KEY_REALIZED_DATE, realizedDate);
        cv.put(KEY_CHEQUE_IMAGE, image);
        cv.put(KEY_UPLOAD_STATUS, 0);

        return database.insert(TABLE_NAME, null, cv);

    }

    public void setCellectionNoteChequeUpdatedStatus(String rowid, String status) {

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
    public List<String[]> getCollectionNoteByUploadStatus() {
        List<String[]> invoice = new ArrayList<String[]>();

        String countQuery = "SELECT collection_note_number,Cheque_number,Cheque_ammount,collect_date,bank,branch,realized_date,cheque_image,row_id FROM CollectionNote_Cheque WHERE upload_status = '0' ";

        Cursor cursor =  database.rawQuery(countQuery, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoiceData = new String[9];
            invoiceData[0] = cursor.getString(0);//KEY_COLLECTIONNOTE_NO
            invoiceData[1] = cursor.getString(1);//KEY_CHEQUENUMBER
            invoiceData[2] = cursor.getString(2);// KEY_CHEQUEAMOUNT
            invoiceData[3] = cursor.getString(3);//KEY_COLLECT_DATE
            invoiceData[4] = cursor.getString(4);//KEY_BANK
            invoiceData[5] = cursor.getString(5);//KEY_BRANCH
            invoiceData[6] = cursor.getString(6);//KEY_REALIZED_DATE
            invoiceData[7] = cursor.getString(7);//KEY_CHEQUE_IMAGE
            invoiceData[8] = cursor.getString(8);//ROWID





            invoice.add(invoiceData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoice size", "inside : " + invoice.size());

        return invoice;
    }



}
