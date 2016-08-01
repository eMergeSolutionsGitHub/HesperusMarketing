package com.HesperusMarketing.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.HesperusMarketing.Entity.Product;
import com.HesperusMarketing.Entity.TempInvoiceStock;
import com.HesperusMarketing.channelbridge.SelectedProduct;
import com.HesperusMarketing.channelbridgeaddapters.ListProduct;
import com.HesperusMarketing.channelbridgeaddapters.ReportList;

import java.util.ArrayList;

/**
 * Created by Amila on 11/15/15.
 */
public class TemporaryInvoice {


    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_PRODUCT_CODE = "product_code";
    private static final String KEY_BATCH_NO = "batch_number";
    private static final String KEY_SHELF_QUANTITY = "shelf_quantity";
    private static final String KEY_REQUEST_QUANTITY = "request_quantity";
    private static final String KEY_FREE_QUANTITY = "free_quantity";
    private static final String KEY_NORMAL_QUANTITY = "normal_quantity";
    private static final String KEY_PRO_DES = "pro_des";
    private static final String KEY_SELLING_PRICE = "selling_price";
    private static final String KEY_DISCOUNT = "discount";
    private static final String KEY_DISCOUNT_RATE = "discount_rate";
    private static final String KEY_STOCK = "stock";
    private static final String KEY_EXPIRY = "expiryDate";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_IS_FREE_ALLOWED = "isFreeAllowed";
    private static final String KEY_IS_DISCOUNT_ALLOWED = "isDiscountAllowed";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_PRINCIPLE = "principle";
    private static final String KEY_IMAGE = "productImage";


    String[] columns = {KEY_ROW_ID, KEY_PRODUCT_ID, KEY_PRODUCT_CODE, KEY_BATCH_NO, KEY_SHELF_QUANTITY, KEY_REQUEST_QUANTITY, KEY_FREE_QUANTITY, KEY_NORMAL_QUANTITY, KEY_PRO_DES, KEY_SELLING_PRICE, KEY_DISCOUNT, KEY_DISCOUNT_RATE, KEY_STOCK, KEY_EXPIRY, KEY_TIMESTAMP, KEY_IS_FREE_ALLOWED, KEY_IS_DISCOUNT_ALLOWED,
            KEY_CATEGORY,KEY_PRINCIPLE,KEY_IMAGE};
    private static final String TABLE_NAME = "invoice_temporary";
    private static final String TEMPORARY_INVOICE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER, "
            + KEY_PRODUCT_ID + " TEXT  ,"
            + KEY_PRODUCT_CODE + " TEXT  ,"
            + KEY_BATCH_NO + " TEXT ,"
            + KEY_SHELF_QUANTITY + " TEXT  ,"
            + KEY_REQUEST_QUANTITY + " TEXT ,"
            + KEY_FREE_QUANTITY + " TEXT ,"  //
            + KEY_NORMAL_QUANTITY + " TEXT ,"   //" TEXT );";
            + KEY_PRO_DES + " TEXT ,"
            + KEY_SELLING_PRICE + " TEXT ,"
            + KEY_DISCOUNT + " TEXT ,"
            + KEY_DISCOUNT_RATE + " TEXT ,"
            + KEY_STOCK + " TEXT ,"
            + KEY_EXPIRY + " TEXT ,"
            + KEY_TIMESTAMP + " TEXT,"
            + KEY_IS_FREE_ALLOWED + " TEXT,"
            + KEY_IS_DISCOUNT_ALLOWED + " TEXT,"
            + KEY_CATEGORY + " TEXT,"
            + KEY_PRINCIPLE + " TEXT,"
            + KEY_IMAGE + " TEXT" + " );";


    public final Context context;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public TemporaryInvoice(Context c) {
        context = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TEMPORARY_INVOICE_CREATE);

        Log.i("iii-->", TEMPORARY_INVOICE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public TemporaryInvoice openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public TemporaryInvoice openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }


    public void insertTempInvoStock(Product pro) {
        openReadableDatabase();
        ContentValues cv = new ContentValues();
        int result = 0;
        try {
            cv.put(KEY_ROW_ID, pro.getRowId());
            cv.put(KEY_PRODUCT_CODE, pro.getCode());
            cv.put(KEY_PRODUCT_ID, pro.getId());
            cv.put(KEY_BATCH_NO, pro.getBatchNumber());
            cv.put(KEY_SHELF_QUANTITY, "");
            cv.put(KEY_REQUEST_QUANTITY, "0");
            cv.put(KEY_FREE_QUANTITY, "0");
            cv.put(KEY_NORMAL_QUANTITY, "0");
            cv.put(KEY_PRO_DES, pro.getProDes());
            cv.put(KEY_STOCK, "" + pro.getQuantity());
            cv.put(KEY_EXPIRY, pro.getExpiryDate());
            cv.put(KEY_TIMESTAMP, pro.getTimeStamp());
            cv.put(KEY_DISCOUNT, "0");
            cv.put(KEY_DISCOUNT_RATE, "0");
            cv.put(KEY_SELLING_PRICE, Double.toString(pro.getSellingPrice()));
            cv.put(KEY_IS_FREE_ALLOWED, Boolean.toString(true));
            cv.put(KEY_IS_DISCOUNT_ALLOWED, Boolean.toString(true));
            cv.put(KEY_CATEGORY, pro.getCategory());
            cv.put(KEY_PRINCIPLE, pro.getPrinciple());
            cv.put(KEY_IMAGE, pro.getImageUrl());

            database.insert(TABLE_NAME, null, cv);
        } catch (SQLException e) {
            Log.e("temp invo error - >", e.toString());
            cv.put(KEY_SELLING_PRICE, pro.getSellingPrice());
        } catch (Exception e) {
            Log.e("temp invo error - >", e.toString());
        }

        closeDatabase();
    }


    public void updateDicount(String productCode, String batchNO, String dicount) {
        //  String strqu = "UPDATE " +TABLE_NAME+" SET "+KEY_CreditAmount+" = "+creditAmount+" WHERE "+KEY_CustomerNo+" = "+customerNo+" AND "+KEY_InvoiceNo+" = "+invoiceNo+";";
        Log.i("update called", "----------------->");
        openWritableDatabase();
        ContentValues cv = null;
        try {

            cv = new ContentValues();
            cv.put(KEY_DISCOUNT, dicount);

            database.update(TABLE_NAME, cv, KEY_PRODUCT_CODE + " = ? AND " + KEY_BATCH_NO + " = ?", new String[]{productCode, batchNO});
            Log.i("updated successfully", productCode + "_" + batchNO);


        } catch (SQLException e) {
            Log.e("Temp invoice ---->", "Error updating temp invoice stock");
        }

        closeDatabase();
    }

    public void deleteAllRecords() {
        openReadableDatabase();
        try {
            database.execSQL("delete from " + TABLE_NAME);
        } catch (SQLException e) {
            Log.e("Temp invoice ---->", "Error deleting temp invoice stock");
        }
        closeDatabase();
    }
    public ArrayList<TempInvoiceStock> getTempDataForTable(String category, String principle, String pCode, int stat) {

        openReadableDatabase();
        ArrayList<TempInvoiceStock> products = new ArrayList<>();
        Cursor cursor;

        if(stat==0){
             cursor = database.rawQuery("select product_code,batch_number,pro_des,stock,shelf_quantity,request_quantity,free_quantity,normal_quantity,discount,selling_price,productImage from invoice_temporary where principle = ?", new String[]{principle});

        }else if(stat==1) {
             cursor = database.rawQuery("select product_code,batch_number,pro_des,stock,shelf_quantity,request_quantity,free_quantity,normal_quantity,discount,selling_price,productImage from invoice_temporary where category = ?  and principle = ?", new String[]{category, principle});

        }else{
            cursor = database.rawQuery("select product_code,batch_number,pro_des,stock,shelf_quantity,request_quantity,free_quantity,normal_quantity,discount,selling_price,productImage from invoice_temporary where category = ?  and principle = ? and product_code = ?", new String[]{category, principle, pCode});
        }



        cursor.moveToFirst();
        TempInvoiceStock temp = null;
        while (!cursor.isAfterLast()) {
            temp = new TempInvoiceStock();


            temp.setProductCode(cursor.getString(0));
            temp.setBatchCode(cursor.getString(1));
            temp.setProductDes(cursor.getString(2));
            temp.setStock(Integer.parseInt(cursor.getString(3)));
            temp.setShelfQuantity(cursor.getString(4));
            temp.setRequestQuantity(cursor.getString(5));
            temp.setFreeQuantity(cursor.getString(6));
            temp.setNormalQuantity(cursor.getString(7));
            temp.setPercentage(Double.parseDouble(cursor.getString(8)));
            temp.setPrice(cursor.getString(9));
            temp.setProImage(cursor.getString(10));



            products.add(temp);
            // principleList.add(principleName);
            cursor.moveToNext();
        }

        cursor.close();
        closeDatabase();
        return products;
    }


    public TempInvoiceStock getTempData(String prodCode, String batchNo) {

        openReadableDatabase();
        Cursor cursor = database.rawQuery("select stock,shelf_quantity,request_quantity,free_quantity,normal_quantity,discount from invoice_temporary where product_code = ?  and batch_number = ?", new String[]{prodCode, batchNo});

        cursor.moveToFirst();
        TempInvoiceStock temp = null;
        while (!cursor.isAfterLast()) {
            temp = new TempInvoiceStock();

            temp.setStock(Integer.parseInt(cursor.getString(0)));
            temp.setShelfQuantity(cursor.getString(1));
            temp.setRequestQuantity(cursor.getString(2));
            temp.setFreeQuantity(cursor.getString(3));
            temp.setNormalQuantity(cursor.getString(4));
            temp.setPercentage(Double.parseDouble(cursor.getString(5)));



            // principleList.add(principleName);
            cursor.moveToNext();
        }

        cursor.close();
        closeDatabase();
        return temp;
    }

    public void updateShelfQuantity(String productCode, String batchNO, String quantity) {
        //  String strqu = "UPDATE " +TABLE_NAME+" SET "+KEY_CreditAmount+" = "+creditAmount+" WHERE "+KEY_CustomerNo+" = "+customerNo+" AND "+KEY_InvoiceNo+" = "+invoiceNo+";";
        Log.i("update called", "----------------->");
        ContentValues cv = null;
        openWritableDatabase();
        try {

            cv = new ContentValues();
            cv.put(KEY_SHELF_QUANTITY, quantity);

            database.update(TABLE_NAME, cv, KEY_PRODUCT_CODE + " = ? AND " + KEY_BATCH_NO + " = ?", new String[]{productCode, batchNO});
            Log.i("updated successfully", productCode + "_" + batchNO);


        } catch (SQLException e) {
            Log.e("Temp invoice ---->", "Error updating temp shelf stock");
        }

        closeDatabase();
    }


    public void updateRequestQuantity(String productCode, String batchNO, String quantity) {
        //  String strqu = "UPDATE " +TABLE_NAME+" SET "+KEY_CreditAmount+" = "+creditAmount+" WHERE "+KEY_CustomerNo+" = "+customerNo+" AND "+KEY_InvoiceNo+" = "+invoiceNo+";";
        openWritableDatabase();
        ContentValues cv = null;
        try {

            cv = new ContentValues();
            cv.put(KEY_REQUEST_QUANTITY, quantity);

            database.update(TABLE_NAME, cv, KEY_PRODUCT_CODE + " = ? AND " + KEY_BATCH_NO + " = ?", new String[]{productCode, batchNO});
            Log.i("updated successfully", productCode + "_" + batchNO);

        } catch (SQLException e) {
            Log.e("Temp invoice ---->", "Error updating temp request stock");
        }
        closeDatabase();

    }


    public void updateFreeQuantity(String productCode, String batchNO, String quantity) {
        //  String strqu = "UPDATE " +TABLE_NAME+" SET "+KEY_CreditAmount+" = "+creditAmount+" WHERE "+KEY_CustomerNo+" = "+customerNo+" AND "+KEY_InvoiceNo+" = "+invoiceNo+";";
        Log.i("update called", "----------------->");
        ContentValues cv = null;
        openWritableDatabase();
        try {

            cv = new ContentValues();
            cv.put(KEY_FREE_QUANTITY, quantity);

            database.update(TABLE_NAME, cv, KEY_PRODUCT_CODE + " = ? AND " + KEY_BATCH_NO + " = ?", new String[]{productCode, batchNO});
            Log.i("updated successfully", productCode + "_" + batchNO);

        } catch (SQLException e) {
            Log.e("Temp invoice ---->", "Error updating temp request stock");
        }
        closeDatabase();

    }


    public void updateNormalQuantity(String productCode, String batchNO, String quantity) {

        ContentValues cv = null;
        openWritableDatabase();
        try {

            cv = new ContentValues();
            cv.put(KEY_NORMAL_QUANTITY, quantity);

            database.update(TABLE_NAME, cv, KEY_PRODUCT_CODE + " = ? AND " + KEY_BATCH_NO + " = ?", new String[]{productCode, batchNO});

        } catch (SQLException e) {

        }
        closeDatabase();

    }

    public void updateFreeAlloed(String productCode, String batchNO, String allowed) {
        //  String strqu = "UPDATE " +TABLE_NAME+" SET "+KEY_CreditAmount+" = "+creditAmount+" WHERE "+KEY_CustomerNo+" = "+customerNo+" AND "+KEY_InvoiceNo+" = "+invoiceNo+";";
        Log.i("update called", "----------------->");
        ContentValues cv = null;
        openWritableDatabase();
        try {

            cv = new ContentValues();
            cv.put(KEY_IS_FREE_ALLOWED, allowed);

            database.update(TABLE_NAME, cv, KEY_PRODUCT_CODE + " = ? AND " + KEY_BATCH_NO + " = ?", new String[]{productCode, batchNO});
            Log.i("updated successfully", productCode + "_" + batchNO);

        } catch (SQLException e) {
            Log.e("Temp invoice ---->", "Error updating temp request stock");
        }
        closeDatabase();

    }

    public void updateDiscountAlloed(String productCode, String batchNO, String allowed) {
        //  String strqu = "UPDATE " +TABLE_NAME+" SET "+KEY_CreditAmount+" = "+creditAmount+" WHERE "+KEY_CustomerNo+" = "+customerNo+" AND "+KEY_InvoiceNo+" = "+invoiceNo+";";
        Log.i("update called", "----------------->");
        ContentValues cv = null;
        openWritableDatabase();
        try {

            cv = new ContentValues();
            cv.put(KEY_IS_DISCOUNT_ALLOWED, allowed);

            database.update(TABLE_NAME, cv, KEY_PRODUCT_CODE + " = ? AND " + KEY_BATCH_NO + " = ?", new String[]{productCode, batchNO});
            Log.i("updated successfully", productCode + "_" + batchNO);

        } catch (SQLException e) {
            Log.e("Temp invoice ---->", "Error updating temp request stock");
        }

        closeDatabase();
    }

    public ArrayList<TempInvoiceStock> getProductTempList() {

        ArrayList<TempInvoiceStock> tempList = new ArrayList<>();
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select * from invoice_temporary where normal_quantity > 0  UNION ALL select * from invoice_temporary where free_quantity > 0 and normal_quantity = 0 ", null);
        TempInvoiceStock temp = null;
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                temp = new TempInvoiceStock();
                temp.setRow_ID(cursor.getString(cursor.getColumnIndex("row_id")));
                temp.setProductId(cursor.getString(cursor.getColumnIndex("product_id")));
                temp.setProductCode(cursor.getString(cursor.getColumnIndex("product_code")));
                temp.setBatchCode(cursor.getString(cursor.getColumnIndex("batch_number")));
                temp.setShelfQuantity(cursor.getString(cursor.getColumnIndex("shelf_quantity")));
                temp.setRequestQuantity(cursor.getString(cursor.getColumnIndex("request_quantity")));
                temp.setFreeQuantity(cursor.getString(cursor.getColumnIndex("free_quantity")));
                temp.setNormalQuantity(cursor.getString(cursor.getColumnIndex("normal_quantity")));
                temp.setProductDes(cursor.getString(cursor.getColumnIndex("pro_des")));
                temp.setPrice(cursor.getString(cursor.getColumnIndex("selling_price")));
                Log.i("sell -s->", "" + temp.getPrice());
                temp.setPercentage(Double.parseDouble(cursor.getString(cursor.getColumnIndex("discount"))));
                temp.setStock(Integer.parseInt(cursor.getString(cursor.getColumnIndex("stock"))));
                temp.setExpiryDate(cursor.getString(cursor.getColumnIndex("expiryDate")));
                temp.setTimestamp(cursor.getString(cursor.getColumnIndex("timestamp")));

                tempList.add(temp);
                cursor.moveToNext();
            }
        } catch (Exception e) {


        }
        cursor.close();
        closeDatabase();
        return tempList;
    }

    public ArrayList<SelectedProduct> getShelfQuantityTempList() {

        ArrayList<SelectedProduct> tempList = new ArrayList<>();
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select * from invoice_temporary where normal_quantity = 0 AND shelf_quantity > 0 ", null);
        // TempInvoiceStock temp = null;
        try {


            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {


                SelectedProduct product = new SelectedProduct();

                product.setRowId(Integer.parseInt(cursor.getString(0)));
                product.setProductId(cursor.getString(1));
                product.setProductCode(cursor.getString(2));
                product.setProductBatch(cursor.getString(3));
                product.setQuantity(Integer.parseInt(cursor.getString(11)));
                product.setExpiryDate(cursor.getString(12));
                product.setTimeStamp(cursor.getString(13));

                product.setRequestedQuantity(Integer.parseInt(cursor.getString(5)));
                product.setFree(Integer.parseInt(cursor.getString(6)));
                product.setNormal(Integer.parseInt(cursor.getString(7)));

                product.setDiscount(Double.parseDouble(cursor.getString(10)));
                product.setShelfQuantity(Integer.parseInt(cursor.getString(4)));
//                    Log.w("next Button 090928340283423098", selectedProductData[13]);
//
                product.setProductDescription(cursor.getString(8));
                product.setPrice(Double.parseDouble(cursor.getString(9)));
                tempList.add(product);
                cursor.moveToNext();
            }
        } catch (Exception e) {

        }
        cursor.close();
        closeDatabase();

        return tempList;
    }


    //himanshu


    public void updateFreeIssueRate(String principel, String cat, String rate) {

        openReadableDatabase();
        Cursor c = null;
        c = database.rawQuery("SELECT it.product_code FROM invoice_temporary it inner join products p on it.product_code = p.code  And p.principle = '" + principel + "' ", null);

        ContentValues cv = null;
        if (c.getCount() == 0) {

        } else {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                cv = new ContentValues();
                cv.put(KEY_DISCOUNT_RATE, rate);

                database.update(TABLE_NAME, cv, KEY_PRODUCT_CODE + " = ?", new String[]{c.getString(c.getColumnIndex(KEY_PRODUCT_CODE))});

            }
        }
        closeDatabase();

    }

    public void setDiscount(String principle, double discount) {

        openWritableDatabase();
        Cursor c = null;
        c = database.rawQuery("SELECT product_code FROM invoice_temporary it inner join products p on it.product_code = p.code  And p.principle = '" + principle + "' AND normal_quantity >0 ", null);

        ContentValues cv = null;
        if (c.getCount() == 0) {

        } else {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                cv = new ContentValues();
                cv.put(KEY_DISCOUNT, discount);

                database.update(TABLE_NAME, cv, KEY_PRODUCT_CODE + " = ?", new String[]{c.getString(c.getColumnIndex(KEY_PRODUCT_CODE))});

            }
        }
        closeDatabase();
    }


    public void clearFreeqty(String principle, String cat) {

        openWritableDatabase();
        Cursor c = null;
        if (cat == null) {
            c = database.rawQuery("SELECT product_code FROM invoice_temporary it inner join products p on it.product_code = p.code  And p.principle = '" + principle + "' AND free_quantity >0 ", null);

        } else {
            c = database.rawQuery("SELECT product_code FROM invoice_temporary it inner join products p on it.product_code = p.code  And p.principle = '" + principle + "' and p.category = '" + cat + "' AND free_quantity >0 ", null);

        }
        ContentValues cv = null;
        if (c.getCount() == 0) {

        } else {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                cv = new ContentValues();
                cv.put(KEY_FREE_QUANTITY, 0);

                database.update(TABLE_NAME, cv, KEY_PRODUCT_CODE + " = ?", new String[]{c.getString(c.getColumnIndex(KEY_PRODUCT_CODE))});

            }
        }

        closeDatabase();
    }


    public int getTotle(String principle, String cat) {
        openReadableDatabase();
        Cursor c = null;
        if (cat == null) {
            c = database.rawQuery("SELECT SUM(CAST(request_quantity as decimal)) FROM invoice_temporary it inner join products p on it.product_code = p.code  And p.principle = '" + principle + "' ", null);

        } else {
            c = database.rawQuery("SELECT SUM(CAST(request_quantity as decimal)) FROM invoice_temporary it inner join products p on it.product_code = p.code  And p.principle = '" + principle + "' and p.category = '" + cat + "' ", null);

        }
        int result = 0;
        if (c.getCount() == 0) {
            result = 0;
        } else {
            c.moveToFirst();
            result = c.getInt(0);

        }
        closeDatabase();
        return result;
    }

    public int getFreeTotle(String principle, String cat) {
        openReadableDatabase();
        Cursor c = null;
        if (cat == null) {
            c = database.rawQuery("SELECT SUM(CAST(free_quantity as decimal)) FROM invoice_temporary it inner join products p on it.product_code = p.code  And p.principle = '" + principle + "' ", null);

        } else {
            c = database.rawQuery("SELECT SUM(CAST(free_quantity as decimal)) FROM invoice_temporary it inner join products p on it.product_code = p.code  And p.principle = '" + principle + "' and p.category = '" + cat + "' ", null);

        }

        int result = 0;
        if (c.getCount() == 0) {
            result = 0;
        } else {
            c.moveToFirst();
            result = c.getInt(0);
        }
        closeDatabase();
        return result;
    }


    public String getFreeIssueRate(String principle, String cat) {

        openWritableDatabase();

        Cursor c = null;
        if (cat == null) {
            c = database.rawQuery("SELECT discount_rate FROM invoice_temporary it inner join products p on it.product_code = p.code  And p.principle = '" + principle + "' LIMIT 1 ", null);

        } else {
            c = database.rawQuery("SELECT discount_rate FROM invoice_temporary it inner join products p on it.product_code = p.code  And p.principle = '" + principle + "' and p.category = '" + cat + "' LIMIT 1 ", null);

        }
        String result = "1";
        if (c.getCount() == 0) {

        } else {
            c.moveToFirst();
            result = c.getString(0);
        }
        closeDatabase();
        return result;
    }

}
