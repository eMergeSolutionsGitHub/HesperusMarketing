package com.HesperusMarketing.channelbridge;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.HesperusMarketing.Entity.Product;
import com.HesperusMarketing.channelbridgedb.Branch;
import com.HesperusMarketing.channelbridgedb.CreditPeriod;
import com.HesperusMarketing.channelbridgedb.Customers;
import com.HesperusMarketing.channelbridgedb.InvoicePaymentType;
import com.HesperusMarketing.channelbridgedb.Master_Banks;
import com.HesperusMarketing.channelbridgedb.Products;
import com.HesperusMarketing.channelbridgedb.Sequence;
import com.HesperusMarketing.channelbridgedb.ShelfQuantity;

public class InvoiceGen2Activity extends Activity {
    static final int COLLECTED_DATE = 0;
    static final int REALIZE_DATE = 1;
    static boolean saveFlag; // this one is used to stop the save button getting enabled when putting in to sleep and again opens.
    Button btnGenerateInvoice, btnReturns, btnDiscard;
    ImageButton iBtnChequeDetails;
    RadioGroup rGroupPaymentOption;
    TextView tViewInvoiceNo, tViewCustomerName, tViewInvoiceQuantity, tViewInvoiceGross, tViewNetInvoiceAmount, tViewReturnsGross, tViewReturnsDiscount, tViewNetReturnAmount, tViewNetPayable;
    TextView labelCash, labelCheque, labelCredit, tViewRemain;
    TextView tViewCollectedDate, tViewRealizedDate;
    Dialog chequeDetailsPopup;
    Spinner spinCreditPeriod;//spInvoType
    EditText txtCheque, txtCash, txtCredit, txtDiscountPercentage, txtDiscountValue;
    String rowId, pharmacyId,discountVal;
    ArrayList<String> creditList;
    ArrayList<String> bankList;
    ArrayList<String> branchList;
    String paymentOption, cash = "0", credit = "0", remain = "0", totalPrice = "0", marketReturns = "0", discount = "0", needToPay = "0", totalQty = "0", cheque = "0", invoiceNumber = "0", returnedInvoice = "-1";
    String chequeNumber = "0", collectionDate = "", releaseDate = "", creditDuration = "";
    double temporaryAmountToPay = 0;
    double temporaryNeedToPay;
    boolean discountEditable = true;
    boolean discountEntered = false;
    boolean chequeEnabled = false;
    String startTime = "";
    ArrayList<String[]> selectedData = new ArrayList<String[]>();
    ArrayList<SelectedProduct> selectedProductsArray = new ArrayList<SelectedProduct>();
    ArrayList<ReturnProduct> returnProductsArray = new ArrayList<ReturnProduct>();
    private int collectedYear;
    private int collectedMonth;
    private int collectedDay;
    private int selectedCreditPeriodIndex;
    private Boolean isInvoiceOption1 = false;
    AutoCompleteTextView spBank, spBranch;
    Master_Banks bankController;
    private String selectedBank = "", selectedBranch = "";
    private DatePickerDialog.OnDateSetListener collectedDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            collectedYear = year;
            collectedMonth = monthOfYear;
            collectedDay = dayOfMonth;
            updateCollectedDateDisplay();
        }
    };
    Intent startInvoiceGen1;
    private int realizeYear;
    private int realizeMonth;
    private int realizeDay;
    private Boolean invoiceOption2Enable;
    private DatePickerDialog.OnDateSetListener realizeDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            realizeYear = year;
            realizeMonth = monthOfYear;
            realizeDay = dayOfMonth;
            updateRealizeDateDisplay();
        }
    };
    private CreditPeriod creditPeriodController;
    private Branch branchController;
    private int selectedBranchIndex, selectedBankIndex;
    private String referenceNumber;
    private Boolean isCheckEntered = false;
    List<String> paymentTypeList;
    InvoicePaymentType pt;
    String selectedInvoOption = "";
    String seletedPaymentOptionCode = "";
    String branchCode = "";
    private static final int CAMERA_REQUEST = 1889;
    Bitmap photo;
    byte[] chequeimage = new byte[0];
    private String disountValue = "0";
    int totalQuantity;
    Customers customersHandler;
    private Boolean isBlockerActivated;
    String[] customerDetails;

    //  double discountValue = 0.0;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_gen_2);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        tViewCustomerName = (TextView) findViewById(R.id.tvCustomerName);
        tViewInvoiceGross = (TextView) findViewById(R.id.tvInvoiceGross);
        tViewNetInvoiceAmount = (TextView) findViewById(R.id.tvNetInvoiceAmount);
        tViewReturnsGross = (TextView) findViewById(R.id.tvReturnGross);
        tViewReturnsDiscount = (TextView) findViewById(R.id.tvReturnDiscount);
        tViewNetReturnAmount = (TextView) findViewById(R.id.tvNetReturnAmount);
        tViewNetPayable = (TextView) findViewById(R.id.tvNetPayable);
        tViewInvoiceNo = (TextView) findViewById(R.id.tvInvoiceNo);
        tViewInvoiceQuantity = (TextView) findViewById(R.id.tvInvoiceQty);
        spinCreditPeriod = (Spinner) findViewById(R.id.sCreditPeriod);
        labelCash = (TextView) findViewById(R.id.labelCash);
        labelCheque = (TextView) findViewById(R.id.labelCheque);
        labelCredit = (TextView) findViewById(R.id.labelCredit);
        tViewRemain = (TextView) findViewById(R.id.tvRemain);
        txtCash = (EditText) findViewById(R.id.etCash);
        // spInvoType = (Spinner)findViewById(R.id.spInvOption);
        txtCheque = (EditText) findViewById(R.id.etCheque);
        txtDiscountPercentage = (EditText) findViewById(R.id.etDiscountPercentage);
        txtDiscountValue = (EditText) findViewById(R.id.etDiscountValue);
        txtCredit = (EditText) findViewById(R.id.etCredit);
        txtCredit.setFocusable(false);
        txtCredit.setEnabled(false);
        txtCredit.setClickable(false);
        btnGenerateInvoice = (Button) findViewById(R.id.bGenerateInvoice);
        btnDiscard = (Button) findViewById(R.id.bDiscard);
        btnReturns = (Button) findViewById(R.id.bProductReturns);
        iBtnChequeDetails = (ImageButton) findViewById(R.id.bChequeDetails);
        creditList = new ArrayList<>();
        branchList = new ArrayList<>();
        saveFlag = true;
        bankController = new Master_Banks(InvoiceGen2Activity.this);
        branchController = new Branch(InvoiceGen2Activity.this);
        bankList = new ArrayList<>();
        chequeDetailsPopup = new Dialog(InvoiceGen2Activity.this);
        chequeDetailsPopup.setContentView(R.layout.cheque_details_popup);
        totalQuantity = 0;
        referenceNumber = new String();
        customersHandler = new Customers(InvoiceGen2Activity.this);
        if (savedInstanceState != null) {
            getDataFromPreviousActivity(savedInstanceState);
        } else {
            getDataFromPreviousActivity(getIntent().getExtras());
        }
        tViewReturnsDiscount.setText(discountVal);
        SharedPreferences sharedCB = getApplicationContext().getSharedPreferences("CBHesPreferences", Context.MODE_PRIVATE);

        isBlockerActivated = (sharedCB.getBoolean("ISOutStandingBlock", true));
//        paymentTypeList = new ArrayList<>();
        pt = new InvoicePaymentType(InvoiceGen2Activity.this);
        customerDetails = new String[20];
//        pt.openReadableDatabase();
//        if (pt.get_rowcount() > 0) {
//            try {
//                paymentTypeList = pt.loadPayment_Type();
//            } catch (Exception e) {
//
//            }
//        }
//        pt.closeDatabase();
//        ArrayAdapter<String> payOptionAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.single_list_item,paymentTypeList);
//        spInvoType.setAdapter(payOptionAdapter);
        creditPeriodController = new CreditPeriod(getApplicationContext());
        creditList = creditPeriodController.getCreditPeriodList();
//        spInvoType.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selectedInvoOption =  spInvoType.getSelectedItem().toString();
//                pt.openReadableDatabase();
//                seletedPaymentOptionCode  = pt.GetPyementtypeCode(selectedInvoOption);
//                pt.closeDatabase();
//                Log.i("OptionCode -O->2 ",""+seletedPaymentOptionCode);
//                switch (selectedInvoOption){
//                    case "Cash":
//                        txtCash.setEnabled(true);
//                        iBtnChequeDetails.setEnabled(false);
//                        break;
//                    case "Cheque":
//                        txtCash.setEnabled(false);
//                        iBtnChequeDetails.setEnabled(true);
//                        break;
//                    case "Cash+Cheque":
//                        txtCash.setEnabled(true);
//                        iBtnChequeDetails.setEnabled(true);
//                        break;
//                    default:
//                        txtCash.setEnabled(true);
//                        iBtnChequeDetails.setEnabled(true);
//                        break;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        setCreditSpinner(creditList);
        SharedPreferences shared = getApplicationContext().getSharedPreferences("CBHesPreferences", Context.MODE_PRIVATE);
        invoiceOption2Enable = (shared.getBoolean("InvoiceOption", true));
        txtDiscountPercentage.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (!s.toString().isEmpty()) {
                    discount = s.toString();
                    txtCash.setText("0");
                    txtCheque.setText("0");
                    final EditText txtChequeAmount0 = (EditText) chequeDetailsPopup.findViewById(R.id.etChequeAmount);
                    final EditText txtChequeNumber0 = (EditText) chequeDetailsPopup.findViewById(R.id.etChequeNumber);
                    cheque = "0";
                    chequeNumber = "0";
                    txtChequeAmount0.setText("0");
                    txtChequeNumber0.setText("");
                } else {
                    discount = "0";
                }

                if (!discount.isEmpty()) {
                    double discountPercentage = 0;
                    if (!txtDiscountPercentage.getText().toString().isEmpty()) {
                        discountPercentage = Double.parseDouble(txtDiscountPercentage.getText().toString());
                    } else {
                        discountPercentage = 0;
                    }
                    double totalAmount = Double.parseDouble(totalPrice);
                    double marketRet = 0;
                    discount = txtDiscountPercentage.getText().toString();
                    try {
                        marketRet = Double.parseDouble(marketReturns);
                    } catch (Exception e) {
                        Log.w("Error from market ret", e.toString());
                    }
                    if ((discountPercentage <= 100)) {
                        if ((discountPercentage >= 0)) {



                            double discountValue = (discountPercentage / 100) * totalAmount;
                            txtDiscountValue.setText(String.format("%.2f", discountValue));
                            double needToPayAmount = totalAmount - (discountValue);
                            tViewNetInvoiceAmount.setText(String.format("%.2f", needToPayAmount));
                            double netPayable = totalAmount - (discountValue + marketRet);
                            tViewNetPayable.setText(String.format("%.2f", netPayable));

                            txtCredit.setText(String.format("%.2f", netPayable));

                        } else {

                            txtDiscountPercentage.setText("0");
                            txtDiscountPercentage.requestFocus();

                            Toast discountPercentageMin = Toast.makeText(getApplication(), "Discount Percentage cannot be less than 0!", Toast.LENGTH_SHORT);
                            discountPercentageMin.show();
                        }
                    } else {
                        txtDiscountPercentage.setText("0");
                        txtDiscountPercentage.requestFocus();
                        Toast discountPercentageMax = Toast.makeText(getApplication(), "Discount Percentage cannot be greater than 100!", Toast.LENGTH_SHORT);
                        discountPercentageMax.show();
                    }

                } else {
                    txtDiscountValue.setText("");
                }

//                if (!s.toString().isEmpty()) {
//                    txtCash.setText("0");
//                    txtCheque.setText("0");
//                    final EditText txtChequeAmount0 = (EditText) chequeDetailsPopup.findViewById(R.id.etChequeAmount);
//                    final EditText txtChequeNumber0 = (EditText) chequeDetailsPopup.findViewById(R.id.etChequeNumber);
//                    cheque = "0";
//                    chequeNumber = "0";
//                    txtChequeAmount0.setText("0");
//                    txtChequeNumber0.setText("");
//                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {

            }
        });

        txtCash.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double cashValue = 0;
                double cheque = 0;
                if (!s.toString().isEmpty() || !s.toString().equals("")) {
                    cashValue = Double.parseDouble(s.toString());
                }
                if (!s.toString().isEmpty() || !s.toString().equals("")) {
                    cheque = Double.parseDouble(txtCheque.getText().toString());
                }
                double totalAmount1 = Double.parseDouble(tViewNetInvoiceAmount.getText().toString());
                double deductingValue = cashValue + cheque;
                totalAmount1 -= deductingValue;
                txtCredit.setText(String.format("%.2f", totalAmount1));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtCheque.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double chequeValue = 0;
                double cash = 0;
                if (!s.toString().isEmpty() || !s.toString().equals("")) {

                    try {
                        chequeValue = Double.parseDouble(s.toString());
                    } catch (NumberFormatException e) {
                        Log.i("Num exception", e.toString());
                    }

                }
                if (!txtCash.getText().toString().isEmpty() || !txtCash.getText().toString().equals("")) {
                    try {
                        cash = Double.parseDouble(txtCash.getText().toString());
                    } catch (NumberFormatException e) {
                        Log.i("Num exception", e.toString());
                    }
                }
                double totalAmount1 = Double.parseDouble(tViewNetInvoiceAmount.getText().toString());
                double deductingValue = chequeValue + cash;
                totalAmount1 -= deductingValue;
                txtCredit.setText(String.format("%.2f", totalAmount1));
            }
        });

        txtDiscountValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub


                if (!txtDiscountValue.getText().toString().isEmpty()) {
                    double discountValue = Double.parseDouble(txtDiscountValue.getText().toString());
                    double totalAmount = Double.parseDouble(totalPrice);
                    double marketRet = 0;
                    try {
                        marketRet = Double.parseDouble(marketReturns);
                    } catch (Exception e) {
                        Log.w("Error from market ret", e.toString());
                    }

                    if (!(discountValue == 0)) {



                        if (discountValue < totalAmount) {
                            double discountPercentage = 100 - (((totalAmount - discountValue) / totalAmount) * 100);
                            txtDiscountPercentage.setText(String.format("%.2f", discountPercentage));
                            discount = txtDiscountPercentage.getText().toString();
                            double needToPayAmount = totalAmount - (discountValue);
                            tViewNetInvoiceAmount.setText(String.format("%.2f", needToPayAmount));
                            double netPayable = totalAmount - (discountValue + marketRet);
                            tViewNetPayable.setText(String.format("%.2f", netPayable));

                            txtCredit.setText(String.format("%.2f", netPayable));
                        } else {
                            txtDiscountValue.setText("0");
//							txtDiscountValue.requestFocus();
                            Toast discountPercentageMin = Toast.makeText(getApplication(), "Discount Value cannot be greater than Total!", Toast.LENGTH_SHORT);
                            discountPercentageMin.show();
                        }

                    }
                }


            }
        });

        btnDiscard.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub


                if (isInvoiceOption1) {
                    startInvoiceGen1 = new Intent("com.HesperusMarketing.channelbridge.INVOICEGEN1ACTIVITY");
                } else {
                    startInvoiceGen1 = new Intent("com.HesperusMarketing.channelbridge.INVOICEGEN1ALTERNATE");
                }
                Bundle bundleToView = new Bundle();
                bundleToView.putString("Id", rowId);
                bundleToView.putString("PharmacyId", pharmacyId);

                if (chequeEnabled) {
                    bundleToView.putString("ChequeNumber", chequeNumber);
                    bundleToView.putString("CollectionDate", collectionDate);
                    bundleToView.putString("ReleaseDate", releaseDate);
                }
                bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                bundleToView.putString("startTime", startTime);

                bundleToView.putString("InvoiceNumber", invoiceNumber);
                startInvoiceGen1.putExtras(bundleToView);
                finish();
                startActivity(startInvoiceGen1);


            }
        });

        btnGenerateInvoice.setOnClickListener(new View.OnClickListener() {

                                                  public void onClick(View v) {
                                                      // TODO Auto-generated method stub
                                                      if (creditDuration.equals("")) {
                                                          Toast t = Toast.makeText(getApplicationContext(), "Please select credit duration", Toast.LENGTH_SHORT);
                                                          t.show();

                                                      } else {

                                                          double outStanding = customersHandler.GetOustand_value(pharmacyId);
                                                          String sCredit = txtCredit.getText().toString();
                                                          if (sCredit.isEmpty()) {
                                                              sCredit = "0";
                                                          }
                                                          double dCredit = Double.parseDouble(sCredit);
                                                          double totalOutStanding = outStanding + dCredit;
                                                          double creditLimit = 0.0;
                                                          //customersHandler.getCreditLimit(pharmacyId);
                                                          customersHandler.openReadableDatabase();
                                                          String[] selectedCustomer = customersHandler.getCustomerDetailsByPharmacyId(pharmacyId);
                                                          customersHandler.closeDatabase();


                                                          creditLimit = Double.parseDouble(selectedCustomer[14]);

                                                          if (totalOutStanding > creditLimit) {
                                                              AlertDialog.Builder creditAlert = new AlertDialog.Builder(InvoiceGen2Activity.this);
                                                              creditAlert.setTitle("Alert");
                                                              creditAlert.setMessage("Credit limit exceeded.Do you want to continue?");

                                                              creditAlert.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                                                  @Override
                                                                  public void onClick(DialogInterface dialog, int which) {
                                                                      if (isBlockerActivated == false) {
                                                                          dialog.dismiss();
                                                                          gotoGen3();
                                                                      } else {
                                                                          dialog.dismiss();
                                                                          Toast blockerToast = Toast.makeText(InvoiceGen2Activity.this, "You don't have permission to proceed", Toast.LENGTH_LONG);
                                                                          blockerToast.setGravity(Gravity.CENTER, 0, 0);
                                                                          blockerToast.show();
                                                                      }
                                                                  }
                                                              });

                                                              creditAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                  @Override
                                                                  public void onClick(DialogInterface dialog, int which) {
                                                                      dialog.dismiss();
                                                                  }
                                                              });
                                                              creditAlert.show();

                                                          } else {
                                                              gotoGen3();
                                                          }
                                                      }
                                                  }
                                              }

        );

        btnReturns.setOnClickListener(new View.OnClickListener()

                                      {

                                          public void onClick(View v) {
                                              // TODO Auto-generated method stub


                                              discount = txtDiscountPercentage.getText().toString();
                                              AlertDialog.Builder returnPopup = new AlertDialog.Builder(InvoiceGen2Activity.this);
                                              returnPopup.setMessage("Where do you want to go?")
                                                      .setPositiveButton("Return with history validation", new DialogInterface.OnClickListener() {
                                                          public void onClick(DialogInterface dialog, int id) {
                                                              Intent startProductReturns = new Intent(getApplication(), ReturnProductHistoryActivity.class);
                                                              Bundle bundleToView = new Bundle();
                                                              bundleToView.putString("Id", rowId);
                                                              bundleToView.putString("PharmacyId", pharmacyId);
                                                              bundleToView.putString("Cash", cash);
                                                              bundleToView.putString("Credit", credit);
                                                              bundleToView.putString("onTimeOrNot", "" + 0);
                                                              bundleToView.putString("MarketReturns", marketReturns);
                                                              bundleToView.putString("Discount", discount);
                                                              bundleToView.putString("Cheque", cheque);

                                                              if (chequeEnabled) {
                                                                  bundleToView.putString("ChequeNumber", chequeNumber);
                                                                  bundleToView.putString("CollectionDate", collectionDate);
                                                                  bundleToView.putString("ReleaseDate", releaseDate);
                                                              }
                                                              bundleToView.putString("NeedToPay", needToPay);
                                                              bundleToView.putString("PaymentOption", paymentOption);
                                                              bundleToView.putString("TotalPrice", totalPrice);
                                                              bundleToView.putString("TotalQuantity", totalQty);
                                                              bundleToView.putString("InvoiceNumber", invoiceNumber);
                                                              bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                                                              bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                                                              startProductReturns.putExtras(bundleToView);
                                                              finish();
                                                              startActivity(startProductReturns);
                                                          }
                                                      })
                                                      .setNegativeButton("Return without validation", new DialogInterface.OnClickListener() {
                                                          public void onClick(DialogInterface dialog, int id) {
                                                              SharedPreferences preferences = PreferenceManager
                                                                      .getDefaultSharedPreferences(getBaseContext());
                                                              boolean history = preferences.getBoolean("cbPrefEnableNoHistoryReturns", true);
                                                              if (history) {
                                                                  Intent startProductReturns = new Intent(getApplication(), ReturnProductNoHistoryActivity.class);
                                                                  Bundle bundleToView = new Bundle();
                                                                  bundleToView.putString("Id", rowId);
                                                                  bundleToView.putString("PharmacyId", pharmacyId);
                                                                  bundleToView.putString("onTimeOrNot", "" + 0);
                                                                  bundleToView.putString("Cash", cash);
                                                                  bundleToView.putString("Credit", credit);
                                                                  bundleToView.putString("MarketReturns", marketReturns);
                                                                  bundleToView.putString("Discount", discount);
                                                                  bundleToView.putString("Cheque", cheque);

                                                                  if (chequeEnabled) {
                                                                      bundleToView.putString("ChequeNumber", chequeNumber);
                                                                      bundleToView.putString("CollectionDate", collectionDate);
                                                                      bundleToView.putString("ReleaseDate", releaseDate);
                                                                  }
                                                                  bundleToView.putString("NeedToPay", needToPay);
                                                                  bundleToView.putString("PaymentOption", paymentOption);
                                                                  bundleToView.putString("TotalPrice", totalPrice);
                                                                  bundleToView.putString("TotalQuantity", totalQty);
                                                                  bundleToView.putString("InvoiceNumber", invoiceNumber);
                                                                  bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                                                                  bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                                                                  startProductReturns.putExtras(bundleToView);
                                                                  finish();
                                                                  startActivity(startProductReturns);
                                                              } else {
                                                                  Toast featureNotEnabled = Toast.makeText(InvoiceGen2Activity.this, "Sorry, this feature has not been enabled", Toast.LENGTH_SHORT);
                                                                  featureNotEnabled.setGravity(Gravity.TOP, 100, 100);
                                                                  featureNotEnabled.show();
                                                              }
                                                          }
                                                      });
                                              returnPopup.create();
                                              returnPopup.show();


                                          }
                                      }

        );

        iBtnChequeDetails.setOnClickListener(new OnClickListener() {
                                                 public void onClick(View v) {
                                                     // TODO Auto-generated method stub


                                                     chequeDetailsPopup.setCanceledOnTouchOutside(false);

                                                     final EditText txtChequeAmount = (EditText) chequeDetailsPopup.findViewById(R.id.etChequeAmount);
                                                     final EditText txtChequeNumber = (EditText) chequeDetailsPopup.findViewById(R.id.etChequeNumber);
                                                     final EditText txtRefNo = (EditText) chequeDetailsPopup.findViewById(R.id.edRefNo);
                                                     final ImageButton imgCsmera = (ImageButton) chequeDetailsPopup.findViewById(R.id.btnCamera);
                                                     imgCsmera.setOnClickListener(new OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {
                                                             Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                                             startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                                         }
                                                     });
                                                     tViewCollectedDate = (TextView) chequeDetailsPopup.findViewById(R.id.tvCollectedDate);
                                                     tViewRealizedDate = (TextView) chequeDetailsPopup.findViewById(R.id.tvRealizeDate);
                                                     spBank = (AutoCompleteTextView) chequeDetailsPopup.findViewById(R.id.spBank);
                                                     spBranch = (AutoCompleteTextView) chequeDetailsPopup.findViewById(R.id.spBranch);
                                                     bankList = bankController.getBankList();
                                                     branchList = branchController.getBranchList();

                                                     ArrayAdapter<String> bankListAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.single_list_item, bankList);
                                                     ArrayAdapter<String> branchListAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.single_list_item, branchList);
                                                     // bankListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                                     spBank.setAdapter(bankListAdapter);
                                                     spBank.setSelection(selectedBankIndex);
                                                     spBank.setOnItemSelectedListener(new OnItemSelectedListener() {
                                                         @Override
                                                         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                             selectedBankIndex = position;
                                                         }

                                                         @Override
                                                         public void onNothingSelected(AdapterView<?> parent) {

                                                         }
                                                     });
                                                     spBranch.setAdapter(branchListAdapter);
                                                     spBranch.setSelection(selectedBranchIndex);
                                                     spBranch.setOnItemSelectedListener(new OnItemSelectedListener() {
                                                         @Override
                                                         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                             selectedBranchIndex = position;
                                                            // selectedBranch = spBranch.getSelectedItem().toString();
                                                             branchCode = branchController.GetBranchCode(selectedBranch);
                                                         }

                                                         @Override
                                                         public void onNothingSelected(AdapterView<?> parent) {

                                                         }
                                                     });

                                                     ImageButton iBtnCollectedDate = (ImageButton) chequeDetailsPopup.findViewById(R.id.bCalendarCollectedDate);
                                                     ImageButton iBtnRealizeDate = (ImageButton) chequeDetailsPopup.findViewById(R.id.bCalendarRealizeDate);
                                                     Button btnDoneChequePopUp = (Button) chequeDetailsPopup.findViewById(R.id.bDone);
                                                     Button btnCancelChequePopup = (Button) chequeDetailsPopup.findViewById(R.id.bCancel);

                                                     final Calendar c = Calendar.getInstance();
                                                     collectedYear = c.get(Calendar.YEAR);
                                                     collectedMonth = c.get(Calendar.MONTH);
                                                     collectedDay = c.get(Calendar.DAY_OF_MONTH);
                                                     realizeYear = c.get(Calendar.YEAR);
                                                     realizeMonth = c.get(Calendar.MONTH);
                                                     realizeDay = c.get(Calendar.DAY_OF_MONTH);


                                                     // display the current date
                                                     updateCollectedDateDisplay();
                                                     updateRealizeDateDisplay();

                                                     try {
                                                         if ((!cheque.isEmpty()) && (cheque != "")) {
                                                             txtChequeAmount.setText(cheque);
                                                         }

                                                         if ((!chequeNumber.isEmpty()) && (chequeNumber != "")) {
                                                             txtChequeNumber.setText(chequeNumber);
                                                         }

                                                         if ((!collectionDate.isEmpty()) && (collectionDate != "")) {
                                                             tViewCollectedDate.setText(collectionDate);

                                                             collectedDay = Integer.parseInt(collectionDate.substring(0, 2));
                                                             collectedMonth = Integer.parseInt(collectionDate.substring(3, 5));
                                                             collectedYear = Integer.parseInt(collectionDate.substring(6, collectionDate.length()));
                                                         }

                                                         if ((!releaseDate.isEmpty()) && (releaseDate != "")) {
                                                             tViewRealizedDate.setText(releaseDate);

                                                             realizeDay = Integer.parseInt(collectionDate.substring(0, 2));
                                                             realizeMonth = Integer.parseInt(collectionDate.substring(3, 5));
                                                             realizeYear = Integer.parseInt(collectionDate.substring(6, collectionDate.length()));
                                                         }
                                                     } catch (Exception e) {
                                                         Log.e("unable to load popup data", e.toString());
                                                     }


                                                     iBtnCollectedDate.setOnClickListener(new OnClickListener() {

                                                         public void onClick(View v) {
                                                             // TODO Auto-generated method stub
                                                             showDialog(COLLECTED_DATE);
                                                         }
                                                     });


                                                     iBtnRealizeDate.setOnClickListener(new OnClickListener() {

                                                         public void onClick(View v) {
                                                             // TODO Auto-generated method stub
                                                             showDialog(REALIZE_DATE);
                                                         }
                                                     });


                                                     btnDoneChequePopUp.setOnClickListener(new View.OnClickListener() {

                                                         public void onClick(View v) {
                                                             // TODO Auto-generated method stub

                                                             if (txtChequeAmount.getText().toString().isEmpty()) {
                                                                 Toast chequeAmountEmpty = Toast.makeText(InvoiceGen2Activity.this, "You have to enter the cheque amount!", Toast.LENGTH_SHORT);
                                                                 chequeAmountEmpty.setGravity(Gravity.TOP, 100, 100);
                                                                 chequeAmountEmpty.show();
                                                             } else if (txtChequeNumber.getText().toString().isEmpty()) {
                                                                 Toast chequeNumberEmpty = Toast.makeText(InvoiceGen2Activity.this, "You have to enter the cheque number!", Toast.LENGTH_SHORT);
                                                                 chequeNumberEmpty.setGravity(Gravity.TOP, 100, 100);
                                                                 chequeNumberEmpty.show();
                                                             } else if (txtRefNo.getText().toString().isEmpty()) {
                                                                 Toast chequeNumberEmpty = Toast.makeText(InvoiceGen2Activity.this, "You have to enter the reference number!", Toast.LENGTH_SHORT);
                                                                 chequeNumberEmpty.setGravity(Gravity.TOP, 100, 100);
                                                                 chequeNumberEmpty.show();

                                                             } else {

                                                                 cheque = txtChequeAmount.getText().toString();
                                                                 if (cheque.isEmpty()) {
                                                                     cheque = "0";
                                                                 }
                                                                 chequeNumber = txtChequeNumber.getText().toString();
                                                                 collectionDate = tViewCollectedDate.getText().toString();
                                                                 releaseDate = tViewRealizedDate.getText().toString();
                                                                 referenceNumber = txtRefNo.getText().toString();
                                                                 txtCheque.setText(cheque);
                                                                // selectedBank = spBank.getSelectedItem().toString();
                                                               //  selectedBranch = spBranch.getSelectedItem().toString();
                                                                 Log.w("c day", collectionDate.substring(0, 2));
                                                                 Log.w("c month", collectionDate.substring(3, 5));
                                                                 Log.w("c year", collectionDate.substring(6, collectionDate.length()));

                                                                 Log.w("r day", releaseDate.substring(0, 2));
                                                                 Log.w("r month", releaseDate.substring(3, 5));
                                                                 Log.w("r year", releaseDate.substring(6, releaseDate.length()));

                                                                 chequeDetailsPopup.dismiss();
                                                             }

                                                         }

                                                     });

                                                     btnCancelChequePopup.setOnClickListener(new OnClickListener() {

                                                         public void onClick(View v) {
                                                             // TODO Auto-generated method stub
                                                             chequeDetailsPopup.dismiss();
                                                         }
                                                     });


                                                     chequeDetailsPopup.show();

                                                 }
                                             }

        );

        spinCreditPeriod.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub

                try {
                    creditDuration = spinCreditPeriod.getSelectedItem().toString();
                    selectedCreditPeriodIndex = arg2;

                } catch (Exception e) {
                    Log.e("InvoiceGen2: error getting data from spinCreditPeriod ", e.toString());
                }


            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case COLLECTED_DATE:
                return new DatePickerDialog(this,
                        collectedDateSetListener,
                        collectedYear, collectedMonth, collectedDay);
            case REALIZE_DATE:
                return new DatePickerDialog(this,
                        realizeDateSetListener,
                        realizeYear, realizeMonth, realizeDay);
        }
        return null;
    }

    private void updateCollectedDateDisplay() {

        try {

            String finalCollectedMonth = "";
            String finalCollectedDay = "";
            if (collectedMonth + 1 < 10) {
                finalCollectedMonth = "0" + String.valueOf(collectedMonth + 1);
            } else {
                finalCollectedMonth = String.valueOf(collectedMonth + 1);
            }

            if (collectedDay < 10) {
                finalCollectedDay = "0" + String.valueOf(collectedDay);
            } else {
                finalCollectedDay = String.valueOf(collectedDay);
            }

            tViewCollectedDate.setText(new StringBuilder()
                    // Month is 0 based so btnAdd 1
                    .append(finalCollectedMonth).append("-").append(finalCollectedDay).append("-")
                    .append(collectedYear).append(""));
        } catch (Exception e) {
            Log.e("InvoiceGen2: error getting data from setInitialData ", e.toString());

        }


    }

    private void updateRealizeDateDisplay() {

        try {
            String finalRealizeMonth = "";
            String finalRealizeDay = "";
            if (realizeMonth + 1 < 10) {
                finalRealizeMonth = "0" + String.valueOf(realizeMonth + 1);
            } else {
                finalRealizeMonth = String.valueOf(realizeMonth + 1);
            }

            if (realizeDay < 10) {
                finalRealizeDay = "0" + String.valueOf(realizeDay);
            } else {
                finalRealizeDay = String.valueOf(realizeDay);
            }

            tViewRealizedDate.setText(new StringBuilder()
                    // Month is 0 based so btnAdd 1
                    .append(finalRealizeMonth).append("-").append(finalRealizeDay).append("-")
                    .append(realizeYear).append(""));

        } catch (Exception e) {
            Log.e("InvoiceGen2: error getting data from updateRealizeDateDisplay ", e.toString());
        }


    }

    private void setCreditSpinner(ArrayList<String> crList) {
        // TODO Auto-generated method stub

        try {

            ArrayAdapter<String> creditListAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_dropdown_item_1line, crList);
            creditListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinCreditPeriod.setAdapter(creditListAdapter);
            spinCreditPeriod.setSelection(selectedCreditPeriodIndex);
        } catch (Exception e) {
            Log.e("InvoiceGen2: error getting data from setCreditSpinner ", e.toString());

        }


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if (invoiceOption2Enable == true) {
                startInvoiceGen1 = new Intent("com.HesperusMarketing.channelbridge.INVOICEGEN1ALTERNATE");
            } else {
                startInvoiceGen1 = new Intent("com.HesperusMarketing.channelbridge.INVOICEGEN1ACTIVITY");
            }
            Bundle bundleToView = new Bundle();
            bundleToView.putString("Id", rowId);
            bundleToView.putString("PharmacyId", pharmacyId);
            bundleToView.putString("startTime", startTime);


            if (chequeEnabled) {
                bundleToView.putString("ChequeNumber", chequeNumber);
                bundleToView.putString("CollectionDate", collectionDate);
                bundleToView.putString("ReleaseDate", releaseDate);
            }
            bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
            bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
            startInvoiceGen1.putExtras(bundleToView);
            finish();
            startActivity(startInvoiceGen1);
        }
        return super.onKeyDown(keyCode, event);


    }

    private void setInitialData(String rId, String pId) {
        // TODO Auto-generated method stub


        try {


            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            chequeEnabled = preferences.getBoolean("cbPrefEnableCheckDetails", true);

            if (chequeEnabled) {
                iBtnChequeDetails.setEnabled(true);
                txtCheque.setEnabled(false);
                txtCheque.setHint("");
                iBtnChequeDetails.setVisibility(View.VISIBLE);
            } else {
                iBtnChequeDetails.setEnabled(false);
                txtCheque.setEnabled(true);
                txtCheque.setHint(R.string.cheque_amount);
                iBtnChequeDetails.setVisibility(View.GONE);
            }


            Customers customersObject = new Customers(this);
            customersObject.openReadableDatabase();
            customerDetails = customersObject.getCustomerDetailsByPharmacyId(pharmacyId);
            customersObject.closeDatabase();

/**
 * may be need to enabled
 */
//            creditList[0] = customerDetails[17];
//            if ((!creditDuration.isEmpty()) && (creditDuration != "")) {
//                for (int i = 0; i < creditList.size(); i++) {
//                    if (creditDuration.contentEquals(creditList[i])) {
//                        spinCreditPeriod.setSelection(i);
//                    }
//                }
//            }


            //0 - rowid //1 - pharmacyId //2 - pharmacyCode //3 - dealerId //4 - companyCode //5 - customerName //6 - address //7 - area //8 - town //9 - district
            //10 - telephone //11 - fax //12 - email //13 - customerStatus //14 - creditLimit //16 - currentCredit //16 - creditExpiryDate //17 - creditDuration
            //18 - vatNo //19 - status

            tViewCustomerName.setText(customerDetails[5]);
            double totalP = 0.0;
            int totalQ = 0;
            Sequence sequence = new Sequence(this);

            sequence.openReadableDatabase();
            String lastInv = sequence.getLastRowId("invoice");
            sequence.closeDatabase();




            String invNum = String.valueOf(Integer.parseInt(lastInv) + 1);

            tViewInvoiceNo.setText(invNum);
            this.invoiceNumber = invNum;

            if ((!cheque.isEmpty()) || (cheque != "")) {
                txtCheque.setText(cheque);
            }


            for (SelectedProduct selectedProduct : selectedProductsArray) {
                double normalP = 0.0;
                double discounted = 0.0;

                if ((selectedProduct.getDiscount() == 0) || (selectedProduct.getDiscount() == 0.0)) {
                    normalP = (selectedProduct.getNormal() * selectedProduct.getPrice());
                    discounted = normalP;
                } else {
                    Products pro = new Products(this);
                    pro.openReadableDatabase();

                    double reteilsPrice =Double.parseDouble(pro.getRetailsPrice(selectedProduct.getProductCode()));
                    normalP = (selectedProduct.getNormal() * reteilsPrice);
                    discounted = normalP * ((100 - selectedProduct.getDiscount()) / 100);
                }

                totalP = totalP + discounted;
                totalPrice = String.format("%.2f", totalP);

            }


            tViewInvoiceGross.setText(String.format("%.2f", totalP));

            for (SelectedProduct selectedProduct : selectedProductsArray) {
                int normalQ = 0;
                normalQ = selectedProduct.getNormal() + selectedProduct.getFree();
                totalQ = totalQ + normalQ;
                totalQty = String.valueOf(totalQ);

            }

            tViewInvoiceQuantity.setText(String.valueOf(totalQ));
            tViewNetPayable.setText(String.format("%.2f", totalP));
            totalQuantity = totalQ;

            double marketRet = 0.0;
            if (!returnProductsArray.isEmpty()) {
                for (ReturnProduct retProduct : returnProductsArray) {

                    Log.w("Return Product: 456", "getDiscount : " + retProduct.getDiscount());
                    Log.w("Return Product: 456", "getQuantity : " + retProduct.getQuantity());
                    Log.w("Return Product: 456", "getUnitPrice : " + retProduct.getUnitPrice());

                    double value = 0.0;
                    if (retProduct.getDiscount() > 0) {
                        double normalValue = retProduct.getQuantity() * retProduct.getUnitPrice();
                        value = normalValue - ((normalValue / 100) * retProduct.getDiscount());
                    } else {
                        value = retProduct.getQuantity() * retProduct.getUnitPrice();
                    }

                    marketRet = marketRet + value;
                }
            } else {
                marketRet = 0.0;
            }

            Log.w("Return Product: 456", "marketRet : " + marketRet);

            marketReturns = String.valueOf(String.format("%.2f", marketRet));
            double totalAmt = Double.parseDouble(totalPrice);
            double needToPayAmt = totalAmt - marketRet;

            tViewReturnsGross.setText(marketReturns);
            tViewNetReturnAmount.setText(marketReturns);
            tViewNetPayable.setText(String.format("%.2f", needToPayAmt));

            txtCredit.setText(String.format("%.2f", needToPayAmt));
            txtCredit.selectAll();

            tViewReturnsDiscount.setText("0");


            tViewNetInvoiceAmount.setText(String.format("%.2f", totalP));

            for (SelectedProduct sProduct : selectedProductsArray) {
                if (sProduct.getDiscount() != 0) {
                    discountEditable = false;
                }
            }

            if (discountEditable || discountEntered) {
                txtDiscountValue.setEnabled(true);
                txtDiscountPercentage.setEnabled(true);


                if (!discount.isEmpty()) {
                    double discountPercentage = 0;
                    if (!txtDiscountPercentage.getText().toString().isEmpty()) {
                        discountPercentage = Double.parseDouble(txtDiscountPercentage.getText().toString());
                    } else {
                        discountPercentage = 0;
                    }
                    double totalAmount = Double.parseDouble(totalPrice);
                    double marketRetn = 0;
                    discount = txtDiscountPercentage.getText().toString();
                    try {
                        marketRetn = Double.parseDouble(marketReturns);
                    } catch (Exception e) {
                        Log.w("Error from market ret", e.toString());
                    }
                    if (!(discountPercentage > 100)) {
                        if (!(discountPercentage < 0)) {
                            double discountValue = (discountPercentage / 100) * totalAmount;
                            txtDiscountValue.setText(String.format("%.2f", discountValue));
                            double needToPayAmount = totalAmount - (discountValue);
                            tViewNetInvoiceAmount.setText(String.format("%.2f", needToPayAmount));
                            double netPayable = totalAmount - (discountValue + marketRetn);
                            tViewNetPayable.setText(String.format("%.2f", netPayable));

                            txtCredit.setText(String.format("%.2f", netPayable));
                        } else {
                            Toast discountPercentageMin = Toast.makeText(getApplication(), "Discount Percentage cannot be less than 0!", Toast.LENGTH_SHORT);
                            discountPercentageMin.show();
                        }
                    } else {
                        Toast discountPercentageMax = Toast.makeText(getApplication(), "Discount Percentage cannot be greater than 100!", Toast.LENGTH_SHORT);
                        discountPercentageMax.show();
                    }

                } else {
                    txtDiscountValue.setText("");
                }


            } else {
                txtDiscountValue.setEnabled(false);
                txtDiscountPercentage.setEnabled(false);
            }
        } catch (Exception e) {
            Log.e("InvoiceGen2: error getting data from setInitialData ", e.toString());

        }


    }

    private void getDataFromPreviousActivity(Bundle extras) {
        // TODO Auto-generated method stub

        // try {
        referenceNumber = extras.getString("referenceNumber");
        rowId = extras.getString("Id");
        pharmacyId = extras.getString("PharmacyId");
        selectedProductsArray = extras.getParcelableArrayList("SelectedProducts");
        selectedCreditPeriodIndex = extras.getInt("selectedCreditIndex");
        selectedBranchIndex = extras.getInt("selectedBranchIndex");
        selectedBankIndex = extras.getInt("selectedBankIndex");
        startTime = extras.getString("startTime");
        discountVal=extras.getString("discountval");
       // tViewReturnsDiscount.setText(extras.getString("discountval"));
      //  tViewReturnsDiscount.setText("025");
        //On Log.i("time gen2 -e->",startTime);
        if (extras.containsKey("Cash")) {
            cash = extras.getString("Cash");
        }

        if (extras.containsKey("Cheque")) {
            cheque = extras.getString("Cheque");
        }

        if (extras.containsKey("ReturnProducts")) {
            returnProductsArray = extras.getParcelableArrayList("ReturnProducts");
        }
        if (extras.containsKey("CreditDuration")) {
            creditDuration = extras.getString("CreditDuration");
        }


        if (extras.containsKey("Discount")) {
            txtDiscountPercentage.setText(extras.getString("Discount"));
            txtDiscountPercentage.setFocusable(true);
            txtDiscountPercentage.requestFocus();
            txtCredit.setFocusable(true);
            txtCredit.requestFocus();
            Log.w("discount UIPO((B$%&%976", String.valueOf(extras.containsKey("Discount")));
        }

        if (extras.containsKey("DiscountEntered")) {
            discountEntered = extras.getBoolean("DiscountEntered");
        }


        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        chequeEnabled = preferences.getBoolean("cbPrefEnableCheckDetails", true);


        if (chequeEnabled) {
            if (extras.containsKey("ChequeNumber")) {
                chequeNumber = extras.getString("ChequeNumber");
            }
            if (extras.containsKey("CollectionDate")) {
                collectionDate = extras.getString("CollectionDate");
            }
            if (extras.containsKey("ReleaseDate")) {
                releaseDate = extras.getString("ReleaseDate");
            }

            Log.w("cheque details", chequeNumber + " # " + collectionDate + " # " + releaseDate);
        }


        setInitialData(rowId, pharmacyId);


        Log.w("invoicegen2", "selectedProductsArray size : " + selectedProductsArray.size());
        Log.w("invoicegen2", "returnProductArray size : " + returnProductsArray.size());
//        } catch (Exception e) {
//            Log.e("InvoiceGen2: error getting data from prev activity ", e.toString());
//        }

    }

    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);


        try {

            outState.putString("Id", rowId);
            outState.putString("PharmacyId", pharmacyId);
            outState.putString("Cash", cash);
            outState.putString("Credit", credit);
            outState.putString("Cheque", cheque);
            outState.putString("startTime", startTime);
            outState.putString("disountValue", disountValue);
            Log.w("CHECK VALUE $$$$$$$$$$$###############", cheque);
            outState.putString("MarketReturns", marketReturns);
            outState.putString("Discount", discount);
            outState.putString("NeedToPay", needToPay);
            outState.putString("CreditDuration", creditDuration);
            outState.putString("PaymentOption", paymentOption);
            outState.putString("TotalPrice", totalPrice);
            outState.putString("TotalQuantity", totalQty);
            outState.putString("InvoiceNumber", invoiceNumber);
            outState.putBoolean("DiscountEntered", discountEntered);
            outState.putInt("selectedCreditIndex", selectedCreditPeriodIndex);
            outState.putParcelableArrayList("SelectedProducts", selectedProductsArray);
            outState.putParcelableArrayList("ReturnProducts", returnProductsArray);
            outState.putInt("selectedBranchIndex", selectedBranchIndex);
            outState.putInt("selectedBankIndex", selectedBankIndex);
            if (chequeEnabled) {
                outState.putString("ChequeNumber", chequeNumber);
                outState.putString("CollectionDate", collectionDate);
                outState.putString("ReleaseDate", releaseDate);
            }


        } catch (Exception e) {
            Log.e("InvoiceGen2: error getting data from setInitialData ", e.toString());
        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            chequeimage = getBytes(photo);
            // Im.setImageBitmap(photo);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        spinCreditPeriod.setSelection(selectedCreditPeriodIndex);
        txtCash.setText(cash);
        txtCheque.setText(cheque);
        // txtDiscountPercentage.setText(discount);


    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }


    private void gotoGen3() {


        Intent startInvoiceGen3 = new Intent("com.HesperusMarketing.channelbridge.INVOICEGEN3ACTIVITY");
        Bundle bundleToView = new Bundle();
        try {
            creditDuration = spinCreditPeriod.getSelectedItem().toString();
        } catch (Exception e) {
            creditDuration = "0";

        }

        cash = txtCash.getText().toString();
        cheque = txtCheque.getText().toString();
        credit = txtCredit.getText().toString();
        discount = txtDiscountPercentage.getText().toString();
        if (discount == null || discount.contentEquals("null") || discount.length() == 0) {
            discount = "0";
        }


        if (!txtDiscountPercentage.getText().toString().isEmpty()) {
            discountEntered = true;
        }
        if (!txtDiscountValue.getText().toString().equals("") || !txtDiscountValue.getText().toString().isEmpty()) {
            disountValue = txtDiscountValue.getText().toString();
        }

        needToPay = tViewNetPayable.getText().toString();
        if (cash.equals("")) {
            cash = "0";
        }
        if (cheque.equals("")) {
            cheque = "0";
        }
        double nuMericCash = Double.parseDouble(cash);
        double nuMericCheque = Double.parseDouble(cheque);
        if (nuMericCash > 0 && nuMericCheque > 0) {
            selectedInvoOption = "Cash+Cheque";
        } else if (nuMericCash > 0) {
            selectedInvoOption = "Cash";
        } else if (nuMericCheque > 0) {
            selectedInvoOption = "Cheque";
        }
        pt.openReadableDatabase();
        seletedPaymentOptionCode = pt.GetPyementtypeCode(selectedInvoOption);
        pt.closeDatabase();
        Log.w("IG3", "MarketReturns 333 " + marketReturns);

        bundleToView.putString("Id", rowId);
        bundleToView.putString("PharmacyId", pharmacyId);
        bundleToView.putString("Cash", cash);
        bundleToView.putString("Credit", credit);
        bundleToView.putString("Cheque", cheque);
        bundleToView.putString("onTimeOrNot", "" + 0);
        bundleToView.putString("MarketReturns", marketReturns);
        bundleToView.putString("Discount", discount);
        bundleToView.putString("NeedToPay", needToPay);
        bundleToView.putString("CreditDuration", creditDuration);
        bundleToView.putString("PaymentOption", paymentOption);
        bundleToView.putString("TotalPrice", totalPrice);
        bundleToView.putString("TotalQuantity", totalQty);
        bundleToView.putString("InvoiceNumber", invoiceNumber);
        bundleToView.putBoolean("DiscountEntered", discountEntered);
        bundleToView.putInt("selectedCreditIndex", selectedCreditPeriodIndex);
        bundleToView.putInt("selectedBranchIndex", selectedBranchIndex);
        bundleToView.putInt("selectedBankIndex", selectedBankIndex);
        bundleToView.putString("branchCode", branchCode);
        bundleToView.putByteArray("chequeimage", chequeimage);
        bundleToView.putString("dicountValue", txtDiscountValue.getText().toString());
        bundleToView.putInt("totalQuantity", totalQuantity);


        //btnAdd by thunder
        bundleToView.putString("BankName", selectedBank);
        bundleToView.putString("Branch", selectedBranch);
        if (referenceNumber == null || referenceNumber.isEmpty()) {
            referenceNumber = "";
        }
        bundleToView.putString("referenceNumber", referenceNumber);
        bundleToView.putBoolean("isCheckEntered", isCheckEntered);
        bundleToView.putString("selectedInvoOption", selectedInvoOption);
        bundleToView.putString("seletedPaymentOptionCode", seletedPaymentOptionCode);


        bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
        bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
        bundleToView.putString("startTime", startTime);
        if (chequeEnabled) {
            bundleToView.putString("ChequeNumber", chequeNumber);
            bundleToView.putString("CollectionDate", collectionDate);
            bundleToView.putString("ReleaseDate", releaseDate);

        }
        startInvoiceGen3.putExtras(bundleToView);
        startActivity(startInvoiceGen3);
        finish();

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        txtCredit.setText(savedInstanceState.getString("Credit"));
    }
}
