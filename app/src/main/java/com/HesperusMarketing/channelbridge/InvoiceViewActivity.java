package com.HesperusMarketing.channelbridge;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.HesperusMarketing.Entity.Product;
import com.HesperusMarketing.channelbridgedb.Customers;
import com.HesperusMarketing.channelbridgedb.Invoice;
import com.HesperusMarketing.channelbridgedb.InvoicedProducts;
import com.HesperusMarketing.channelbridgedb.Itinerary;
import com.HesperusMarketing.channelbridgedb.ProductRepStore;
import com.HesperusMarketing.channelbridgedb.ProductReturns;
import com.HesperusMarketing.channelbridgedb.Products;
import com.HesperusMarketing.channelbridgedb.Reps;

public class InvoiceViewActivity extends Activity {

    String rowId, pharmacyId, invoiceId, customerName, custAddress,cusTel="0112978188";
    List<String[]> invoicedProducts = new ArrayList<String[]>();
    ArrayList<String> invoiceData = new ArrayList<String>();

    TextView tViewCustomerName, tViewInvoiceNumber, tViewAddress, tViewDate,
            tViewTotalItems, tViewTotalAmount, tViewCash, tViewCheque,
            tViewCredit, tViewRemain, tViewMarketReturn, tViewDiscount,
            tViewNeedToPay;
    TableLayout tblInvoicedItems;
    Button btnDone, btnPrint, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_view);
        tblInvoicedItems = (TableLayout) findViewById(R.id.tlInvoice);

        tViewCustomerName = (TextView) findViewById(R.id.tvCustomerName);
        tViewInvoiceNumber = (TextView) findViewById(R.id.tvInvoiceNumber);
        tViewAddress = (TextView) findViewById(R.id.tvAddress);
        tViewDate = (TextView) findViewById(R.id.tvDate);
        tViewTotalItems = (TextView) findViewById(R.id.tvTotalQuantity);
        tViewTotalAmount = (TextView) findViewById(R.id.tvTotal);
        tViewCash = (TextView) findViewById(R.id.tvCash);
        tViewCredit = (TextView) findViewById(R.id.tvCredit);
        tViewCheque = (TextView) findViewById(R.id.tvCheque);
        tViewRemain = (TextView) findViewById(R.id.tvRemain);
        tViewMarketReturn = (TextView) findViewById(R.id.tvMarketReturn);
        tViewNeedToPay = (TextView) findViewById(R.id.tvNeedToPay);
        tViewDiscount = (TextView) findViewById(R.id.tvDiscount);

        btnDone = (Button) findViewById(R.id.bDone);
        btnPrint = (Button) findViewById(R.id.bPrint);
        btnCancel = (Button) findViewById(R.id.bCancel);

        if (savedInstanceState != null) {
            setBundleData(savedInstanceState);
        } else {
            getDataFromPreviousActivity();
        }


        getAllInvoicedProducts();
        setInitialData(invoicedProducts, invoiceData);
        populateInvoiceTable(invoicedProducts);

        btnPrint.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                String repName = getRepName();
                printFunction(customerName, custAddress, repName);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent lastInvoice = new Intent(InvoiceViewActivity.this,
                        LastInvoiceActivity.class);
                finish();
                Bundle extras = new Bundle();
                extras.putString("Id", rowId);
                extras.putString("PharmacyId", pharmacyId);

                lastInvoice.putExtras(extras);
                startActivity(lastInvoice);
            }
        });

    }

    protected void printFunction(String custName, String address, String repName) {
        // TODO Auto-generated method stub
        //try {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean prePrintInvoiceFormatEnabled = sharedPreferences.getBoolean("cbPrefPrePrintInvoice", true);
            String repId = sharedPreferences.getString("RepId", "-1");

            if (!prePrintInvoiceFormatEnabled) {


                // boolean flag = true;
                int count = 48;
                int spaceCount = 8;

                Invoice invoice = new Invoice(this);
                invoice.openReadableDatabase();
                ArrayList<String> invoiceD = invoice.getInvoiceDetailsByInvoiceId(invoiceId);
                invoice.closeDatabase();

                ArrayList<String[]> returnedProductList = new ArrayList<String[]>();

                if (invoiceId != "") {

                    ProductReturns productReturns = new ProductReturns(
                            InvoiceViewActivity.this);
                    productReturns.openReadableDatabase();
                    returnedProductList = productReturns
                            .getReturnDetailsByInvoiceId(invoiceId);
                    productReturns.closeDatabase();

                }

                Reps reps = new Reps(this);
                reps.openReadableDatabase();
                ArrayList<String> delearDetails = reps.getRepDetailsForPrinting(repId);
                reps.closeDatabase();

                String dealerName = delearDetails.get(1).trim();
                String dealerCity = delearDetails.get(2).trim();
                String dealerTel = delearDetails.get(3).trim();

                if (dealerName.length() > 18) {
                    dealerName = dealerName.substring(0, 18);
                }

                if (dealerCity.length() > 18) {
                    dealerCity = dealerCity.substring(0, 18);
                }

                String invoiceValue = invoiceD.get(3);// IMPORTANT
                String returns = invoiceD.get(7);// IMPORTANT

                //printData = printData
                //+ "--------------------------------------------\n";
                Log.w("IG3", "invoiceD.get(11) 332 " + invoiceD.get(11));

                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss.SSS");

                // Date dateObj = dateFormat.parse(invoiceD.get(11));

                // String date = new SimpleDateFormat("yyyy-MM-dd").format(dateObj);
                String date = invoiceD.get(11).substring(0, 10);
                // String time =new SimpleDateFormat("hh:mm:ss a").format(dateObj);
                String printDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")
                        .format(new Date());

                int customerNameRemain = 0;
                int addressRemain = 0;

                if (custName.length() > 24) {
                    custName = custName.substring(0, 25);
                } else {
                    customerNameRemain = 25 - custName.length();
                }
                customerNameRemain = customerNameRemain + 1;
                for (int i = 0; i <= customerNameRemain; i++) {
                    custName = custName + " ";
                }

                if (address.length() > 24) {
                    address = address.substring(0, 25);
                } else {
                    addressRemain = 25 - address.length();
                }
                addressRemain = addressRemain + 1;
                for (int i = 0; i <= addressRemain; i++) {
                    address = address + " ";
                }

                String repTelNo = "";
                for (int i = 0; i <= 6; i++) {
                    repTelNo = repTelNo + " ";
                }
                repTelNo = repTelNo + dealerTel;

                String headerData = "\n\n\n";
                headerData = headerData + "                                  " + invoiceId + "\n";
                headerData = headerData + "                                  " + date + "\n\n";
                headerData = headerData + "\n";
                headerData = headerData + custName + dealerName + "\n";
                headerData = headerData + address + repTelNo + "\n";
                headerData = headerData + "\n\n\n";

                String printData = "";
                int totalQty = 0;
                double totalProdsValue = 0;

                List<String[]> freeProducts = new ArrayList<String[]>();

                for (String[] invoicedProduct : invoicedProducts) {

                    if (count == 48) {
                        if (printData.length() > 1) {

                            int k = spaceCount;

                            for (int i = 0; i <= k; i++) {
                                printData = printData + "\n";
                            }

                        }

                        printData = printData + headerData;
                        count = 0;
                    }

                    Products products = new Products(this);
                    products.openReadableDatabase();
                    String[] productdetail = products.getProductDetailsById(invoicedProduct[2]);
                    products.closeDatabase();

                    String productCode = productdetail[2];// IMPORTANT
                    String productDescription = productdetail[8];// IMPORTANT
                    String batch = invoicedProduct[3];// IMPORTANT

                    ProductRepStore productRepStore = new ProductRepStore(this);
                    productRepStore.openReadableDatabase();
                    @SuppressWarnings("unused")
                    String expiry = productRepStore.getExpiryByProductCodeAndBatch(productCode, batch);// IMPORTANT
                    productRepStore.closeDatabase();

                    String discount = invoicedProduct[6];// IMPORTANT
                    String unitPrice = invoicedProduct[9];// IMPORTANT
                    String normal = invoicedProduct[7];// IMPORTANT
                    String free = invoicedProduct[5];// IMPORTANT

                    if (free != "" && Integer.parseInt(free) > 0) {
                        freeProducts.add(invoicedProduct);
                    }

                    totalQty = totalQty + Integer.parseInt(normal);// IMPORTANT

                    int qty = Integer.parseInt(normal);// IMPORTANT

                    double prodValue = (Integer.parseInt(normal) * Double.parseDouble(unitPrice)) * ((100 - Double.parseDouble(discount)) / 100);

                    totalProdsValue = totalProdsValue + prodValue;

                    String value = (String.format("%.2f", prodValue));
                    String quantityString = String.valueOf(qty);
                    String unitPriceString = String.valueOf(unitPrice);
                    String valueString = String.valueOf(value);

                    int quantityRemain = 0;
                    int unitPriceRemain = 0;
                    int valueRemain = 0;

                    productDescription = productDescription.trim();
                    quantityString = quantityString.trim();
                    unitPriceString = unitPriceString.trim();
                    valueString = valueString.trim();

                    Log.w("SIZE",
                            "quantityString size : " + quantityString.length());
                    Log.w("SIZE",
                            "unitPriceString size : " + unitPriceString.length());
                    Log.w("SIZE", "valueString size : " + valueString.length());

                    if (quantityString.length() > 7) {
                        quantityString = quantityString.substring(0, 7);
                    }
                    if (unitPriceString.length() > 9) {
                        unitPriceString = unitPriceString.substring(0, 9);
                    }
                    if (valueString.length() > 11) {
                        valueString = valueString.substring(0, 11);
                    }

                    if (productDescription.length() > 44) {
                        productDescription = productDescription.substring(0, 44);
                    }

                    if (quantityString.length() < 7) {
                        quantityRemain = 6 - quantityString.length();
                    }

                    for (int i = 0; i <= quantityRemain; i++) {
                        quantityString = " " + quantityString;
                    }

                    if (unitPriceString.length() < 9) {
                        unitPriceRemain = 8 - unitPriceString.length();
                    }

                    for (int i = 0; i <= unitPriceRemain; i++) {
                        unitPriceString = " " + unitPriceString;
                    }

                    if (valueString.length() < 11) {
                        valueRemain = 10 - valueString.length();
                    }

                    for (int i = 0; i <= valueRemain; i++) {
                        valueString = " " + valueString;
                    }

                    printData = printData + productDescription.substring(0,15)+" " + quantityString + " " + unitPriceString + " " + valueString + "\n";

                    count = count + 2;
                    Log.w("COUNT", count + "lines");

                }

                // Log.w("IG3", "discount 332 " +
                // Integer.parseInt(invoiceD.get(8)));

                double discountedPrice = (Float.parseFloat(invoiceValue) / 100)
                        * Double.parseDouble(invoiceD.get(8));

                Log.w("IG3", "discountedPrice 332 " + discountedPrice);

                double totalDiscountedValue = (discountedPrice + Float
                        .parseFloat(returns));

                Log.w("IG3", "totalDiscountedValue 332 " + totalDiscountedValue);

                Log.w("IG3", "total 332 " + count);

                double needToPay = Float.parseFloat(invoiceValue)
                        - totalDiscountedValue;
                String needToPayString = String.format("%.2f", needToPay);

                Log.w("IG3", "needToPay 332 " + needToPay);

                String totalQt = String.valueOf(totalQty);
                String invoiceVal = String.format("%.2f", totalProdsValue);

                if (invoiceVal.length() > 9) {
                    invoiceVal = invoiceVal.substring(0, 9);
                }

                int invoiceValRemainRemain = 0;
                if (invoiceVal.length() < 11) {
                    invoiceValRemainRemain = 11 - invoiceVal.length();
                }

                int quantityRemain = 0;

                if (totalQt.length() < 7) {
                    quantityRemain = 6 - totalQt.length();
                }

                for (int i = 0; i <= quantityRemain; i++) {
                    totalQt = " " + totalQt;
                }

                invoiceValRemainRemain = invoiceValRemainRemain + 10;

                for (int i = 0; i <= invoiceValRemainRemain; i++) {
                    invoiceVal = " " + invoiceVal;
                }

                if (count < 45) {
                    printData = printData
                            + "--------------------------------------------\n";
                    printData = printData + "Total           " + " " + totalQt + invoiceVal + "\n";
                    printData = printData
                            + "--------------------------------------------\n";

                    count = count + 3;
                } else {

                    int k = 48 - count;
                    k = k + spaceCount;

                    for (int i = 0; i <= k; i++) {
                        printData = printData + "\n";
                    }

                    printData = printData + headerData;
                    count = 0;

                    printData = printData
                            + "--------------------------------------------\n";
                    printData = printData + "Total           " + " " + totalQt
                            + invoiceVal + "\n";
                    printData = printData
                            + "--------------------------------------------\n";
                    count = count + 3;
                }

                if (returnedProductList.size() > 0) {

                    if (count < 45) {

                        printData = printData + "\n Returned Products\n";

                        printData = printData
                                + "--------------------------------------------";
                        printData = printData + "\n";

                        count = count + 3;

                    } else {

                        int k = 48 - count;
                        k = k + spaceCount;

                        for (int i = 0; i <= k; i++) {
                            printData = printData + "\n";
                        }

                        printData = printData + headerData;
                        count = 0;

                        printData = printData + "\n Returned Products\n";

                        printData = printData
                                + "--------------------------------------------";
                        printData = printData + "\n";

                        count = count + 3;

                    }

                    for (String[] selectedProduct : returnedProductList) {

                        if (count == 48) {
                            if (printData.length() > 1) {

                                int k = spaceCount;

                                for (int i = 0; i <= k; i++) {
                                    printData = printData + "\n";
                                }
                            }

                            printData = printData + headerData;
                            count = 0;
                        }

                        int quantityReturnRemain = 0;
                        int priceReturnRemain = 0;
                        int valueReturnRemain = 0;

                        int normal = 0, free = 0;
                        if (selectedProduct[4] != "") {
                            normal = Integer.parseInt(selectedProduct[4]);
                        }

                        if (selectedProduct[5] != "") {
                            free = Integer.parseInt(selectedProduct[5]);
                        }

                        double price = 0;
                        if (selectedProduct[8] != "") {
                            price = Double.parseDouble(selectedProduct[8]);
                        }

                        double discountVal = 0;
                        if (selectedProduct[10] != "") {
                            discountVal = Double.parseDouble(selectedProduct[10]);
                        }

                        String quantityReturnString = String.valueOf(normal + free);
                        String priceReturnString = String.valueOf(price);

                        double prodDiscountValue = 0;
                        if (discountVal > 0) {
                            prodDiscountValue = (normal * price) / 100
                                    * discountVal;
                        }

                        String valueReturnString = String.format("%.2f",
                                (normal * price) - prodDiscountValue);

                        if (quantityReturnString.length() < 7) {
                            quantityReturnRemain = 6 - quantityReturnString
                                    .length();
                        }
                        if (priceReturnString.length() < 9) {
                            priceReturnRemain = 8 - priceReturnString.length();
                        }
                        if (valueReturnString.length() < 10) {
                            valueReturnRemain = 10 - valueReturnString.length();
                        }

                        for (int i = 0; i <= quantityReturnRemain; i++) {
                            quantityReturnString = " " + quantityReturnString;
                        }
                        for (int i = 0; i <= priceReturnRemain; i++) {
                            priceReturnString = " " + priceReturnString;
                        }
                        for (int i = 0; i <= valueReturnRemain; i++) {
                            valueReturnString = " " + valueReturnString;
                        }

                        printData = printData + selectedProduct[9].substring(0,15)+" " + (quantityReturnString) + " " + priceReturnString + " " + (valueReturnString) + "\n";

                        count = count + 2;
                        Log.w("COUNT", count + "lines");

                    }

                    printData = printData
                            + "--------------------------------------------";
                    printData = printData + "\n";
                    count++;

                }

                if (freeProducts.size() > 0) {

                    if (count < 45) {

                        printData = printData
                                + "\n Free Issues or Special Discount\n";

                        printData = printData
                                + "--------------------------------------------";
                        printData = printData + "\n";

                        count = count + 3;

                    } else {

                        int k = 48 - count;
                        k = k + spaceCount;

                        for (int i = 0; i <= k; i++) {
                            printData = printData + "\n";
                        }

                        printData = printData + headerData;
                        count = 0;

                        printData = printData
                                + "\n Free Issues or Special Discount\n";

                        printData = printData
                                + "--------------------------------------------";
                        printData = printData + "\n";

                        count = count + 3;

                    }

                    for (String[] invoicedProduct : freeProducts) {

                        if (count == 48) {
                            if (printData.length() > 1) {

                                int k = spaceCount;

                                for (int i = 0; i <= k; i++) {
                                    printData = printData + "\n";
                                }

                            }

                            printData = printData + headerData;
                            count = 0;
                        }

                        Products products = new Products(this);
                        products.openReadableDatabase();
                        String[] productdetail = products
                                .getProductDetailsById(invoicedProduct[2]);
                        products.closeDatabase();

                        String productCode = productdetail[2];// IMPORTANT
                        String productDescription = productdetail[8];// IMPORTANT
                        String batch = invoicedProduct[3];// IMPORTANT

                        ProductRepStore productRepStore = new ProductRepStore(this);
                        productRepStore.openReadableDatabase();
                        @SuppressWarnings("unused")
                        String expiry = productRepStore
                                .getExpiryByProductCodeAndBatch(productCode, batch);// IMPORTANT
                        productRepStore.closeDatabase();

                        String discount = invoicedProduct[6];// IMPORTANT
                        String unitPrice = invoicedProduct[9];// IMPORTANT
                        String normal = invoicedProduct[7];// IMPORTANT
                        String free = invoicedProduct[5];// IMPORTANT

                        int qty = Integer.parseInt(free);// IMPORTANT

                        String quantityString = String.valueOf(qty);
                        String unitPriceString = "0";
                        String valueString = "0";

                        int qtyRemain = 0;
                        int unitPriceRemain = 0;
                        int valueRemain = 0;

                        productDescription = productDescription.trim();
                        quantityString = quantityString.trim();
                        unitPriceString = unitPriceString.trim();
                        valueString = valueString.trim();

                        Log.w("SIZE",
                                "quantityString size : " + quantityString.length());
                        Log.w("SIZE",
                                "unitPriceString size : "
                                        + unitPriceString.length());
                        Log.w("SIZE", "valueString size : " + valueString.length());

                        if (quantityString.length() > 7) {
                            quantityString = quantityString.substring(0, 7);
                        }
                        if (unitPriceString.length() > 9) {
                            unitPriceString = unitPriceString.substring(0, 9);
                        }
                        if (valueString.length() > 11) {
                            valueString = valueString.substring(0, 11);
                        }

                        if (productDescription.length() > 44) {
                            productDescription = productDescription
                                    .substring(0, 44);
                        }

                        if (quantityString.length() < 7) {
                            qtyRemain = 6 - quantityString.length();
                        }

                        for (int i = 0; i <= qtyRemain; i++) {
                            quantityString = " " + quantityString;
                        }

                        if (unitPriceString.length() < 9) {
                            unitPriceRemain = 8 - unitPriceString.length();
                        }

                        for (int i = 0; i <= unitPriceRemain; i++) {
                            unitPriceString = " " + unitPriceString;
                        }

                        if (valueString.length() < 11) {
                            valueRemain = 10 - valueString.length();
                        }

                        for (int i = 0; i <= valueRemain; i++) {
                            valueString = " " + valueString;
                        }

                        printData = printData + productDescription.substring(0,15)+" " + quantityString + " " + unitPriceString + " " + valueString + "\n";

                        count = count + 2;
                        Log.w("COUNT", count + "lines");

                    }

                    printData = printData
                            + "--------------------------------------------";
                    printData = printData + "\n";
                    count++;

                }



                printData = printData + "\n";
                count++;

                String footerData = "";

                double discountValue = 0;

                if (invoiceD.get(8) != ""
                        && Double.parseDouble(invoiceD.get(8)) > 0) {
                    double invoiceTotalVal = Double.parseDouble(invoiceD.get(3));
                    double invoiceDiscount = Double.parseDouble(invoiceD.get(8));
                    discountValue = (invoiceTotalVal / 100) * invoiceDiscount;
                }

                if ((count + 17) < 48) {

                    footerData = footerData + "Gross Value : " + invoiceD.get(3)
                            + "\n";
                  //  footerData = footerData + "Discount    : " + invoiceD.get(8) + "%  (" + String.format("%.2f", discountValue) + ")\n";
                    footerData = footerData + "Return Value: " + returns + "\n";
                    footerData = footerData + "Free/Special: 0\n";
                    footerData = footerData + "Need to pay : " + needToPayString
                            + "\n";

                    footerData = footerData + "\n";
                    footerData = footerData + "-----------------------------\n";
                    footerData = footerData + "  Customer NIC No.\n\n";
                    footerData = footerData + "\n";
                    footerData = footerData + "-----------------------------\n";
                    footerData = footerData + "  Customer Signature & Seal\n\n";
                    footerData = footerData + "\n";
                    footerData = footerData + "-----------------------------\n";
                    footerData = footerData + "  Signature of Sales Rep \n";
                    footerData = footerData + "  On behalf of Distributor \n\n";
                    footerData = footerData
                            + "We confirm that the goods have been received\n";
                    footerData = footerData
                            + "in good order and payment will be made as per\n";
                    footerData = footerData
                            + "the payment term above.\n\n";

                    footerData = footerData + "Print Date & Time : "
                            + printDateTime + "\n\n";

                    footerData = footerData
                            + "Software By eMerge Solutions - 0115 960 960\n";

                    printData = printData + footerData;

                } else {

                    footerData = footerData + "Gross Value : " + invoiceD.get(3)
                            + "\n";
                //    footerData = footerData + "Discount    : " + invoiceD.get(8) + "%  (" + String.format("%.2f", discountValue) + ")\n";
                    footerData = footerData + "Return Value: " + returns + "\n";
                    footerData = footerData + "Free/Special: 0\n";
                    footerData = footerData + "Need to pay : " + needToPayString
                            + "\n";

                    footerData = footerData + "\n\n";
                    footerData = footerData + "-----------------------------\n";
                    footerData = footerData + "  Customer Signature & Seal\n\n";

                    footerData = footerData
                            + "We confirm that the goods have been received\n";
                    footerData = footerData
                            + "in good order and payment will be made as per\n";
                    footerData = footerData
                            + "the payment term above.\n\n";
                    footerData = footerData + "Print Date & Time : "
                            + printDateTime + "\n\n";

                    footerData = footerData
                            + "Software By eMerge Solutions - 0115 960 960\n";

                    int k = 48 - count;
                    k = k + spaceCount;

                    for (int i = 0; i <= k; i++) {
                        printData = printData + "\n";
                    }

                    printData = printData + headerData;
                    printData = printData + footerData;

                }

                Bundle bundleToView = new Bundle();
                bundleToView.putString("PrintData", printData);

                // Print invoice

                Intent activityIntent = new Intent(getApplicationContext(),
                        PrintUtility.class);
                activityIntent.putExtras(bundleToView);
                startActivityForResult(activityIntent, 0);


            } else {


                // boolean flag = true;
                int count = 15;//14
//				int spaceCount = 8;

                Invoice invoice = new Invoice(this);
                invoice.openReadableDatabase();
                ArrayList<String> invoiceD = invoice.getInvoiceDetailsByInvoiceId(invoiceId);
                invoice.closeDatabase();
                InvoicedProducts invoicedProductsObject = new InvoicedProducts(this);
                ArrayList<String[]> returnedProductList = new ArrayList<String[]>();
                List<String[]> invoicedProductsPrintDetails = new ArrayList<String[]>();
                List<String[]> invoicedProductsPrintDetailsbByBrand = new ArrayList<String[]>();
                if (invoiceId != "") {

                    ProductReturns productReturns = new ProductReturns(InvoiceViewActivity.this);
                    productReturns.openReadableDatabase();
                    returnedProductList = productReturns.getReturnDetailsByInvoiceId(invoiceId);
                    productReturns.closeDatabase();


                    invoicedProductsObject.openReadableDatabase();
                    invoicedProductsPrintDetails = invoicedProductsObject.getInvoicedProductsByInvoiceId(invoiceId);
                    invoicedProductsObject.closeDatabase();


                }

                Reps reps = new Reps(this);
                reps.openReadableDatabase();
                ArrayList<String> delearDetails = reps.getRepDetailsForPrinting(repId);
                reps.closeDatabase();

                String dealerName = delearDetails.get(1).trim();
                String dealerCity = delearDetails.get(2).trim();
                String dealerTel = delearDetails.get(3).trim();

                if (dealerName.length() > 18) {
                    dealerName = dealerName.substring(0, 18);
                }

                if (dealerCity.length() > 18) {
                    dealerCity = dealerCity.substring(0, 18);
                }

                String invoiceValue = invoiceD.get(3);// IMPORTANT
                String returns = invoiceD.get(7);// IMPORTANT

                Log.w("IG3", "invoiceD.get(11) 332 " + invoiceD.get(11));

                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss.SSS");

                // Date dateObj = dateFormat.parse(invoiceD.get(11));

                // String date = new SimpleDateFormat("yyyy-MM-dd").format(dateObj);
                String date = invoiceD.get(11).substring(0, 10);
                // String time =new SimpleDateFormat("hh:mm:ss a").format(dateObj);
                String printDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")
                        .format(new Date());

                int customerNameRemain = 0;
                int addressRemain = 0;

                if (custName.length() > 24) {
                    custName = custName.substring(0, 25);
                } else {
                    customerNameRemain = 25 - custName.length();
                }
                customerNameRemain = customerNameRemain + 1;
                for (int i = 0; i <= customerNameRemain; i++) {
                    custName = custName + " ";
                }

                if (address.length() > 24) {
                    address = address.substring(0, 25);
                } else {
                    addressRemain = 25 - address.length();
                }
                addressRemain = addressRemain + 1;
                for (int i = 0; i <= addressRemain; i++) {
                    address = address + " ";
                }

                String repname =delearDetails.get(0).trim();
                repname="Rep : "+repname;
                String repMargin = "";


                if(repname.length()<16){
                    for (int i = 0; i <17; i++) {
                        repMargin=repMargin+" ";
                    }
                }else {

                    for (int i = 0; i <17-(repname.length()-16); i++) {
                        repMargin=repMargin+" ";
                    }
                }


                String headerData = "\n";
                headerData = headerData + "        Hesperus Marketing(Pvt)Ltd.\n";
                headerData = headerData + "        No.100,5th Lane,Colombo 03.\n\n";
                headerData = headerData + "       94 11 2576736,94 11 2576737 ";
                headerData = headerData + "\n\n";
                headerData = headerData + "Invoice To"+repMargin+repname+"\n";
                headerData = headerData + custName + "Invoice No: " + invoiceId + "\n";
                headerData = headerData + address + "Date :" + date + "\n";
                headerData = headerData + "TEL : " + cusTel + "\n";
                headerData = headerData + "\n\n";


                String printData = "";
                int totalQty = 0;
                double totalProdsValue = 0;

                int invoicePageCount = 1;

                printData = printData + headerData;
                printData = printData + "Description            Qty  Price     Value\n";
                printData = printData + "--------------------------------------------------";
                printData = printData + "\n";

                List<String[]> freeProducts = new ArrayList<String[]>();

                for (String[] invoicedProduct : invoicedProductsPrintDetails) {

                    if (count == 60) {

                        printData = printData + "\nPage " + invoicePageCount +"                     Invoice No: " + invoiceId + "\n\n\n\n\n";
                        invoicePageCount++;
                        count = 0;
                    }

                    Products products = new Products(this);
                    products.openReadableDatabase();
                    String[] productdetail = products.getProductDetailsById(invoicedProduct[2]);
                    products.closeDatabase();

                    String productCode = productdetail[2];// IMPORTANT
                    String productDescription = productdetail[23];// IMPORTANT
                    String batch = invoicedProduct[3];// IMPORTANT

                    ProductRepStore productRepStore = new ProductRepStore(this);
                    productRepStore.openReadableDatabase();
                    @SuppressWarnings("unused")
                    String expiry = productRepStore.getExpiryByProductCodeAndBatch(
                            productCode, batch);// IMPORTANT
                    productRepStore.closeDatabase();

                    String discount = invoicedProduct[6];// IMPORTANT
                    String unitPrice;

                    invoicedProductsObject.openReadableDatabase();
                    if(Double.parseDouble(discount)>0.0){
                        Products pro = new Products(this);
                        pro.openReadableDatabase();
                        unitPrice =pro.getRetailsPrice(productdetail[2]);
                        pro.closeDatabase();

                        invoicedProductsPrintDetailsbByBrand = invoicedProductsObject.getInvoicedProductsByInvoiceIdFoBrand(invoiceId,1);
                    }else {
                        unitPrice = invoicedProduct[9];// IMPORTANT
                        invoicedProductsPrintDetailsbByBrand = invoicedProductsObject.getInvoicedProductsByInvoiceIdFoBrand(invoiceId,0);
                    }

                    invoicedProductsObject.closeDatabase();


                    String normal = invoicedProduct[7];// IMPORTANT
                    String free = invoicedProduct[5];// IMPORTANT

                   if (free != "" && Integer.parseInt(free) > 0) {
                        freeProducts.add(invoicedProduct);
                    }

                    totalQty = totalQty + Integer.parseInt(normal);// IMPORTANT

                    int qty = Integer.parseInt(normal);// IMPORTANT

                    double prodValue = (Integer.parseInt(normal) * Double
                            .parseDouble(unitPrice))
                            * ((100 - Double.parseDouble(discount)) / 100);

                    totalProdsValue = totalProdsValue + prodValue;

                    String value = (String.format("%.2f", prodValue));
                    String quantityString = String.valueOf(qty);


                    ////
                    String unitPriceString = String.valueOf(unitPrice);

                    ////
                    String valueString = String.valueOf(value);

                    int quantityRemain = 0;
                    int unitPriceRemain = 0;
                    int valueRemain = 0;



                    if(productDescription==null){
                        productDescription="No Description";
                        productDescription = productDescription.trim();
                    }else {
                        productDescription = productDescription.trim();
                    }


                    quantityString = quantityString.trim();
                    unitPriceString = unitPriceString.trim();
                    valueString = valueString.trim();


                    if (quantityString.length() > 7) {
                        quantityString = quantityString.substring(0, 7);
                    }
                    if (unitPriceString.length() > 9) {
                        unitPriceString = unitPriceString.substring(0, 9);
                    }
                    if (valueString.length() > 11) {
                        valueString = valueString.substring(0, 11);
                    }



                    if (quantityString.length() < 4) {
                        quantityRemain = 3 - quantityString.length();
                    }

                    for (int i = 0; i <= quantityRemain; i++) {
                        quantityString = " " + quantityString;
                    }

                    if (unitPriceString.length() < 7) {
                        unitPriceRemain = 6 - unitPriceString.length();
                    }

                    for (int i = 0; i <= unitPriceRemain; i++) {
                        unitPriceString = " " + unitPriceString;
                    }

                    if (valueString.length() < 10) {
                        valueRemain = 9 - valueString.length();
                    }

                    for (int i = 0; i <= valueRemain; i++) {
                        valueString = " " + valueString;
                    }

                    int descriptionLength =productDescription.length();
                    if(productDescription.length()>21){
                        productDescription=productDescription.substring(0,21);
                    }else {
                        for (int i = 0; i <21-descriptionLength; i++) {
                            productDescription=productDescription+" ";
                        }
                    }
                    printData = printData + productDescription+" "+ quantityString + "" + unitPriceString + " " + valueString + "\n";

                    count = count + 1;


                }

                // Log.w("IG3", "discount 332 " +
                // Integer.parseInt(invoiceD.get(8)));

                double discountedPrice = (Float.parseFloat(invoiceValue) / 100) * Double.parseDouble(invoiceD.get(8));

                Log.w("IG3", "discountedPrice 332 " + discountedPrice);

                double totalDiscountedValue = (discountedPrice + Float
                        .parseFloat(returns));

                Log.w("IG3", "totalDiscountedValue 332 " + totalDiscountedValue);

                Log.w("IG3", "total 332 " + count);

                double needToPay = Float.parseFloat(invoiceValue)
                        - totalDiscountedValue;
                String needToPayString = String.format("%.2f", needToPay);

                Log.w("IG3", "needToPay 332 " + needToPay);

                String totalQt = String.valueOf(totalQty);
                String invoiceVal = String.format("%.2f", totalProdsValue);

                if (invoiceVal.length() > 9) {
                    invoiceVal = invoiceVal.substring(0, 9);
                }

                int invoiceValRemainRemain = 0;
                if (invoiceVal.length() < 11) {
                    invoiceValRemainRemain = 11 - invoiceVal.length();
                }

                int quantityRemain = 0;

                if (totalQt.length() < 7) {
                    quantityRemain = 6 - totalQt.length();
                }

                for (int i = 0; i <= quantityRemain; i++) {
                    totalQt = " " + totalQt;
                }

                invoiceValRemainRemain = invoiceValRemainRemain + 6;

                for (int i = 0; i <= invoiceValRemainRemain; i++) {
                    invoiceVal = " " + invoiceVal;
                }

                if (count < 60) {//change
                    printData = printData + "--------------------------------------------------\n";
                    printData = printData + "Total             " + " " + totalQt + invoiceVal + "\n";
                    printData = printData + "--------------------------------------------------\n";
                    printData = printData + "--------------------------------------------------\n";

                    count = count + 1;
                } else {

                    int k = 63 - count;//change
                    //k = k + spaceCount;

                    for (int i = 0; i <= k; i++) {
                        printData = printData + "\n";
                    }

                    printData = printData + "\nPage " + invoicePageCount +"                     Invoice No: " + invoiceId + "\n\n\n\n\n";
                    invoicePageCount++;
                    count = 0;


                    printData = printData + "--------------------------------------------------\n";
                    printData = printData + "Total          " + " " + totalQt
                            + invoiceVal + "\n";
                    printData = printData + "--------------------------------------------------\n";
                    printData = printData + "--------------------------------------------------\n";
                    count = count + 1;
                }

                if (returnedProductList.size() > 0) {

                    if (count < 60) {//change

                        printData = printData + "\n Returned Products\n";

                        printData = printData + "------------------------------------------------";
                        printData = printData + "\n";

                        count = count + 1;

                    } else {

                        int k = 63 - count;//change
                        //k = k + spaceCount;

                        for (int i = 0; i <= k; i++) {
                            printData = printData + "\n";
                        }

                        printData = printData + "\nPage " + invoicePageCount + "\n\n\n\n\n";
                        invoicePageCount++;
                        count = 0;

                        printData = printData + "\n Returned Products\n";

                        printData = printData + "------------------------------------------------";
                        printData = printData + "\n";

                        count = count + 1;

                    }

                    for (String[] selectedProduct : returnedProductList) {

                        if (count == 63) {//change
                            printData = printData + "\nPage " + invoicePageCount +"                     Invoice No: " + invoiceId + "\n\n\n\n\n";
                            invoicePageCount++;
                            count = 0;
                        }

                        int quantityReturnRemain = 0;
                        int priceReturnRemain = 0;
                        int valueReturnRemain = 0;

                        int normal = 0, free = 0;
                        if (selectedProduct[4] != "") {
                            normal = Integer.parseInt(selectedProduct[4]);
                        }

                        if (selectedProduct[5] != "") {
                            free = Integer.parseInt(selectedProduct[5]);
                        }

                        double price = 0;
                        if (selectedProduct[8] != "") {
                            price = Double.parseDouble(selectedProduct[8]);
                        }

                        double discountVal = 0;
                        if (selectedProduct[10] != "") {
                            discountVal = Double.parseDouble(selectedProduct[10]);
                        }

                        String quantityReturnString = String.valueOf(normal + free);
                        String priceReturnString = String.valueOf(price);

                        double prodDiscountValue = 0;
                        if (discountVal > 0) {
                            prodDiscountValue = (normal * price) / 100
                                    * discountVal;
                        }

                        String valueReturnString = String.format("%.2f",
                                (normal * price) - prodDiscountValue);

                        if (quantityReturnString.length() < 4) {
                            quantityReturnRemain = 3 - quantityReturnString
                                    .length();
                        }
                        if (priceReturnString.length() < 9) {
                            priceReturnRemain = 8 - priceReturnString.length();
                        }
                        if (valueReturnString.length() < 10) {
                            valueReturnRemain = 10 - valueReturnString.length();
                        }

                        for (int i = 0; i <= quantityReturnRemain; i++) {
                            quantityReturnString = " " + quantityReturnString;
                        }
                        for (int i = 0; i <= priceReturnRemain; i++) {
                            priceReturnString = " " + priceReturnString;
                        }
                        for (int i = 0; i <= valueReturnRemain; i++) {
                            valueReturnString = " " + valueReturnString;
                        }


                        if(selectedProduct[9].length()>21){
                            selectedProduct[9]=selectedProduct[9].substring(0, 21);
                        }else {
                            for (int i = 0; i <21-selectedProduct[9].length(); i++) {
                                selectedProduct[9]=selectedProduct[9]+" ";
                            }
                        }

                        printData = printData + selectedProduct[9]+ "" +(quantityReturnString) + " " + priceReturnString + " " + (valueReturnString) + "\n";

                        count = count + 1;
                        Log.w("COUNT", count + "lines");

                    }

                    printData = printData + "-------------------------------------------------";
                    printData = printData + "\n";
                    count++;

                }

                if (freeProducts.size() > 0) {

                    if (count < 60) {//change

                        printData = printData + "\nFree Issues or Special Discount\n";

                        printData = printData + "-----------------------------------------------";
                        printData = printData + "\n";

                        count = count + 3;

                    } else {

                        int k = 63 - count;//change
//						k = k + spaceCount;

                        for (int i = 0; i <= k; i++) {
                            printData = printData + "\n";
                        }

                        printData = printData + "\nPage " + invoicePageCount +"                     Invoice No: " + invoiceId + "\n\n\n\n\n";
                        invoicePageCount++;
                        count = 0;

                        printData = printData + "\nFree Issues or Special Discount\n";
                        printData = printData + "--------------------------------------------------";
                        printData = printData + "\n";

                        count = count + 1;

                    }

                    for (String[] invoicedProduct : freeProducts) {

                        if (count == 63) {//change
                            printData = printData + "\nPage " + invoicePageCount +"                     Invoice No: " + invoiceId + "\n\n\n\n\n";
                            invoicePageCount++;
                            count = 0;
                        }

                        Products products = new Products(this);
                        products.openReadableDatabase();
                        String[] productdetail = products.getProductDetailsById(invoicedProduct[2]);
                        products.closeDatabase();

                        String productCode = productdetail[2];// IMPORTANT
                        String productDescription = productdetail[23];// IMPORTANT
                        String batch = invoicedProduct[3];// IMPORTANT

                        ProductRepStore productRepStore = new ProductRepStore(this);
                        productRepStore.openReadableDatabase();
                        @SuppressWarnings("unused")
                        String expiry = productRepStore.getExpiryByProductCodeAndBatch(productCode, batch);// IMPORTANT
                        productRepStore.closeDatabase();

                        String discount = invoicedProduct[6];// IMPORTANT
                        String unitPrice = invoicedProduct[9];// IMPORTANT
                        String normal = invoicedProduct[7];// IMPORTANT
                        String free = invoicedProduct[5];// IMPORTANT


                        int qty = Integer.parseInt(free);// IMPORTANT

                        String quantityString = String.valueOf(qty);
                        String unitPriceString = "0";
                        String valueString = "0";

                        int qtyRemain = 0;
                        int unitPriceRemain = 0;
                        int valueRemain = 0;

                        productDescription = productDescription.trim();
                        quantityString = quantityString.trim();
                        unitPriceString = unitPriceString.trim();
                        valueString = valueString.trim();

                        Log.w("SIZE",
                                "quantityString size : " + quantityString.length());
                        Log.w("SIZE",
                                "unitPriceString size : "
                                        + unitPriceString.length());
                        Log.w("SIZE", "valueString size : " + valueString.length());


                        if (unitPriceString.length() > 9) {
                            unitPriceString = unitPriceString.substring(0, 9);
                        }
                        if (valueString.length() > 11) {
                            valueString = valueString.substring(0, 11);
                        }



                        if (quantityString.length() < 4) {
                            qtyRemain = 3 - quantityString.length();
                        }

                        for (int i = 0; i <= qtyRemain; i++) {
                            quantityString = " " + quantityString;
                        }

                        if (unitPriceString.length() <7) {
                            unitPriceRemain = 6 - unitPriceString.length();
                        }

                        for (int i = 0; i <= unitPriceRemain; i++) {
                            unitPriceString = " " + unitPriceString;
                        }

                        if (valueString.length() < 10) {
                            valueRemain = 9 - valueString.length();
                        }

                        for (int i = 0; i <= valueRemain; i++) {
                            valueString = " " + valueString;
                        }


                        if(productDescription==null){
                            productDescription="No Description";
                            productDescription = productDescription.trim();
                        }else {
                            productDescription = productDescription.trim();
                        }
                        int descriptionLength =productDescription.length();
                        if(productDescription.length()>21){
                            productDescription=productDescription.substring(0,21);
                        }else {
                            for (int i = 0; i <21-descriptionLength; i++) {
                                productDescription=productDescription+" ";
                            }
                        }

                        printData = printData + productDescription+" "+ quantityString + "" + unitPriceString + " " + valueString + "\n";

                        count = count + 1;
                        Log.w("COUNT", count + "lines");

                    }

                    printData = printData + "-------------------------------------------------";
                    printData = printData + "\n";
                    count++;

                }
                printData = printData + "-----------------------------------------------------\n";
                printData = printData + "Brand         TotQty FQty Di.V Di.R  Value\n";
                printData = printData + "-----------------------------------------------------\n";


                int totqtyfinal = 0,totfreefinal = 0;
                Double   totdicountfinal = 0.0;


                for (String[] invoicedProduct : invoicedProductsPrintDetailsbByBrand) {


                    String discription = invoicedProduct[0];// IMPORTANT
                    String totqty = invoicedProduct[1];// IMPORTANT
                    String freeqty = invoicedProduct[2];// IMPORTANT
                    String discount = invoicedProduct[3];// IMPORTANT
                    String value = invoicedProduct[4];// IMPORTANT
                    String discountRate = invoicedProduct[5];


                    DecimalFormat df = new DecimalFormat("0.00");
                    Double dValue=Double.parseDouble(value);

                    String  valuefinale= String.format("%.2f", dValue);


                    totqtyfinal=totqtyfinal+Integer.parseInt(totqty);
                    totfreefinal=totfreefinal+Integer.parseInt(freeqty);
                    totdicountfinal=totdicountfinal+Double.parseDouble(discount);

                    int totqtyRemain = 0,freeqtyremin=0,discounRemin=0,valueremin=0,discounRateRemin = 0;


                    Double dicval=Double.parseDouble(discount);
                    discount=String.format("%.1f", dicval);

                    int discriptionlength =discription.length();

                    if(discription.length()>15){
                        discription=discription.substring(0,15);
                    }else {
                        for (int i = 0; i <15-discriptionlength; i++) {
                            discription=discription+" ";
                        }
                    }

                    if (totqty.length() < 5) {
                        totqtyRemain = 4 - totqty.length();
                    }

                    for (int i = 0; i <= totqtyRemain; i++) {
                        totqty = " " + totqty;
                    }


                    if (freeqty.length() < 5) {
                        freeqtyremin = 4 - freeqty.length();
                    }

                    for (int i = 0; i <= freeqtyremin; i++) {
                        freeqty = " " + freeqty;
                    }

                    if (discount.length() < 4) {
                        discounRemin = 3 - discount.length();
                    }

                    for (int i = 0; i <= discounRemin; i++) {
                        discount = " " + discount;
                    }
                    if (discountRate.length() < 5) {
                        discounRateRemin = 4- discountRate.length();
                    }

                    for (int i = 0; i <= discounRateRemin; i++) {
                        discountRate = " " + discountRate;
                    }

                    valuefinale=valuefinale.trim();
                    if (valuefinale.length() < 10) {
                        valueremin = 9- valuefinale.length();
                    }

                    for (int i = 0; i <= valueremin; i++) {
                        valuefinale = " " + valuefinale;
                    }




                    printData = printData + discription + "" + totqty + "" + freeqty + "" +discount + ""+discountRate+ valuefinale + "\n";

                    //  count = count + 2;
                    // Log.w("COUNT", count + "lines");

                }

                int totqtyrem=0,freerem=0,disrem=0,totrem=0;
                String totqty=String.valueOf(totqtyfinal);
                String freetotqty=String.valueOf(totfreefinal);
                String discountTot=String.valueOf(totdicountfinal);


                Double dictotval=Double.parseDouble(discountTot);

                discountTot=String.format("%.1f", dictotval);

                if (totqty.length() < 7) {
                    totqtyrem = 6- totqty.length();
                }

                for (int i = 0; i <= totqtyrem; i++) {
                    totqty = " " + totqty;
                }


                if (freetotqty.length() < 4) {
                    freerem = 3 - freetotqty.length();
                }

                for (int i = 0; i <= freerem; i++) {
                    freetotqty = " " + freetotqty;
                }


                if (discountTot.length() < 3) {
                    disrem = 2 - discountTot.length();
                }

                for (int i = 0; i <= disrem; i++) {
                    discountTot = " " + discountTot;
                }

                invoiceVal=invoiceVal.trim();

                if (invoiceVal.length() < 12) {
                    totrem = 11 - invoiceVal.length();
                }

                for (int i = 0; i <= totrem; i++) {
                    invoiceVal = " " + invoiceVal;
                }



                printData = printData + "---------------------------------------------------\n";
                printData = printData + "Total        " +totqty+" "+ freetotqty +""+discountTot+" "+invoiceVal + "\n";
                printData = printData + "---------------------------------------------------\n";

                printData = printData + "\n\n";




                printData = printData + "\n";
                count++;

                String footerData = "";

                double discountValue = 0;

                if (invoiceD.get(8) != ""
                        && Double.parseDouble(invoiceD.get(8)) > 0) {
                    double invoiceTotalVal = Double.parseDouble(invoiceD.get(3));
                    double invoiceDiscount = Double.parseDouble(invoiceD.get(8));
                    discountValue = (invoiceTotalVal / 100) * invoiceDiscount;
                }

                if ((count + 35) < 78) {//change

                    footerData = footerData + "Gross Value : " + invoiceD.get(3)
                            + "\n";
                  //  footerData = footerData + "Discount    : " + invoiceD.get(8) + "%  (" + String.format("%.2f", discountValue) + ")\n";
                    footerData = footerData + "Return Value: " + returns + "\n";
                    footerData = footerData + "Free/Special: 0\n";
                    footerData = footerData + "Need to pay : " + needToPayString
                            + "\n";

                    footerData = footerData + "\n\n";
                    footerData = footerData + "--------------------------------------\n";
                    footerData = footerData + "  Customer Signature & Seal\n\n";

                    footerData = footerData + "Print Date & Time : "
                            + printDateTime + "\n\n";

                    footerData = footerData
                            + "Software By eMerge Solutions - 0115 960 960\n";

                    printData = printData + footerData;

                } else {

                    footerData = footerData + "Gross Value : " + invoiceD.get(3)
                            + "\n";
                   // footerData = footerData + "Discount    : " + invoiceD.get(8) + "%  (" + String.format("%.2f", discountValue) + ")\n";
                    footerData = footerData + "Return Value: " + returns + "\n";
                    footerData = footerData + "Free/Special: 0\n";
                    footerData = footerData + "Need to pay : " + needToPayString
                            + "\n";

                    footerData = footerData + "\n\n";
                    footerData = footerData + "--------------------------------------\n";
                    footerData = footerData + "  Customer Signature & Seal\n\n";

                    footerData = footerData + "Print Date & Time : "
                            + printDateTime + "\n\n";

                    footerData = footerData
                            + "Software By eMerge Solutions - 0115 960 960\n";

                    int k = 63 - count;//change
//					k = k + spaceCount;

                    for (int i = 0; i <= k; i++) {
                        printData = printData + "\n";
                    }

                    printData = printData + "\nPage " + invoicePageCount +"                     Invoice No: " + invoiceId + "\n\n\n\n\n";
                    invoicePageCount++;


//					printData = printData + headerData;
                    printData = printData + footerData;

                }

                Bundle bundleToView = new Bundle();
                bundleToView.putString("PrintData", printData);

                // Print invoice

                Intent activityIntent = new Intent(getApplicationContext(),
                        PrintUtility.class);
                activityIntent.putExtras(bundleToView);
                startActivityForResult(activityIntent, 0);


            }

        /*} catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }

    private void getAllInvoicedProducts() {
        // TODO Auto-generated method stub
        InvoicedProducts invoicedProductsObject = new InvoicedProducts(this);
        invoicedProductsObject.openReadableDatabase();
        invoicedProducts = invoicedProductsObject.getInvoicedProductsByInvoiceId(invoiceId);
        invoicedProductsObject.closeDatabase();

        Invoice invoice = new Invoice(this);
        invoice.openReadableDatabase();
        invoiceData = invoice.getInvoiceDetailsByInvoiceId(invoiceId);
        invoice.closeDatabase();

    }

    private void populateInvoiceTable(List<String[]> invProducts) {
        // TODO Auto-generated method stub
        TableRow tr;
        tblInvoicedItems.setShrinkAllColumns(true);
        double tempPrice;
        try {

            int count = 1;
            for (String[] invoicedProduct : invoicedProducts) {
                Log.w("called", "inside populate for");

                tr = new TableRow(this);
                tr.setId(1000 + count);
                tr.setPadding(4, 4, 4, 4);
                tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.WRAP_CONTENT));

                if (count % 2 != 0) {
                    tr.setBackgroundColor(Color.DKGRAY);

                }

                Products products = new Products(this);
                products.openReadableDatabase();
                String[] productdetail = products.getProductDetailsById(invoicedProduct[2]);
                products.closeDatabase();

                TextView tvProductDescription = new TextView(this);
                tvProductDescription.setId(200 + count);
                tvProductDescription.setText(productdetail[8]);
                tvProductDescription.setGravity(Gravity.LEFT);
                tvProductDescription.setPadding(3, 3, 3, 3);
                tvProductDescription.setTextColor(Color.WHITE);
                tr.addView(tvProductDescription);

                TextView tvPrice = new TextView(this);
                tvPrice.setId(200 + count);

                System.out.println("productdetail[8] :" + productdetail[14]);

                if(Double.parseDouble(invoicedProduct[6])>0.00){
                    Products pro = new Products(this);
                    pro.openReadableDatabase();
                    Double retialsPrice=Double.parseDouble(productdetail[14]);
                    tvPrice.setText(String.valueOf(retialsPrice));
                    tempPrice = Double.parseDouble(invoicedProduct[7]) * retialsPrice  * ((100 - Double.parseDouble(invoicedProduct[6])) / 100);

                }else {
                    tvPrice.setText(String.valueOf(invoicedProduct[9]));
                    tempPrice = Double.parseDouble(String.valueOf(Double.parseDouble(invoicedProduct[7]) * Double.parseDouble(invoicedProduct[9]))) * ((100 - Double.parseDouble(invoicedProduct[6])) / 100);

                }

                tvPrice.setGravity(Gravity.LEFT);
                tvPrice.setPadding(3, 3, 3, 3);
                tvPrice.setTextColor(Color.WHITE);
                tr.addView(tvPrice);

                TextView tvNormal = new TextView(this);
                tvNormal.setId(200 + count);
                tvNormal.setText(String.valueOf(invoicedProduct[7]));
                tvNormal.setGravity(Gravity.LEFT);
                tvNormal.setPadding(3, 3, 3, 3);
                tvNormal.setTextColor(Color.WHITE);
                tr.addView(tvNormal);

                TextView tvFree = new TextView(this);
                tvFree.setId(200 + count);
                tvFree.setText(String.valueOf(invoicedProduct[5]));
                tvFree.setGravity(Gravity.LEFT);
                tvFree.setPadding(3, 3, 3, 3);
                tvFree.setTextColor(Color.WHITE);
                tr.addView(tvFree);

                TextView tvDiscount = new TextView(this);
                tvDiscount.setId(200 + count);
                tvDiscount.setText(String.valueOf(invoicedProduct[6]));
                tvDiscount.setGravity(Gravity.LEFT);
                tvDiscount.setPadding(3, 3, 3, 3);
                tvDiscount.setTextColor(Color.WHITE);
                tr.addView(tvDiscount);

                TextView tvQuantity = new TextView(this);
                tvQuantity.setId(200 + count);
                tvQuantity.setText(String.valueOf(Integer
                        .parseInt(invoicedProduct[5])
                        + Integer.parseInt(invoicedProduct[7])));
                tvQuantity.setGravity(Gravity.LEFT);
                tvQuantity.setPadding(3, 3, 3, 3);
                tvQuantity.setTextColor(Color.WHITE);
                tr.addView(tvQuantity);

                TextView tvTotal = new TextView(this);
                tvTotal.setId(200 + count);



                tvTotal.setText(String.format("%.2f", tempPrice));
                tvTotal.setGravity(Gravity.LEFT);
                tvTotal.setPadding(3, 3, 3, 3);
                tvTotal.setTextColor(Color.WHITE);
                tr.addView(tvTotal);

                count++;

                tblInvoicedItems.addView(tr, new TableLayout.LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            }
        } catch (Exception e) {
            Log.w("pop Table error", e.toString());
        }
    }

    private void getDataFromPreviousActivity() {
        // TODO Auto-generated method stub
        Bundle extras = getIntent().getExtras();
        rowId = extras.getString("Id");
        pharmacyId = extras.getString("PharmacyId");
        invoiceId = extras.getString("InvoiceNumber");
    }

    private void setInitialData(List<String[]> invProds, ArrayList<String> invData) {
        // TODO Auto-generated method stub

        Itinerary itinerary = new Itinerary(this);
        itinerary.openReadableDatabase();
        String status = itinerary.getItineraryStatus(rowId);
        itinerary.closeDatabase();

        String systemDate = DateFormat.getDateInstance().format(new Date());

        if (status.contentEquals("true")) {
            itinerary.openReadableDatabase();
            String[] itnDetails = itinerary
                    .getItineraryDetailsForTemporaryCustomer(rowId);
            itinerary.closeDatabase();
            String address = itnDetails[2] + ", " + itnDetails[3] + ", "
                    + itnDetails[4] + ", " + itnDetails[5];

            customerName = itnDetails[0];
            tViewCustomerName.setText(itnDetails[0]);
            tViewAddress.setText(address);
            custAddress = address;
            cusTel=itnDetails[6];


        } else {
            Customers customers = new Customers(this);
            customers.openReadableDatabase();
            String[] customerDetails = customers
                    .getCustomerDetailsByPharmacyId(pharmacyId);
            customers.closeDatabase();

            customerName = customerDetails[5];
            tViewCustomerName.setText(customerDetails[5]);
            tViewAddress.setText(customerDetails[6]);
            custAddress = customerDetails[6];
            cusTel=customerDetails[10];

        }

        tViewInvoiceNumber.setText(invoiceId);
        tViewDate.setText(systemDate);
        tViewTotalAmount.setText(invData.get(3));
        double total = 0;
        for (String[] invoicedproduct : invProds) {
            total = total + Double.parseDouble(invoicedproduct[5])
                    + Double.parseDouble(invoicedproduct[7]);
        }
        String tempTotal = String.valueOf(total);
        int d = tempTotal.indexOf(".");

        tViewTotalItems.setText(tempTotal.substring(0, d));
        tViewCash.setText(invData.get(4));
        tViewCredit.setText(invData.get(5));
        tViewCheque.setText(invData.get(6));
        tViewMarketReturn.setText(invData.get(7));
        tViewDiscount.setText(invData.get(8));
        tViewRemain.setText("");

        double needToPay = 0;
        needToPay = Double.parseDouble(invData.get(3))
                - (Double.parseDouble(invData.get(7)) + (Double
                .parseDouble(invData.get(3)) * ((Double
                .parseDouble(invData.get(8))) / 100)));

        tViewNeedToPay.setText(String.format("%.2f", needToPay));

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            Intent lastInvoice = new Intent(InvoiceViewActivity.this,
                    LastInvoiceActivity.class);
            finish();
            Bundle extras = new Bundle();
            extras.putString("Id", rowId);
            extras.putString("PharmacyId", pharmacyId);

            lastInvoice.putExtras(extras);
            startActivity(lastInvoice);
        }
        return super.onKeyDown(keyCode, event);
    }

    private String getRepName() {
        // TODO Auto-generated method stub

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        String repId = sharedPreferences.getString("RepId", "-1");

        Reps repsObject = new Reps(this);
        repsObject.openReadableDatabase();
        String repName = repsObject.getRepNameByRepId(repId);
        repsObject.closeDatabase();
        return repName;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub

        super.onSaveInstanceState(outState);

        outState.putSerializable("invoiceData", invoiceData);
        outState.putString("rowId", rowId);
        outState.putString("pharmacyId", pharmacyId);
        outState.putString("invoiceId", invoiceId);
        outState.putString("customerName", customerName);
        outState.putString("custAddress", custAddress);

    }

    @SuppressWarnings("unchecked")
    private void setBundleData(Bundle bundlData) {

        invoiceData = (ArrayList<String>) bundlData.getSerializable("invoiceData");
        rowId = bundlData.getString("rowId");
        pharmacyId = bundlData.getString("pharmacyId");
        invoiceId = bundlData.getString("invoiceId");
        customerName = bundlData.getString("customerName");
        custAddress = bundlData.getString("custAddress");

    }

}
