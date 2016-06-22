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
public class CollectionReportAdapter extends BaseAdapter {

    private Context mContext;
    ArrayList<ReportList> collectionDetailList;
    private static LayoutInflater inflater = null;
    int status;

    public CollectionReportAdapter(Context mContext, ArrayList<ReportList> collectionSalesDetailList, int status) {
        this.mContext = mContext;
        this.collectionDetailList = collectionSalesDetailList;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.status = status;
    }

    @Override
    public int getCount() {
        return collectionDetailList.size();
    }

    @Override
    public Object getItem(int i) {
        return collectionDetailList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolderItem viewHolder = null;
        viewHolder = new ViewHolderItem();
        view = inflater.inflate(R.layout.list_report_collection, null);



        viewHolder.textcollNo = (TextView) view.findViewById(R.id.tvName3);
        viewHolder.textcollDate = (TextView) view.findViewById(R.id.tvDate3);
        viewHolder.textInvoicesNo = (TextView) view.findViewById(R.id.tvInvNo3);
        viewHolder.textChash = (TextView) view.findViewById(R.id.tvValue3);
        viewHolder.textCheque = (TextView) view.findViewById(R.id.tvValue33);

        viewHolder.textNAme = (TextView) view.findViewById(R.id.tvcollname);


        viewHolder.textcollNo.setText(String.valueOf(collectionDetailList.get(i).getCollectionNo()));
        viewHolder.textcollDate.setText(String.valueOf(collectionDetailList.get(i).getCollectionDate()));
        viewHolder.textInvoicesNo.setText(String.valueOf(collectionDetailList.get(i).getInvoiceNo()));
        viewHolder.textChash.setText(String.valueOf(collectionDetailList.get(i).getCashAmmount()));
        viewHolder.textCheque.setText(String.valueOf(collectionDetailList.get(i).getChequsAmmount()));


        if(status==3 ||status==4 ){
            viewHolder.textNAme.setText(String.valueOf(collectionDetailList.get(i).getDelName()));
        }else {
            viewHolder.textNAme.setVisibility(View.GONE);
        }

        return view;
    }

    static class ViewHolderItem {
        TextView textcollNo,textcollDate, textInvoicesNo,textChash,textCheque,textNAme;


    }
}
