package com.kjh.hairshop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import util.IpInfo;
import util.Tag;

public class StoreImgPagerAdapter extends FragmentStatePagerAdapter {

    Bitmap img1, img2;


    public StoreImgPagerAdapter(FragmentManager fm, Bitmap bitmap1, Bitmap bitmap2) {
        super(fm);
        this.img1 = bitmap1;
        this.img2 = bitmap2;
    }

    @Override
    public Fragment getItem(int position) {

        if(img2 == null) {
            return new StoreImgFragment1(img1);
        } else {

            switch (position) {
                case 0:
                    return new StoreImgFragment1(img1);

                case 1:
                    return new StoreImgFragment2(img2);
            }
        }
        return null;
    }

    @Override
    public int getCount() {

        if(img2 == null) {
            return 1;
        } else {
            return 2;
        }
    }
}
