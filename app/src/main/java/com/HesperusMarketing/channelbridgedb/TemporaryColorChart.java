package com.HesperusMarketing.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Himanshu on 3/15/2016.
 */
public class TemporaryColorChart {
    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_CODE = "code";
    private static final String KEY_SHELF = "shelf";
    private static final String KEY_REQUEST = "request";
    private static final String KEY_FREE = "free";
    private static final String KEY_ACCESS = "access";



    String[] columns = new String[]{KEY_ROW_ID,KEY_CODE, KEY_SHELF, KEY_REQUEST, KEY_FREE, KEY_ACCESS};


    private static final String TABLE_NAME = "temporarycolorchat";

    private static final String TABLE_TEM = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_CODE + " TEXT NOT NULL,"
            + KEY_SHELF + " INTEGER ,"
            + KEY_REQUEST + " INTEGER ,"
            + KEY_FREE + " INTEGER ,"
            + KEY_ACCESS + " INTEGER " + " );"; //add customer image  sk


    public final Context customerContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    public TemporaryColorChart(Context c) {
        customerContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_TEM);

    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);

    }

    public TemporaryColorChart openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public TemporaryColorChart openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public void deleteAllRecords(){

        try {
            database.execSQL("delete from " + TABLE_NAME);
        }catch (SQLException e){

        }
    }
    public long insertColorChart(String code, int shelf, int request, int free, int access,int status) throws SQLException {

        ContentValues cv = new ContentValues();
        Cursor c = database.rawQuery("SELECT * FROM temporarycolorchat where code ='" + code + "'", null);
        Cursor c2 = database.rawQuery("SELECT * FROM temporarycolorchat where code ='" + code + "' AND access =1 ", null);
        if(c.getCount()==0){
            cv.put(KEY_CODE, code);
            cv.put(KEY_SHELF, shelf);
            cv.put(KEY_REQUEST, request);
            cv.put(KEY_FREE, free);
            cv.put(KEY_ACCESS, access);
        }else {
            if(c2.getCount()==0){
                cv.put(KEY_SHELF, shelf);
                cv.put(KEY_REQUEST, request);
                cv.put(KEY_FREE, free);
                cv.put(KEY_ACCESS, access);
                database.update(TABLE_NAME, cv, KEY_CODE + " = ?", new String[]{code});
            }else {
                if(status==1){
                    cv.put(KEY_SHELF,  shelf);
                    cv.put(KEY_REQUEST, request);
                    cv.put(KEY_FREE,  free);
                }else{
                    Cursor c3 = database.rawQuery("SELECT * FROM temporarycolorchat where code ='" + code + "'", null);
                    c3.moveToFirst();
                    while (!c3.isAfterLast()) {
                        cv.put(KEY_SHELF,  c3.getInt(2)+shelf);
                        cv.put(KEY_REQUEST,  c3.getInt(3)+request);
                        cv.put(KEY_FREE,  c3.getInt(4)+free);

                        c3.moveToNext();
                    }
                }



                database.update(TABLE_NAME, cv, KEY_CODE + " = ?", new String[]{code});
            }

        }



        return database.insert(TABLE_NAME, null, cv);

    }

    public int checkCount() {
        Cursor c = database.rawQuery("SELECT * FROM temporarycolorchat ", null);
        return c.getCount();

    }
    public ArrayList<String[]> getAllColorChartCodeDetails() {

        ArrayList<String[]> chartDataList = new ArrayList<String[]>();
        Cursor c = database.rawQuery("SELECT * FROM temporarycolorchat ", null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            String[] chartdata = new String[6];
            chartdata[1] = c.getString(1);//code
            chartdata[2] = c.getString(2);//shelf
            chartdata[3] = c.getString(3);//request
            chartdata[4] = c.getString(4);//free
            chartdata[5] = c.getString(5);//acces

            chartDataList.add(chartdata);
            c.moveToNext();
        }
        c.close();
        return chartDataList;
    }
    public int getTotle() {
        Cursor c = database.rawQuery("SELECT request FROM temporarycolorchat ", null);
        int result=0;
        if (c.getCount() == 0) {
            result= 0;
        } else {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result=result+c.getInt(c.getColumnIndex(KEY_REQUEST));
                System.out.println("qqqqqqqq : "+c.getInt(c.getColumnIndex(KEY_REQUEST)));
            }

        }

        return result;
    }

    public int getFreeTotle() {
        Cursor c = database.rawQuery("SELECT free FROM temporarycolorchat ", null);
        int result=0;
        if (c.getCount() == 0) {
            result= 0;
        } else {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result=result+c.getInt(c.getColumnIndex(KEY_FREE));

            }

        }

        return result;
    }

    public int getFreeTotleByCode(String code) {
        Cursor c = database.rawQuery("SELECT free FROM temporarycolorchat where code ='" + code + "'", null);
        int result=0;
        if (c.getCount() == 0) {
            result= 0;
        } else {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result=result+c.getInt(c.getColumnIndex(KEY_FREE));

            }

        }

        return result;
    }
}

