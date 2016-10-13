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
 * Created by Himanshu on 5/18/2016.
 */
public class CollectionChequeAdapter extends BaseAdapter {


    private Context mContext;
    ArrayList<ListCollectionCheque> listCollectionCheque;


    public CollectionChequeAdapter(Context context, ArrayList<ListCollectionCheque> listcollectioncheque) {
        mContext = context;
        listCollectionCheque = listcollectioncheque;

    }

    @Override
    public int getCount() {
        return listCollectionCheque.size();
    }

    @Override
    public Object getItem(int i) {
        return listCollectionCheque.get(i);
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
        view = inflater.inflate(R.layout.list_dialogcollection_cheque, null);

        viewHolder.textcNumber = (TextView) view.findViewById(R.id.textView_cheque_number);
        viewHolder.textcValue = (TextView) view.findViewById(R.id.textView_value);

        viewHolder.textcNumber.setText(String.valueOf(listCollectionCheque.get(i).cNumber));
        viewHolder.textcValue.setText(String.valueOf(listCollectionCheque.get(i).cAmmount));


        return view;
    }

    static class ViewHolderItem {
        TextView textcNumber, textcValue;


    }
}
