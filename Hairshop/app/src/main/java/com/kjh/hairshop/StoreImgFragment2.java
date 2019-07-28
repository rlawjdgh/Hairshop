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

    String photo2;
    ImageView imageView;
    Bitmap src;

    public StoreImgFragment2(String photo2) {
        this.photo2 = photo2;
    }

    public class getItemStaffImg extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {

            URL url = null;
            String photo = photo2;
            Bitmap bitmap = null;

            try {
                url = new URL(IpInfo.SERVERIP + "store_photo/" + photo);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);

                is.close();
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            src = bitmap;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        imageView = (ImageView) inflater.inflate(R.layout.store_img2, container, false);
        imageView.setImageBitmap(src);

        return imageView;
    }

}
