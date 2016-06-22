package com.HesperusMarketing.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Himanshu on 4/19/2016.
 */
public class DiscountStructures {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_SEVER_ID = "server_id";
    private static final String KEY_BRAND = "Brand";
    private static final String KEY_CUSTOMER_CATEGORY_ID = "CustomerCategoryID";
    private static final String KEY_ORDER_QTY= "OrderQTY";
    private static final String KEY_FREE_QTY = "FreeQTY";
    private static final String KEY_IS_ACTIVE = "is_active";


    String[] columns = new String[]{KEY_ROW_ID,KEY_SEVER_ID, KEY_BRAND, KEY_CUSTOMER_CATEGORY_ID,
            KEY_ORDER_QTY, KEY_FREE_QTY, KEY_IS_ACTIVE};

    private static final String TABLE_NAME = "discountstructures";
    private static final String DISCOUNT_CREATE = "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_SEVER_ID + " TEXT, "
            + KEY_BRAND + " TEXT, "
            + KEY_CUSTOMER_CATEGORY_ID + " TEXT, "
            + KEY_ORDER_QTY + " INTEGER, "
            + KEY_FREE_QTY + " INTEGER, "
            + KEY_IS_ACTIVE + " INTEGER " + ");";


    public final Context DicountContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public DiscountStructures(Context c) {
        DicountContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DISCOUNT_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public DiscountStructures openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(DicountContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public DiscountStructures openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(DicountContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertDiscountStructures(String serverID, String brand, String custmoerCatId, String orederQty, String freeQty) throws SQLException {
        ;

        ContentValues cv = new ContentValues();
        cv.put(KEY_SEVER_ID, serverID);
        cv.put(KEY_BRAND, brand);
        cv.put(KEY_CUSTOMER_CATEGORY_ID, custmoerCatId);
        cv.put(KEY_ORDER_QTY,Integer.parseInt(orederQty));
        cv.put(KEY_FREE_QTY,Integer.parseInt(freeQty));
        cv.put(KEY_IS_ACTIVE,1);

        return database.insert(TABLE_NAME, null, cv);

    }
    public Cursor getFreeIssues(String principle) {
        Cursor cursor=  database.rawQuery("SELECT row_id,OrderQTY,FreeQTY FROM discountstructures  WHERE Brand = '" + principle + "' ORDER BY OrderQTY ASC ",null);
        return cursor;

    }

/*
    public int getFreeIssues(String code, int request) {
        Cursor c = database.rawQuery("SELECT nqty,fqty FROM discountstructures where  item_code = '" + code + "'  ORDER BY nqty DESC", null);
        int[] values = new int[c.getCount()];
        int[] qty = new int[c.getCount()];
        int i = 0;
        int free=0;
        if (c != null) {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                values[i] = Integer.parseInt(c.getString(c.getColumnIndex(KEY_NQTY)));
                qty[i] = Integer.parseInt(c.getString(c.getColumnIndex(KEY_FQTY)));
                int remaning = request / values[i];
                if (remaning==0) {

                }else {
                    free= remaning*qty[i];
                    break;
                }

                i++;
            }
        } else {

        }
        return free;
    }
*/

}
