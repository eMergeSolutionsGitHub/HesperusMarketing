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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Amila on 11/12/15.
 */
public class InvoiceGen1Alternate extends Activity {

    String selected, selectedCategory, rowId, pharmacyId, startTime, collectionDate = "", releaseDate = "", chequeNumber = "";
    String colerchartProductCode = "0", colerchartProductBatch = "0", colerchartProductShelf = "0", colerchartProductRequest = "0", colerchartProductOrder = "0", colerchartProductFree = "0", colerchartProductStock = "0";

    private Boolean iswebApprovalActive = true;
    private Boolean isChanged = false;
    boolean chequeEnabled = false;
    int prosessStatus = 0;
    public static int freeStatus = 0, freeTotleList = 0, freeTotleListReminig;


    Spinner spPrinciple, spCategory;
    private TableLayout tblTest;
    private Button btnAdd, btnSearch;
    EditText editDiscount, editshelf, editrequest, editfree;
    Dialog dialogBox;
    CheckBox checkboxOne, checkboxTwo, checkboxThree;
    TextView textViewtotal, textViewtotal2, textViewfree, textViewfree2, textViewreamingfree, textViewreamingfree2, selectProduct;
    RelativeLayout layoutDefoult, layoutRequest;


    AlertDialog.Builder alertCancel;


    ArrayAdapter<String> principleAdapter;
    ArrayAdapter<String> categoryAdapter;
    private ArrayList<String> principleList;
    private ArrayList<String> categoryList;
    private ArrayList<TempInvoiceStock> prductList;
    ArrayList<ReturnProduct> returnProductsArray;
    ArrayList<SelectedProduct> mergeList;
    ArrayList<String[]> itemCodeDetailList;

    private Products productController;
    TemporaryInvoice tempInvoiceStockController;

    private RecyclerView recyclerView;
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
        categoryList = new ArrayList<>();
        prductList = new ArrayList<>();
        returnProductsArray = new ArrayList<ReturnProduct>();
        mergeList = new ArrayList<>();
        itemCodeDetailList = new ArrayList<String[]>();

        spPrinciple = (Spinner) findViewById(R.id.spPrinciple);
        spCategory = (Spinner) findViewById(R.id.spCategory);

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
        categoryAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.single_list_item, categoryList);
        spCategory.setAdapter(categoryAdapter);


        spPrinciple.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // editDiscount.setText("");
                //  refreshViewOnSelection(0);
                productController.openReadableDatabase();
                selected = spPrinciple.getSelectedItem().toString();
                categoryList = productController.getCategoryListForPriciple(selected);
                categoryAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.single_list_item, categoryList);
                spCategory.setAdapter(categoryAdapter);
                productTablefill(0);
                productController.closeDatabase();
                editDiscount.setText("");

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
                    int free =tempInvoiceStockController.getFreeTotle(selected,selectedCategory);
                    if(free!=0){
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
                    }else {
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
                showDialogProductImage(InvoiceGen1Alternate.this);

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

        prductList= tem.getTempDataForTable(selected, selectedCategory,status);


        populateProductTableNew(prductList);
        productController.closeDatabase();
    }
    private void populateProductTableNew(ArrayList<TempInvoiceStock> prductList) {

        adapter = new RecyclerListProductAdapter(this,prductList);
        recyclerView.setAdapter(adapter);

    }
  /*  private void populateProductTable(ArrayList<Product> prductList) {

        tblTest.removeAllViews();
        try {
            for (final Product product : prductList) {

                tempInvoiceStockController.openReadableDatabase();
                final TempInvoiceStock stock = tempInvoiceStockController.getTempData(product.getCode(), product.getBatchNumber());
                TableRow dataRow = new TableRow(this);
                TableRow.LayoutParams lpInner = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                lpInner.weight = 1;
                dataRow.setLayoutParams(lpInner);


                final TextView proNumber = new TextView(this);
                proNumber.setText(product.getCode());
                proNumber.setTextColor(Color.BLACK);
                proNumber.setMinWidth(100);
                proNumber.setTextSize(12);


                TextView proName = new TextView(this);
                proName.setText(product.getProDes());
                proName.setTextColor(Color.BLUE);
                proName.setSingleLine(false);
                proName.setMaxLines(3);
                proName.setMinWidth(300);
                proName.setWidth(300);
                proName.setTextSize(12);
                proName.setLines(3);


                //2
                TextView proBatch = new TextView(this);
                proBatch.setText(product.getBatchNumber());
                proBatch.setTextColor(Color.BLACK);
                proName.setTypeface(null, Typeface.BOLD);
                proBatch.setMinWidth(100);
                proBatch.setTextSize(12);


                //3


                TextView proStock = new TextView(this);
                proStock.setText("" + stock.getStock());
                proStock.setTextColor(Color.RED);
                proName.setTypeface(null, Typeface.BOLD);
                proStock.setMinWidth(80);


                //4
                TextView proPrice = new TextView(this);
                proPrice.setText("" + product.getSellingPrice());
                proPrice.setTextColor(Color.RED);
                proPrice.setMinWidth(60);
                proPrice.setTypeface(null, Typeface.BOLD);


                final EditText edSQuantity = new EditText(this);
                final EditText edNormal = new EditText(this);
                final EditText edQuantity = new EditText(this);
                final EditText edRequest = new EditText(this);
                final EditText edDiscount = new EditText(this);




                edSQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
                edSQuantity.setLayoutParams(lpInner);
                edSQuantity.setBackgroundResource(R.drawable.cell_border);
                edSQuantity.setText("" + stock.getShelfQuantity());
                edSQuantity.setSelection(edSQuantity.getText().length());
                edSQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus == true) {
                            edSQuantity.setText("");
                        }
                    }
                });
                edSQuantity.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == 66) {
                            if (edSQuantity.getText().toString().equals("")) {
                                edSQuantity.setText("0");
                            }
                        }
                        return false;
                    }
                });
                edSQuantity.setMinWidth(80);
                edSQuantity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        TableRow row = (TableRow) edSQuantity.getParent();
                        TextView tvDynaProNo = (TextView) row.getChildAt(0);
                        TextView tvDynaBatchNo = (TextView) row.getChildAt(2);

                        if ((!s.toString().isEmpty() || !s.toString().equals(""))) {
                            tempInvoiceStockController.updateShelfQuantity(tvDynaProNo.getText().toString(), tvDynaBatchNo.getText().toString(), s.toString());
                            ((EditText) row.getChildAt(6)).setEnabled(true);
                            ((EditText) row.getChildAt(7)).setEnabled(true);
                            ((EditText) row.getChildAt(8)).setEnabled(true);
                            ((EditText) row.getChildAt(9)).setEnabled(true);
                        }

                        isChanged = true;
                    }
                });


                edRequest.setInputType(InputType.TYPE_CLASS_NUMBER);
                edRequest.setLayoutParams(lpInner);
                edRequest.setBackgroundResource(R.drawable.cell_border);
                if(stock.getRequestQuantity().equals("0")){
                    edRequest.setEnabled(false);
                }else {
                    edRequest.setEnabled(true);
                }

                edRequest.setText("" + stock.getRequestQuantity());
                edRequest.setSelection(edRequest.getText().length());
                edRequest.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == 66) {
                            if (edRequest.getText().toString().equals("")) {
                                edRequest.setText("0");
                            }
                        }
                        return false;
                    }
                });
                edRequest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus == true) {
                            edRequest.setText("");
                        }
                    }
                });
                //check whether entered quantity is valid depend on the approval
                edRequest.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            TableRow row = (TableRow) edRequest.getParent();
                            TextView tvDynaProNo = (TextView) row.getChildAt(0);
                            TextView tvDynaBatchNo = (TextView) row.getChildAt(2);

                            TextView tvDynaStock = (TextView) row.getChildAt(3);
                            EditText edDynaNormal = (EditText) row.getChildAt(7);
                            if (!s.toString().isEmpty() || !s.toString().equals("")) {

                                tempInvoiceStockController.updateRequestQuantity(tvDynaProNo.getText().toString(), tvDynaBatchNo.getText().toString(), s.toString());

                                if (iswebApprovalActive == true) {

                                    if (Double.parseDouble(tvDynaStock.getText().toString()) < Double.parseDouble(edRequest.getText().toString())) {
                                        Toast toast = Toast.makeText(InvoiceGen1Alternate.this, "Enter valid quantity", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        edDynaNormal.setText(tvDynaStock.getText().toString());
                                    } else {
                                        edDynaNormal.setText(s.toString());
                                    }

                                } else {
                                    if (Double.parseDouble(tvDynaStock.getText().toString()) < Double.parseDouble(edRequest.getText().toString())) {
                                        Toast toast = Toast.makeText(InvoiceGen1Alternate.this, "Enter valid quantity", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        edDynaNormal.setText(tvDynaStock.getText().toString());

                                    } else {
                                        edDynaNormal.setText(s.toString());
                                    }

                                }

                            } else {
                                edDynaNormal.setText("0");
                            }
                            isChanged = true;
                        } catch (Exception e) {

                        }


                    }

                    @Override
                    public void afterTextChanged(Editable s) {


                    }
                });
                edRequest.setMinWidth(80);
                edQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
                edQuantity.setLayoutParams(lpInner);
                edQuantity.setBackgroundResource(R.drawable.cell_border);
                if(stock.getFreeQuantity().equals("0")){
                    edQuantity.setEnabled(false);
                }else {
                    edQuantity.setEnabled(true);
                }

                edQuantity.setText("" + stock.getFreeQuantity());
                edQuantity.setMinWidth(80);
                edQuantity.setSelection(edQuantity.getText().length());
                edQuantity.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {


                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        TableRow row = (TableRow) edQuantity.getParent();

                        TextView tvDynaProNo = (TextView) row.getChildAt(0);
                        TextView tvDynaBatchNo = (TextView) row.getChildAt(2);
                        TextView tvDynaStock = (TextView) row.getChildAt(3);
                        EditText edDynaNormal = (EditText) row.getChildAt(7);

                        if ((!s.toString().isEmpty() || !s.toString().equals(""))) {
                            tempInvoiceStockController.openWritableDatabase();


                            int stock = Integer.parseInt(tvDynaStock.getText().toString());
                            int request = 0;
                            if (!edDynaNormal.getText().toString().equals("")) {
                                request = Integer.parseInt(edDynaNormal.getText().toString());
                            }
                            int free = Integer.parseInt(edQuantity.getText().toString());


                            if (iswebApprovalActive == false) {
                                if (stock - request >= free) { // check whether entered free quantity is smaller than stock -  requested
                                    tempInvoiceStockController.updateFreeQuantity(tvDynaProNo.getText().toString(), tvDynaBatchNo.getText().toString(), s.toString());

                                    ((EditText) row.getChildAt(9)).setEnabled(false);
                                    tempInvoiceStockController.updateDiscountAlloed(tvDynaProNo.getText().toString(), tvDynaBatchNo.getText().toString(), Boolean.toString(false));

                                } else {
                                    Toast freeToast = Toast.makeText(InvoiceGen1Alternate.this, "Not enough quantity", Toast.LENGTH_SHORT);
                                    freeToast.setGravity(Gravity.CENTER, 0, 0);
                                    freeToast.show();

                                }
                            } else {
                                tempInvoiceStockController.updateFreeQuantity(tvDynaProNo.getText().toString(), tvDynaBatchNo.getText().toString(), s.toString());
                                ((EditText) row.getChildAt(9)).setEnabled(false);
                                tempInvoiceStockController.updateDiscountAlloed(tvDynaProNo.getText().toString(), tvDynaBatchNo.getText().toString(), Boolean.toString(false));
                            }

                        } else {
                            tempInvoiceStockController.updateFreeQuantity(tvDynaProNo.getText().toString(), tvDynaBatchNo.getText().toString(), "0");

                        }


                        isChanged = true;
                    }
                });


                edNormal.setInputType(InputType.TYPE_CLASS_NUMBER);
                edNormal.setLayoutParams(lpInner);
                edNormal.setBackgroundResource(R.drawable.cell_border);
                if(stock.getNormalQuantity().equals("0")){
                    edNormal.setEnabled(false);
                }else {
                    edNormal.setEnabled(true);
                }
                edNormal.setText("" + stock.getNormalQuantity());
                edNormal.setMinWidth(80);
                edNormal.setSelection(edNormal.getText().length());
                edNormal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus == true) {
                            edNormal.setText("");
                        }
                    }
                });
                edNormal.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == 66) {
                            if (edNormal.getText().toString().equals("")) {
                                edNormal.setText("0");
                            }
                        }
                        return false;
                    }
                });
                edNormal.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        TableRow row = (TableRow) edNormal.getParent();
                        TextView tvDynaProNo = (TextView) row.getChildAt(0);
                        TextView tvDynaBatchNo = (TextView) row.getChildAt(2);
                        if ((!s.toString().isEmpty() || !s.toString().equals(""))) {
                            tempInvoiceStockController.updateNormalQuantity(tvDynaProNo.getText().toString(), tvDynaBatchNo.getText().toString(), s.toString());


                        }
                        isChanged = true;
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                edDiscount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                edDiscount.setLayoutParams(lpInner);
                edDiscount.setBackgroundResource(R.drawable.cell_border);
                if(stock.getPercentage()==0 ||stock.getPercentage()==0.0){
                    edDiscount.setEnabled(false);
                }else {
                    edDiscount.setEnabled(true);
                }
                edDiscount.setText("" + stock.getPercentage());
                edDiscount.setMinWidth(80);
                edDiscount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus == true) {
                            edDiscount.setText("");
                        }
                    }
                });
                edDiscount.setSelection(edQuantity.getText().length());

                edDiscount.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == 66) {
                            if (edDiscount.getText().toString().equals("")) {
                                edDiscount.setText("0.0");
                            }
                        }
                        return false;
                    }
                });
                edDiscount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        TableRow row = (TableRow) edDiscount.getParent();
                        TextView tvDynaProNo = (TextView) row.getChildAt(0);
                        TextView tvDynaBatchNo = (TextView) row.getChildAt(2);

                        if ((!s.toString().isEmpty() || !s.toString().equals(""))) {


                            if (s.length() < 4) {
                                if (Double.parseDouble(s.toString()) <= 100) {
                                    tempInvoiceStockController.updateDicount(tvDynaProNo.getText().toString(), tvDynaBatchNo.getText().toString(), s.toString());
                                    if (Double.parseDouble(s.toString()) > 0) {
                                        ((EditText) row.getChildAt(9)).setEnabled(false);
                                        tempInvoiceStockController.updateFreeAlloed(tvDynaProNo.getText().toString(), tvDynaBatchNo.getText().toString(), Boolean.toString(false));
                                    } else {
                                        ((EditText) row.getChildAt(9)).setEnabled(true);
                                        tempInvoiceStockController.updateFreeAlloed(tvDynaProNo.getText().toString(), tvDynaBatchNo.getText().toString(), Boolean.toString(true));
                                    }
                                } else {
                                    Toast toast1 = Toast.makeText(InvoiceGen1Alternate.this, "Enter valid discount", Toast.LENGTH_LONG);
                                    toast1.setGravity(Gravity.CENTER, 0, 0);
                                    toast1.show();
                                    edDiscount.setText("0.0");
                                }
                            } else {
                                Toast toast1 = Toast.makeText(InvoiceGen1Alternate.this, "Enter valid amount", Toast.LENGTH_LONG);
                                toast1.setGravity(Gravity.CENTER, 0, 0);
                                toast1.show();
                                edDiscount.setText("0.0");
                            }


                        }

                        isChanged = true;
                    }
                });


                if (iswebApprovalActive == false) {
                    if (stock.getStock() == 0) {
                        edQuantity.setEnabled(false);
                        edNormal.setEnabled(false);
                        edDiscount.setEnabled(false);
                    }
                } else {
                    if (stock.getStock() == 0) {
                        edQuantity.setEnabled(true);
                        edNormal.setEnabled(true);
                        edDiscount.setEnabled(true);
                    }
                }


                dataRow.addView(proNumber, 0);
                dataRow.addView(proName, 1);
                dataRow.addView(proBatch, 2);
                dataRow.addView(proStock, 3);
                dataRow.addView(proPrice, 4);

                dataRow.addView(edSQuantity, 5);
                dataRow.addView(edRequest, 6);
                dataRow.addView(edNormal, 7);
                dataRow.addView(edQuantity, 8);
                dataRow.addView(edDiscount, 9);


                tblTest.addView(dataRow);


            }


        } catch (Exception e) {
            Log.e("loading view error", "task error");
        }
        tempInvoiceStockController.closeDatabase();
    }*/

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
        super.onBackPressed();
        alertCancel = new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Are you sure you want Cancel this Invoice?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // Intent customerItineraryListIntent = new Intent("com.HesperusMarketing.channelbridge.ITINERARYLIST");
                                tempInvoiceStockController.openWritableDatabase();
                                tempInvoiceStockController.deleteAllRecords();
                                tempInvoiceStockController.closeDatabase();
                                finish();

                                //  startActivity(customerItineraryListIntent);
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        alertCancel.show();

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


        final ArrayList<ProductImages> albumItem = new ArrayList<ProductImages>();
        albumItem.clear();
        final ProductImagesAdapter productAdapter = new ProductImagesAdapter(this, itemCodeDetailList, albumItem);

        final TemporaryInvoice temInvies = new TemporaryInvoice(getApplicationContext());
        temInvies.openWritableDatabase();


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

        final GridView gridView = (GridView) dialogBox.findViewById(R.id.gridView_album);
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


/*
        for (final Product product : prductList) {
            TempInvoiceStock stock = tempInvoiceStockController.getTempData(product.getCode(), product.getBatchNumber());
            File SNP01 = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Channel_Bridge_Images" + File.separator + product.getCode() + ".jpg");
            albumItem.add(new ProductImages(product.getCode(), product.getBatchNumber(), String.valueOf(SNP01), stock.getShelfQuantity(),
                    stock.getRequestQuantity(), stock.getNormalQuantity(), stock.getFreeQuantity(), String.valueOf(stock.getStock())));


        }
*/
        temInvies.closeDatabase();
        gridView.setAdapter(productAdapter);


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


                albumItem.clear();


/*
                for (final Product product : prductList) {
                    tempInvoiceStockController.openReadableDatabase();
                    TempInvoiceStock newStock = tempInvoiceStockController.getTempData(product.getCode(), product.getBatchNumber());
                    File SNP01 = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Channel_Bridge_Images" + File.separator + product.getCode() + ".jpg");
                    albumItem.add(new ProductImages(product.getCode(), product.getBatchNumber(), String.valueOf(SNP01), newStock.getShelfQuantity(),
                            newStock.getRequestQuantity(), newStock.getNormalQuantity(), newStock.getFreeQuantity(), String.valueOf(newStock.getStock())));


                }
*/


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(gridView.getWindowToken(), 0);
                gridView.setAdapter(productAdapter);
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

        selectProduct.setText("Selected Product : "+icode);

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

    public void lodeSelectedProducutCode(String icode,String dis, String batch,String productStock,String price, String shelf, String request, String order, String free, String discount) {


        System.out.println("sssssssssssssssds icode"+icode);


    }



}
