package com.HesperusMarketing.channelbridgebs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.HesperusMarketing.channelbridgedb.CollectionNoteCheques;
import com.HesperusMarketing.channelbridgedb.CollectionNoteInvoice;
import com.HesperusMarketing.channelbridgews.WebService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Puritha Dev on 12/3/2014.
 */
public class UploadCollectionInvoicesTask extends AsyncTask<String, Integer, Integer> {

    private final Context context;
    ProgressDialog dialog;

    public UploadCollectionInvoicesTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {


    }

    @Override
    protected Integer doInBackground(String... params) {
        int returnValue = 1;
        if (isNetworkAvailable() == true) {
            try {
                CollectionNoteInvoice collectionNote = new CollectionNoteInvoice(context);
                collectionNote.openReadableDatabase();

                List<String[]> rtnProducts = collectionNote.getCollectionNoteInvoiceByUploadStatus();
                collectionNote.closeDatabase();

                Log.w("Log", "rtnProducts size :  " + rtnProducts.size());

                ArrayList<String[]> invoicedProductDetailList = new ArrayList<String[]>();

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String deviceId = sharedPreferences.getString("DeviceId", "-1");
                String repId = sharedPreferences.getString("RepId", "-1");

                for (String[] rtnProdData : rtnProducts) {

                    Log.w("Log", "rtnProducts id :  " + rtnProdData[0]);
                    // Log.w("Log", "rtnProducts date :  " + rtnProdData[10]);

                    String[] invoiceDetails = new String[7];
                    invoiceDetails[0] = rtnProdData[0];
                    invoiceDetails[1] = rtnProdData[1];
                    invoiceDetails[2] = rtnProdData[2];
                    invoiceDetails[3] = rtnProdData[3];
                    invoiceDetails[4] = rtnProdData[4];
                    invoiceDetails[5] = rtnProdData[5];



                    publishProgress(2);
                    String responseArr = null;
                    while (responseArr == null) {
                        try {

                            WebService webService = new WebService();
                            responseArr = webService.uploadCollectionInvoiceTask(repId, invoiceDetails);

                            Thread.sleep(100);

                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }


                    if (responseArr.contains("No Error")) {
                        Log.w("Log", "Update the Collection Note status");

                        // setCellectionNoteUpdatedStatus


                        CollectionNoteInvoice rtnProdObj = new CollectionNoteInvoice(context);
                        rtnProdObj.openWritableDatabase();
                        rtnProdObj.setCellectionNoteInvoiceUpdatedStatus(rtnProdData[6], "1");
                        rtnProdObj.closeDatabase();
                        returnValue = 2;

                    } else {
                        returnValue = 1;

                    }

                    Log.w("Log", "loadProductRepStoreData result : "
                            + responseArr);

                }

                Log.w("Log", "invoicedProductDetailList size :  "
                        + invoicedProductDetailList.size());

                if (rtnProducts.size() < 1) {

                    returnValue = 3;
                }


            } catch (Exception e) {
                Log.w("Log", "Upload Collection Note  error: "
                        + e.toString());
            }
        }
        return returnValue;
    }


    protected void onPostExecute(Integer returnCode) {

        super.onPostExecute(returnCode);

        if (returnCode == 2) {
            Toast notificationToast = Toast.makeText(context, "Collection Note Cheques uploaded to the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 1) {//unable to
            Toast notificationToast = Toast.makeText(context, "Unable to Upload Collection Note Cheques to the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 3) {
            Toast notificationToast = Toast.makeText(context, "No new Collection Note Cheques data Upload to the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        }


    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}
