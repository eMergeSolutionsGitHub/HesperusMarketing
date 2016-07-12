package com.HesperusMarketing.channelbridgeaddapters;

import android.content.Context;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
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

import com.HesperusMarketing.Entity.TempInvoiceStock;
import com.HesperusMarketing.channelbridge.InvoiceGen1Alternate;
import com.HesperusMarketing.channelbridge.InvoiceHistoryActivity;
import com.HesperusMarketing.channelbridge.R;
import com.HesperusMarketing.channelbridgedb.TemporaryInvoice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Himanshu on 3/28/2016.
 */
public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.MyViewHolder> {

    private Context mContext;
    private List<TempInvoiceStock> albumList;

    public ListProductAdapter(Context mContext, ArrayList<TempInvoiceStock> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_product, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TempInvoiceStock album = albumList.get(position);
        holder.code.setText(album.getProductCode());
       // holder.discripton.setText(album.getDiscription());


    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView code, discripton, batch, stock, price, shelf, request, order, free, discount;


        public MyViewHolder(View view) {
            super(view);
            code = (TextView) view.findViewById(R.id.textViewCode);
            discripton = (TextView) view.findViewById(R.id.textViewdescription);
            batch = (TextView) view.findViewById(R.id.textViewBatch);
            stock = (TextView) view.findViewById(R.id.textViewStock);
            price = (TextView) view.findViewById(R.id.textViewPrice);

            shelf = (TextView) view.findViewById(R.id.TextShelf);
            request = (TextView) view.findViewById(R.id.TextRequest);
            order = (TextView) view.findViewById(R.id.TextOrder);
            free = (TextView) view.findViewById(R.id.TextFree);
            discount = (TextView) view.findViewById(R.id.editTextDiscount);
        }
    }


}
