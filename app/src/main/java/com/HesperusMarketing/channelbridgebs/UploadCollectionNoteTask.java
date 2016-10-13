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

import com.HesperusMarketing.channelbridgedb.CollectionNoteSendToApprovel;
import com.HesperusMarketing.channelbridgews.WebService;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Puritha Dev on 12/3/2014.
 */
public class UploadCollectionNoteTask extends AsyncTask<String, Integer, Integer> {

    private final Context context;
    ProgressDialog dialog;

    public UploadCollectionNoteTask(Context context) {
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
                CollectionNoteSendToApprovel rtnProdObject = new CollectionNoteSendToApprovel(context);
                rtnProdObject.openReadableDatabase();

                List<String[]> rtnProducts = rtnProdObject.getCollectionNoteByUploadStatus();
                rtnProdObject.closeDatabase();

                ArrayList<String[]> invoicedProductDetailList = new ArrayList<String[]>();

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String deviceId = sharedPreferences.getString("DeviceId", "-1");
                String repId = sharedPreferences.getString("RepId", "-1");

                for (String[] rtnProdData : rtnProducts) {


                    String[] invoiceDetails = new String[20];
                    invoiceDetails[0] = rtnProdData[0];
                    invoiceDetails[1] = rtnProdData[1];
                    invoiceDetails[2] = rtnProdData[2];
                    invoiceDetails[3] = rtnProdData[3];
                    invoiceDetails[4] = rtnProdData[4];
                    invoiceDetails[5] = rtnProdData[5];
                    invoiceDetails[6] = rtnProdData[6];

                    invoiceDetails[7] = rtnProdData[7];
                    invoiceDetails[8] = rtnProdData[8];
                    invoiceDetails[9] = rtnProdData[9];
                    invoiceDetails[10] = rtnProdData[10];
                    invoiceDetails[11] = rtnProdData[11];
                    invoiceDetails[12] = rtnProdData[12];
                    invoiceDetails[13] = rtnProdData[13];


                    invoiceDetails[14] = rtnProdData[14];
                    invoiceDetails[15] = rtnProdData[15];
                    invoiceDetails[16] = rtnProdData[16];




                    publishProgress(2);
                    String responseArr = null;
                    while (responseArr == null) {
                        try {

                            WebService webService = new WebService();
                            try {
                                responseArr = webService.uploadCollectionNoteTask(deviceId, repId, invoiceDetails);
                            } catch (SocketException e) {
                                e.printStackTrace();
                            }

                            Thread.sleep(100);

                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }


                    if (responseArr.contains("No Error")) {
                        Log.w("Log", "Update the Collection Note status");

                        // setCellectionNoteUpdatedStatus


                        CollectionNoteSendToApprovel rtnProdObj = new CollectionNoteSendToApprovel(context);
                        rtnProdObj.openWritableDatabase();
                        rtnProdObj.setCellectionNoteUpdatedStatus(rtnProdData[7], "true");
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
            Toast notificationToast = Toast
                    .makeText(context, "Collection Note  uploaded to the server.",
                            Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 1) {//unable to
            Toast notificationToast = Toast.makeText(context,
                    "Unable to Upload Collection Note to the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 3) {
            Toast notificationToast = Toast.makeText(context,
                    "No new Collection Note data Upload to the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        }
       /* if (dialog.isShowing()) {
            dialog.dismiss();
        }*/

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}
