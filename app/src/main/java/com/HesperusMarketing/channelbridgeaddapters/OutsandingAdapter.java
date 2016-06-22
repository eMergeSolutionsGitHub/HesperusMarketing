package com.HesperusMarketing.channelbridgeaddapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.HesperusMarketing.channelbridge.R;

import com.HesperusMarketing.channelbridgedb.TemporaryInvoice;

import java.util.ArrayList;

/**
 * Created by Himanshu on 5/18/2016.
 */
public class OutsandingAdapter extends BaseAdapter {


    private Context mContext;
    ArrayList<OutsandingList> outsandingDetailList;


    public OutsandingAdapter(Context context, ArrayList<OutsandingList> outsandingdetailList) {
        mContext = context;
        outsandingDetailList = outsandingdetailList;

    }

    @Override
    public int getCount() {
        return outsandingDetailList.size();
    }

    @Override
    public Object getItem(int i) {
        return outsandingDetailList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolderItem viewHolder = null;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        viewHolder = new ViewHolderItem();
        view = inflater.inflate(R.layout.list_dialogsendapprovel_outsanding, null);

        viewHolder.textInvoicesNo = (TextView) view.findViewById(R.id.textView_invno);
        viewHolder.textValue = (TextView) view.findViewById(R.id.textView_value);
        viewHolder.textDueDate = (TextView) view.findViewById(R.id.textView_duedate);


        viewHolder.textInvoicesNo.setText(String.valueOf(outsandingDetailList.get(i).invoiceNo));
        viewHolder.textValue.setText(String.valueOf(outsandingDetailList.get(i).invoiceValue));
        viewHolder.textDueDate.setText(String.valueOf(outsandingDetailList.get(i).invoiceDurDate));


        return view;
    }

    static class ViewHolderItem {
        TextView textInvoicesNo, textValue, textDueDate;


    }
}
