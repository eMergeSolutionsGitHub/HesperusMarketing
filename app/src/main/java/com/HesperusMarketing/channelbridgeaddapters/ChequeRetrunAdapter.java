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
public class ChequeRetrunAdapter extends BaseAdapter {

    private Context mContext;
    ArrayList<ReportList> chequesReturnsDetailList;
    private static LayoutInflater inflater = null;
    int status;


    public ChequeRetrunAdapter(Context context, ArrayList<ReportList> chequesreturnsdetailList, int stat) {
        mContext = context;
        chequesReturnsDetailList = chequesreturnsdetailList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        status= stat;
    }

    @Override
    public int getCount() {
        return chequesReturnsDetailList.size();
    }

    @Override
    public Object getItem(int i) {
        return chequesReturnsDetailList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolderItem viewHolder = null;
        viewHolder = new ViewHolderItem();
        view = inflater.inflate(R.layout.list_report_cheque, null);

        viewHolder.textNAme = (TextView) view.findViewById(R.id.textView11);
        viewHolder.textcollectedDate = (TextView) view.findViewById(R.id.textView29);
        viewHolder.textCollectionNo = (TextView) view.findViewById(R.id.textView31);
        viewHolder.textChequeNumber = (TextView) view.findViewById(R.id.textView66);
        viewHolder.textInvoicesNo = (TextView) view.findViewById(R.id.textView33);
        viewHolder.textAmount = (TextView) view.findViewById(R.id.textView44);

        viewHolder.textNAme.setText(String.valueOf(chequesReturnsDetailList.get(i).getDelName()));
        viewHolder.textcollectedDate.setText(String.valueOf(chequesReturnsDetailList.get(i).getCollectedDate()).substring(0,10));
        viewHolder.textCollectionNo.setText(String.valueOf(chequesReturnsDetailList.get(i).getCollectionNo()));
        viewHolder.textChequeNumber.setText(String.valueOf(chequesReturnsDetailList.get(i).getChequeNum()));
        viewHolder.textInvoicesNo.setText(String.valueOf(chequesReturnsDetailList.get(i).getInvoiceNo()));
        viewHolder.textAmount.setText(String.valueOf(chequesReturnsDetailList.get(i).getChequAmount()));




        return view;
    }
    static class ViewHolderItem {
        TextView textNAme,textcollectedDate, textCollectionNo, textChequeNumber, textInvoicesNo,textAmount;


    }
}
