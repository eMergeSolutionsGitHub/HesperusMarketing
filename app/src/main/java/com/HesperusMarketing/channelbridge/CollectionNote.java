package com.HesperusMarketing.channelbridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.HesperusMarketing.channelbridgeaddapters.CollectionChequeAdapter;
import com.HesperusMarketing.channelbridgeaddapters.CollectionNoteAdapter;
import com.HesperusMarketing.channelbridgeaddapters.CollectionNoteList;
import com.HesperusMarketing.channelbridgeaddapters.ListCollectionCheque;
import com.HesperusMarketing.channelbridgebs.UploadCollectionNoteTask;
import com.HesperusMarketing.channelbridgedb.Branch;
import com.HesperusMarketing.channelbridgedb.CollectionNoteCheques;
import com.HesperusMarketing.channelbridgedb.CollectionNoteInvoice;
import com.HesperusMarketing.channelbridgedb.CollectionNoteSendToApprovel;
import com.HesperusMarketing.channelbridgedb.Customers;
import com.HesperusMarketing.channelbridgedb.DEL_Outstandiing;
import com.HesperusMarketing.channelbridgedb.Master_Banks;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CollectionNote extends Activity implements DatePickerDialog.OnDateSetListener {

    private static final int CAMERA_REQUEST = 1888;
    AutoCompleteTextView customerName;
    TextView textViewInvoiceCradite, textViewRealizedate, textCheqe;
    MaterialSpinner invoiceNumbersSp, cheqeNumberSp;
    RelativeLayout relativeLayoutCheque, calenderView, layoutChequeImage, btnSubmit;

    Button btnAdd;
    EditText editCash;
    boolean stsuts = false;
    String custmomerNumber, selectedInvoiceNum, selectChuqNumber, collectionNoteNumber;
    Double oustandingVal;
    CollectionNoteAdapter listAdapter;
    ImageView chequeimage;
    String[] returnDetails;
    String[] chqDetails;
    double balance = 0.0, cashBal = 0, cheqBal = 0, cashbalance = 0.0, cheqbalance = 0.0;
    ListView addedInvoiceList;
    List<String> CustomerNameList, InvoiesList;
    ArrayList<String[]> cheqeDetails;
    ArrayList<String[]> tempcheqeDetails;
    int selectedCheq = -1;
    ArrayList<String[]> tempreturnProducts;
    ArrayList<String[]> tempreturnChq;
    Bitmap photo;
    byte[] chequeimageByte;
    ArrayList<CollectionNoteList> listCollectionNoteItem = new ArrayList<CollectionNoteList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_note);


        customerName = (AutoCompleteTextView) findViewById(R.id.AutoComplete_CustomerName);
        invoiceNumbersSp = (MaterialSpinner) findViewById(R.id.spinnerInvoiceNum);
        textViewInvoiceCradite = (TextView) findViewById(R.id.textView_invoiceCradite);
        relativeLayoutCheque = (RelativeLayout) findViewById(R.id.relativeLayoutCheque);
        textCheqe = (TextView) findViewById(R.id.editText_cheqe);
        cheqeNumberSp = (MaterialSpinner) findViewById(R.id.spinnerChq);
        btnAdd = (Button) findViewById(R.id.button_add);
        editCash = (EditText) findViewById(R.id.editTextCash);
        addedInvoiceList = (ListView) findViewById(R.id.listViewCollecton);
        btnSubmit = (RelativeLayout) findViewById(R.id.relativeLayoutsubmit);

        CustomerNameList = new ArrayList();
        InvoiesList = new ArrayList();
        cheqeDetails = new ArrayList<String[]>();
        tempcheqeDetails = new ArrayList<String[]>();
        tempreturnProducts = new ArrayList<String[]>();
        tempreturnChq = new ArrayList<String[]>();

        listAdapter = new CollectionNoteAdapter(this, listCollectionNoteItem);
        getCustomers();

        customerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String selectCusName[] = arg0.getItemAtPosition(arg2).toString().split("-");
                customerName.setText(selectCusName[0]);
                custmomerNumber = selectCusName[1];
                getInvoiesFromCustmor(custmomerNumber);

            }
        });

        invoiceNumbersSp.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                selectedInvoiceNum = item;
                getOustandingValue(custmomerNumber, selectedInvoiceNum);

            }
        });

        cheqeNumberSp.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                selectChuqNumber = item;

            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnDetails = new String[7];

                boolean zrooChek = false;
                try {
                    int convertAmmount = 1 / Integer.parseInt(editCash.getText().toString().trim());
                } catch (ArithmeticException a) {
                    zrooChek = true;
                } catch (NumberFormatException n) {

                }

                String chequeAmmount[] = textCheqe.getText().toString().split(":");
                if (customerName.getText().toString().equals("")) {
                    Toast.makeText(CollectionNote.this, "Customer name is empty", Toast.LENGTH_LONG).show();
                    customerName.setHintTextColor(getResources().getColor(R.color.myRed));
                    customerName.setHint("Customer name is empty");
                } else if ((chequeAmmount[1].toString().trim().equals("0.0")) && (editCash.getText().toString().equals(""))) {
                    Toast.makeText(CollectionNote.this, "Need cash amount or cheque amount.", Toast.LENGTH_LONG).show();
                } else if (zrooChek == true) {
                    Toast.makeText(CollectionNote.this, "Please enter valid number to cash", Toast.LENGTH_LONG).show();
                } else if (selectedInvoiceNum == null) {
                    Toast.makeText(CollectionNote.this, "No Invoice Numbers ", Toast.LENGTH_LONG).show();
                } else if (!tempreturnProducts.isEmpty() && balance == 0) {
                    Toast.makeText(CollectionNote.this, "Your balance is 0 ", Toast.LENGTH_LONG).show();
                } else {

                    //cheque & cash
                    if ((!chequeAmmount[1].toString().trim().equals("0.0")) && (!editCash.getText().toString().equals(""))) {
                        if (tempreturnProducts.isEmpty()) {
                            balance = (Double.parseDouble(chequeAmmount[1].toString().trim()) + Double.parseDouble(editCash.getText().toString()) - Double.parseDouble(textViewInvoiceCradite.getText().toString()));
                            if (Double.parseDouble(textViewInvoiceCradite.getText().toString()) > Double.parseDouble(editCash.getText().toString())) {
                                stsuts = true;
                                cashbalance = Double.parseDouble(editCash.getText().toString());
                                cheqbalance = Double.parseDouble(textViewInvoiceCradite.getText().toString()) - Double.parseDouble(editCash.getText().toString());
                                if (balance < 0) {
                                    cheqbalance = Double.parseDouble(chequeAmmount[1].toString().trim());
                                } else {
                                }

                            } else {
                                cashbalance = Double.parseDouble(editCash.getText().toString()) - Double.parseDouble(textViewInvoiceCradite.getText().toString());
                            }

                        } else {
                            String[] rBal = tempreturnProducts.get(tempreturnProducts.size() - 1);
                            balance = Double.parseDouble(rBal[3]) - Double.parseDouble(textViewInvoiceCradite.getText().toString());

                            if (stsuts == true) {
                                cashbalance = 0.0;
                                cheqbalance = balance;
                                Double seChqAmount = Double.parseDouble(textViewInvoiceCradite.getText().toString());
                                for (int i = 0; i <= cheqeDetails.size(); i++) {
                                    String[] r;
                                    if (i == 0) {
                                        r = cheqeDetails.get(i);
                                    } else {
                                        r = cheqeDetails.get(i - 1);
                                    }
                                    if (seChqAmount < Double.parseDouble(r[0])) {
                                        chqDetails = new String[3];
                                        chqDetails[0] = selectedInvoiceNum;
                                        chqDetails[1] = r[1];
                                        tempreturnChq.add(chqDetails);

                                        break;
                                    } else {
                                        chqDetails = new String[3];
                                        chqDetails[0] = selectedInvoiceNum;
                                        chqDetails[1] = r[1];
                                        tempreturnChq.add(chqDetails);
                                        seChqAmount = seChqAmount - Double.parseDouble(r[0]);
                                        cheqeDetails.remove(i);
                                    }
                                }

                            } else {
                                cashbalance = Double.parseDouble(rBal[1]) - Double.parseDouble(textViewInvoiceCradite.getText().toString());
                                cheqbalance = (Double.parseDouble(chequeAmmount[1].toString().trim()));
                            }
                            if (balance < 0) {
                                if (stsuts == true) {
                                    cashbalance = 0.0;
                                    cheqbalance = Double.parseDouble(rBal[3]);
                                } else {
                                    cashbalance = Double.parseDouble(rBal[3]);
                                    cheqbalance = 0.0;
                                }
                            } else {

                            }
                        }
                        if (balance < 0) {
                            balance = 0;
                        } else {
                        }
                        if (tempreturnProducts.size() == 0) {
                            returnDetails[0] = selectedInvoiceNum;
                            returnDetails[1] = String.valueOf(cashbalance);
                            returnDetails[2] = String.valueOf(cheqbalance);
                            returnDetails[3] = String.valueOf(balance);
                            tempreturnProducts.add(returnDetails);
                            listCollectionNoteItem.add(new CollectionNoteList(selectedInvoiceNum, String.valueOf(cashbalance), String.valueOf(cheqbalance), String.valueOf(balance)));
                            addedInvoiceList.setAdapter(listAdapter);
                        } else {
                            String[] rBal = tempreturnProducts.get(tempreturnProducts.size() - 1);
                            int listaddedStutes = 0;
                            for (int i = 0; i < tempreturnProducts.size(); i++) {
                                String[] r = tempreturnProducts.get(i);
                                if ((r[0].contentEquals(selectedInvoiceNum))) {
                                    listaddedStutes = 1;
                                    balance = Double.parseDouble(rBal[3]) - Double.parseDouble(textViewInvoiceCradite.getText().toString());
                                    Toast.makeText(CollectionNote.this, "This Invoice number has already been added!", Toast.LENGTH_LONG).show();
                                    break;
                                } else {
                                    listaddedStutes = 0;
                                }

                            }
                            if (listaddedStutes == 0) {
                                String[] r = tempreturnProducts.get(tempreturnProducts.size() - 1);
                                returnDetails[0] = selectedInvoiceNum;
                                returnDetails[1] = String.valueOf(cashbalance);
                                returnDetails[2] = String.valueOf(cheqbalance);
                                returnDetails[3] = String.valueOf(balance);
                                tempreturnProducts.add(returnDetails);
                                listCollectionNoteItem.add(new CollectionNoteList(selectedInvoiceNum, returnDetails[1], returnDetails[2], returnDetails[3]));
                                addedInvoiceList.setAdapter(listAdapter);
                            } else {

                            }
                        }
//cheque
                    } else if (editCash.getText().toString().equals("")) {

                        String cheqAmmount = chequeAmmount[1].toString().trim();
                        if (tempreturnProducts.isEmpty()) {
                            balance = Double.parseDouble(chequeAmmount[1].toString().trim()) - Double.parseDouble(textViewInvoiceCradite.getText().toString());
                        } else {
                            String[] rBal = tempreturnProducts.get(tempreturnProducts.size() - 1);
                            balance = Double.parseDouble(rBal[3]) - Double.parseDouble(textViewInvoiceCradite.getText().toString());
                        }

                        if (balance < 0) {
                            balance = 0;
                        } else {

                        }
                        Double seChqAmount = Double.parseDouble(textViewInvoiceCradite.getText().toString());
                        for (int i = 0; i <= cheqeDetails.size(); i++) {
                            String[] r;
                            if (i == 0) {
                                r = cheqeDetails.get(i);
                            } else {
                                r = cheqeDetails.get(i - 1);
                            }
                            if (seChqAmount < Double.parseDouble(r[0])) {
                                chqDetails = new String[3];
                                chqDetails[0] = selectedInvoiceNum;
                                chqDetails[1] = r[1];
                                tempreturnChq.add(chqDetails);

                                break;
                            } else {
                                chqDetails = new String[3];
                                chqDetails[0] = selectedInvoiceNum;
                                chqDetails[1] = r[1];
                                tempreturnChq.add(chqDetails);
                                seChqAmount = seChqAmount - Double.parseDouble(r[0]);
                                cheqeDetails.remove(0);
                            }
                        }


                        if (tempreturnProducts.size() == 0) {

                            returnDetails[0] = selectedInvoiceNum;
                            returnDetails[1] = "0.0";
                            returnDetails[2] = chequeAmmount[1].toString().trim();
                            returnDetails[3] = String.valueOf(balance);

                            cheqBal = balance;
                            tempreturnProducts.add(returnDetails);
                            listCollectionNoteItem.add(new CollectionNoteList(selectedInvoiceNum, editCash.getText().toString(), cheqAmmount, String.valueOf(balance)));
                            addedInvoiceList.setAdapter(listAdapter);
                        } else {
                            String[] rBal = tempreturnProducts.get(tempreturnProducts.size() - 1);

                            int listaddedStutes = 0;
                            for (int i = 0; i < tempreturnProducts.size(); i++) {
                                String[] r = tempreturnProducts.get(i);
                                if ((r[0].contentEquals(selectedInvoiceNum))) {
                                    listaddedStutes = 1;
                                    balance = Double.parseDouble(rBal[3]) - Double.parseDouble(textViewInvoiceCradite.getText().toString());

                                    Toast.makeText(CollectionNote.this, "This Invoice number has already been added!", Toast.LENGTH_LONG).show();
                                    break;
                                } else {
                                    listaddedStutes = 0;
                                }

                            }
                            if (listaddedStutes == 0) {
                                String[] r = tempreturnProducts.get(tempreturnProducts.size() - 1);
                                returnDetails[0] = selectedInvoiceNum;
                                returnDetails[1] = String.valueOf(cashBal);
                                returnDetails[2] = String.valueOf(cheqBal);
                                returnDetails[3] = String.valueOf(balance);
                                cheqBal = balance;
                                tempreturnProducts.add(returnDetails);
                                listCollectionNoteItem.add(new CollectionNoteList(selectedInvoiceNum, returnDetails[1], returnDetails[2], returnDetails[3]));
                                addedInvoiceList.setAdapter(listAdapter);
                            } else {

                            }
                        }

                    }


                    //cash
                    else if (chequeAmmount[1].toString().trim().equals("0.0")) {
                        String cashAmmount;
                        cashAmmount = editCash.getText().toString();

                        if (tempreturnProducts.isEmpty()) {
                            balance = Double.parseDouble(cashAmmount) - Double.parseDouble(textViewInvoiceCradite.getText().toString());
                        } else {
                            String[] rBal = tempreturnProducts.get(tempreturnProducts.size() - 1);
                            balance = Double.parseDouble(rBal[3]) - Double.parseDouble(textViewInvoiceCradite.getText().toString());
                        }

                        if (balance < 0) {
                            balance = 0;
                        } else {
                        }
                        if (tempreturnProducts.size() == 0) {

                            returnDetails[0] = selectedInvoiceNum;
                            returnDetails[1] = editCash.getText().toString();
                            returnDetails[2] = chequeAmmount[1].toString();
                            returnDetails[3] = String.valueOf(balance);
                            cashBal = balance;
                            tempreturnProducts.add(returnDetails);
                            listCollectionNoteItem.add(new CollectionNoteList(selectedInvoiceNum, returnDetails[1], returnDetails[2], returnDetails[3]));
                            addedInvoiceList.setAdapter(listAdapter);
                        } else {
                            int listaddedStutes = 0;
                            String[] rBal = tempreturnProducts.get(tempreturnProducts.size() - 1);

                            for (int i = 0; i < tempreturnProducts.size(); i++) {
                                String[] r = tempreturnProducts.get(i);
                                if ((r[0].contentEquals(selectedInvoiceNum))) {
                                    listaddedStutes = 1;
                                    balance = Double.parseDouble(rBal[3]) - Double.parseDouble(textViewInvoiceCradite.getText().toString());
                                    Toast.makeText(CollectionNote.this, "This Invoice number has already been added!", Toast.LENGTH_LONG).show();
                                    break;
                                } else {
                                    listaddedStutes = 0;
                                }

                            }
                            if (listaddedStutes == 0) {
                                String[] r = tempreturnProducts.get(tempreturnProducts.size() - 1);
                                returnDetails[0] = selectedInvoiceNum;
                                returnDetails[1] = String.valueOf(cashBal);
                                returnDetails[2] = String.valueOf(cheqBal);
                                returnDetails[3] = String.valueOf(balance);
                                cashBal = balance;

                                tempreturnProducts.add(returnDetails);
                                listCollectionNoteItem.add(new CollectionNoteList(selectedInvoiceNum, returnDetails[1], returnDetails[2], returnDetails[3]));
                                addedInvoiceList.setAdapter(listAdapter);
                            } else {

                            }
                        }

                    }
                }
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CollectionNoteSendToApprovel cns = new CollectionNoteSendToApprovel(CollectionNote.this);
                cns.openWritableDatabase();

                final Customers customer = new Customers(CollectionNote.this);
                customer.openReadableDatabase();

                CollectionNoteInvoice cninvoice = new CollectionNoteInvoice(CollectionNote.this);
                cninvoice.openWritableDatabase();

                String paymentType = null;
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CollectionNote.this);
                String repId = sharedPreferences.getString("RepId", "-1");

                collectionNoteNumber = GenaterCollectionNoteNumber();

                String[] chqAmmount = textCheqe.getText().toString().split(":");
                double OutStand_value = 0;
                try {
                    OutStand_value = customer.GetOustand_value(custmomerNumber);
                } catch (Exception e) {

                }
                if (tempreturnProducts.isEmpty()) {
                    Toast.makeText(CollectionNote.this, "Empty Collection!", Toast.LENGTH_SHORT).show();
                } else {
                    cns.insertCollectionNoteSendToApprovel(collectionNoteNumber, repId, custmomerNumber,
                            String.valueOf(OutStand_value), editCash.getText().toString(), chqAmmount[1]);
                    cns.closeDatabase();
                    for (int i = 0; i < tempreturnProducts.size(); i++) {
                        String cNoteDetail[] = tempreturnProducts.get(i);
                        if (!cNoteDetail[1].trim().equals("0.0") && !cNoteDetail[2].trim().equals("0.0")) {
                            paymentType = "Cash+Cheque";
                        } else if (!cNoteDetail[1].equals("0.0")) {
                            paymentType = "Cash";
                        } else if (!cNoteDetail[2].equals("0.0")) {
                            paymentType = "Cheque";
                        }

                        cninvoice.insert_CollectionInvoice(collectionNoteNumber, cNoteDetail[0], paymentType, cNoteDetail[1], cNoteDetail[2], textViewInvoiceCradite.getText().toString(), cNoteDetail[3]);

                    }
                    CollectionNoteCheques noteCheques = new CollectionNoteCheques(CollectionNote.this);
                    noteCheques.openWritableDatabase();

                    int a = cheqeDetails.size();
                    for (String[] chaqeData : tempcheqeDetails) {
                        byte[] bytes;
                        try {
                            bytes = chaqeData[5].getBytes("UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            bytes = null;
                        }

                        String invnum = null;
                        for (int j = 0; j <tempreturnChq.size() ; j++) {
                            String c[] = tempreturnChq.get(j);
                            if(c[1].equals(chaqeData[1])) {
                                invnum=c[0];
                            }
                        }

                        noteCheques.insert_CollectionCheqes(collectionNoteNumber, chaqeData[1], chaqeData[0], chaqeData[2], chaqeData[3], chaqeData[4], bytes,invnum);

                    }
                    noteCheques.closeDatabase();

                    Toast.makeText(CollectionNote.this, "Collection note save successfully", Toast.LENGTH_SHORT).show();
                    cns.closeDatabase();
                    valueclear();

                    if (isNetworkAvailable() == true) {
                        upload();

                    } else {

                    }


                }


            }
        });

        relativeLayoutCheque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChequeDialog();
            }
        });
    }

    public void getCustomers() {
        final Customers customer = new Customers(CollectionNote.this);
        customer.openReadableDatabase();
        if (CustomerNameList != null) {
            CustomerNameList.clear();
        } else {
        }
        CustomerNameList = customer.getAllCustomerDetails();
        ArrayAdapter<String> customerAdapterList = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, CustomerNameList);
        customerName.setAdapter(customerAdapterList);
        customer.closeDatabase();
    }


    public void getInvoiesFromCustmor(String cutmoreID) {
        final DEL_Outstandiing oustanding = new DEL_Outstandiing(CollectionNote.this);
        oustanding.openReadableDatabase();
        if (InvoiesList != null) {
            InvoiesList.clear();
        } else {
        }
        InvoiesList = oustanding.loadInvoiceNumberFromCusID(cutmoreID);
        if (InvoiesList.size() != 0) {
            invoiceNumbersSp.setItems(InvoiesList);
        } else {
            Toast.makeText(CollectionNote.this, "No any outstanding for this Customer", Toast.LENGTH_LONG).show();
        }
        oustanding.closeDatabase();
        selectedInvoiceNum = InvoiesList.get(0);
        getOustandingValue(custmomerNumber, InvoiesList.get(0));

    }

    public void getOustandingValue(String cusNum, String invNumber) {
        final DEL_Outstandiing oustanding = new DEL_Outstandiing(CollectionNote.this);
        oustanding.openReadableDatabase();
        oustandingVal = Double.parseDouble(oustanding.getOustandingFromInvoiesNumber(cusNum, invNumber));
        textViewInvoiceCradite.setText(String.valueOf(oustandingVal));
        oustanding.closeDatabase();

    }


    private void showChequeDialog() {

        final Dialog dialogBox = new Dialog(CollectionNote.this);
        dialogBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBox.setContentView(R.layout.dialog_colection_note_cheque);
        dialogBox.setCancelable(false);

        List<String> bankList, branchList;

        calenderView = (RelativeLayout) dialogBox.findViewById(R.id.relativeLayout_Dialog_calender);
        textViewRealizedate = (TextView) dialogBox.findViewById(R.id.textViewRealizedate);
        final AutoCompleteTextView bankTextView = (AutoCompleteTextView) dialogBox.findViewById(R.id.editTextdilaog_bank);
        final AutoCompleteTextView edtBranch = (AutoCompleteTextView) dialogBox.findViewById(R.id.editTextdilaog_branch);
        ImageView btnClose = (ImageView) dialogBox.findViewById(R.id.imageViewClose);
        final EditText edtNumber = (EditText) dialogBox.findViewById(R.id.editTextdilaog_number);
        Button btnDone = (Button) dialogBox.findViewById(R.id.button_dialog_done);
        layoutChequeImage = (RelativeLayout) dialogBox.findViewById(R.id.relativeLayoutCheqeImage);
        textCheqe = (TextView) findViewById(R.id.editText_cheqe);
        final ListView cheqList = (ListView) dialogBox.findViewById(R.id.listViewChewues);
        final Button btnDelete = (Button) dialogBox.findViewById(R.id.buttonChange);
        chequeimage = (ImageView) dialogBox.findViewById(R.id.imageViewCheque);

        layoutChequeImage = (RelativeLayout) dialogBox.findViewById(R.id.relativeLayoutCheqeImage);

        final TextView bankCode = (TextView) dialogBox.findViewById(R.id.textView_bankcode);
        final TextView barnchCode = (TextView) dialogBox.findViewById(R.id.textView_barnchCode);
        final EditText editAmmount = (EditText) dialogBox.findViewById(R.id.editTextdilaog_ammount);

        final ArrayList<ListCollectionCheque> cheqLists = new ArrayList<>();

        bankList = new ArrayList<String>();
        branchList = new ArrayList<String>();


        final CollectionChequeAdapter cheqAdapter;

        Master_Banks banks = new Master_Banks(this);
        banks.openReadableDatabase();
        bankList = banks.GetBank();
        banks.closeDatabase();

        Branch branch = new Branch(this);
        branch.openReadableDatabase();
        branchList = branch.GetBranchName();
        branch.closeDatabase();
        cheqAdapter = new CollectionChequeAdapter(this, cheqLists);
        for (String[] chaqeData : cheqeDetails) {
            cheqLists.add(new ListCollectionCheque(chaqeData[1], chaqeData[0]));
        }

        cheqList.setAdapter(cheqAdapter);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBox.dismiss();
            }
        });

        calenderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog().newInstance(CollectionNote.this, now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });


        ArrayAdapter<String> bankAdapterList = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, bankList);
        bankTextView.setAdapter(bankAdapterList);

        ArrayAdapter<String> barnchAdapterList = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, branchList);
        edtBranch.setAdapter(barnchAdapterList);


        bankTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                if (bankTextView.getText().toString().equals("")) {

                } else {
                    Master_Banks banks = new Master_Banks(CollectionNote.this);
                    banks.openReadableDatabase();
                    bankCode.setText("Bank Code : " + banks.getBankCode((String) arg0.getItemAtPosition(arg2)));
                    banks.closeDatabase();
                    if (edtBranch.getText().toString().equals("")) {

                    } else {
                        Branch branch = new Branch(CollectionNote.this);
                        branch.openReadableDatabase();
                        barnchCode.setText("Branch Code : " + branch.getBranchCodeForCollection((String) arg0.getItemAtPosition(arg2), edtBranch.getText().toString()));
                        branch.closeDatabase();
                    }

                }

            }
        });

        edtBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                if (barnchCode.getText().toString().equals("")) {
                } else {

                    Branch branch = new Branch(CollectionNote.this);
                    branch.openReadableDatabase();
                    barnchCode.setText("Branch Code : " + branch.getBranchCodeForCollection(bankTextView.getText().toString(), (String) arg0.getItemAtPosition(arg2)));
                    branch.closeDatabase();


                    if (bankTextView.getText().toString().equals("")) {

                    } else {
                        Master_Banks banks = new Master_Banks(CollectionNote.this);
                        banks.openReadableDatabase();
                        bankCode.setText("Bank Code : " + banks.getBankCode(bankTextView.getText().toString()));
                        banks.closeDatabase();
                    }

                }

            }
        });

        layoutChequeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            }
        });

        cheqList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String[] c = cheqeDetails.get(i);
                selectedCheq = i;

                editAmmount.setText(c[0]);
                edtNumber.setText(c[1]);
                bankTextView.setText(c[2]);
                edtBranch.setText(c[3]);
                textViewRealizedate.setText(c[4]);

                try {
                    chequeimage.setImageBitmap(base64ToBitmap(c[5]));
                } catch (NullPointerException n) {

                }

                bankCode.setText("Bank Code : " + c[6]);
                barnchCode.setText("Branch Code : " + c[7]);

            }
        });


        edtNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 6) {
                    Toast.makeText(CollectionNote.this, "You exceed maximum characters", Toast.LENGTH_LONG).show();
                    edtNumber.setText(charSequence.toString().substring(0, 6));
                } else {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean zrooChek = false;
                try {
                    int convertAmmount = 1 / Integer.parseInt(editAmmount.getText().toString().trim());
                } catch (ArithmeticException a) {
                    zrooChek = true;
                } catch (NumberFormatException nu) {

                }
                if (!editAmmount.getText().toString().trim().equals("")) {
                    if (edtNumber.getText().toString().trim().equals("")) {
                        Toast.makeText(CollectionNote.this, "Please enter the cheque number!", Toast.LENGTH_LONG).show();
                    } else if (zrooChek == true) {
                        Toast.makeText(CollectionNote.this, "Please enter valid number!", Toast.LENGTH_LONG).show();
                    } else if (bankTextView.getText().toString().trim().equals("")) {
                        Toast.makeText(CollectionNote.this, "Please enter the Bank name!", Toast.LENGTH_LONG).show();
                    } else if (edtBranch.getText().toString().trim().equals("")) {
                        Toast.makeText(CollectionNote.this, "Please enter the Bank Branch name!", Toast.LENGTH_LONG).show();
                    } else if (textViewRealizedate.getText().toString().equals("")) {
                        Toast.makeText(CollectionNote.this, "Please enter the Realize Date!", Toast.LENGTH_LONG).show();
                    } else {
                        String[] chaqeDetail = new String[15];
                        int cheqNumberStates = 0;
                        double cheqeValue = 0.0;


                        for (String[] chaqeData : cheqeDetails) {
                            cheqeValue = cheqeValue + Double.parseDouble(chaqeData[0]);
                            if (edtNumber.getText().toString().trim().equals(chaqeData[1])) {
                                cheqNumberStates = 1;
                            } else {

                            }
                        }


                        if (cheqeDetails.isEmpty()) {
                            cheqeValue = Double.parseDouble(editAmmount.getText().toString().trim());
                        } else {
                            cheqeValue = cheqeValue + Double.parseDouble(editAmmount.getText().toString().trim());
                        }

                        if (cheqNumberStates == 1) {
                            Toast.makeText(CollectionNote.this, "This cheque already added", Toast.LENGTH_LONG).show();
                        } else {
                            chaqeDetail[0] = editAmmount.getText().toString().trim();
                            chaqeDetail[1] = edtNumber.getText().toString().trim();
                            chaqeDetail[2] = bankTextView.getText().toString().trim();
                            chaqeDetail[3] = edtBranch.getText().toString().trim();
                            chaqeDetail[4] = textViewRealizedate.getText().toString().trim();

                            if (chequeimageByte == null) {
                                chaqeDetail[5] = "no image";
                            } else {
                                try {

                                    Bitmap bmp = BitmapFactory.decodeByteArray(chequeimageByte, 0, chequeimageByte.length);
                                    chaqeDetail[5] = bitmapToBase64(bmp);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            //  String[] bankcode = bankCode.getText().toString().split(":");
                            //  chaqeDetail[6] = bankcode[1];

                            // String[] branchcode = barnchCode.getText().toString().split(":");
                            //  chaqeDetail[7] = branchcode[1];

                            cheqeDetails.add(chaqeDetail);
                            //   tempcheqeDetails.add(chaqeDetail);

                            tempcheqeDetails = (ArrayList<String[]>) cheqeDetails.clone();

                            editAmmount.setText("");
                            edtNumber.setText("");
                            bankTextView.setText("");
                            edtBranch.setText("");
                            textViewRealizedate.setText("");
                            bankCode.setText("Bank Code :");
                            barnchCode.setText("Branch Code : ");
                            chequeimage.setImageDrawable(null);

                            textCheqe.setText("Cheque Ammount : " + cheqeValue);

                        }

                        cheqLists.clear();

                        List<String> cheqNumberList = null;
                        cheqNumberList = new ArrayList();
                        for (String[] chaqeData : cheqeDetails) {
                            cheqLists.add(new ListCollectionCheque(chaqeData[1], chaqeData[0]));
                            cheqNumberList.add(chaqeData[1]);
                        }
                        cheqList.setAdapter(cheqAdapter);
                        cheqeNumberSp.setItems(cheqNumberList);
                        selectChuqNumber = cheqNumberList.get(0);
                    }
                } else {
                    dialogBox.dismiss();
                }

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CollectionNote.this);
                alertDialogBuilder.setTitle("Warring");
                alertDialogBuilder
                        .setMessage("Do you want to clear cheque data,if yes you will lost all date which you add to list ")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                double cheqeValue = 0.0;

                                if (cheqeDetails.isEmpty()) {

                                } else if (selectedCheq == -1) {

                                } else {
                                    cheqeDetails.remove(selectedCheq);
                                    cheqLists.clear();

                                    for (String[] chaqeData : cheqeDetails) {
                                        cheqLists.add(new ListCollectionCheque(chaqeData[1], chaqeData[0]));
                                        cheqeValue = cheqeValue + Double.parseDouble(chaqeData[0]);
                                    }
                                    cheqList.setAdapter(cheqAdapter);
                                    editAmmount.setText("");
                                    edtNumber.setText("");
                                    bankTextView.setText("");
                                    edtBranch.setText("");
                                    textViewRealizedate.setText("");
                                    bankCode.setText("Bank Code :");
                                    barnchCode.setText("Branch Code : ");
                                    chequeimage.setImageDrawable(null);
                                    textCheqe.setText("Cheque Ammount : " + cheqeValue);
                                }


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


        dialogBox.show();

    }

    public void valueclear() {


        editCash.setText("");
        textViewInvoiceCradite.setText("0");
        textCheqe.setText("Cheque Ammount : 0.0");

        cheqeDetails.clear();
        tempreturnChq.clear();
        tempcheqeDetails.clear();

        listCollectionNoteItem.clear();
        tempreturnProducts.clear();
        addedInvoiceList.setAdapter(listAdapter);


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date today = c.getTime();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        Date dateSpecified = c.getTime();
        if (dateSpecified.after(today)) {
            String month, day;
            if (String.valueOf(dayOfMonth).length() == 1) {
                day = "0" + String.valueOf(dayOfMonth);
            } else {
                day = String.valueOf(dayOfMonth);
            }
            if (String.valueOf((monthOfYear + 1)).length() == 1) {
                month = "0" + String.valueOf((monthOfYear + 1));
            } else {
                month = String.valueOf((monthOfYear + 1));
            }

          //  textViewRealizedate.setText(day + "/" + month + "/" + String.valueOf(year));
            textViewRealizedate.setText(month + "/" + day + "/" + String.valueOf(year));
        } else {
            Toast.makeText(CollectionNote.this, "Please select future date", Toast.LENGTH_LONG).show();
        }

    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            chequeimageByte = getBytes(photo);
            chequeimage.setImageBitmap(photo);
        }

    }

    public void getDeleteIdFromList(String id) {
        listCollectionNoteItem.clear();
        for (int i = 0; i < tempreturnProducts.size(); i++) {
            String[] r = tempreturnProducts.get(i);
            if ((r[0].contentEquals(id))) {
                String chequeAmmount[] = textCheqe.getText().toString().split(":");
                if ((tempreturnProducts.size() - 1) == i) {
                    String[] r2;
                    if (tempreturnProducts.size() == 1) {
                        r2 = tempreturnProducts.get(0);
                    } else {
                        r2 = tempreturnProducts.get(i - 1);
                    }

                    if ((!chequeAmmount[1].toString().trim().equals("0.0")) && (!editCash.getText().toString().equals(""))) {
                        cashbalance = Double.parseDouble(r2[1]);
                        cheqbalance = Double.parseDouble(r2[2]);
                        balance = Double.parseDouble(r2[3]);

                    } else if (editCash.getText().toString().equals("0.0")) {
                        cheqBal = Double.parseDouble(r[2]);
                        balance = Double.parseDouble(r[3]);
                    } else if (chequeAmmount[1].toString().trim().equals("0.0")) {
                        cashBal = Double.parseDouble(r[1]);
                        balance = Double.parseDouble(r[3]);
                    }

                    tempreturnProducts.remove(i);

                } else {
                    Toast.makeText(CollectionNote.this, "You can remove only the last row", Toast.LENGTH_SHORT).show();
                }


            } else {

            }
        }
        for (int i = 0; i < tempreturnProducts.size(); i++) {
            String[] r = tempreturnProducts.get(i);
            listCollectionNoteItem.add(new CollectionNoteList(r[0], r[1], r[2], r[3]));
        }

        if (cheqeDetails.isEmpty()) {

            // int a =cheqeDetails.size();
            // int b =tempcheqeDetails.size();

            cheqeDetails = (ArrayList<String[]>) tempcheqeDetails.clone();


            //  int aa =cheqeDetails.size();
            //   int ba =tempcheqeDetails.size();
        } else {
            for (int i = 0; i < tempcheqeDetails.size(); i++) {
                String[] r = tempcheqeDetails.get(i);

                for (int j = 0; j < cheqeDetails.size(); j++) {
                    String[] c = cheqeDetails.get(j);
                    if (r[1].equals(c[1])) {
                    } else {
                        String[] chaqeDetail = new String[3];
                        chaqeDetail[0] = r[0];
                        chaqeDetail[1] = r[1];

                        String a = chaqeDetail[0];
                        String b = chaqeDetail[1];

                        System.out.println("ssssssssssss");
                    }

                }


            }

        }


        addedInvoiceList.setAdapter(listAdapter);
    }

    private String GenaterCollectionNoteNumber() {
        String Cnumber = null;
        try {
            CollectionNoteSendToApprovel aprove = new CollectionNoteSendToApprovel(CollectionNote.this);
            Cnumber = aprove.GenareCollectionNoteNumber();
            aprove.closeDatabase();
        } catch (Exception e) {
        }
        return Cnumber;
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public void upload() {
        try {
            UploadCollectionNoteTask up = new UploadCollectionNoteTask(CollectionNote.this);
            up.execute();
        } catch (Exception e) {

        }

    }

}
