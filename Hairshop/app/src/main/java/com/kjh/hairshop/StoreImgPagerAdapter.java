package com.kjh.hairshop;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class StoreImgPagerAdapter extends FragmentStatePagerAdapter {

    String photo1;
    String photo2;

    public StoreImgPagerAdapter(FragmentManager fm, String photo1, String photo2) {
        super(fm);
        this.photo1 = photo1;
        this.photo2 = photo2;
    }

    @Override
    public Fragment getItem(int position) {

        if(photo2.equals("null")) {
            return new StoreImgFragment1(photo1);
        } else {

            switch (position) {
                case 0:
                    return new StoreImgFragment1(photo1);

                case 1:
                    return new StoreImgFragment2(photo2);
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
