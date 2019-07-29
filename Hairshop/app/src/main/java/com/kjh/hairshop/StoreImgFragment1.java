package com.kjh.hairshop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import util.Tag;

public class StoreImgFragment1 extends Fragment {

    ImageView imageView;
    Bitmap img;

    public StoreImgFragment1(Bitmap bitmap) {
        this.img = bitmap;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        imageView = (ImageView) inflater.inflate(R.layout.store_img1, container, false);
        imageView.setImageBitmap(img);

        return imageView;
    }
}
