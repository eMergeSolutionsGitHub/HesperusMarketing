package com.HesperusMarketing.channelbridgedb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.HesperusMarketing.Entity.TempInvoiceStock;
import com.HesperusMarketing.channelbridgeaddapters.ReportList;

public class Invoice {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_ITINERARY_ID = "itinerary_id";
    private static final String KEY_PAYMENT_TYPE = "payment_type";
    private static final String KEY_TOTAL_AMOUNT = "total_amount";
    private static final String KEY_PAID_AMOUNT = "paid_amount";
    private static final String KEY_CREDIT_AMOUNT = "credit_amount";
    private static final String KEY_CHEQUE_AMOUNT = "cheque_amount";
    private static final String KEY_MARKET_RETURN = "market_return";
    private static final String KEY_DISCOUNT = "discount";
    private static final String KEY_DISCOUNT_VALUE = "discount_value";
    private static final String KEY_UPLOADED_STATUS = "uploaded_status";
    private static final String KEY_TIME_STAMP = "time_stamp";
    private static final String KEY_IS_RETURNED = "is_returned";
    private static final String KEY_CREDIT_DURATION = "credit_duration";
    private static final String KEY_OUTSTANDING_UPLOADED_STATUS = "outstanding_uploaded_status";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_TOTAL_QUANTITY = "quantity";
    private static final String KEY_CUSTOMER_ID = "customer_id";
    String[] columns = {KEY_ROW_ID, KEY_ITINERARY_ID, KEY_PAYMENT_TYPE, KEY_TOTAL_AMOUNT, KEY_PAID_AMOUNT, KEY_CREDIT_AMOUNT, KEY_CHEQUE_AMOUNT, KEY_MARKET_RETURN,
            KEY_DISCOUNT,KEY_DISCOUNT_VALUE, KEY_UPLOADED_STATUS, KEY_TIME_STAMP, KEY_IS_RETURNED, KEY_CREDIT_DURATION, KEY_OUTSTANDING_UPLOADED_STATUS, KEY_LATITUDE, KEY_LONGITUDE,KEY_START_TIME,KEY_TOTAL_QUANTITY,KEY_CUSTOMER_ID};

    private static final String TABLE_NAME = "invoice";
    private static final String INVOICE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_ITINERARY_ID + " TEXT NOT NULL,"
            + KEY_PAYMENT_TYPE + " TEXT ,"
            + KEY_TOTAL_AMOUNT + " TEXT ,"
            + KEY_PAID_AMOUNT + " TEXT ,"
            + KEY_CREDIT_AMOUNT + " TEXT ,"
            + KEY_CHEQUE_AMOUNT + " TEXT ,"
            + KEY_MARKET_RETURN + " TEXT ,"
            + KEY_DISCOUNT + " TEXT , "
            + KEY_UPLOADED_STATUS + " TEXT ,"
            + KEY_TIME_STAMP + " TEXT ,"
            + KEY_IS_RETURNED + " TEXT ,"
            + KEY_CREDIT_DURATION + " TEXT ,"
            + KEY_OUTSTANDING_UPLOADED_STATUS + " TEXT ,"
            + KEY_LATITUDE + " TEXT ,"
            + KEY_LONGITUDE + " TEXT , "
            +  KEY_START_TIME + " TEXT ,"
            + KEY_DISCOUNT_VALUE + " TEXT ,"
            + KEY_TOTAL_QUANTITY + " TEXT, "
            + KEY_CUSTOMER_ID + " TEXT "
            + ");";
    public final Context invoiceContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public Invoice(Context c) {
        invoiceContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(INVOICE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public Invoice openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(invoiceContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public Invoice openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(invoiceContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertInvoice(String itineraryId, String paymentType, String totalAmount, String paidAmount, String creditAmount, String cheque, String markekReturn, String discount
            ,String uploadedStatus, String timeStamp, String isReturned, String creditDuration, String lat, String lon,String startTime,String dicountValue,String quantity,String pharmaID) throws SQLException {

        ContentValues cv = new ContentValues();

        cv.put(KEY_ITINERARY_ID, itineraryId);
        cv.put(KEY_PAYMENT_TYPE, paymentType);
        cv.put(KEY_TOTAL_AMOUNT, totalAmount);
        cv.put(KEY_PAID_AMOUNT, paidAmount);
        cv.put(KEY_CREDIT_AMOUNT, creditAmount);
        cv.put(KEY_CHEQUE_AMOUNT, cheque);
        cv.put(KEY_MARKET_RETURN, markekReturn);
        cv.put(KEY_DISCOUNT, discount);
        cv.put(KEY_UPLOADED_STATUS, uploadedStatus);
        cv.put(KEY_TIME_STAMP, timeStamp);
        cv.put(KEY_IS_RETURNED, isReturned);
        cv.put(KEY_CREDIT_DURATION, creditDuration);
        cv.put(KEY_OUTSTANDING_UPLOADED_STATUS, "false");
        cv.put(KEY_LATITUDE, lat);
        cv.put(KEY_LONGITUDE, lon);
        cv.put(KEY_START_TIME,startTime);
        cv.put(KEY_DISCOUNT_VALUE,dicountValue);
        cv.put(KEY_TOTAL_QUANTITY,quantity);
        cv.put(KEY_CUSTOMER_ID,pharmaID);
        Log.w("insertInvoice ", "insertInvoice : " + itineraryId);

        return database.insert(TABLE_NAME, null, cv);

    }

    public List<String[]> getAllInvoice() {
        List<String[]> invoice = new ArrayList<String[]>();

        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null,
                null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoiceData = new String[13];
            invoiceData[0] = cursor.getString(0);
            invoiceData[1] = cursor.getString(1);
            invoiceData[2] = cursor.getString(2);
            invoiceData[3] = cursor.getString(3);
            invoiceData[4] = cursor.getString(4);
            invoiceData[5] = cursor.getString(5);
            invoiceData[6] = cursor.getString(6);
            invoiceData[7] = cursor.getString(7);
            invoiceData[8] = cursor.getString(8);
            invoiceData[9] = cursor.getString(9);
            invoiceData[10] = cursor.getString(10);
            invoiceData[11] = cursor.getString(11);
            invoiceData[12] = cursor.getString(12);


            Log.w("Invoice", "INVOICE[0]: " + cursor.getString(0));
            Log.w("Invoice", "INVOICE[1]: " + cursor.getString(1));
            Log.w("Invoice", "INVOICE[2]: " + cursor.getString(2));
            Log.w("Invoice", "INVOICE[3]: " + cursor.getString(3));
            Log.w("Invoice", "INVOICE[4]: " + cursor.getString(4));
            Log.w("Invoice", "INVOICE[5]: " + cursor.getString(5));
            Log.w("Invoice", "INVOICE[6]: " + cursor.getString(6));
            Log.w("Invoice", "INVOICE[7]: " + cursor.getString(7));
            Log.w("Invoice", "INVOICE[8]: " + cursor.getString(8));
            Log.w("Invoice", "INVOICE[9]: " + cursor.getString(9));
            Log.w("Invoice", "INVOICE[10]: " + cursor.getString(10));

            invoice.add(invoiceData);
            cursor.moveToNext();
        }

        cursor.close();

        return invoice;
    }

    public List<String[]> getInvoicesByStatus(String status) {
        List<String[]> invoice = new ArrayList<String[]>();


        Log.w("invoice size", "status : " + status);

//		Cursor cursor = database.query(TABLE_NAME,
//				columns, KEY_UPLOADED_STATUS+" = ?", new String[]{status}, null, null, null);

        Cursor cursor = database.query(TABLE_NAME,
                columns, KEY_UPLOADED_STATUS + " = '" + status + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoiceData = new String[20];
            invoiceData[0] = cursor.getString(0);
            invoiceData[1] = cursor.getString(1);
            invoiceData[2] = cursor.getString(2);
            invoiceData[3] = cursor.getString(3);
            invoiceData[4] = cursor.getString(4);
            invoiceData[5] = cursor.getString(5);
            invoiceData[6] = cursor.getString(6);
            invoiceData[7] = cursor.getString(7);
            invoiceData[8] = cursor.getString(8);
            invoiceData[9] = cursor.getString(9);
            invoiceData[10] = cursor.getString(10);
            invoiceData[11] = cursor.getString(11);
            invoiceData[12] = cursor.getString(12);
            invoiceData[13] = cursor.getString(13);
            invoiceData[14] = cursor.getString(14);
            invoiceData[15] = cursor.getString(15);
            invoiceData[16] = cursor.getString(16);
            invoiceData[17] = cursor.getString(17);
            invoiceData[18] = cursor.getString(18);
            invoiceData[19] = cursor.getString(19);
            invoice.add(invoiceData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoice size", "inside : " + invoice.size());

        return invoice;
    }

    public void setInvoiceUpdatedStatus(String invoiceId, String status) {

        String updateQuery = "UPDATE " + TABLE_NAME
                + " SET "
                + KEY_UPLOADED_STATUS
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

    public ArrayList<String> getInvoiceIdByItineraryId(String itineraryId) {

        ArrayList<String> invoiceId = new ArrayList<String>();

        Cursor cursor = database.query(TABLE_NAME, new String[]{KEY_ROW_ID}, KEY_ITINERARY_ID + "=?", new String[]{itineraryId}, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            invoiceId.add(cursor.getString(0));
            cursor.moveToNext();
        }

        return invoiceId;

    }

    public ArrayList<String> getInvoiceDetailsByInvoiceId(String invoiceId) {
        ArrayList<String> invoiceDetails = new ArrayList<String>();
        // final String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ROW_ID + "='" + invoiceId + "' GROUP BY " + KEY_ROW_ID + " DESC" ;

        //  Cursor cursor = database.rawQuery(query, null);
        Cursor cursor = database.query(TABLE_NAME, columns, KEY_ROW_ID + "=?", new String[]{invoiceId}, null, null, null);
        cursor.moveToFirst();
        invoiceDetails.add(cursor.getString(0));
        invoiceDetails.add(cursor.getString(1));
        invoiceDetails.add(cursor.getString(2));
        invoiceDetails.add(cursor.getString(3));
        invoiceDetails.add(cursor.getString(4));
        invoiceDetails.add(cursor.getString(5));
        invoiceDetails.add(cursor.getString(6));
        invoiceDetails.add(cursor.getString(7));
        invoiceDetails.add(cursor.getString(8));
        invoiceDetails.add(cursor.getString(9));
        invoiceDetails.add(cursor.getString(10));
        invoiceDetails.add(cursor.getString(11));
        invoiceDetails.add(cursor.getString(12));
        return invoiceDetails;
    }

    public String getlastInvoiceForCustomerItinerary(String itineraryId) {

        String query = "SELECT " + KEY_ROW_ID
                + " FROM " + TABLE_NAME
                + " WHERE " + KEY_ITINERARY_ID + "='" + itineraryId + "'"
                + " ORDER BY " + KEY_TIME_STAMP + " DESC";


        Log.w("getInvoiceDetailsByInvoiceId QUERY", query);
        Cursor cursor = database.rawQuery(query, null);

        cursor.moveToFirst();
        String lastInvoiceNumber = "not_invoiced";
        if (cursor.getCount() == 0) {
            lastInvoiceNumber = "not_invoiced";
        } else {
            lastInvoiceNumber = cursor.getString(0);
        }

        return lastInvoiceNumber;
    }

    public long setIsReturnedStatus(boolean isReturned, String invoiceNo) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_IS_RETURNED, String.valueOf(isReturned));
        return database.update(TABLE_NAME, cv, KEY_ROW_ID + "=?", new String[]{invoiceNo});
    }

    public boolean getInvoiceReturnStatus(String invoiceNo) {
        Cursor cursor = database.query(TABLE_NAME, new String[]{KEY_IS_RETURNED}, KEY_ROW_ID + "=?", new String[]{invoiceNo}, null, null, null);

        boolean flag = false;
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            String status = cursor.getString(0);
            if (status.contentEquals("true")) {
                flag = true;
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }
        return flag;
    }

    public List<String[]> getInvoicesByOutstandingUploadStatus(String status) {
        List<String[]> invoice = new ArrayList<String[]>();

        Cursor cursor = database.query(TABLE_NAME,
                columns, KEY_OUTSTANDING_UPLOADED_STATUS + " = '" + status + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoiceData = new String[14];
            invoiceData[0] = cursor.getString(0);
            invoiceData[1] = cursor.getString(1);
            invoiceData[2] = cursor.getString(2);
            invoiceData[3] = cursor.getString(3);
            invoiceData[4] = cursor.getString(4);
            invoiceData[5] = cursor.getString(5);
            invoiceData[6] = cursor.getString(6);
            invoiceData[7] = cursor.getString(7);
            invoiceData[8] = cursor.getString(8);
            invoiceData[9] = cursor.getString(9);
            invoiceData[10] = cursor.getString(10);
            invoiceData[11] = cursor.getString(11);
            invoiceData[12] = cursor.getString(12);
            invoiceData[13] = cursor.getString(13);

            invoice.add(invoiceData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoice size", "inside : " + invoice.size());

        return invoice;
    }

    public void setInvoiceOutstandingUpdatedStatus(String invoiceId, String status) {

        String updateQuery = "UPDATE " + TABLE_NAME
                + " SET "
                + KEY_OUTSTANDING_UPLOADED_STATUS
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


    public String getInvoiceSumforGivenDate(String date) {
        date = date + "%";
        String sum = "";
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select sum(total_amount) from invoice where time_stamp like ?", new String[]{date});

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            sum = cursor.getString(0);
            cursor.moveToNext();
        }
        closeDatabase();
        if(sum == null){
            sum = "0.00";
        }
        return sum;
    }

    public String getInvoiceSumforGivenDateAndCustomer(String custNo,String date) {
        date = date + "%";
        String sum = "0";
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select sum(total_amount) from invoice where customer_id = ? and time_stamp like ? ", new String[]{custNo,date});

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            sum = cursor.getString(0);
            cursor.moveToNext();
        }
        closeDatabase();
        if(sum == null){
            sum = "0.00";
        }
        return sum;
    }


    public String getLastInvoiceForFivenDate(String custNo,String date) {
        date = date + "%";
        String no = "";
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select row_id from invoice where customer_id = ? and time_stamp like ? ORDER BY row_id DESC LIMIT 1 ", new String[]{custNo,date});
        String returnVal;
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            no = cursor.getString(0);
            cursor.moveToNext();
        }
        closeDatabase();
        if(no == null){
            no = "Invoice No.";
        }
        return no;
    }

    public ArrayList<ReportList> getDataForDealerSales(String customer, String town,String fromDate,String toDate,int searchStatus) {

        Cursor cursorMain = null;
        Cursor cursorReturn = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        String status = null,returnVal;
        int dcount=0,colcount=0,recount=0,settcount=0;

        ArrayList<ReportList> sales = new ArrayList<>();
        try {
            if (searchStatus==0) {
                cursorMain= database.rawQuery("select inv.time_stamp, inv.row_id, inv.total_amount, inv.credit_duration from invoice inv INNER JOIN customers  cus on cus.pharmacy_id = inv.customer_id where cus.customer_name = '" + customer + "' ORDER BY inv.row_id", null);

            }else if(searchStatus==1){
                cursorMain= database.rawQuery("select inv.time_stamp, inv.row_id, inv.total_amount, inv.credit_duration from invoice inv INNER JOIN customers  cus on cus.pharmacy_id = inv.customer_id where cus.customer_name = '" + customer + "' AND inv.time_stamp BETWEEN '" + fromDate + "' AND '" + toDate + "'  ORDER BY inv.row_id", null);

            }else if (searchStatus==3){
                cursorMain= database.rawQuery("select inv.time_stamp, inv.row_id, inv.total_amount, inv.credit_duration,cus.customer_name from invoice inv INNER JOIN customers  cus on cus.pharmacy_id = inv.customer_id where cus.town = '" + town + "' ORDER BY inv.row_id",null);
            }else if (searchStatus==4){
                cursorMain= database.rawQuery("select inv.time_stamp, inv.row_id, inv.total_amount, inv.credit_duration,cus.customer_name from invoice inv INNER JOIN customers  cus on cus.pharmacy_id = inv.customer_id where cus.town = '" + town + "'  AND inv.time_stamp BETWEEN '" + fromDate + "' AND '" + toDate + "' ORDER BY inv.row_id",null);
            }

            cursorMain.moveToFirst();

            while (!cursorMain.isAfterLast()) {
                ReportList reportList = new ReportList();
                String invoiceNo = cursorMain.getString(1);


                try {
                    c.setTime(sdf.parse(cursorMain.getString(0)));
                    c.add(Calendar.DATE, Integer.parseInt(cursorMain.getString(3)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    cursorReturn = database.rawQuery("select totalAmount from return_header where invoiceNumber = ? GROUP BY invoiceNumber", new String[]{invoiceNo});
                    cursorReturn.moveToFirst();
                    returnVal = cursorReturn.getString(0);
                }catch (Exception ex){
                    returnVal ="No Value";
                }
                //Set status
                Cursor cursor1 = database.rawQuery("select  delout.InvoiceNo " + "from Invoice inv INNER JOIN DEL_Outstanding delout " + "ON inv.row_id = delout.InvoiceNo where delout.InvoiceNo = ?", new String[]{invoiceNo});

                Cursor cursor2 = database.rawQuery("select col.invoice_no " +
                        "from Invoice inv INNER JOIN CollectionNote_Invoice col " +
                        "ON inv.row_id = col.invoice_no where col.invoice_no = ?", new String[]{invoiceNo});

                Cursor cursor3 = database.rawQuery("select rhd.invoiceNumber " +
                        "from Invoice inv INNER JOIN return_header rhd " +
                        "ON inv.row_id = rhd.invoiceNumber where rhd.invoiceNumber = ?", new String[]{invoiceNo});

                Cursor cursor4 = database.rawQuery("select rhd.invoiceNumber " +
                        "from Invoice inv INNER JOIN return_header rhd " +
                        "ON inv.row_id = rhd.invoiceNumber where rhd.invoiceNumber = ?", new String[]{invoiceNo});

                if (cursor1.getCount() > 0) {
                    status = "D";
                    dcount++;
                }
                if (cursor2.getCount() > 0) {
                    status = status.concat(" ,C");
                    colcount++;

                }
                if (cursor3.getCount() > 0) {
                    status = status.concat(" ,R");
                    recount++;
                }
                if (cursor4.getCount() > 0) {
                    status = status.concat(" ,S");
                    settcount++;
                }



                reportList.setInvoiceDate(cursorMain.getString(0));
                reportList.setInvoiceNo(cursorMain.getString(1));
                reportList.setReturnValue(returnVal);
                reportList.setValue(cursorMain.getString(2));
                reportList.setCreditDuration(cursorMain.getString(3));
                reportList.setDueDate(sdf.format(c.getTime()));

                if(searchStatus ==3||searchStatus ==4){
                    reportList.setDelName(cursorMain.getString(4));
                }else {

                }

                reportList.setStatus(status);
                reportList.setDueCount(dcount);
                reportList.setCollectionCount(colcount);
                reportList.setReturnCount(recount);
                reportList.setSettelCount(settcount);

                sales.add(reportList);
                cursorMain.moveToNext();
            }



        }catch (Exception ex){

        }

        return sales;
    }


    }
