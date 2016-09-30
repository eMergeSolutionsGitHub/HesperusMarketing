package com.HesperusMarketing.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import java.util.ArrayList;

/**
 * Created by Himanshu on 3/24/2016.
 */
public class Product_Image {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_ID = "id";
    private static final String KEY_CODE = "code";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_DISPLAY_CODE = "display_code";
    private static final String KEY_SEQUENCE_NO = "sequence_no";


    String[] columns = new String[]{KEY_ROW_ID, KEY_ID, KEY_CODE, KEY_IMAGE,
            KEY_DISPLAY_CODE, KEY_SEQUENCE_NO};

    private static final String TABLE_NAME = "products_image";

    private static final String PRODUCTS_IMAGE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_ID + " INTEGER NOT NULL ,"
            + KEY_CODE + " TEXT NOT NULL ,"
            + KEY_IMAGE + " BLOB DEFAULT 0,"
            + KEY_DISPLAY_CODE + " TEXT,"
            + KEY_SEQUENCE_NO + " INTEGER" + " );";

    public final Context productContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    public Product_Image(Context productContext) {
        this.productContext = productContext;
    }
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(PRODUCTS_IMAGE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public Product_Image openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(productContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public Product_Image openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(productContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertProduct(String id, String code, String image, String displayCode, String sequence) throws SQLException {

        ContentValues cv = new ContentValues();

        cv.put(KEY_ID, id);
        cv.put(KEY_CODE, code);
        cv.put(KEY_IMAGE, image);
        cv.put(KEY_DISPLAY_CODE, android.util.Base64.decode(displayCode, Base64.DEFAULT));
        cv.put(KEY_SEQUENCE_NO, sequence);
        return database.insert(TABLE_NAME, null, cv);
    }

    public ArrayList<String> getImages(){
        ArrayList<String> imageList =null;
        imageList = new ArrayList<String>();
        Cursor c = database.rawQuery("SELECT image FROM products_image ", null);
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                if(c.getString( c.getColumnIndex("image"))==null){

                }else {
                    imageList.add(c.getString( c.getColumnIndex("image")) );
                }
                c.moveToNext();
            }

        }else {


        }
        return imageList;
    }
}
