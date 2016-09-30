package com.HesperusMarketing.channelbridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.HesperusMarketing.Entity.TempInvoiceStock;
import com.HesperusMarketing.channelbridgeaddapters.ListProduct;
import com.HesperusMarketing.channelbridgeaddapters.ProductImages;
import com.HesperusMarketing.channelbridgeaddapters.ProductImagesAdapter;
import com.HesperusMarketing.channelbridgeaddapters.RecyclerListProductAdapter;
import com.HesperusMarketing.channelbridgedb.Products;
import com.HesperusMarketing.channelbridgedb.Sequence;
import com.HesperusMarketing.channelbridgedb.ShelfQuantity;
import com.HesperusMarketing.channelbridgedb.TemporaryInvoice;


import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Amila on 11/12/15
 */
public class InvoiceGen1Alternate extends Activity {

    String selected, selectedCategory, rowId, pharmacyId, startTime, collectionDate = "", releaseDate = "", chequeNumber = "";
    String colerchartProductCode = "0", colerchartProductBatch = "0", colerchartProductShelf = "0", colerchartProductRequest = "0", colerchartProductOrder = "0", colerchartProductFree = "0", colerchartProductStock = "0";
    String productCode, productBatch, productStock;
    String actSelectedProduct = "test";
    private Boolean iswebApprovalActive = true;
    private Boolean isChanged = false;
    boolean chequeEnabled = false;
    int prosessStatus = 0;
    public static int freeStatus = 0, freeTotleList = 0, freeTotleListReminig;


    Spinner spPrinciple, spCategory;
    AutoCompleteTextView actvDescription;
    private TableLayout tblTest;
    private Button btnAdd, btnSearch;
    EditText editDiscount, editshelf, editrequest, editfree, edtShelf, edtRequest, edtOrder, edtFree, edtDiscountProduct;
    Dialog dialogBox;
    CheckBox checkboxOne, checkboxTwo, checkboxThree;
    TextView textViewtotal, textViewtotal2, textViewfree, textViewfree2, textViewreamingfree, textViewreamingfree2, selectProduct, textCode, textDiscr, textBatch, textStock, textPrice;
    RelativeLayout layoutDefoult, layoutRequest, layout;

    String[] productOdreDetail = new String[5];

    AlertDialog.Builder alertCancel;

    ArrayList<String> productDescList;

    ArrayAdapter<String> principleAdapter;
    ArrayAdapter<String> categoryAdapter;
    ArrayAdapter<String> productCodeAdapter;
    private ArrayList<String> principleList;
    private ArrayList<String> categoryList;
    private ArrayList<TempInvoiceStock> prductList;
    ArrayList<ReturnProduct> returnProductsArray;
    ArrayList<SelectedProduct> mergeList;
    ArrayList<String[]> itemCodeDetailList;

    ArrayList<TempInvoiceStock> prductListImage;
    ProductImagesAdapter productAdapterImage;

    private Products productController;
    TemporaryInvoice tempInvoiceStockController;

    private RecyclerView recyclerView, recyclerViewImages;
    private List<ListProduct> albumList;
    private RecyclerListProductAdapter adapter;

    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_gen_1_option2);

        SharedPreferences shared = getApplicationContext().getSharedPreferences("CBHesPreferences", Context.MODE_PRIVATE);
        iswebApprovalActive = (shared.getBoolean("WebApproval", true));

        if (savedInstanceState != null) {
            getDataFromPreviousActivity(savedInstanceState);
        } else {
            getDataFromPreviousActivity(getIntent().getExtras());
        }

        productController = new Products(getApplicationContext());
        tempInvoiceStockController = new TemporaryInvoice(getApplicationContext());


        principleList = new ArrayList<>();
        productDescList = new ArrayList<String>();
        categoryList = new ArrayList<>();
        prductList = new ArrayList<>();
        returnProductsArray = new ArrayList<ReturnProduct>();
        mergeList = new ArrayList<>();
        itemCodeDetailList = new ArrayList<String[]>();


        layout = (RelativeLayout) findViewById(R.id.relativeLayout99);

        textCode = (TextView) findViewById(R.id.textViewCodeSingleProduct);
        textDiscr = (TextView) findViewById(R.id.textViewdescriptionSingleProduct);
        textBatch = (TextView) findViewById(R.id.textViewBatchSingleProduct);
        textStock = (TextView) findViewById(R.id.txtStockSingleProduct);
        textPrice = (TextView) findViewById(R.id.textViewPriceSingleProduct);

        edtShelf = (EditText) findViewById(R.id.editTextShelfSingleProduct);
        edtRequest = (EditText) findViewById(R.id.editTextRequestSingleProduct);
        edtOrder = (EditText) findViewById(R.id.editTextOrderSingleProduct);
        edtFree = (EditText) findViewById(R.id.editTextFreeSingleProduct);
        edtDiscountProduct = (EditText) findViewById(R.id.editTextDiscountSingleProduct);

        spPrinciple = (Spinner) findViewById(R.id.spPrinciple);
        spCategory = (Spinner) findViewById(R.id.spCategory);
        actvDescription = (AutoCompleteTextView) findViewById(R.id.actvCode);

        btnAdd = (Button) findViewById(R.id.btnNextToPayment);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        editDiscount = (EditText) findViewById(R.id.editTextListDiscount);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);


        albumList = new ArrayList<>();


        productController.openReadableDatabase();
        principleList = productController.getPrincipleList();


        principleAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.single_list_item, principleList);
        spPrinciple.setAdapter(principleAdapter);

        selected = spPrinciple.getSelectedItem().toString();

        categoryList = productController.getCategoryListForPriciple(selected);
        categoryAdapter = new ArrayAdapter<String>(InvoiceGen1Alternate.this, R.layout.single_list_item, categoryList);
        spCategory.setAdapter(categoryAdapter);



        productCodeAdapter = new ArrayAdapter<String>(InvoiceGen1Alternate.this, R.layout.single_list_item, productDescList);
        actvDescription.setThreshold(1);
        actvDescription.setAdapter(productCodeAdapter);


        actvDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if(charSequence.toString().equals("")||charSequence.toString().isEmpty()){
                   actSelectedProduct = "test";
                   productTablefill(1);
               }else {

               }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        actvDescription.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                actSelectedProduct = (String) adapterView.getItemAtPosition(i);
                productTablefill(0);
                for(int k=0;k<productDescList.size();k++){
                    System.out.print("2. Selected desc: "+productDescList.get(k).toString()+", ");
                }
                Toast.makeText(InvoiceGen1Alternate.this,"CM :"+ actSelectedProduct, Toast.LENGTH_SHORT).show();
            }
        });

        oderDetailsEdittextOntextChange();

        spPrinciple.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // editDiscount.setText("");
                //  refreshViewOnSelection(0);
                categoryList.clear();
                productController.openReadableDatabase();
                selected = spPrinciple.getSelectedItem().toString();
                categoryList = productController.getCategoryListForPriciple(selected);
                categoryAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.single_list_item, categoryList);
                spCategory.setAdapter(categoryAdapter);
                productTablefill(0);
                productController.closeDatabase();
                editDiscount.setText("");
                actvDescription.setText("");

                populateProductTableNew(prductList);
                productCodeAdapter = new ArrayAdapter<String>(InvoiceGen1Alternate.this, android.R.layout.simple_list_item_1, productDescList);
                actvDescription.setAdapter(productCodeAdapter);

                //  freeStatus = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = spCategory.getSelectedItem().toString();
                productTablefill(1);
                editDiscount.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                layout.setVisibility(View.GONE);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String discountValue = editDiscount.getText().toString();
                editDiscount.setText("");
                tempInvoiceStockController.openReadableDatabase();
                ArrayList<TempInvoiceStock> selectedProductList = new ArrayList<>();
                selectedProductList = tempInvoiceStockController.getProductTempList();
                Log.i("size arr", "" + selectedProductList.size());

                boolean flag = false;
                if (!selectedProductList.isEmpty()) {
                    Intent invoiceGen2Intent = new Intent("com.HesperusMarketing.channelbridge.INVOICEGEN2ACTIVITY");
                    Bundle bundleToView = new Bundle();
                    bundleToView.putString("Id", rowId);
                    bundleToView.putString("PharmacyId", pharmacyId);
                    bundleToView.putString("startTime", startTime);
                    if (chequeEnabled) {
                        bundleToView.putString("ChequeNumber", chequeNumber);
                        bundleToView.putString("CollectionDate", collectionDate);
                        bundleToView.putString("ReleaseDate", releaseDate);
                    }

                    Log.w("invoicegen2", "selectedProductList size : " + selectedProductList.size());
                    ArrayList<SelectedProduct> selectedProductsArray = new ArrayList<SelectedProduct>();

                    for (TempInvoiceStock stockData : selectedProductList) {
                        SelectedProduct product = new SelectedProduct();


                        product.setRowId(Integer.parseInt(stockData.getRow_ID()));
                        product.setProductId(stockData.getProductId());
                        product.setProductCode(stockData.getProductCode());
                        product.setProductBatch(stockData.getBatchCode());
                        product.setQuantity(stockData.getStock());
                        product.setExpiryDate(stockData.getExpiryDate());
                        product.setTimeStamp(stockData.getTimestamp());

                        product.setRequestedQuantity(Integer.parseInt(stockData.getRequestQuantity()));
                        product.setFree(Integer.parseInt(stockData.getFreeQuantity()));
                        product.setNormal(Integer.parseInt(stockData.getNormalQuantity()));

                        product.setDiscount(stockData.getPercentage());

                        if (stockData.getShelfQuantity().equals("")) {
                            product.setShelfQuantity(0);
                        } else {
                            product.setShelfQuantity(Integer.parseInt(stockData.getShelfQuantity()));
                        }

//
//
                        product.setProductDescription(stockData.getProductDes());
                        product.setPrice(Double.parseDouble(stockData.getPrice()));
//

//
                        selectedProductsArray.add(product);
//
                    }

                    bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                    bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                    bundleToView.putString("discountval", discountValue);

                    invoiceGen2Intent.putExtras(bundleToView);
                    finish();
                    startActivity(invoiceGen2Intent);
//

                } else {
                    mergeList = tempInvoiceStockController.getShelfQuantityTempList();
                    if (mergeList.size() > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceGen1Alternate.this);
                        builder.setTitle("Save");
                        builder.setMessage("You just only entered shelf quantity.Are you want to save");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new ShelfQuantityTask(InvoiceGen1Alternate.this).execute();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                }
                tempInvoiceStockController.closeDatabase();
            }

        });


        editDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                selected = spPrinciple.getSelectedItem().toString();
                tempInvoiceStockController.openWritableDatabase();
                if (editable.toString().isEmpty() || editable.toString().equals("")) {

                } else {
                    int free = tempInvoiceStockController.getFreeTotle(selected, selectedCategory);
                    if (free != 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceGen1Alternate.this);
                        builder.setTitle("Free Issues");
                        builder.setMessage("Do you want to clear free data");
                        builder.setCancelable(false);
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                tempInvoiceStockController.clearFreeqty(selected, null);
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        try {
                            if (Double.parseDouble(editable.toString()) > 20.0 && selected.equals("RESTORIA")) {
                                Toast.makeText(InvoiceGen1Alternate.this, "Your exceed maximum discount level", Toast.LENGTH_LONG).show();
                                editDiscount.setText("");
                                tempInvoiceStockController.setDiscount(selected, 0.0);
                            } else if (Double.parseDouble(editable.toString()) > 30.0) {
                                Toast.makeText(InvoiceGen1Alternate.this, "Your exceed maximum discount level", Toast.LENGTH_LONG).show();
                                editDiscount.setText("");
                                tempInvoiceStockController.setDiscount(selected, 0.0);
                            } else {
                                tempInvoiceStockController.setDiscount(selected, Double.parseDouble(editable.toString()));
                                tempInvoiceStockController.closeDatabase();

                            }
                        } catch (NumberFormatException ss) {
                            tempInvoiceStockController.setDiscount(selected, 0.0);
                            tempInvoiceStockController.closeDatabase();

                        }

                    }


                }
                productTablefill(1);

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnSearch.setEnabled(false);

                if(prductList.isEmpty()||prductList.size()==0){
                    Toast.makeText(InvoiceGen1Alternate.this, "No Products", Toast.LENGTH_LONG).show();
                }else {
                    showDialogProductImage(InvoiceGen1Alternate.this);
                }


            }
        });

        productController.closeDatabase();
    }

    public void productTablefill(int status) {

        prductList.clear();
       /* categoryAdapter.notifyDataSetChanged();
        productController.openReadableDatabase();

        if (status == 0) {
            prductList = productController.getProductsByPriciple(selected);

        } else if (status == 1) {
            String selectedCategory = spCategory.getSelectedItem().toString();
            prductList = productController.getProductsByPricipleAndCategory(selected, selectedCategory);
        }*/

        TemporaryInvoice tem = new TemporaryInvoice(InvoiceGen1Alternate.this);
        tem.openReadableDatabase();
        String selectedCategory = spCategory.getSelectedItem().toString();

        prductList = tem.getTempDataForTable(selectedCategory, selected, actSelectedProduct, status);


        populateProductTableNew(prductList);
        productController.closeDatabase();
    }

    private void populateProductTableNew(ArrayList<TempInvoiceStock> prductList) {

        adapter = new RecyclerListProductAdapter(this, prductList);
        recyclerView.setAdapter(adapter);

        productDescList.clear();
        for(int i=0;i<prductList.size();i++){
            productDescList.add(prductList.get(i).getProductDes());
            System.out.println("1. Add to product list: "+productDescList.get(i).toString());
        }

    }

    private void getDataFromPreviousActivity(Bundle extras) {

        try {
            extras = getIntent().getExtras();
            rowId = extras.getString("Id");
            pharmacyId = extras.getString("PharmacyId");
            startTime = extras.getString("startTime");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
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


            if (extras.containsKey("ReturnProducts")) {
                returnProductsArray = extras.getParcelableArrayList("ReturnProducts");
            }

        } catch (Exception e) {
            Log.w("InvoiceGen1: ", e.toString());
        }
    }


    @Override
    public void onBackPressed() {
        // super.onBackPressed();


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage("Are you sure you want Cancel this Invoice?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        tempInvoiceStockController.openWritableDatabase();
                        tempInvoiceStockController.deleteAllRecords();
                        tempInvoiceStockController.closeDatabase();

                        Intent in = new Intent(InvoiceGen1Alternate.this, ItineraryList.class);
                        finish();
                        startActivity(in);


                    }
                });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alertDialogBuilder.show();


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("startTime", startTime);
        outState.putString("Id", rowId);
        outState.putString("PharmacyId", pharmacyId);

        outState.putParcelableArrayList("ReturnProducts", returnProductsArray);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        chequeEnabled = preferences.getBoolean("cbPrefEnableCheckDetails", true);
        if (chequeEnabled) {
            outState.putString("ChequeNumber", chequeNumber);
            outState.putString("CollectionDate", collectionDate);
            outState.putString("ReleaseDate", releaseDate);
        }

    }


    protected void updateShelfQuantityDB(ArrayList<SelectedProduct> shelfQuantityList) {
        // TODO Auto-generated method stub
        ShelfQuantity shelfQuantity = new ShelfQuantity(this);
        String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                .format(new Date());

        shelfQuantity.openWritableDatabase();
        Sequence sequence = new Sequence(this);

        sequence.openReadableDatabase();
        String lastInv = sequence.getLastRowId("invoice");
        sequence.closeDatabase();

        String invNum = String.valueOf(Integer.parseInt(lastInv) + 1);
        invNum = "NOT" + invNum;
        for (SelectedProduct shelfQuantityDetails : shelfQuantityList) {

            shelfQuantity.insertShelfQuantity(invNum, timeStamp,
                    pharmacyId, shelfQuantityDetails.getProductCode(),
                    shelfQuantityDetails.getProductBatch(),
                    String.valueOf(shelfQuantityDetails.getShelfQuantity()),
                    timeStamp, "false");


        }
        shelfQuantity.closeDatabase();
    }


    public class ShelfQuantityTask extends AsyncTask<Void, Void, Void> {

        private Context context;

        private ProgressDialog dialog;

        public ShelfQuantityTask(Context context) {
            this.context = context;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(context);
            this.dialog.setMessage("Saving shelf quantities");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            updateShelfQuantityDB(mergeList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            this.dialog.dismiss();
            finish();
        }
    }

    //Himanshu
    public void showDialogProductImage(final Context context) {

        dialogBox = new Dialog(context);
        dialogBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBox.setContentView(R.layout.dialog_product_image);
        dialogBox.setCancelable(false);

        recyclerViewImages = (RecyclerView) dialogBox.findViewById(R.id.gridView_album);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 10);
        recyclerViewImages.setLayoutManager(mLayoutManager);
        recyclerViewImages.setItemAnimator(new DefaultItemAnimator());

        final TemporaryInvoice temInvies = new TemporaryInvoice(getApplicationContext());
        temInvies.openWritableDatabase();


        prductListImage = new ArrayList<>();

        prductListImage = temInvies.getTempDataForTable(selected, selectedCategory, actSelectedProduct, 1);
        productAdapterImage = new ProductImagesAdapter(this, prductListImage);


        final TextView shelf = (TextView) dialogBox.findViewById(R.id.textView20);
        final TextView request = (TextView) dialogBox.findViewById(R.id.textView23);
        final TextView free = (TextView) dialogBox.findViewById(R.id.textView_free);


        checkboxOne = (CheckBox) dialogBox.findViewById(R.id.checkBox1);
        checkboxTwo = (CheckBox) dialogBox.findViewById(R.id.checkBox2);
        checkboxThree = (CheckBox) dialogBox.findViewById(R.id.checkBox3);

        textViewtotal = (TextView) dialogBox.findViewById(R.id.textViewtotal);
        textViewfree = (TextView) dialogBox.findViewById(R.id.textViewtotalfree);
        textViewreamingfree = (TextView) dialogBox.findViewById(R.id.textViewremaningFree);
        selectProduct = (TextView) dialogBox.findViewById(R.id.textView45);

        textViewtotal2 = (TextView) dialogBox.findViewById(R.id.textView25);
        textViewfree2 = (TextView) dialogBox.findViewById(R.id.textView26);
        textViewreamingfree2 = (TextView) dialogBox.findViewById(R.id.textView27);

        layoutDefoult = (RelativeLayout) dialogBox.findViewById(R.id.relativeLayout5);
        layoutRequest = (RelativeLayout) dialogBox.findViewById(R.id.relativeLayoutquentity);

        layoutDefoult.setVisibility(View.VISIBLE);
        layoutRequest.setVisibility(View.INVISIBLE);

        Button btnDone = (Button) dialogBox.findViewById(R.id.btnalldone);


        ImageView imageViewClose = (ImageView) dialogBox.findViewById(R.id.imageViewClose);

        editshelf = (EditText) dialogBox.findViewById(R.id.editText_shelf);
        editrequest = (EditText) dialogBox.findViewById(R.id.editText_request);
        editfree = (EditText) dialogBox.findViewById(R.id.editText_free);
        Button Done = (Button) dialogBox.findViewById(R.id.button_done);


        if (prosessStatus == 0) {
            editshelf.setVisibility(View.VISIBLE);
            editrequest.setVisibility(View.VISIBLE);
            editfree.setVisibility(View.INVISIBLE);

            shelf.setVisibility(View.VISIBLE);
            request.setVisibility(View.VISIBLE);
            free.setVisibility(View.INVISIBLE);


        } else {
            editshelf.setVisibility(View.INVISIBLE);
            editrequest.setVisibility(View.INVISIBLE);
            editfree.setVisibility(View.VISIBLE);

            shelf.setVisibility(View.INVISIBLE);
            request.setVisibility(View.INVISIBLE);
            free.setVisibility(View.VISIBLE);


            checkboxOne.setEnabled(false);
            checkboxTwo.setEnabled(false);
            checkboxThree.setEnabled(false);


        }


        String split[] = tempInvoiceStockController.getFreeIssueRate(selected, null).split("/");
        int totle = tempInvoiceStockController.getTotle(selected, null);
        if (split[0].equals("0")) {
            textViewtotal.setText("Total Qty :" + totle);
            textViewtotal2.setText("Total Qty :" + totle);
        } else {

            int remain = (totle / Integer.parseInt(split[0])) * Integer.parseInt(split[1]) - tempInvoiceStockController.getFreeTotle(selected, null);


            textViewtotal.setText("Total Qty :" + totle);
            textViewfree.setText("Total Free : " + (totle / Integer.parseInt(split[0])) * Integer.parseInt(split[1]));
            textViewreamingfree.setText("Remi Free : " + remain);

            textViewtotal2.setText("Total Qty :" + totle);
            textViewfree2.setText("Total Free : " + (totle / Integer.parseInt(split[0])) * Integer.parseInt(split[1]));
            textViewreamingfree2.setText("Remi Free : " + remain);

            String isserRate = tempInvoiceStockController.getFreeIssueRate(selected, null);

            if (isserRate.equals(checkboxOne.getText().toString())) {
                checkboxOne.setChecked(true);
            } else if (isserRate.equals(checkboxTwo.getText().toString())) {
                checkboxTwo.setChecked(true);
            } else if (isserRate.equals(checkboxThree.getText().toString())) {
                checkboxThree.setChecked(true);
            } else {
                checkboxOne.setChecked(false);
                checkboxTwo.setChecked(false);
                checkboxThree.setChecked(false);
            }

        }


        temInvies.closeDatabase();
        recyclerViewImages.setAdapter(productAdapterImage);


        checkboxOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempInvoiceStockController.openWritableDatabase();
                final int totleList = tempInvoiceStockController.getTotle(selected, null);
                int freetot = tempInvoiceStockController.getFreeTotle(selected, null);

                if (checkboxTwo.isChecked() || checkboxThree.isChecked()) {
                    if (freetot != 0) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceGen1Alternate.this);
                        builder.setTitle("Free Issues");
                        builder.setMessage("Do you want to clear free data");
                        builder.setCancelable(false);
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                tempInvoiceStockController.clearFreeqty(selected, null);
                                checkboxTwo.setChecked(false);
                                checkboxThree.setChecked(false);

                                freeStatus = 1;
                                tempInvoiceStockController.updateFreeIssueRate(selected, null, checkboxOne.getText().toString());
                                String split[] = checkboxOne.getText().toString().split("/");
                                freeTotleList = (totleList / Integer.parseInt(split[0].trim())) * Integer.parseInt(split[1].trim());

                                freeTotleListReminig = freeTotleList;
                                textViewtotal.setText("Total Qty : " + totleList);
                                textViewfree.setText("Total Free : " + freeTotleList);
                                textViewreamingfree.setText("Remi Free : " + freeTotleListReminig);

                                textViewtotal2.setText("Total Qty : " + totleList);
                                textViewfree2.setText("Total Free : " + freeTotleList);
                                textViewreamingfree2.setText("Remi Free : " + freeTotleListReminig);


                                editshelf.setVisibility(View.VISIBLE);
                                editrequest.setVisibility(View.VISIBLE);
                                editfree.setVisibility(View.INVISIBLE);

                                shelf.setVisibility(View.VISIBLE);
                                request.setVisibility(View.VISIBLE);
                                free.setVisibility(View.INVISIBLE);


                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                checkboxOne.setChecked(false);
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();


                    } else {
                        checkboxTwo.setChecked(false);
                        checkboxThree.setChecked(false);

                        freeStatus = 1;
                        tempInvoiceStockController.updateFreeIssueRate(selected, null, checkboxOne.getText().toString());
                        String split[] = checkboxOne.getText().toString().split("/");
                        freeTotleList = (totleList / Integer.parseInt(split[0].trim())) * Integer.parseInt(split[1].trim());

                        freeTotleListReminig = freeTotleList;
                        textViewtotal.setText("Total Qty : " + totleList);
                        textViewfree.setText("Total Free : " + freeTotleList);
                        textViewreamingfree.setText("Remi Free : " + freeTotleListReminig);

                        textViewtotal2.setText("Total Qty : " + totleList);
                        textViewfree2.setText("Total Free : " + freeTotleList);
                        textViewreamingfree2.setText("Remi Free : " + freeTotleListReminig);

                        editshelf.setVisibility(View.INVISIBLE);
                        editrequest.setVisibility(View.INVISIBLE);
                        editfree.setVisibility(View.VISIBLE);

                        shelf.setVisibility(View.INVISIBLE);
                        request.setVisibility(View.INVISIBLE);
                        free.setVisibility(View.VISIBLE);


                    }


                } else {
                    freeStatus = 1;
                    tempInvoiceStockController.updateFreeIssueRate(selected, null, checkboxOne.getText().toString());
                    String split[] = checkboxOne.getText().toString().split("/");
                    freeTotleList = (totleList / Integer.parseInt(split[0].trim())) * Integer.parseInt(split[1].trim());

                    freeTotleListReminig = freeTotleList;
                    textViewtotal.setText("Total Qty : " + totleList);
                    textViewfree.setText("Total Free : " + freeTotleList);
                    textViewreamingfree.setText("Remi Free : " + freeTotleListReminig);

                    textViewtotal2.setText("Total Qty : " + totleList);
                    textViewfree2.setText("Total Free : " + freeTotleList);
                    textViewreamingfree2.setText("Remi Free : " + freeTotleListReminig);

                    editshelf.setVisibility(View.INVISIBLE);
                    editrequest.setVisibility(View.INVISIBLE);
                    editfree.setVisibility(View.VISIBLE);

                    shelf.setVisibility(View.INVISIBLE);
                    request.setVisibility(View.INVISIBLE);
                    free.setVisibility(View.VISIBLE);

                }


                tempInvoiceStockController.closeDatabase();
            }
        });

        checkboxTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempInvoiceStockController.openWritableDatabase();
                final int totleList = tempInvoiceStockController.getTotle(selected, null);
                int freetot = tempInvoiceStockController.getFreeTotle(selected, null);

                if (checkboxOne.isChecked() || checkboxThree.isChecked()) {
                    if (freetot != 0) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceGen1Alternate.this);
                        builder.setTitle("Free Issues");
                        builder.setMessage("Do you want to clear free data");
                        builder.setCancelable(false);
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                tempInvoiceStockController.clearFreeqty(selected, null);
                                checkboxOne.setChecked(false);
                                checkboxThree.setChecked(false);

                                freeStatus = 1;
                                tempInvoiceStockController.updateFreeIssueRate(selected, null, checkboxTwo.getText().toString());
                                String split[] = checkboxTwo.getText().toString().split("/");
                                freeTotleList = (totleList / Integer.parseInt(split[0].trim())) * Integer.parseInt(split[1].trim());

                                freeTotleListReminig = freeTotleList;
                                textViewtotal.setText("Total Qty : " + totleList);
                                textViewfree.setText("Total Free : " + freeTotleList);
                                textViewreamingfree.setText("Remi Free : " + freeTotleListReminig);

                                textViewtotal2.setText("Total Qty : " + totleList);
                                textViewfree2.setText("Total Free : " + freeTotleList);
                                textViewreamingfree2.setText("Remi Free : " + freeTotleListReminig);


                                editshelf.setVisibility(View.VISIBLE);
                                editrequest.setVisibility(View.VISIBLE);
                                editfree.setVisibility(View.INVISIBLE);

                                shelf.setVisibility(View.VISIBLE);
                                request.setVisibility(View.VISIBLE);
                                free.setVisibility(View.INVISIBLE);

                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                checkboxTwo.setChecked(false);
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();


                    } else {
                        checkboxOne.setChecked(false);
                        checkboxThree.setChecked(false);

                        freeStatus = 1;
                        tempInvoiceStockController.updateFreeIssueRate(selected, null, checkboxTwo.getText().toString());
                        String split[] = checkboxTwo.getText().toString().split("/");
                        freeTotleList = (totleList / Integer.parseInt(split[0].trim())) * Integer.parseInt(split[1].trim());

                        freeTotleListReminig = freeTotleList;
                        textViewtotal.setText("Total Qty : " + totleList);
                        textViewfree.setText("Total Free : " + freeTotleList);
                        textViewreamingfree.setText("Remi Free : " + freeTotleListReminig);

                        textViewtotal2.setText("Total Qty : " + totleList);
                        textViewfree2.setText("Total Free : " + freeTotleList);
                        textViewreamingfree2.setText("Remi Free : " + freeTotleListReminig);


                        editshelf.setVisibility(View.INVISIBLE);
                        editrequest.setVisibility(View.INVISIBLE);
                        editfree.setVisibility(View.VISIBLE);

                        shelf.setVisibility(View.INVISIBLE);
                        request.setVisibility(View.INVISIBLE);
                        free.setVisibility(View.VISIBLE);

                    }


                } else {
                    freeStatus = 1;
                    tempInvoiceStockController.updateFreeIssueRate(selected, null, checkboxTwo.getText().toString());
                    String split[] = checkboxTwo.getText().toString().split("/");
                    freeTotleList = (totleList / Integer.parseInt(split[0].trim())) * Integer.parseInt(split[1].trim());

                    freeTotleListReminig = freeTotleList;
                    textViewtotal.setText("Total Qty : " + totleList);
                    textViewfree.setText("Total Free : " + freeTotleList);
                    textViewreamingfree.setText("Remi Free : " + freeTotleListReminig);

                    textViewtotal2.setText("Total Qty : " + totleList);
                    textViewfree2.setText("Total Free : " + freeTotleList);
                    textViewreamingfree2.setText("Remi Free : " + freeTotleListReminig);


                    editshelf.setVisibility(View.INVISIBLE);
                    editrequest.setVisibility(View.INVISIBLE);
                    editfree.setVisibility(View.VISIBLE);

                    shelf.setVisibility(View.INVISIBLE);
                    request.setVisibility(View.INVISIBLE);
                    free.setVisibility(View.VISIBLE);

                }


                tempInvoiceStockController.closeDatabase();
            }
        });


        checkboxThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempInvoiceStockController.openWritableDatabase();
                final int totleList = tempInvoiceStockController.getTotle(selected, null);
                int freetot = tempInvoiceStockController.getFreeTotle(selected, null);

                if (checkboxOne.isChecked() || checkboxTwo.isChecked()) {
                    if (freetot != 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceGen1Alternate.this);
                        builder.setTitle("Free Issues");
                        builder.setMessage("Do you want to clear free data");
                        builder.setCancelable(false);
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                tempInvoiceStockController.clearFreeqty(selected, null);
                                checkboxOne.setChecked(false);
                                checkboxTwo.setChecked(false);

                                freeStatus = 1;
                                tempInvoiceStockController.updateFreeIssueRate(selected, null, checkboxThree.getText().toString());
                                String split[] = checkboxTwo.getText().toString().split("/");
                                freeTotleList = (totleList / Integer.parseInt(split[0].trim())) * Integer.parseInt(split[1].trim());

                                freeTotleListReminig = freeTotleList;
                                textViewtotal.setText("Total Qty : " + totleList);
                                textViewfree.setText("Total Free : " + freeTotleList);
                                textViewreamingfree.setText("Remi Free : " + freeTotleListReminig);


                                editshelf.setVisibility(View.VISIBLE);
                                editrequest.setVisibility(View.VISIBLE);
                                editfree.setVisibility(View.INVISIBLE);

                                shelf.setVisibility(View.VISIBLE);
                                request.setVisibility(View.VISIBLE);
                                free.setVisibility(View.INVISIBLE);

                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                checkboxThree.setChecked(false);
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();


                    } else {
                        checkboxOne.setChecked(false);
                        checkboxTwo.setChecked(false);

                        freeStatus = 1;
                        tempInvoiceStockController.updateFreeIssueRate(selected, null, checkboxThree.getText().toString());
                        String split[] = checkboxThree.getText().toString().split("/");
                        freeTotleList = (totleList / Integer.parseInt(split[0].trim())) * Integer.parseInt(split[1].trim());

                        freeTotleListReminig = freeTotleList;
                        textViewtotal.setText("Total Qty : " + totleList);
                        textViewfree.setText("Total Free : " + freeTotleList);
                        textViewreamingfree.setText("Remi Free : " + freeTotleListReminig);

                        textViewtotal2.setText("Total Qty : " + totleList);
                        textViewfree2.setText("Total Free : " + freeTotleList);
                        textViewreamingfree2.setText("Remi Free : " + freeTotleListReminig);

                        editshelf.setVisibility(View.INVISIBLE);
                        editrequest.setVisibility(View.INVISIBLE);
                        editfree.setVisibility(View.VISIBLE);

                        shelf.setVisibility(View.INVISIBLE);
                        request.setVisibility(View.INVISIBLE);
                        free.setVisibility(View.VISIBLE);

                    }


                } else {
                    freeStatus = 1;
                    tempInvoiceStockController.updateFreeIssueRate(selected, null, checkboxThree.getText().toString());
                    String split[] = checkboxThree.getText().toString().split("/");
                    freeTotleList = (totleList / Integer.parseInt(split[0].trim())) * Integer.parseInt(split[1].trim());

                    freeTotleListReminig = freeTotleList;
                    textViewtotal.setText("Total Qty : " + totleList);
                    textViewfree.setText("Total Free : " + freeTotleList);
                    textViewreamingfree.setText("Remi Free : " + freeTotleListReminig);

                    textViewtotal2.setText("Total Qty : " + totleList);
                    textViewfree2.setText("Total Free : " + freeTotleList);
                    textViewreamingfree2.setText("Remi Free : " + freeTotleListReminig);

                    editshelf.setVisibility(View.INVISIBLE);
                    editrequest.setVisibility(View.INVISIBLE);
                    editfree.setVisibility(View.VISIBLE);

                    shelf.setVisibility(View.INVISIBLE);
                    request.setVisibility(View.INVISIBLE);
                    free.setVisibility(View.VISIBLE);

                }


                tempInvoiceStockController.closeDatabase();
            }
        });


        checkboxThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempInvoiceStockController.openWritableDatabase();
                final int totleList = tempInvoiceStockController.getTotle(selected, null);
                int freetot = tempInvoiceStockController.getFreeTotle(selected, null);

                if (checkboxOne.isChecked() || checkboxTwo.isChecked()) {
                    if (freetot != 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceGen1Alternate.this);
                        builder.setTitle("Free Issues");
                        builder.setMessage("Do you want to clear free data");
                        builder.setCancelable(false);
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                tempInvoiceStockController.clearFreeqty(selected, null);
                                checkboxOne.setChecked(false);
                                checkboxTwo.setChecked(false);

                                freeStatus = 1;
                                tempInvoiceStockController.updateFreeIssueRate(selected, null, checkboxThree.getText().toString());
                                String split[] = checkboxThree.getText().toString().split("/");
                                freeTotleList = (totleList / Integer.parseInt(split[0].trim())) * Integer.parseInt(split[1].trim());

                                freeTotleListReminig = freeTotleList;
                                textViewtotal.setText("Total Qty : " + totleList);
                                textViewfree.setText("Total Free : " + freeTotleList);
                                textViewreamingfree.setText("Remi Free : " + freeTotleListReminig);

                                textViewtotal2.setText("Total Qty : " + totleList);
                                textViewfree2.setText("Total Free : " + freeTotleList);
                                textViewreamingfree2.setText("Remi Free : " + freeTotleListReminig);


                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                checkboxThree.setChecked(false);
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();


                    } else {
                        checkboxOne.setChecked(false);
                        checkboxTwo.setChecked(false);

                        freeStatus = 1;
                        tempInvoiceStockController.updateFreeIssueRate(selected, null, checkboxThree.getText().toString());
                        String split[] = checkboxThree.getText().toString().split("/");
                        freeTotleList = (totleList / Integer.parseInt(split[0].trim())) * Integer.parseInt(split[1].trim());

                        freeTotleListReminig = freeTotleList;
                        textViewtotal.setText("Total Qty : " + totleList);
                        textViewfree.setText("Total Free : " + freeTotleList);
                        textViewreamingfree.setText("Remi Free : " + freeTotleListReminig);

                        textViewtotal2.setText("Total Qty : " + totleList);
                        textViewfree2.setText("Total Free : " + freeTotleList);
                        textViewreamingfree2.setText("Remi Free : " + freeTotleListReminig);


                        editshelf.setVisibility(View.INVISIBLE);
                        editrequest.setVisibility(View.INVISIBLE);
                        editfree.setVisibility(View.VISIBLE);

                        shelf.setVisibility(View.INVISIBLE);
                        request.setVisibility(View.INVISIBLE);
                        free.setVisibility(View.VISIBLE);


                    }


                } else {
                    freeStatus = 1;
                    tempInvoiceStockController.updateFreeIssueRate(selected, null, checkboxThree.getText().toString());
                    String split[] = checkboxThree.getText().toString().split("/");
                    freeTotleList = (totleList / Integer.parseInt(split[0].trim())) * Integer.parseInt(split[1].trim());

                    freeTotleListReminig = freeTotleList;
                    textViewtotal.setText("Total Qty : " + totleList);
                    textViewfree.setText("Total Free : " + freeTotleList);
                    textViewreamingfree.setText("Remi Free : " + freeTotleListReminig);

                    textViewtotal2.setText("Total Qty : " + totleList);
                    textViewfree2.setText("Total Free : " + freeTotleList);
                    textViewreamingfree2.setText("Remi Free : " + freeTotleListReminig);


                    editshelf.setVisibility(View.INVISIBLE);
                    editrequest.setVisibility(View.INVISIBLE);
                    editfree.setVisibility(View.VISIBLE);

                    shelf.setVisibility(View.INVISIBLE);
                    request.setVisibility(View.INVISIBLE);
                    free.setVisibility(View.VISIBLE);

                }


                tempInvoiceStockController.closeDatabase();
            }
        });


        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSearch.setEnabled(true);
                dialogBox.dismiss();
            }
        });


        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int Totle = tempInvoiceStockController.getTotle(selected, null);
                if (checkboxTwo.isChecked() || checkboxThree.isChecked() || checkboxOne.isChecked()) {

                    try {
                        tempInvoiceStockController.updateFreeQuantity(colerchartProductCode, colerchartProductBatch, editfree.getText().toString());
                        String split[] = tempInvoiceStockController.getFreeIssueRate(selected, null).split("/");
                        int remain = (Totle / Integer.parseInt(split[0])) * Integer.parseInt(split[1]);

                        if (remain < tempInvoiceStockController.getFreeTotle(selected, null)) {
                            Toast.makeText(InvoiceGen1Alternate.this, "Remaining free quantity exceed ,Please try agin", Toast.LENGTH_LONG).show();
                            textViewfree.setText("0");
                            tempInvoiceStockController.updateFreeQuantity(colerchartProductCode, colerchartProductBatch, "0");

                        } else {

                            tempInvoiceStockController.updateDiscountAlloed(colerchartProductCode, colerchartProductBatch, Boolean.toString(false));

                        }
                        layoutDefoult.setVisibility(View.VISIBLE);
                        layoutRequest.setVisibility(View.INVISIBLE);
                    } catch (NumberFormatException nfe) {
                        Toast.makeText(context, "Your value can not be null", Toast.LENGTH_LONG).show();
                    }

                } else {
                    try {
                        if (Integer.parseInt(colerchartProductStock) < Integer.parseInt(editrequest.getText().toString())) {
                            Toast.makeText(context, "Your order quantity exceed Stock quantity", Toast.LENGTH_LONG).show();
                            editrequest.setText(colerchartProductStock);
                        } else {
                            // Totle = Totle + Integer.parseInt(editrequest.getText().toString());
                            tempInvoiceStockController.updateShelfQuantity(colerchartProductCode, colerchartProductBatch, editshelf.getText().toString());
                            tempInvoiceStockController.updateRequestQuantity(colerchartProductCode, colerchartProductBatch, editrequest.getText().toString());
                            tempInvoiceStockController.updateNormalQuantity(colerchartProductCode, colerchartProductBatch, editrequest.getText().toString());

                            layoutDefoult.setVisibility(View.VISIBLE);
                            layoutRequest.setVisibility(View.INVISIBLE);
                        }
                    } catch (NumberFormatException nfe) {
                        Toast.makeText(context, "Your value can not be null", Toast.LENGTH_LONG).show();
                    }
                }

                tempInvoiceStockController.openReadableDatabase();


                int totle = tempInvoiceStockController.getTotle(selected, null);
                textViewtotal.setText("Total Qty :" + totle);
                textViewtotal2.setText("Total Qty :" + totle);
                String split[] = tempInvoiceStockController.getFreeIssueRate(selected, null).split("/");
                if (split[0].equals("0")) {

                } else {
                    int totleFree = (totle / Integer.parseInt(split[0])) * Integer.parseInt(split[1]);
                    int remain = totleFree - tempInvoiceStockController.getFreeTotle(selected, null);

                    textViewfree.setText("Total Free : " + String.valueOf(totleFree));
                    textViewreamingfree.setText("Remi Free : " + String.valueOf(remain));
                    textViewfree2.setText("Total Free : " + String.valueOf(totleFree));
                    textViewreamingfree2.setText("Remi Free : " + String.valueOf(remain));

                }

                productTablefill(1);


                final TemporaryInvoice temInvies = new TemporaryInvoice(getApplicationContext());
                temInvies.openWritableDatabase();

                prductListImage = temInvies.getTempDataForTable(selected, selectedCategory, actSelectedProduct, 1);
                productAdapterImage = new ProductImagesAdapter(InvoiceGen1Alternate.this, prductListImage);
                temInvies.closeDatabase();
                recyclerViewImages.setAdapter(productAdapterImage);
              /*  InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(recyclerViewImages.getWindowToken(), 0);
                recyclerViewImages.setAdapter(productAdapter);*/
            }
        });


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String split[] = textViewreamingfree.getText().toString().split(":");

                if (!split[1].trim().equals("0")) {
                    Toast.makeText(context, "Please fill the Free issues", Toast.LENGTH_LONG).show();
                } else {
                    tempInvoiceStockController.closeDatabase();
                    prosessStatus = 0;
                    btnSearch.setEnabled(true);
                    dialogBox.dismiss();
                    final TemporaryInvoice temInvies = new TemporaryInvoice(getApplicationContext());
                    temInvies.openWritableDatabase();
                    prductListImage.clear();
                    prductListImage = temInvies.getTempDataForTable(selected, selectedCategory, actSelectedProduct, 1);
                    productAdapterImage = new ProductImagesAdapter(InvoiceGen1Alternate.this, prductListImage);
                    temInvies.closeDatabase();
                    recyclerViewImages.setAdapter(productAdapterImage);
                    productTablefill(1);
                }


            }
        });

        dialogBox.show();


    }

    public void lodeSelectCode(String icode, String batch, String shelf, String request, String order, String free, String stock) {

        editshelf.setSelection(editshelf.length());
        layoutRequest.setVisibility(View.VISIBLE);
        layoutDefoult.setVisibility(View.INVISIBLE);
        colerchartProductCode = icode;
        colerchartProductBatch = batch;
        colerchartProductShelf = shelf;
        colerchartProductRequest = request;
        colerchartProductOrder = order;
        colerchartProductFree = free;
        colerchartProductStock = stock;

        selectProduct.setText("Selected Product : " + icode);

        if (colerchartProductShelf.equals("0")) {
            editshelf.setText("");
        } else {
            editshelf.setText(colerchartProductShelf);
        }
        if (colerchartProductRequest.equals("0")) {
            editrequest.setText("");
        } else {
            editrequest.setText(colerchartProductRequest);
        }
        if (colerchartProductFree.equals("0")) {
            editfree.setText("");
        } else {
            editfree.setText(colerchartProductFree);
        }


    }

    public void lodeSelectedProducutCode(final String icode, String dis, final String batch, String productstock, String price, String shelf, String request, String order, String free, String discount) {

        productCode = icode;
        productBatch = batch;
        productStock = productstock;

        textCode.setText(icode);
        textDiscr.setText(dis);
        textBatch.setText(batch);
        textStock.setText(productstock);
        textPrice.setText(price);

        edtShelf.setText(shelf);
        edtRequest.setText(request);
        edtOrder.setText(order);
        edtFree.setText(free);
        edtDiscountProduct.setText(discount);

        if (discount.equals("0.0")) {
            edtDiscountProduct.setEnabled(false);
        } else {
            edtDiscountProduct.setEnabled(true);
        }


        if (order.equals("0")) {
            edtRequest.setEnabled(false);
            edtOrder.setEnabled(false);
            edtFree.setEnabled(false);

        } else {
            edtRequest.setEnabled(true);
            edtOrder.setEnabled(true);
            edtFree.setEnabled(true);

        }


        layout.setVisibility(View.VISIBLE);
    }

    public void oderDetailsEdittextOntextChange() {


        edtShelf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    edtShelf.setText("");
                }
            }
        });
        edtShelf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if ((!charSequence.toString().isEmpty() || !charSequence.toString().equals(""))) {
                    edtDiscountProduct.setEnabled(true);
                    tempInvoiceStockController.updateShelfQuantity(productCode, productBatch, charSequence.toString());
                    edtRequest.setEnabled(true);
                    edtOrder.setEnabled(true);
                    edtFree.setEnabled(true);


                } else {
                    tempInvoiceStockController.updateShelfQuantity(productCode, productBatch, "0");

                }
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        edtRequest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    edtRequest.setText("");
                }
            }
        });

        edtRequest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() || !charSequence.toString().equals("")) {
                    tempInvoiceStockController.updateRequestQuantity(productCode, productBatch, charSequence.toString());
                    if (Double.parseDouble(textStock.getText().toString()) < Double.parseDouble(edtRequest.getText().toString())) {
                        Toast.makeText(InvoiceGen1Alternate.this, "Enter valid quantity", Toast.LENGTH_LONG).show();
                        edtOrder.setText(productStock);
                    } else {
                        edtOrder.setText(charSequence.toString());
                    }
                } else {
//
                    tempInvoiceStockController.updateRequestQuantity(productCode, productBatch, "0");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtOrder.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    edtOrder.setText("");
                }
            }
        });
        edtOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ((!charSequence.toString().isEmpty() || !charSequence.toString().equals(""))) {
                    tempInvoiceStockController.updateNormalQuantity(productCode, productBatch, charSequence.toString());
                } else {
                    tempInvoiceStockController.updateNormalQuantity(productCode, productBatch, "0");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                productTablefill(1);
            }
        });

        edtFree.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    edtFree.setText("");
                }
            }
        });

        edtFree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if ((!charSequence.toString().isEmpty() || !charSequence.toString().equals(""))) {
                    int stock = Integer.parseInt(textStock.getText().toString());
                    int request = 0;
                    if (!edtOrder.getText().toString().equals("")) {
                        request = Integer.parseInt(edtOrder.getText().toString());
                    }
                    int free = Integer.parseInt(edtFree.getText().toString());

                    if (iswebApprovalActive == false) {
                        if (stock - request >= free) { // check whether entered free quantity is smaller than stock -  requested
                            tempInvoiceStockController.updateFreeQuantity(productCode, productBatch, charSequence.toString());

                            edtDiscountProduct.setEnabled(false);
                            tempInvoiceStockController.updateDiscountAlloed(productCode, productBatch, Boolean.toString(false));

                        } else {
                            Toast.makeText(InvoiceGen1Alternate.this, "Not enough quantity", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        tempInvoiceStockController.updateFreeQuantity(productCode, productBatch, charSequence.toString());
                        edtDiscountProduct.setEnabled(false);
                        tempInvoiceStockController.updateDiscountAlloed(productCode, productBatch, Boolean.toString(false));
                    }


                } else {
                    tempInvoiceStockController.updateFreeQuantity(productCode, productBatch, "0");
                    edtDiscountProduct.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                productTablefill(1);
            }
        });

        edtDiscountProduct.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    edtDiscountProduct.setText("");
                }
            }
        });

        edtDiscountProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ((!charSequence.toString().isEmpty() || !charSequence.toString().equals(""))) {
                    if (charSequence.length() < 4) {
                        if (Double.parseDouble(charSequence.toString()) <= 100) {
                            tempInvoiceStockController.updateDicount(productCode, productBatch, charSequence.toString());
                            if (Double.parseDouble(charSequence.toString()) > 0) {

                                edtFree.setEnabled(false);
                                tempInvoiceStockController.updateFreeAlloed(productCode, productBatch, Boolean.toString(false));
                            } else {
                                tempInvoiceStockController.updateFreeAlloed(productCode, productBatch, Boolean.toString(true));
                            }
                        } else {
                            Toast.makeText(InvoiceGen1Alternate.this, "Enter valid discount", Toast.LENGTH_LONG).show();
                            edtDiscountProduct.setText("0.0");
                        }
                    } else {
                        Toast.makeText(InvoiceGen1Alternate.this, "Enter valid amount", Toast.LENGTH_LONG).show();
                        edtDiscountProduct.setText("0.0");
                    }
                } else {
                    tempInvoiceStockController.updateDicount(productCode, productBatch, "0.0");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                productTablefill(1);
            }
        });


    }

}
