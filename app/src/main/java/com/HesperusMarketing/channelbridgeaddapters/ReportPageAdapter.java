package com.HesperusMarketing.channelbridgeaddapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.HesperusMarketing.channelbridge.ChequesReturnsFragment;
import com.HesperusMarketing.channelbridge.CollectionReportFragment;
import com.HesperusMarketing.channelbridge.DealerSaleFragment;
import com.HesperusMarketing.channelbridge.RetrunSalesFragment;

/**
 * Created by Himanshu on 6/4/2016.
 */
public class ReportPageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;


    public ReportPageAdapter(FragmentManager fm,int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                DealerSaleFragment tab1 = new DealerSaleFragment();
                return tab1;
            case 1:
                RetrunSalesFragment tab2 = new RetrunSalesFragment();
                return tab2;
            case 2:
                CollectionReportFragment tab3 = new CollectionReportFragment();
                return tab3;
            case 3:
                ChequesReturnsFragment tab4 = new ChequesReturnsFragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
