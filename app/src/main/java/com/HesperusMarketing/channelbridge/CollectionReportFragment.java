package com.HesperusMarketing.channelbridge;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HesperusMarketing.channelbridgeaddapters.CollectionReportAdapter;
import com.HesperusMarketing.channelbridgeaddapters.DealerSalesAdapter;
import com.HesperusMarketing.channelbridgeaddapters.ReportList;
import com.HesperusMarketing.channelbridgedb.CollectionNoteSendToApprovel;
import com.HesperusMarketing.channelbridgedb.Customers;
import com.HesperusMarketing.channelbridgedb.Invoice;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Himanshu on 6/4/2016.
 */
public class CollectionReportFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    AutoCompleteTextView edtCustomer, edtTown;
    ListView list;
    TextView txtDelName, txtfromDate, txtToDate;
    RelativeLayout layoutClander;

    private List<String> customerNameList;
    private List<String> townNameList;

    String fromDate = null, dateTo, cusNAme = null, town = null;
    int serachStatus = 0;


    CollectionReportAdapter colladapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        edtCustomer = (AutoCompleteTextView) view.findViewById(R.id.actvCustomer);
        edtTown = (AutoCompleteTextView) view.findViewById(R.id.actvTown);
        txtDelName = (TextView) view.findViewById(R.id.textView44);
        list = (ListView) view.findViewById(R.id.listView_collection);
        layoutClander = (RelativeLayout) view.findViewById(R.id.relativeLayout_Dialog_calender);
        txtfromDate = (TextView) view.findViewById(R.id.textViewDAteFrom);
        txtToDate = (TextView) view.findViewById(R.id.textViewDateTo);


        customerNameList = getNameList();
        ArrayAdapter<String> customerListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, customerNameList);
        edtCustomer.setAdapter(customerListAdapter);

        townNameList = getTownList();
        ArrayAdapter<String> townListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, townNameList);
        edtTown.setAdapter(townListAdapter);


        edtCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                txtDelName.setVisibility(View.GONE);
                cusNAme = adapterView.getItemAtPosition(i).toString();
                edtTown.setText("");
                if (fromDate == null) {
                    fillGrid(cusNAme, "", "", "", 0);
                } else {
                    serachStatus = 1;
                    fillGrid(cusNAme, "", fromDate, dateTo, 1);
                }

            }
        });

        edtTown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                edtCustomer.setText("");
                txtDelName.setVisibility(View.VISIBLE);
                town = adapterView.getItemAtPosition(i).toString();
                if (fromDate == null) {
                    fillGrid("", town, "", "", 3);
                } else {
                    fillGrid("", town, fromDate, dateTo, 4);
                }

            }
        });


        layoutClander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDate=null;
                dateTo=null;

                txtfromDate.setText("From : ");
                txtToDate.setText("To : ");

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog().newInstance(CollectionReportFragment.this, now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                final Activity activity = getActivity();
                dpd.show(activity.getFragmentManager(), "Datepickerdialog");
            }
        });

        return view;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        String month,day,endmonth,enddate;


        if(String.valueOf(dayOfMonth).length()==1){
            day="0"+String.valueOf(dayOfMonth);
        }else {
            day=String.valueOf(dayOfMonth);
        }

        if(String.valueOf((monthOfYear+1)).length()==1){
            month="0"+String.valueOf((monthOfYear+1));
        }else {
            month=String.valueOf((monthOfYear+1));
        }

        if(String.valueOf(dayOfMonthEnd).length()==1){
            enddate="0"+String.valueOf(dayOfMonthEnd);
        }else {
            enddate=String.valueOf(dayOfMonthEnd);
        }

        if(String.valueOf(( monthOfYearEnd+1)).length()==1){
            endmonth="0"+String.valueOf(( monthOfYearEnd+1));
        }else {
            endmonth=String.valueOf(( monthOfYearEnd+1));
        }



        fromDate = day + "-" + month+ "-" + year;
        dateTo = enddate + "-" +endmonth + "-" + yearEnd;

        txtfromDate.setText("From : " + fromDate);
        txtToDate.setText("To : " + dateTo);

        if (cusNAme == null) {
            fillGrid("", town, fromDate, dateTo, 4);
        } else if (town == null) {
            fillGrid(cusNAme, "", fromDate, dateTo, 1);
        } else {

        }


    }


    public ArrayList<String> getNameList() {
        Customers customersObject = new Customers(getActivity());
        customersObject.openReadableDatabase();
        ArrayList<String> customerNamesListArray = customersObject.getCustomerNames();
        customersObject.closeDatabase();
        customerNameList = customerNamesListArray;
        return customerNamesListArray;
    }

    public ArrayList<String> getTownList() {
        Customers customers = new Customers(getActivity());
        customers.openReadableDatabase();
        ArrayList<String> townList = customers.getTownList();
        customers.closeDatabase();
        townNameList = townList;
        return townList;

    }

    public void fillGrid(String name, String twon, String toDAte, String fromDate, int ststus) {

        CollectionNoteSendToApprovel collectionNoteSendToApprovel = new CollectionNoteSendToApprovel(getContext());
        collectionNoteSendToApprovel.openReadableDatabase();

        List<ReportList> List;
        List = collectionNoteSendToApprovel.getDataForCollection(name, twon, toDAte, fromDate, ststus);
        colladapter = new CollectionReportAdapter(getActivity(), (ArrayList<ReportList>) List, ststus);
        list.setAdapter(colladapter);
        collectionNoteSendToApprovel.closeDatabase();
    }
}
