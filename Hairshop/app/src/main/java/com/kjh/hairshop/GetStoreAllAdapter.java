package com.kjh.hairshop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import util.IpInfo;
import util.Tag;

public class GetStoreAllAdapter extends BaseAdapter {

    ArrayList<LocationVO> list;
    MainActivity mainActivity;
    LocationVO vo;
    Double my_lat, my_lng;


    public GetStoreAllAdapter(ArrayList<LocationVO> list, MainActivity mainActivity, double my_lat, double my_lng) {
        this.list = list;
        this.mainActivity = mainActivity;
        this.my_lat = my_lat;
        this.my_lng = my_lng;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        MyHolder holder;
        vo = list.get(i);

        Log.d(Tag.t, "ddddd : " + list.get(i).getPhoto());

        if(view == null) {

            view = View.inflate(mainActivity, R.layout.item_hair_list, null);

            holder = new MyHolder();
            holder.imageView = view.findViewById(R.id.item_storeImg);
            holder.name = view.findViewById(R.id.item_store_name);
            holder.info = view.findViewById(R.id.item_store_info);
            holder.street = view.findViewById(R.id.item_store_street);

            new getStoreImg(holder.imageView, vo).execute();

            view.setTag(holder);

        } else {
            holder = (MyHolder)view.getTag();
        }

        holder.name.setText(list.get(i).getName());
        holder.info.setText(list.get(i).getInfo());

        double distance = list.get(i).getAddress();
        String store_location = String.format("%.2f", distance / 1000);
        holder.street.setText(store_location + "KM");

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mainActivity, StoreInfoActivity.class);
                intent.putExtra("store_idx", list.get(i).getNickName_idx());
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                mainActivity.startActivity(intent);
            }
        });

        return view;
    }

    public class getStoreImg extends AsyncTask<Void,Void, Bitmap> {

        ImageView store_image;
        LocationVO vo;

        public getStoreImg(ImageView imageView, LocationVO vo) {
            this.store_image = imageView;
            this.vo = vo;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            URL url = null;
            String photo = vo.getPhoto();
            Log.d(Tag.t, "dddsfs : "  + photo);
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

            store_image.setImageBitmap(bitmap);
        }
    }

    class MyHolder {
        ImageView imageView;
        TextView name;
        TextView info;
        TextView street;
    }
}
