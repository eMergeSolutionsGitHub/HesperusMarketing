package com.HesperusMarketing.channelbridge;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.HesperusMarketing.Entity.Product;
import com.HesperusMarketing.channelbridgeaddapters.OutsandingAdapter;
import com.HesperusMarketing.channelbridgeaddapters.OutsandingList;
import com.HesperusMarketing.channelbridgebs.DownloadImage;
import com.HesperusMarketing.channelbridgebs.InvocieTemporyLoadDataTask;
import com.HesperusMarketing.channelbridgebs.UploadRemarksTask;
import com.HesperusMarketing.channelbridgedb.Approval_Details;
import com.HesperusMarketing.channelbridgedb.Approval_Persons;
import com.HesperusMarketing.channelbridgedb.Customers;
import com.HesperusMarketing.channelbridgedb.CustomersPendingApproval;
import com.HesperusMarketing.channelbridgedb.DEL_Outstandiing;
import com.HesperusMarketing.channelbridgedb.DealerSales;
import com.HesperusMarketing.channelbridgedb.ImageGallery;
import com.HesperusMarketing.channelbridgedb.Invoice;
import com.HesperusMarketing.channelbridgedb.InvoicedProducts;
import com.HesperusMarketing.channelbridgedb.Itinerary;
import com.HesperusMarketing.channelbridgedb.ProductRepStore;
import com.HesperusMarketing.channelbridgedb.Products;
import com.HesperusMarketing.channelbridgedb.Remarks;
import com.HesperusMarketing.channelbridgedb.Reps;
import com.HesperusMarketing.channelbridgedb.TemporaryColorChart;
import com.HesperusMarketing.channelbridgedb.TemporaryInvoice;
import com.HesperusMarketing.channelbridgehelp.RemarksType;
import com.HesperusMarketing.channelbridgews.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.SocketException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItineraryDetailsFragment extends Fragment implements LocationListener {

    public static String cusName = "", contactNumber = "", pharmacyId1 = "";
    Cursor cursor;
    String error = "";
    String rowID = "";
    String repId = "";
    String pharmacyId = "";
    String itenararyDate;
    TextView tViewName, tViewPOfficer, tViewTelephone, tViewAddress, tViewArea,
            tViewTown, tViewInvoiceNumber, tViewInvoiceVal, tViewVariance, tvCreditLimit,
            tViewTarget, tvVariancefr, tvInvoiceNumber, tvCurrntCredit;
    EditText txtRemarks;
    Button btnViewCustomerDetails, btnGenerateInvoice, btnLastInvoice;
    ImageView iViewCustomerPic;
    ImageButton iBtnSaveRemark, iBtnEditInvoice;
    private String globalPharmaId = "";
    private Reps repconnector;
    private Boolean isInvoiceOption2;
    private Boolean isBlockerActivated;
    private Intent startInvoiceGen1;
    InvocieTemporyLoadDataTask1 temporyLoadDataTask1;
    private Invoice invoHandler;
    private Boolean iswebApprovalActive = false;
    private Customers customerHandler;
    private DealerSales salesHandler;
    private LocationManager locationManager;
    Location location;
    double lat, lng;


    ArrayAdapter<String> pesronNameAdapter;

    public static ItineraryDetailsFragment newInstance(int index, String rowid) {
        ItineraryDetailsFragment itineraryDetailsFragmentObject = new ItineraryDetailsFragment();

        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putString("rowIdString", rowid);
        itineraryDetailsFragmentObject.setArguments(args);

        return itineraryDetailsFragmentObject;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 0;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    @SuppressWarnings("unused")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;

        }

     //   try {

            View v = getActivity().findViewById(R.id.details);
            SharedPreferences shared = getActivity().getApplicationContext().getSharedPreferences("CBHesPreferences", Context.MODE_PRIVATE);
            isInvoiceOption2 = (shared.getBoolean("InvoiceOption", true));
            isBlockerActivated = (shared.getBoolean("ISOutStandingBlock", true));
            Log.i("isInvoiceOption2 -> ", isInvoiceOption2.toString());

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

            String deviceId = sharedPreferences.getString("DeviceId", "-1");
            String repId = sharedPreferences.getString("RepId", "-1");

            System.out.println("hima DeviceId : " + deviceId);
            System.out.println("hima RepId : " + repId);

            iswebApprovalActive = (shared.getBoolean("WebApproval", true));
            invoHandler = new Invoice(getActivity().getApplicationContext());
            salesHandler = new DealerSales(getActivity().getApplicationContext());

            iViewCustomerPic = (ImageView) v.findViewById(R.id.ivCustomerImage);
            tViewName = (TextView) v.findViewById(R.id.tvName);
            tViewTelephone = (TextView) v.findViewById(R.id.tvTelephone);
            tViewAddress = (TextView) v.findViewById(R.id.tvAddress);
            tViewInvoiceNumber = (TextView) v
                    .findViewById(R.id.tvInvoiceNumber);
            tViewInvoiceVal = (TextView) v.findViewById(R.id.tvInvoiceVal);
            tvVariancefr = (TextView) v.findViewById(R.id.tvVariancefr);
            tViewTarget = (TextView) v.findViewById(R.id.tvTarget);
            tViewVariance = (TextView) v.findViewById(R.id.tvVariance);
            tvInvoiceNumber = (TextView) v.findViewById(R.id.tvInvoiceNumber);
            txtRemarks = (EditText) v.findViewById(R.id.etRemarks);
            tvCreditLimit = (TextView) v.findViewById(R.id.tvCreditLimit);
            tvCurrntCredit = (TextView) v.findViewById(R.id.tvCurrntCredit);
            btnViewCustomerDetails = (Button) v
                    .findViewById(R.id.bViewCustomerDetails);

            btnGenerateInvoice = (Button) v.findViewById(R.id.bGenerateInvoice);
            btnLastInvoice = (Button) v.findViewById(R.id.bLastInvoice);
            iBtnSaveRemark = (ImageButton) v.findViewById(R.id.ibSaveRemarks);
            iBtnEditInvoice = (ImageButton) v.findViewById(R.id.ibEditInvoice);
            Button btnInvoiceHistory = (Button) v.findViewById(R.id.bInvoiceHistory);
            rowID = getArguments().getString("rowIdString");
            customerHandler = new Customers(getActivity().getApplicationContext());
            Log.w("ROW ID SENT FROM ITINERARY LIST", rowID + "");

            final Dialog invoiceEdit = new Dialog(getActivity());
            invoiceEdit.setContentView(R.layout.invoice_edit_popup);
            invoiceEdit.setTitle("Edit Invoice");
            invoiceEdit.setCanceledOnTouchOutside(false);
            final RadioGroup rGroupEditType = (RadioGroup) invoiceEdit
                    .findViewById(R.id.rgEditType);
            RadioButton rBtnReturnProduct = (RadioButton) invoiceEdit
                    .findViewById(R.id.rbReturnProduct);
            RadioButton rBtnReturnInvoice = (RadioButton) invoiceEdit
                    .findViewById(R.id.rbReturnInvoice);
            final TextView tViewInvoiceNumber = (TextView) invoiceEdit
                    .findViewById(R.id.tvInvoiceNo);
            final EditText txtEditInvoiceRemark = (EditText) invoiceEdit
                    .findViewById(R.id.etRemarks);
            final Button btnSave = (Button) invoiceEdit.findViewById(R.id.bSave);
            Button btnCancel = (Button) invoiceEdit.findViewById(R.id.bCancel);


//			Itinerary itinerary = new Itinerary(getActivity());
//			itinerary.openReadableDatabase();
//			String[] itineraryDetails = itinerary.getItineraryDetailsById(rowID);
//			itinerary.closeDatabase();


            btnGenerateInvoice.setEnabled(true);//false
            itineraryStatus();
            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(getActivity().getBaseContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("AutoSyncRun", true);
            editor.commit();

            SharedPreferences btnPpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
            boolean invoiceQtySuggestion = btnPpreferences.getBoolean("cbPrefProductAvg", true);


            if (!invoiceQtySuggestion) {
                btnInvoiceHistory.setEnabled(false);
            }


            btnInvoiceHistory.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("PharmacyId", pharmacyId);
                    Intent intent = new Intent("com.HesperusMarketing.channelbridge.INVOICEHISTORYACTIVITY");
                    intent.putExtras(bundle);
                    startActivity(intent);


                }
            });

            btnLastInvoice.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent startLastInvoice = new Intent(
                            "com.HesperusMarketing.channelbridge.LASTINVOICEACTIVITY");
                    Bundle bundleToView = new Bundle();
                    bundleToView.putString("Id", rowID);
                    bundleToView.putString("PharmacyId", pharmacyId);

                    startLastInvoice.putExtras(bundleToView);
                    startActivity(startLastInvoice);
                    getActivity().finish();

                }
            });

            txtEditInvoiceRemark.setOnKeyListener(new View.OnKeyListener() {

                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_TAB) {
                        txtEditInvoiceRemark.setInputType(InputType.TYPE_NULL);
                        btnSave.setFocusable(true);
                        btnSave.requestFocus();
                    }
                    return false;
                }

            });

            txtEditInvoiceRemark.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    txtEditInvoiceRemark.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);

                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    txtEditInvoiceRemark.clearFocus();
                    txtEditInvoiceRemark.setText(null);
                    invoiceEdit.dismiss();
                }
            });
            final Remarks remarksObject = new Remarks(getActivity());
            btnSave.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    String remarks = txtEditInvoiceRemark.getText().toString();
                    String timeStamp = java.text.DateFormat
                            .getDateTimeInstance().format(
                                    Calendar.getInstance().getTime());
                    int selectedEditType = rGroupEditType.getCheckedRadioButtonId();

                    switch (selectedEditType) {
                        case R.id.rbReturnProduct:

                            try {
                                SharedPreferences preferences = PreferenceManager
                                        .getDefaultSharedPreferences(getActivity().getBaseContext());
                                boolean history = preferences.getBoolean(
                                        "cbPrefEnableNoHistoryReturns", true);
                                if (history) {
                                    boolean statusAddNewInvoice = saveInvoice(remarks, timeStamp, RemarksType.RETURN_PRODUCT_WITHOUT_HISTORY.toString());

                                    if (statusAddNewInvoice) {
                                        //   new UploadRemarksTask(getActivity()).execute();
                                        new UploadRemarksTask(getActivity()).execute();
                                        txtEditInvoiceRemark.clearFocus();
                                        txtEditInvoiceRemark.setText(null);
                                        Intent startProductReturn = new Intent(
                                                getActivity(),
                                                ReturnProductNoHistoryActivity.class);

                                        Bundle bundleToView = new Bundle();
                                        bundleToView.putString("Id", rowID);
                                        bundleToView.putString("PharmacyId",
                                                pharmacyId);
                                        bundleToView.putString("onTimeOrNot", "1");
                                        startProductReturn.putExtras(bundleToView);
                                        getActivity().finish();
                                        invoiceEdit.dismiss();
                                        startActivity(startProductReturn);
                                    }
                                } else {
                                    Toast featureNotEnabled = Toast
                                            .makeText(
                                                    getActivity(),
                                                    "Sorry, this feature has not been enabled",
                                                    Toast.LENGTH_SHORT);
                                    featureNotEnabled.setGravity(Gravity.TOP, 100,
                                            100);
                                    featureNotEnabled.show();
                                }
                            } catch (Exception e) {
                                // TODO: handle exception
                                Log.w("error starting no history return", e.toString());
                            }
                            break;

                        case R.id.rbReturnInvoice:
                            try {
                                InvoicedProducts invoiceProductsObject = new InvoicedProducts(getActivity());
                                invoiceProductsObject.openReadableDatabase();
                                List<String[]> invoiceData = invoiceProductsObject.getInvoicesByItineraryDate(rowID);
                                invoiceProductsObject.closeDatabase();

                                if (!invoiceData.isEmpty()) {
                                    boolean statusReturnInvoiceReason = saveInvoice(remarks, timeStamp, RemarksType.RETURN_INVOICE.toString());
                                    if (statusReturnInvoiceReason) {
                                        txtEditInvoiceRemark.clearFocus();
                                        txtEditInvoiceRemark.setText(null);
                                        new UploadRemarksTask(getActivity()).execute();
                                        new UploadRemarksTask(getActivity()).execute();
                                        Intent startInvoiceReturn = new Intent(getActivity(), ReturnInvoiceActivity.class);

                                        Bundle bundleToView = new Bundle();
                                        bundleToView.putString("Id", rowID);
                                        bundleToView.putString("PharmacyId", pharmacyId);
                                        bundleToView.putString("onTimeOrNot", "1");
                                        startInvoiceReturn.putExtras(bundleToView);
                                        txtEditInvoiceRemark.setText(null);
                                        invoiceEdit.dismiss();
                                        getActivity().finish();
                                        startActivity(startInvoiceReturn);
                                    }
                                } else {
                                    Toast noInvoiceToday = Toast.makeText(getActivity(), "No Invoice has been made today!", Toast.LENGTH_SHORT);
                                    noInvoiceToday.setGravity(Gravity.TOP, 100, 100);
                                    noInvoiceToday.show();
                                    invoiceEdit.dismiss();
                                }
                            } catch (Exception e) {

                            }
                            break;

                        case R.id.rbReInvoice:
                            try {

                                boolean statusReInvoice = saveInvoice(remarks, timeStamp, RemarksType.REINVOICE.toString());
                                Products productsController = new Products(getActivity());
                                int count = productsController.getRowCount();

                                if (count > 0) {


                                    if (statusReInvoice) {
                                        new UploadRemarksTask(getActivity()).execute();
                                        //new UploadRemarksTask(getActivity()).execute();
                                        // Intent startInvoiceGen1 = new Intent("com.HesperusMarketing.channelbridge.INVOICEGEN1ACTIVITY");
//                                    if (isInvoiceOption2) {
//                                        startInvoiceGen1 = new Intent("com.HesperusMarketing.channelbridge.INVOICEGEN1ALTERNATE");
//                                    }else{
//                                        startInvoiceGen1 = new Intent(
//                                                "com.HesperusMarketing.channelbridge.INVOICEGEN1ACTIVITY");
//
//                                    }
//                                    Bundle bundleToView = new Bundle();
//                                    bundleToView.putString("Id", rowID);
//                                    bundleToView.putString("PharmacyId", pharmacyId);
//                                    bundleToView.putString("onTimeOrNot","1");
//                                    startInvoiceGen1.putExtras(bundleToView);
//                                    startActivity(startInvoiceGen1);
                                        //                                   getActivity().finish();
                                        invoiceEdit.dismiss();
                                        temporyLoadDataTask1 = new InvocieTemporyLoadDataTask1(getActivity());
                                        temporyLoadDataTask1.execute();
                                    }
                                } else {
                                    Toast to = Toast.makeText(getActivity().getApplicationContext(), "Please synchronise products", Toast.LENGTH_SHORT);
                                    to.setGravity(Gravity.CENTER, 0, 0);
                                    to.show();
                                }
                            } catch (Exception e) {
                                Log.w("Unable to Start ReInvoice", e.toString());
                            }

                            break;

                        case R.id.rbReturnInvoiceHistoryValidated:
                            try {
                                boolean statusAddNewInvoice = saveInvoice(remarks, timeStamp, RemarksType.RETURN_PRODUCT_WITH_HISTORY.toString());

                                if (statusAddNewInvoice) {
                                    txtEditInvoiceRemark.clearFocus();
                                    txtEditInvoiceRemark.setText(null);
                                    new UploadRemarksTask(getActivity()).execute();
                                    new UploadRemarksTask(getActivity()).execute();
                                    Intent startProductReturn = new Intent(getActivity(), ReturnProductHistoryActivity.class);

                                    Bundle bundleToView = new Bundle();
                                    bundleToView.putString("Id", rowID);
                                    bundleToView.putString("PharmacyId", pharmacyId);
                                    bundleToView.putString("onTimeOrNot", "1");
                                    startProductReturn.putExtras(bundleToView);
                                    getActivity().finish();
                                    invoiceEdit.dismiss();
                                    startActivity(startProductReturn);
                                }
                            } catch (Exception e) {
                                Log.w("error trying to start return product with history validated", e.toString());
                            }
                            break;


                        default:
                            break;
                    }


                }
            });

            PopulateItineryDetails(rowID);

            btnGenerateInvoice.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    Products productsController = new Products(getActivity());
                    int count = productsController.getRowCount();
                    Customers data = new Customers(getActivity());
                    data.openReadableDatabase();

                    DEL_Outstandiing deloustanding = new DEL_Outstandiing(getActivity());
                    deloustanding.openReadableDatabase();


                    //  if (statusOfGPS == true) {
                    if (count > 0) {
                        String tempCredit = tvCreditLimit.getText().toString();
                        String tempOutStanding = tvCurrntCredit.getText().toString();

                        if (tempCredit.isEmpty() || tempCredit == null) {
                            tempCredit = "0";
                        }
                        if (tempOutStanding.isEmpty() || tempOutStanding == null) {
                            tempOutStanding = "0";
                        }
                        double dCredit = Double.parseDouble(tempCredit);
                        double dOutstanding = Double.parseDouble(tempOutStanding);


                        if (Integer.parseInt(data.getInvoiceAlloweStstusByPharmacyId(pharmacyId)) == 0) {
                            showDialogSendMessage(getActivity(), 1);
                        } else {
                            if (Integer.parseInt(data.getMaxInvoiceCountByPharmacyId(pharmacyId)) <= deloustanding.getOustandCount(pharmacyId)) {
                                showDialogSendMessage(getActivity(), 2);
                                data.setInvoiceAlloweStstus(pharmacyId, 0);
                            } else {
                                if (dOutstanding > dCredit) {
                                    showDialogSendMessage(getActivity(), 3);
                                    data.setInvoiceAlloweStstus(pharmacyId, 0);
                                } else {
                                    temporyLoadDataTask1 = new InvocieTemporyLoadDataTask1(getActivity());
                                    temporyLoadDataTask1.execute();
                                }
                            }
                        }
                    } else {
                        Toast to = Toast.makeText(getActivity().getApplicationContext(), "Please synchronise products", Toast.LENGTH_SHORT);
                        to.setGravity(Gravity.CENTER, 0, 0);
                        to.show();
                    }
//


                    // } else {
                    //   showGpsAlert();

                    //  }


                    //     }


                    // TODO Auto-generated method stub

                    data.closeDatabase();
                    deloustanding.closeDatabase();
                }
            });


            iBtnEditInvoice.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                    if (statusOfGPS == true) {
                        Invoice invoiceObject = new Invoice(getActivity());
                        invoiceObject.openReadableDatabase();
                        ArrayList<String> invoicedIds = invoiceObject.getInvoiceIdByItineraryId(rowID);
                        invoiceObject.closeDatabase();

                        if (!invoicedIds.isEmpty()) {
                            String invoiceId = invoicedIds.get(invoicedIds.size() - 1);
                            tViewInvoiceNumber.setText(invoiceId);
                            invoiceEdit.show();
                        } else {
                            invoiceObject.openReadableDatabase();
                            List<String[]> invoices = invoiceObject.getAllInvoice();
                            invoiceObject.closeDatabase();
                            String invoiceId = String.valueOf(invoices.size() + 1);
                            tViewInvoiceNumber.setText(invoiceId);
                            invoiceEdit.show();
                        }

                    } else {
                        showGpsAlert();
                    }


                }
                //
            });

            iBtnSaveRemark.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    String remarks = txtRemarks.getText().toString();
                    String timeStamp = java.text.DateFormat
                            .getDateTimeInstance().format(
                                    Calendar.getInstance().getTime());

                    boolean status = saveInvoice(remarks, timeStamp, RemarksType.ITINERARY.toString());
                    /**
                     * upload remarks
                     */

                    if (status) {
                        new UploadRemarksTask(getActivity()).execute();
                        txtRemarks.clearFocus();
                        txtRemarks.setText(null);
                    }
                }
            });

            btnViewCustomerDetails.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent tabWidget = new Intent(
                            "com.HesperusMarketing.channelbridge.CUSTOMERDETAILSCOMMENTSTABWIDGET");

                    Bundle bundleToView = new Bundle();
                    bundleToView.putString("Id", rowID);
                    bundleToView.putString("PharmacyId", pharmacyId);

                    tabWidget.putExtras(bundleToView);
                    startActivity(tabWidget);

                    getActivity().finish();

                }
            });

    /*    } catch (Exception e) {
            e.printStackTrace();

            String error = e.toString();

            AlertDialog alertDialog = new AlertDialog.Builder(
                    this.getActivity()).create();

            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });

            alertDialog.setTitle("Error");
            alertDialog.setMessage(error);
            alertDialog.show();
        }*/

        ScrollView scroller = new ScrollView(getActivity());
        return scroller;
    }

    public void itineraryStatus() {
        Itinerary itineraryObject = new Itinerary(getActivity());

        String lastInvoicedItinerary = "-1";


        itineraryObject.openReadableDatabase();
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String currentDate = sdfDateTime.format(new Date(System.currentTimeMillis()));
        List<String[]> result = itineraryObject.getAllItinerariesForADay(currentDate);
        itineraryObject.closeDatabase();

        for (String[] a : result) {
            if (a[9].contentEquals("true")) {
                Log.w("itinerary Details Frag", a[9] + "");
                lastInvoicedItinerary = a[0];
            }
        }

        if (lastInvoicedItinerary.contentEquals("-1")) {
            if (!result.isEmpty()) {
                String[] itn = result.get(0);
                itineraryObject.openWritableDatabase();
                itineraryObject.setIsActiveTrue(itn[0]);
                itineraryObject.closeDatabase();
            }

        } else {
            if (rowID.contentEquals(lastInvoicedItinerary)) {
                btnGenerateInvoice.setEnabled(true);
            } else {
                btnGenerateInvoice.setEnabled(true);//fale
            }
        }
    }

    public void PopulateItineryDetails(String ROWID) {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());
        String deviceId = sharedPreferences.getString("DeviceId", "-1");
        String repId = sharedPreferences.getString("RepId", "-1");

        Itinerary itinerary = new Itinerary(getActivity());
        itinerary.openReadableDatabase();
        String status = itinerary.getItineraryStatus(rowID);
        itinerary.closeDatabase();

        if (status.contentEquals("true")) {
            itinerary.openReadableDatabase();
            String[] itnDetails = itinerary.getItineraryDetailsForTemporaryCustomer(ROWID);
            itinerary.closeDatabase();
            String address = itnDetails[2] + ", " + itnDetails[3] + ", " + itnDetails[4] + ", " + itnDetails[5];
            tViewName.setText(itnDetails[0]);
            tViewTarget.setText(itnDetails[1]);
            tViewAddress.setText(address);
            tViewTelephone.setText(itnDetails[6]);
            pharmacyId = itnDetails[8];
            String primaryImage = null;
            String[] imgWord = pharmacyId.split("_");


            byte[] image = new byte[0];

            CustomersPendingApproval data = new CustomersPendingApproval(this.getActivity());
            data.openReadableDatabase();
            image = data.getByteArrayImage(imgWord[1]);
            data.closeDatabase();

          /*  Bitmap bm = BitmapFactory.decodeByteArray(image, 0, image.length);
            iViewCustomerPic.setImageBitmap(bm);*/
            try {
                ImageGallery imageGallery = new ImageGallery(getActivity());
                imageGallery.openReadableDatabase();
                primaryImage = imageGallery.getPrimaryImageforCustomerId(itnDetails[7]);
                imageGallery.closeDatabase();
            } catch (Exception e) {
                Log.w("Unable to get display pic", e.toString());
            }

            try {
                Log.w("Primary Image", primaryImage + "");
                File customerImageFile = new File(
                        Environment.getExternalStorageDirectory() + File.separator
                                + "DCIM" + File.separator + "Channel_Bridge_Images"
                                + File.separator + primaryImage);

                if (customerImageFile.exists()) {

                    try {
                        iViewCustomerPic.setImageBitmap(decodeSampledBitmapFromResource(
                                String.valueOf(customerImageFile), 400, 550));
                    } catch (IllegalArgumentException e) {
                        Log.w("Illegal argument exception", e.toString());
                    } catch (OutOfMemoryError e) {
                        Log.w("Out of memory error :(", e.toString());
                    }

                } else {
                    //sk image

                    DownloadImage downloadImage = new DownloadImage(this.getActivity());
                    String imageWithImageId[] = downloadImage.execute(repId, pharmacyId).get();

                    if (imageWithImageId.length > 0) {
                        byte[] image1 = new byte[0];

                        image1 = android.util.Base64.decode(imageWithImageId[0], Base64.DEFAULT);
                        Bitmap bm = BitmapFactory.decodeByteArray(image1, 0, image1.length);
                        iViewCustomerPic.setImageBitmap(bm);

                        createDirectoryAndSaveFile(bm, imageWithImageId[1]);
                    } else {
                        iViewCustomerPic.setImageResource(R.drawable.unknown_image);
                    }
                }
            } catch (Exception e) {
                Log.w("Error setting image file", e.toString());
            }

        } else if (status.contentEquals("false")) {
            itinerary.openReadableDatabase();
            String[] itnDetails = itinerary.getItineraryDetailsById(ROWID);
            itinerary.closeDatabase();
            String primaryImage = itnDetails[7];
            pharmacyId = itnDetails[4];

            try {

                byte[] image = new byte[0];
                Customers data = new Customers(this.getActivity());
                data.openReadableDatabase();
                image = data.getByteArrayImage(pharmacyId);
                data.closeDatabase();
/*
                Bitmap bm = BitmapFactory.decodeByteArray(image, 0, image.length);
                iViewCustomerPic.setImageBitmap(bm);*/


                Log.w("Primary Image", primaryImage + "");
                File customerImageFile = new File(
                        Environment.getExternalStorageDirectory() + File.separator
                                + "DCIM" + File.separator + "Channel_Bridge_Images"
                                + File.separator + primaryImage);

                if (customerImageFile.exists()) {

                    try {
                        iViewCustomerPic.setImageBitmap(decodeSampledBitmapFromResource(
                                String.valueOf(customerImageFile), 400, 550));
                    } catch (IllegalArgumentException e) {
                        Log.w("Illegal argument exception", e.toString());
                    } catch (OutOfMemoryError e) {
                        Log.w("Out of memory error :(", e.toString());
                    }

                } else {

                    //sk image


                    DownloadImage downloadImage = new DownloadImage(this.getActivity());
                    String imageWithImageId[] = downloadImage.execute(repId, pharmacyId).get();

                    if (imageWithImageId.length > 0) {
                        byte[] image1 = new byte[0];
                        image1 = android.util.Base64.decode(imageWithImageId[0], Base64.DEFAULT);
                        Bitmap bm = BitmapFactory.decodeByteArray(image1, 0, image1.length);
                        createDirectoryAndSaveFile(bm, imageWithImageId[1]);
                        iViewCustomerPic.setImageBitmap(bm);


                    } else {
                        iViewCustomerPic.setImageResource(R.drawable.unknown_image);
                    }

                }
            } catch (Exception e) {
                Log.w("Error setting image file", e.toString());
                iViewCustomerPic.setImageResource(R.drawable.unknown_image);
            }
            //			Log.w("Itn DETAILS LEngth", itnDetails.length+"");
//			Log.w("ITN DETAILS", itnDetails[0]);
//			Log.w("ITN DETAILS", itnDetails[1]);
//			Log.w("ITN DETAILS", itnDetails[2]);
//			Log.w("ITN DETAILS", itnDetails[3]);
            tViewName.setText(itnDetails[0]);
            tViewTarget.setText(itnDetails[1]);
            tViewAddress.setText(itnDetails[2]);
            tViewTelephone.setText(itnDetails[3]);
            pharmacyId = itnDetails[4];
        }

        String invoDate = new SimpleDateFormat("dd/MM/yyyy")
                .format(new Date());
        String xSum = "0";
        String invoNo = "";
        if (iswebApprovalActive == false) {
            xSum = invoHandler.getInvoiceSumforGivenDateAndCustomer(pharmacyId, invoDate);
            invoNo = invoHandler.getLastInvoiceForFivenDate(pharmacyId, invoDate);
        } else {
            String compCode = "";
            customerHandler.openReadableDatabase();
            compCode = customerHandler.getCompanyCodeFromPhamcyId(pharmacyId);
            customerHandler.closeDatabase();
            xSum = salesHandler.getInvoiceSumforGivenDateAndCustomer(compCode, invoDate);
            invoNo = salesHandler.getLastInvoiceForGivenDate(compCode, invoDate);
        }
        if (xSum.isEmpty() || xSum == null) {
            xSum = "0";
        }
        double dSum = Double.parseDouble(xSum);
        tViewInvoiceVal.setText(String.format("%.2f", dSum));
        String sTarget = tViewTarget.getText().toString();
        if (sTarget.isEmpty() || sTarget.equals("") || sTarget == null) {
            sTarget = "0";
        }

        double dTarget = Double.parseDouble(sTarget);
        double dVariance = 0.00;
        dVariance = dTarget - dSum;
        Log.i("vari", "" + dVariance);


        customerHandler.openReadableDatabase();
        String[] selectedCustomer = customerHandler.getCustomerDetailsByPharmacyId(pharmacyId);
        customerHandler.closeDatabase();
        String currCredit = "0";
        currCredit = selectedCustomer[15];
        if (currCredit == null) {
            currCredit = "0";
        }
        tvCreditLimit.setText(selectedCustomer[14]);
        tvCurrntCredit.setText(String.format("%.2f", Double.parseDouble(currCredit)));
        tvVariancefr.setText(String.format("%.2f", dVariance));
        tvInvoiceNumber.setText(invoNo);
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/DCIM/Channel_Bridge_Images");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/DCIM/Channel_Bridge_Images/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/DCIM/Channel_Bridge_Images/"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * should update the
     *
     * @param remark
     * @param timestamp
     * @return
     */

    public boolean saveInvoice(String remark, String timestamp, String remarkType) {

        repconnector = new Reps(getActivity());
        SimpleDateFormat sdfDateTime2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        repconnector.openReadableDatabase();


        repconnector.openReadableDatabase();
        List<String[]> repList = repconnector.getAllRepsDetails();
        repconnector.closeDatabase();
        if (!repList.isEmpty()) {
            for (String[] ids : repList) {
                repId = ids[1];
            }
        }

        Itinerary itinerary = new Itinerary(getActivity());
        itinerary.openReadableDatabase();
        itenararyDate = itinerary.getDateforSelectedROWID(rowID);
        globalPharmaId = itinerary.getGlobalPharmaIDForRowID(rowID);
        Log.i("Itinarary Date ****", "---->" + itenararyDate);
        Log.i("Pharmacy ID ****", "---->" + globalPharmaId);
        Log.i("row    ID ****", "---->" + rowID);

        String[] itineraryDetails = itinerary.getItineraryDetailsById(rowID);
        for (int i = 0; i < itineraryDetails.length; i++) {
            Log.i("server  ID ****", "---->" + itineraryDetails[i]);
        }

        Log.i("server  ID ****", "---->" + itineraryDetails[1]);
        itinerary.closeDatabase();
        getGPS();
        if (!remark.isEmpty()) {
            try {
                Remarks remarksObject = new Remarks(getActivity()
                        .getApplicationContext());
                remarksObject.openWritableDatabase();
                long result = remarksObject.insertRemark(rowID,
                        remark, timestamp, itenararyDate, globalPharmaId, repId, remarkType, "0", itineraryDetails[8], Double.toString(lng), Double.toString(lat));
                Log.w("Remarks Table: ", String.valueOf(result));
                remarksObject.closeDatabase();
                if (result != -1) {
                    Toast toast = Toast.makeText(getActivity(),
                            "Remark has been added",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 50, 100);
                    toast.show();

                    Invoice invoiceObject = new Invoice(getActivity());
                    invoiceObject.openReadableDatabase();
                    ArrayList<String> invoices = new ArrayList<String>();
                    invoices = invoiceObject.getInvoiceIdByItineraryId(rowID);
                    invoiceObject.closeDatabase();

                    if (invoices.isEmpty()) {
                        Itinerary itineraryObject = new Itinerary(getActivity());
                        itineraryObject.openReadableDatabase();
                        SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        String currentDate = sdfDateTime.format(new Date(System.currentTimeMillis()));
                        List<String[]> resultforToday = itineraryObject.getAllItinerariesForADay(currentDate);
                        itineraryObject.closeDatabase();

                        int nxtItn = 0;
                        for (int i = 0; i < resultforToday.size(); i++) {
                            String[] temp = resultforToday.get(i);
                            if (temp[0].contentEquals(rowID)) {
                                nxtItn = i + 1;
                            }
                        }
                        if (nxtItn < resultforToday.size()) {
                            String[] temp = resultforToday.get(nxtItn);

                            itineraryObject.openWritableDatabase();
                            itineraryObject.setIsActiveTrue(temp[0]);
                            itineraryObject.closeDatabase();
                        }

                    }

                    return true;
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            getActivity()).create();

                    alertDialog.setButton("OK",
                            new DialogInterface.OnClickListener() {

                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    return;
                                }
                            });

                    alertDialog.setTitle("Alert");
                    alertDialog
                            .setMessage("Oops! Something Fent wrong, Please try again or contact Administrator");
                    alertDialog.show();
                    return false;
                }

            } catch (Exception e) {
                Log.w("Inserting Remarked: ", e.toString());
                return false;
            }
        } else {
            Toast toast = Toast.makeText(getActivity(),
                    "Remark Feild is Empty!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 50, 100);
            toast.show();
            return false;
        }
    }


    public class InvocieTemporyLoadDataTask1 extends AsyncTask<Void, Void, Void> {

        private Context context;
        private ProductRepStore productRepStoreController;
        private TemporaryInvoice temporaryInvoiceController;
        private ArrayList<Product> repStockList;
        private ProgressDialog dialog;

        public InvocieTemporyLoadDataTask1(Context context) {
            this.context = context;
            productRepStoreController = new ProductRepStore(context);
            temporaryInvoiceController = new TemporaryInvoice(context);
            repStockList = new ArrayList<>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            productRepStoreController.openReadableDatabase();
            temporaryInvoiceController.openWritableDatabase();
            temporaryInvoiceController.deleteAllRecords();
            dialog = new ProgressDialog(context);
            this.dialog.setMessage("Please wait");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (isInvoiceOption2) {
                repStockList = productRepStoreController.getAllRepAtoreDetails();

                for (Product repStock : repStockList) {
                    temporaryInvoiceController.insertTempInvoStock(repStock);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            productRepStoreController.closeDatabase();
            temporaryInvoiceController.closeDatabase();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (isInvoiceOption2) {
                startInvoiceGen1 = new Intent("com.HesperusMarketing.channelbridge.INVOICEGEN1ALTERNATE");
            } else {
                startInvoiceGen1 = new Intent("com.HesperusMarketing.channelbridge.INVOICEGEN1ACTIVITY");

            }
            String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS")
                    .format(new Date().getTime());
            Log.i("time frag -e->", timeStamp);
            Bundle bundleToView = new Bundle();
            bundleToView.putString("Id", rowID);
            bundleToView.putString("PharmacyId", pharmacyId);
            bundleToView.putString("startTime", timeStamp);
            bundleToView.putString("onTimeOrNot", "1");

            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(getActivity().getBaseContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("AutoSyncRun", false);
            editor.commit();

            cusName = tViewName.getText().toString();
            contactNumber = tViewTelephone.getText().toString();
            pharmacyId1 = pharmacyId;

            startInvoiceGen1.putExtras(bundleToView);
            getActivity().finish();
            startActivity(startInvoiceGen1);
        }
    }

    private void showGpsAlert() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Go to Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

//                                Intent iternaryListActivity = new Intent(
//                                        "com.HesperusMarketing.channelbridge.ITINERARYLIST");
//                                startActivity(iternaryListActivity);
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);


                                // GetGPS();
                                // dialog.cancel();

                                // finish();
                                ////------


                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  dialog.cancel();
//                        Intent iternaryListActivity = new Intent(
//                                "com.HesperusMarketing.channelbridge.ITINERARYLIST");
//                        startActivity(iternaryListActivity);
                        //  finish();
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    private void getGPS() {

        String GPS = "";

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 25, this);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (location != null) {


            lat = (double) (location.getLatitude());
            lng = (double) (location.getLongitude());


        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //Himanshu
    public void showDialogSendMessage(Context context, int status) {

        final Dialog dialogBox = new Dialog(context);
        dialogBox.setTitle("Invoice Approval");
        dialogBox.setContentView(R.layout.dialog_send_approval);
        dialogBox.setCancelable(true);

        int approvStatus = 0;

        String reason = null;
        cusName = tViewName.getText().toString();

        OutsandingAdapter outAdapter;
        ArrayList<OutsandingList> outStandingList = new ArrayList<>();

        TextView txtMessage = (TextView) dialogBox.findViewById(R.id.textView_dialogsendapproval_customername);

        final RelativeLayout layoutResend = (RelativeLayout) dialogBox.findViewById(R.id.layout_dialogsendapproval_resend);
        final TextView txtPhoneNumber = (TextView) dialogBox.findViewById(R.id.textView_dialogsendapproval_phoneNumber);
        final Spinner spinPerson = (Spinner) dialogBox.findViewById(R.id.spinner_approve_person);
        final EditText edtComment = (EditText) dialogBox.findViewById(R.id.editText_dialogsendapproval_comment);
        final EditText edtCode = (EditText) dialogBox.findViewById(R.id.editTextdialogsendapproval_code);
        Button btnContinue = (Button) dialogBox.findViewById(R.id.buttondialogsendapproval_continue);
        final Button btnSende = (Button) dialogBox.findViewById(R.id.buttondialogsendapproval_send);
        Button btnCancel = (Button) dialogBox.findViewById(R.id.buttondialogsendapproval_cancel);
        ListView listOutStanding = (ListView) dialogBox.findViewById(R.id.listViewOutsatnding);
        TextView txtCreditLimit = (TextView) dialogBox.findViewById(R.id.textView_tot_CreditLimit);


        outAdapter = new OutsandingAdapter(context, outStandingList);

        final Approval_Persons ap = new Approval_Persons(getActivity());
        ap.openReadableDatabase();

        final Approval_Details aPProDetails = new Approval_Details(getActivity());
        aPProDetails.openWritableDatabase();

        final Reps rep = new Reps(getActivity());
        rep.openReadableDatabase();

        final Customers data = new Customers(getActivity());
        data.openReadableDatabase();

        txtCreditLimit.setText("Credit Limit : " + data.getCreditLimitByPharmacyId(pharmacyId));

        final ArrayList<String> approvalPersonsList = ap.getAllPerson();
        ArrayAdapter<String> pesronNameAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, approvalPersonsList);

        pesronNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPerson.setAdapter(pesronNameAdapter);

        DEL_Outstandiing outStanding = new DEL_Outstandiing(getActivity());
        outStanding.openReadableDatabase();

        List<String[]> oustanginDetails = outStanding.loadOutSatingInvoiceNumberBYId(pharmacyId);
        for (String[] oustangin : oustanginDetails) {

            if (oustangin[0].equals("Null")) {

            } else {
                outStandingList.add(new OutsandingList(oustangin[0], oustangin[1], oustangin[2].substring(0, 10)));
            }

        }

        listOutStanding.setAdapter(outAdapter);

        if (aPProDetails.checkAccessibility(pharmacyId) == 0) {
            // edtCode.setEnabled(false);
            layoutResend.setEnabled(false);
            approvStatus = 0;
        } else {
            //btnSende.setEnabled(false);
            //  edtCode.setEnabled(true);
            approvStatus = 1;

        }

        if (status == 1) {
            reason = "invoice is not allowed";
            txtMessage.setText(cusName + ",This customer invoice is not allowed.Do you want to send for approval and proceed ?");
        } else if (status == 2) {
            reason = "Invoice count exceeded";
            txtMessage.setText(cusName + ",Invoice count exceeded .Do you want to send for approval and proceed ?");
        } else if (status == 3) {
            reason = "Credit limit exceeded";
            txtMessage.setText(cusName + ",Credit limit exceeded.Do you want to send for approval and proceed ?");
        }


        final String pesronName = approvalPersonsList.get(0);
        spinPerson.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        int position = spinPerson.getSelectedItemPosition();
                        String pesronName = approvalPersonsList.get(position);
                        txtPhoneNumber.setText("Phone Number : " + String.valueOf(ap.getPhoneNumberByPersonName(pesronName)));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }

                }
        );
        txtPhoneNumber.setText("Phone Number : " + String.valueOf(ap.getPhoneNumberByPersonName(pesronName)));


        //button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ap.closeDatabase();
                aPProDetails.closeDatabase();
                rep.closeDatabase();
                data.closeDatabase();
                dialogBox.dismiss();
            }
        });


        final String finalReason = reason;
        final int finalApprovStatus = approvStatus;
        btnSende.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TelephonyManager telMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);


                if (finalApprovStatus == 1) {
                    showErrorMessage("Already send for approval");
                } else if (telMgr.getSimState() != 5) {
                    showErrorMessage("Sim Card error");
                } else {

                    String code = nextSessionId();
                    Date date = new Date(System.currentTimeMillis());
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    final String repId = sharedPreferences.getString("RepId", "-1");
                    String msg = "Rep: " + rep.getRepNameByRepId(repId) + " Customer: " + cusName + " Comment: " + edtComment.getText().toString() + " Date: " + dateFormat.format(date) + " Code: " + code;

                    boolean resultSend = sendSMS(String.valueOf(ap.getPhoneNumberByPersonName(pesronName)), msg);
                    // boolean resultSend =true;
                    if (resultSend == true) {
                        aPProDetails.insertDetails(pharmacyId, code, finalReason, edtComment.getText().toString(), pesronName);
                        edtComment.setText("");
                        showErrorMessage("Send Approval Successfully");
                        btnSende.setEnabled(false);
                        edtCode.setEnabled(true);
                        layoutResend.setEnabled(true);

                    } else {
                        showErrorMessage("Send Approval Fail,please try again");
                    }

                    ap.closeDatabase();
                    aPProDetails.closeDatabase();
                    rep.closeDatabase();
                    data.closeDatabase();
                }

            }
        });


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TemporaryColorChart colorchart = new TemporaryColorChart(getActivity());
                colorchart.openWritableDatabase();
                colorchart.deleteAllRecords();
                colorchart.closeDatabase();

                if (edtCode.getText().toString().isEmpty()) {
                    showErrorMessage("Code is empty,please try again");

                } else if (edtCode.getText().toString().equals("0000")) {
                    aPProDetails.setAccess(pharmacyId, edtCode.getText().toString());

                    data.setInvoiceAlloweStstus(pharmacyId, 1);
                    ap.closeDatabase();
                    aPProDetails.closeDatabase();
                    rep.closeDatabase();
                    data.closeDatabase();
                    dialogBox.dismiss();

                    if (isOnline()) {
                        new uploadApproveDetails().execute();
                    } else {

                    }

                    temporyLoadDataTask1 = new InvocieTemporyLoadDataTask1(getActivity());
                    temporyLoadDataTask1.execute();
                } else if (edtCode.getText().toString().length() == 4) {
                    boolean resultContinue = aPProDetails.checkCode(pharmacyId, edtCode.getText().toString());
                    if (resultContinue == true) {

                        aPProDetails.setAccess(pharmacyId, edtCode.getText().toString());

                        data.setInvoiceAlloweStstus(pharmacyId, 1);
                        ap.closeDatabase();
                        aPProDetails.closeDatabase();
                        rep.closeDatabase();
                        data.closeDatabase();
                        dialogBox.dismiss();

                        if (isOnline()) {
                            new uploadApproveDetails().execute();
                        } else {

                        }

                        temporyLoadDataTask1 = new InvocieTemporyLoadDataTask1(getActivity());
                        temporyLoadDataTask1.execute();
                    } else {
                        showErrorMessage("Invalid code,please try again");

                    }

                } else {
                    showErrorMessage("Invalid code,please try again");

                }

            }
        });


        layoutResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TelephonyManager telMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                if (telMgr.getSimState() != 5) {
                    showErrorMessage("Sim Card error");
                } else {
                    String code = nextSessionId();
                    Date date = new Date(System.currentTimeMillis());
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
                    int rowid = aPProDetails.checkAccessibility(pharmacyId);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    final String repId = sharedPreferences.getString("RepId", "-1");
                    String msg = "Rep: " + rep.getRepNameByRepId(repId) + " Customer: " + cusName + " Comment: " + edtComment.getText().toString() + " Date: " + dateFormat.format(date) + " Code: " + code;

                    if (sendSMS(String.valueOf(ap.getPhoneNumberByPersonName(pesronName)), msg)) {
                        boolean resultResend = aPProDetails.updateCode(rowid, code);
                        if (resultResend == true) {
                            showErrorMessage("Resend Approval Successfully");
                        } else {
                            showErrorMessage("Resend Approval Fail,please try again");
                        }
                    } else {
                        showErrorMessage("Resend Approval Fail,please try again");
                    }
                    ap.closeDatabase();
                    aPProDetails.closeDatabase();
                    rep.closeDatabase();
                    data.closeDatabase();
                }
            }
        });
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtComment.getWindowToken(), 0);
        // ap.closeDatabase();
        dialogBox.show();
    }

    public void showErrorMessage(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public String nextSessionId() {
        String genCode = null;
        SecureRandom random = new SecureRandom();
        genCode = new BigInteger(20, random).toString(32);
        if (genCode.length() != 4) {
            genCode = genCode + 0;
        } else {

        }
        return genCode;
    }

    public boolean sendSMS(String number, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, msg, null, null);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    public boolean isOnline() {
        boolean flag = false;
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            flag = true;
        }
        return flag;
    }

    private class uploadApproveDetails extends AsyncTask<Void, Void, Void> {

        Approval_Details approvdetails;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            approvdetails = new Approval_Details(getActivity());
            approvdetails.openReadableDatabase();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final String repId = sharedPreferences.getString("RepId", "-1");

            List<String[]> rtnRemarks = approvdetails.getApprovaDetails();

            System.out.println("accesss cout " + rtnRemarks.size());


            for (java.lang.String[] rtnData : rtnRemarks) {
                java.lang.String[] remarksDetails = new String[10];
                remarksDetails[0] = rtnData[0];
                remarksDetails[1] = rtnData[1];
                remarksDetails[2] = rtnData[2];
                remarksDetails[3] = rtnData[3];
                remarksDetails[4] = rtnData[4];
                remarksDetails[5] = rtnData[5];
                remarksDetails[6] = rtnData[6];
                remarksDetails[7] = rtnData[7];
                remarksDetails[8] = rtnData[8];
                remarksDetails[9] = rtnData[9];

                String responseArr = null;

                while (responseArr == null) {
                    try {

                        WebService webService = new WebService();
                        responseArr = webService.uploadApprovalDetails(remarksDetails, repId);

                        if (responseArr.equals("OK")) {
                            approvdetails.updateUploadStstus(rtnData[0]);
                        }
                        Thread.sleep(100);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }

                }

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }

    }


}
