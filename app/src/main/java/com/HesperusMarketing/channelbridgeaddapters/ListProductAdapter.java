package com.HesperusMarketing.channelbridgeaddapters;

import android.content.Context;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.HesperusMarketing.channelbridge.InvoiceGen1Alternate;
import com.HesperusMarketing.channelbridge.InvoiceHistoryActivity;
import com.HesperusMarketing.channelbridge.R;
import com.HesperusMarketing.channelbridgedb.TemporaryInvoice;

import java.util.ArrayList;

/**
 * Created by Himanshu on 3/28/2016.
 */
public class ListProductAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mContext;
    ArrayList<ListProduct> itemCodeDetailList;
    TemporaryInvoice tempInvoiceStockController;
    private Boolean iswebApprovalActive = true;
    private static LayoutInflater inflater = null;

    public ListProductAdapter(Context context, ArrayList<ListProduct> itemcodedetailList) {
        mContext = context;
        itemCodeDetailList = itemcodedetailList;
        tempInvoiceStockController = new TemporaryInvoice(context);
        SharedPreferences shared = context.getSharedPreferences("CBHesPreferences", Context.MODE_PRIVATE);
        iswebApprovalActive = (shared.getBoolean("WebApproval", true));
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itemCodeDetailList.size();
    }

    @Override
    public Object getItem(int i) {
        return itemCodeDetailList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolderItem viewHolder = null;



        viewHolder = new ViewHolderItem();
        view = inflater.inflate(R.layout.list_product, null);
            viewHolder.code = (TextView) view.findViewById(R.id.textViewCode);
            viewHolder.discripton = (TextView) view.findViewById(R.id.textViewdescription);
            viewHolder.batch = (TextView) view.findViewById(R.id.textViewBatch);
            viewHolder.stock = (TextView) view.findViewById(R.id.textViewStock);
            viewHolder.price = (TextView) view.findViewById(R.id.textViewPrice);

            viewHolder.shelf = (EditText) view.findViewById(R.id.editTextShelf);
            viewHolder.request = (EditText) view.findViewById(R.id.editTextRequest);
            viewHolder.order = (EditText) view.findViewById(R.id.editTextOrder);
            viewHolder.free = (EditText) view.findViewById(R.id.editTextFree);
            viewHolder.discount = (EditText) view.findViewById(R.id.editTextDiscount);
            view.setTag(viewHolder);


        viewHolder.code.setText(itemCodeDetailList.get(i).getCode());
        viewHolder.discripton.setText(itemCodeDetailList.get(i).getDiscription());
        viewHolder.batch.setText(itemCodeDetailList.get(i).getBatch());
        viewHolder.stock.setText(itemCodeDetailList.get(i).getStock());
        viewHolder.price.setText(itemCodeDetailList.get(i).getPrice());

        viewHolder.principle = itemCodeDetailList.get(i).getPrinciple();


        viewHolder.shelf.setText(String.valueOf(itemCodeDetailList.get(i).getShelf()));
        viewHolder.request.setText(String.valueOf(itemCodeDetailList.get(i).getRequest()));
        viewHolder.order.setText(String.valueOf(itemCodeDetailList.get(i).getOrder()));
        viewHolder.free.setText(String.valueOf(itemCodeDetailList.get(i).getFree()));
        viewHolder.discount.setText(String.valueOf(itemCodeDetailList.get(i).getDiscount()));


        viewHolder.discount.setEnabled(false);


        if (String.valueOf(itemCodeDetailList.get(i).getShelf()).equals("")) {
            viewHolder.request.setEnabled(false);
            viewHolder.order.setEnabled(false);

        } else {
            viewHolder.request.setEnabled(true);
            viewHolder.order.setEnabled(true);

        }


        if (!itemCodeDetailList.get(i).getOrder().equals("0")) {
            viewHolder.free.setEnabled(true);
            viewHolder.request.setEnabled(true);
            viewHolder.order.setEnabled(true);
        } else {
            viewHolder.free.setEnabled(false);
        }


        final ViewHolderItem shelfFocusViewHolder = viewHolder;
        viewHolder.shelf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                tempInvoiceStockController.openWritableDatabase();
                if (shelfFocusViewHolder.shelf.getText().toString().equals("")) {
                } else {
                    shelfFocusViewHolder.request.setEnabled(true);
                    shelfFocusViewHolder.order.setEnabled(true);

                }

            }
        });
        viewHolder.shelf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty() || !editable.toString().equals("")) {

                    shelfFocusViewHolder.request.setEnabled(true);
                    shelfFocusViewHolder.order.setEnabled(true);
                    shelfFocusViewHolder.free.setEnabled(true);

                    tempInvoiceStockController.updateShelfQuantity(itemCodeDetailList.get(i).getCode(), itemCodeDetailList.get(i).getCode().toString(), editable.toString());
                    tempInvoiceStockController.closeDatabase();
                } else {
                    shelfFocusViewHolder.request.setEnabled(false);
                    shelfFocusViewHolder.order.setEnabled(false);
                    shelfFocusViewHolder.free.setEnabled(false);

                }

            }
        });


        final ViewHolderItem requestOnFocusViewHolder = viewHolder;
        viewHolder.request.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                tempInvoiceStockController.openWritableDatabase();
                if (requestOnFocusViewHolder.request.getText().toString().equals("0")) {
                    requestOnFocusViewHolder.request.setText(String.valueOf(""));
                } else {

                }
            }
        });
        final ViewHolderItem requestTextChangedViewHolder = viewHolder;
        viewHolder.request.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty() || !editable.toString().equals("")) {
                    tempInvoiceStockController.updateRequestQuantity(itemCodeDetailList.get(i).getCode(), itemCodeDetailList.get(i).getBatch(), editable.toString());

                    if (iswebApprovalActive == true) {

                        if (Double.parseDouble(itemCodeDetailList.get(i).getStock()) < Double.parseDouble(editable.toString())) {
                            Toast toast = Toast.makeText(mContext, "Enter valid quantity", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            requestTextChangedViewHolder.order.setText(itemCodeDetailList.get(i).getStock());
                        }

                    } else {
                        if (Double.parseDouble(editable.toString()) > Double.parseDouble(itemCodeDetailList.get(i).getStock())) {
                            Toast toast = Toast.makeText(mContext, "Enter valid quantity", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            requestTextChangedViewHolder.order.setText(itemCodeDetailList.get(i).getStock());

                        } else {
                            requestTextChangedViewHolder.order.setText(editable.toString());
                        }

                    }

                }
                //  tempInvoiceStockController.closeDatabase();

            }
        });


        final ViewHolderItem freeOnFocusViewHolder = viewHolder;
        viewHolder.order.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                tempInvoiceStockController.openWritableDatabase();
                if (freeOnFocusViewHolder.order.getText().toString().equals("0")) {
                    freeOnFocusViewHolder.order.setText(String.valueOf(""));
                } else {

                }
            }
        });
        viewHolder.order.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty() || !editable.toString().equals("")) {
                    tempInvoiceStockController.openWritableDatabase();
                    tempInvoiceStockController.updateNormalQuantity(itemCodeDetailList.get(i).getCode(), itemCodeDetailList.get(i).getBatch(), editable.toString());

                }
                tempInvoiceStockController.closeDatabase();

            }
        });

        final ViewHolderItem freerOnFocusChangeHolder = viewHolder;
        viewHolder.free.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {


            }
        });

        final ViewHolderItem freerTextChangedViewHolder = viewHolder;
        viewHolder.free.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {


                if (!freerTextChangedViewHolder.free.getText().toString().equals("0") && !freerTextChangedViewHolder.free.getText().toString().equals("")) {
                    tempInvoiceStockController.openWritableDatabase();

                    int stock = Integer.parseInt(itemCodeDetailList.get(i).getStock());
                    int request = 0;
                    if (!freerOnFocusChangeHolder.order.getText().toString().equals("")) {
                        request = Integer.parseInt(freerOnFocusChangeHolder.order.getText().toString());
                    }
                    int free;
                    try {
                        free = Integer.parseInt(freerOnFocusChangeHolder.free.getText().toString());
                    } catch (NumberFormatException n) {
                        free = 0;
                    }


                    if (iswebApprovalActive == false) {
                        if (stock - request >= free) { // check whether entered free quantity is smaller than stock -  requested
                            tempInvoiceStockController.updateFreeQuantity(itemCodeDetailList.get(i).getCode(), itemCodeDetailList.get(i).getBatch(), freerOnFocusChangeHolder.free.getText().toString());

                            freerOnFocusChangeHolder.discount.setEnabled(false);
                            tempInvoiceStockController.updateDiscountAlloed(itemCodeDetailList.get(i).getCode(), itemCodeDetailList.get(i).getBatch(), Boolean.toString(false));

                        } else {
                            Toast freeToast = Toast.makeText(mContext, "Not enough quantity", Toast.LENGTH_SHORT);
                            freeToast.setGravity(Gravity.CENTER, 0, 0);
                            freeToast.show();
                            freerOnFocusChangeHolder.free.setText("0");
                        }
                    } else {
                        tempInvoiceStockController.updateFreeQuantity(itemCodeDetailList.get(i).code, itemCodeDetailList.get(i).batch, freerOnFocusChangeHolder.free.getText().toString());
                        freerOnFocusChangeHolder.discount.setEnabled(false);
                        tempInvoiceStockController.updateDiscountAlloed(itemCodeDetailList.get(i).code, itemCodeDetailList.get(i).batch, Boolean.toString(false));
                    }

                } else {
                    tempInvoiceStockController.updateFreeQuantity(itemCodeDetailList.get(i).code, itemCodeDetailList.get(i).batch, "0");

                }


            }
        });



/*

        final ViewHolderItem discountOnFocusViewHolder = viewHolder;
        viewHolder.discount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                tempInvoiceStockController.openWritableDatabase();
                if (discountOnFocusViewHolder.discount.getText().toString().equals("0")) {
                    discountOnFocusViewHolder.discount.setText(String.valueOf(""));
                } else {

                }
            }
        });
        final ViewHolderItem discountTextChangedViewHolder = viewHolder;

        viewHolder.discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty() || !editable.toString().equals("")) {
                    tempInvoiceStockController.openWritableDatabase();
                    if (editable.length() < 4) {
                        if (Double.parseDouble(editable.toString()) <= 100) {

                            if (Double.parseDouble(editable.toString()) > 0) {
                                discountTextChangedViewHolder.free.setEnabled(false);
                                tempInvoiceStockController.updateFreeAlloed(itemCodeDetailList.get(i).code, itemCodeDetailList.get(i).batch, Boolean.toString(false));
                            } else {
                                discountTextChangedViewHolder.free.setEnabled(true);
                                tempInvoiceStockController.updateFreeAlloed(itemCodeDetailList.get(i).code, itemCodeDetailList.get(i).batch, Boolean.toString(true));
                            }
                        } else {
                            Toast toast1 = Toast.makeText(mContext, "Enter valid discount", Toast.LENGTH_LONG);
                            toast1.setGravity(Gravity.CENTER, 0, 0);
                            toast1.show();
                            discountTextChangedViewHolder.discount.setText("0.0");

                        }
                    } else {
                        Toast toast1 = Toast.makeText(mContext, "Enter valid amount", Toast.LENGTH_LONG);
                        toast1.setGravity(Gravity.CENTER, 0, 0);
                        toast1.show();
                        discountTextChangedViewHolder.discount.setText("0.0");
                    }
                }

                tempInvoiceStockController.closeDatabase();

            }
        });
*/


        return view;

    }

    static class ViewHolderItem {
        TextView code, discripton, batch, stock, price;
        EditText shelf, request, order, free, discount;
        String principle;
    }

    @Override
    public void onClick(View view) {

    }


}
