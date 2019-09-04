package com.kjh.hairshopdesigner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new PageReservation();
            case 1:
                return new PageComment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }


}
