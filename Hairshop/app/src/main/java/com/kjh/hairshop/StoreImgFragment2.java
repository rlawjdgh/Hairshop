package com.kjh.hairshop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import util.IpInfo;


public class StoreImgFragment2 extends Fragment {

    ImageView imageView;
    Bitmap img;

    public StoreImgFragment2(Bitmap bitmap) {
        this.img = bitmap;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        imageView = (ImageView) inflater.inflate(R.layout.store_img2, container, false);
        imageView.setImageBitmap(img);

        return imageView;
    }
}
