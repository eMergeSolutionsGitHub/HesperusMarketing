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
 * Created by Himanshu on 6/6/2016.
 */
public class DealerSalesAdapter extends BaseAdapter {

    private Context mContext;
    ArrayList<ReportList> dealerSalesDetailList;
    private static LayoutInflater inflater = null;
    int status;


    public DealerSalesAdapter(Context context, ArrayList<ReportList> dealersalesdetailList,int stat) {
        mContext = context;
        dealerSalesDetailList = dealersalesdetailList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        status= stat;
    }

    @Override
    public int getCount() {
        return dealerSalesDetailList.size();
    }

    @Override
    public Object getItem(int i) {
        return dealerSalesDetailList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolderItem viewHolder = null;
        viewHolder = new ViewHolderItem();
        view = inflater.inflate(R.layout.list_report_dealersales, null);

        viewHolder.textInvoicesDate = (TextView) view.findViewById(R.id.tvDate1);
        viewHolder.textInvoicesDateNo = (TextView) view.findViewById(R.id.tvInvNo1);
        viewHolder.textReturnValue = (TextView) view.findViewById(R.id.tvRValue1);
        viewHolder.textValue = (TextView) view.findViewById(R.id.tvValue1);
        viewHolder.textCredit = (TextView) view.findViewById(R.id.tvDuration1);
        viewHolder.textDue = (TextView) view.findViewById(R.id.tvDueDate1);
        viewHolder.textStatus = (TextView) view.findViewById(R.id.tvStatus1);

        viewHolder.textNAme = (TextView) view.findViewById(R.id.tvname1);


        viewHolder.textInvoicesDate.setText(String.valueOf(dealerSalesDetailList.get(i).getInvoiceDate().substring(0, 10)));
        viewHolder.textInvoicesDateNo.setText(String.valueOf(dealerSalesDetailList.get(i).getInvoiceNo()));
        viewHolder.textReturnValue.setText(String.valueOf(dealerSalesDetailList.get(i).getReturnValue()));
        viewHolder.textValue.setText(String.valueOf(dealerSalesDetailList.get(i).getValue()));
        viewHolder.textCredit.setText(String.valueOf(dealerSalesDetailList.get(i).getCreditDuration()));
        viewHolder.textDue.setText(String.valueOf(dealerSalesDetailList.get(i).getDueDate()));
        viewHolder.textStatus.setText(String.valueOf(dealerSalesDetailList.get(i).getStatus()));


        if(status==3 ||status==4 ){
            viewHolder.textNAme.setText(String.valueOf(dealerSalesDetailList.get(i).getDelName()));
        }else {
            viewHolder.textNAme.setVisibility(View.GONE);
        }

        return view;
    }
    static class ViewHolderItem {
        TextView textInvoicesDate, textInvoicesDateNo, textReturnValue, textValue,textCredit,textDue,textStatus,textNAme;


    }
}
