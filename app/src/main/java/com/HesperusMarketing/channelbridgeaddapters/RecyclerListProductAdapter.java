package com.HesperusMarketing.channelbridgeaddapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.HesperusMarketing.channelbridge.R;
import com.HesperusMarketing.channelbridgedb.TemporaryInvoice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Himanshu on 6/8/2016.
 */
public class RecyclerListProductAdapter extends RecyclerView.Adapter<RecyclerListProductAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<ListProduct> albumList;
    TemporaryInvoice tempInvoiceStockController;
    private Boolean iswebApprovalActive = true;

    public ArrayList myItems = new ArrayList();

    public RecyclerListProductAdapter(Context mContext, ArrayList<ListProduct> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
        tempInvoiceStockController = new TemporaryInvoice(mContext);
        SharedPreferences shared = mContext.getSharedPreferences("CBHesPreferences", Context.MODE_PRIVATE);
        iswebApprovalActive = (shared.getBoolean("WebApproval", true));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView code, discripton, batch, stock, price;
        EditText shelf, request, order, free, discount;
        String principle;


        public MyViewHolder(View view) {
            super(view);

            code = (TextView) view.findViewById(R.id.textViewCode);
            discripton = (TextView) view.findViewById(R.id.textViewdescription);
            batch = (TextView) view.findViewById(R.id.textViewBatch);
            stock = (TextView) view.findViewById(R.id.textViewStock);
            price = (TextView) view.findViewById(R.id.textViewPrice);

            this.shelf = (EditText) view.findViewById(R.id.editTextShelf);
            request = (EditText) view.findViewById(R.id.editTextRequest);
            order = (EditText) view.findViewById(R.id.editTextOrder);
            free = (EditText) view.findViewById(R.id.editTextFree);
            discount = (EditText) view.findViewById(R.id.editTextDiscount);



        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_product, parent, false);
        MyViewHolder pvh = new MyViewHolder(itemView);
        return  pvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int i) {

        holder.code.setText(albumList.get(i).getCode());
        holder.discripton.setText(albumList.get(i).getDiscription());
        holder.batch.setText(albumList.get(i).getBatch());
        holder.stock.setText(albumList.get(i).getStock());
        holder.price.setText(albumList.get(i).getPrice());

        //holder.principle = itemCodeDetailList.get(i).getPrinciple();


        holder.shelf.setText(String.valueOf(albumList.get(i).getShelf()));
        holder.request.setText(String.valueOf(albumList.get(i).getRequest()));
        holder.order.setText(String.valueOf(albumList.get(i).getOrder()));
        holder.free.setText(String.valueOf(albumList.get(i).getFree()));
        holder.discount.setText(String.valueOf(albumList.get(i).getDiscount()));


        holder.discount.setEnabled(false);

        if (String.valueOf(albumList.get(i).getShelf()).equals("")) {
            holder.request.setEnabled(false);
            holder.order.setEnabled(false);

        } else {
            holder.request.setEnabled(true);
            holder.order.setEnabled(true);

        }


        if (!albumList.get(i).getOrder().equals("0")) {
            holder.free.setEnabled(true);
            holder.request.setEnabled(true);
            holder.order.setEnabled(true);
        } else {
            holder.free.setEnabled(false);
        }


        final MyViewHolder shelfFocusViewHolder = holder;

        holder.shelf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        holder.shelf.addTextChangedListener(new TextWatcher() {
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

                    tempInvoiceStockController.updateShelfQuantity(albumList.get(i).getCode(), albumList.get(i).getCode().toString(), editable.toString());
                    tempInvoiceStockController.closeDatabase();
                } else {
                    shelfFocusViewHolder.request.setEnabled(false);
                    shelfFocusViewHolder.order.setEnabled(false);
                    shelfFocusViewHolder.free.setEnabled(false);

                }

            }
        });


        final MyViewHolder requestOnFocusViewHolder = holder;
        holder.request.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                tempInvoiceStockController.openWritableDatabase();
                if (requestOnFocusViewHolder.request.getText().toString().equals("0")) {
                    requestOnFocusViewHolder.request.setText(String.valueOf(""));
                } else {

                }
            }
        });
        final MyViewHolder requestTextChangedViewHolder = holder;
        holder.request.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty() || !editable.toString().equals("")) {
                    tempInvoiceStockController.updateRequestQuantity(albumList.get(i).getCode(), albumList.get(i).getBatch(), editable.toString());

                    if (iswebApprovalActive == true) {

                        if (Double.parseDouble(albumList.get(i).getStock()) < Double.parseDouble(editable.toString())) {
                            Toast toast = Toast.makeText(mContext, "Enter valid quantity", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            requestTextChangedViewHolder.order.setText(albumList.get(i).getStock());
                        }

                    } else {
                        if (Double.parseDouble(editable.toString()) > Double.parseDouble(albumList.get(i).getStock())) {
                            Toast toast = Toast.makeText(mContext, "Enter valid quantity", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            requestTextChangedViewHolder.order.setText(albumList.get(i).getStock());

                        } else {
                            requestTextChangedViewHolder.order.setText(editable.toString());
                        }

                    }

                }
                //  tempInvoiceStockController.closeDatabase();

            }
        });


        final MyViewHolder freeOnFocusViewHolder = holder;
        holder.order.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                tempInvoiceStockController.openWritableDatabase();
                if (freeOnFocusViewHolder.order.getText().toString().equals("0")) {
                    freeOnFocusViewHolder.order.setText(String.valueOf(""));
                } else {

                }
            }
        });
        holder.order.addTextChangedListener(new TextWatcher() {
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
                    tempInvoiceStockController.updateNormalQuantity(albumList.get(i).getCode(), albumList.get(i).getBatch(), editable.toString());

                }
                tempInvoiceStockController.closeDatabase();

            }
        });

        final MyViewHolder freerOnFocusChangeHolder = holder;
        holder.free.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {


            }
        });

        final MyViewHolder freerTextChangedViewHolder = holder;
        holder.free.addTextChangedListener(new TextWatcher() {


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

                    int stock = Integer.parseInt(albumList.get(i).getStock());
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
                            tempInvoiceStockController.updateFreeQuantity(albumList.get(i).getCode(), albumList.get(i).getBatch(), freerOnFocusChangeHolder.free.getText().toString());

                            freerOnFocusChangeHolder.discount.setEnabled(false);
                            tempInvoiceStockController.updateDiscountAlloed(albumList.get(i).getCode(), albumList.get(i).getBatch(), Boolean.toString(false));

                        } else {
                            Toast freeToast = Toast.makeText(mContext, "Not enough quantity", Toast.LENGTH_SHORT);
                            freeToast.setGravity(Gravity.CENTER, 0, 0);
                            freeToast.show();
                            freerOnFocusChangeHolder.free.setText("0");
                        }
                    } else {
                        tempInvoiceStockController.updateFreeQuantity(albumList.get(i).code, albumList.get(i).batch, freerOnFocusChangeHolder.free.getText().toString());
                        freerOnFocusChangeHolder.discount.setEnabled(false);
                        tempInvoiceStockController.updateDiscountAlloed(albumList.get(i).code, albumList.get(i).batch, Boolean.toString(false));
                    }

                } else {
                    tempInvoiceStockController.updateFreeQuantity(albumList.get(i).code, albumList.get(i).batch, "0");

                }


            }
        });


    }


    @Override
    public int getItemCount() {
        return albumList.size();
    }


}
