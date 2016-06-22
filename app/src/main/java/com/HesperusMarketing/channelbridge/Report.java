package com.HesperusMarketing.channelbridge;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.HesperusMarketing.channelbridgeaddapters.ReportPageAdapter;
import com.HesperusMarketing.channelbridgedb.Approval_Persons;
import com.HesperusMarketing.channelbridgedb.ChequesDetails;
import com.HesperusMarketing.channelbridgews.WebService;

import java.util.ArrayList;

/**
 * Created by Himanshu on 6/4/2016.
 */
public class Report extends AppCompatActivity {

    String rpID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        rpID = sharedPreferences.getString("RepId", "-1");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Dealer Report"));
        tabLayout.addTab(tabLayout.newTab().setText("Return Report"));
        tabLayout.addTab(tabLayout.newTab().setText("Collection Report"));
        tabLayout.addTab(tabLayout.newTab().setText("Cheques Return Report"));
      //  tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final ReportPageAdapter adapter = new ReportPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        if (isOnline()) {
            new downloadCheqDetails().execute("0","0");
        } else {

        }


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    public class downloadCheqDetails extends AsyncTask<String, Integer, Integer>{
        ArrayList<String[]> cheqList = null;


        @Override
        protected Integer doInBackground(String... strings) {
            while (cheqList == null) {
                try {

                    WebService webService = new WebService();
                    cheqList = webService.getChequeDetails(rpID);

                    Thread.sleep(100);

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }
            }

            return null;
        }
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            try {
                ChequesDetails cDetails = new ChequesDetails(Report.this);
                cDetails.openWritableDatabase();

                for (int i = 0; i < cheqList.size(); i++) {
                    String[] personDetails = cheqList.get(i);
                    cDetails.insert_Cheqes(personDetails[0], personDetails[1], personDetails[2], personDetails[3], personDetails[4],
                            personDetails[5],personDetails[6],personDetails[7],personDetails[8],personDetails[9],personDetails[10],personDetails[11]);

                }
                cDetails.closeDatabase();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public boolean isOnline() {
        boolean flag = false;
        ConnectivityManager connMgr = (ConnectivityManager)this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            flag = true;
        }
        return flag;
    }
}
