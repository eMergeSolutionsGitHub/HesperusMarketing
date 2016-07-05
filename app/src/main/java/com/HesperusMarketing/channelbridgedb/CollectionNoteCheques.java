package com.HesperusMarketing.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.HesperusMarketing.channelbridgeaddapters.ReportList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Himanshu on 6/8/2016.
 */
public class CollectionNoteCheques {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_COLLECTIONNOTE_NO = "Collevtion_note_no";
    private static final String KEY_CHEQUENUMBER= "Cheque_number";
    private static final String KEY_CHEQUEAMOUNT= "Cheque_ammount";
    private static final String KEY_COLLECT_DATE = "Collect_Date";
    private static final String KEY_BANK = "Bank";
    private static final String KEY_BRANCH= "Branch";
    private static final String KEY_REALIZED_DATE= "Realized_Date";
    private static final String KEY_CHEQUE_IMAGE = "Cheque_image";


    String[] columns = new String[]{KEY_ROW_ID, KEY_COLLECTIONNOTE_NO, KEY_CHEQUENUMBER, KEY_CHEQUEAMOUNT, KEY_COLLECT_DATE, KEY_BANK,KEY_BRANCH, KEY_REALIZED_DATE,KEY_CHEQUE_IMAGE};


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
            + KEY_CHEQUE_IMAGE + " BLOB " + " );";
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

    public long insert_CollectionCheqes(String CollectionNo, String chequenumber,String cheqamount,String bankcode,String branchcode,String realizedDate,String image) throws SQLException {

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


        return database.insert(TABLE_NAME, null, cv);

    }


}
