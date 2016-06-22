package com.HesperusMarketing.channelbridgeaddapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.HesperusMarketing.channelbridge.R;

import java.util.ArrayList;

/**
 * Created by Himanshu on 6/7/2016.
 */
public class ReturnSalesAdapter extends BaseAdapter {

    private Context mContext;
    ArrayList<ReportList> returnSalesDetailList;
    private static LayoutInflater inflater = null;
    int status;

    public ReturnSalesAdapter(Context mContext, ArrayList<ReportList> returnSalesDetailList, int status) {
        this.mContext = mContext;
        this.returnSalesDetailList = returnSalesDetailList;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.status = status;
    }

    @Override
    public int getCount() {
        return returnSalesDetailList.size();
    }

    @Override
    public Object getItem(int i) {
        return returnSalesDetailList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolderItem viewHolder = null;
        viewHolder = new ViewHolderItem();
        view = inflater.inflate(R.layout.list_report_return, null);



       viewHolder.textNAme = (TextView) view.findViewById(R.id.tvName2);
        viewHolder.textInvoicesDate = (TextView) view.findViewById(R.id.tvDate2);
        viewHolder.textInvoicesDateNo = (TextView) view.findViewById(R.id.tvInvNo2);
        viewHolder.textValue = (TextView) view.findViewById(R.id.tvValue2);
       viewHolder.textRetrunDate = (TextView) view.findViewById(R.id.tvReturnDate2);


        viewHolder.textNAme.setText(String.valueOf(returnSalesDetailList.get(i).getDelName()));
        viewHolder.textInvoicesDate.setText(String.valueOf(returnSalesDetailList.get(i).getInvoiceDate().substring(0, 10)));
        viewHolder.textInvoicesDateNo.setText(String.valueOf(returnSalesDetailList.get(i).getInvoiceNo()));
        viewHolder.textValue.setText(String.valueOf(returnSalesDetailList.get(i).getValue()));
        viewHolder.textRetrunDate.setText(String.valueOf(returnSalesDetailList.get(i).getReturnDate().substring(0, 10)));




        return view;
    }

    static class ViewHolderItem {
        TextView textNAme,textRetrunDate,textInvoicesDate, textInvoicesDateNo,textValue;


    }
}
