package com.kjh.hairshop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import util.IpInfo;

public class GetStoreStaffAdapter extends BaseAdapter {

    StoreInfoActivity storeInfoActivity;
    ArrayList<StaffVO> list;
    String store_name;
    int store_idx;
    StaffVO vo;

    public GetStoreStaffAdapter(StoreInfoActivity storeInfoActivity, ArrayList<StaffVO> list, String store_name, int store_idx) {
        this.storeInfoActivity = storeInfoActivity;
        this.list = list;
        this.store_name = store_name;
        this.store_idx = store_idx;
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

        if(view == null) {

            view = View.inflate(storeInfoActivity, R.layout.item_store_staff, null);

            holder = new MyHolder();
            holder.imageView = view.findViewById(R.id.item_storeInfo_img);
            holder.name = view.findViewById(R.id.item_storeInfo_name);
            holder.info = view.findViewById(R.id.item_storeInfo_info);

            new getItemStaffImg(holder.imageView, vo).execute();

            view.setTag(holder);

        } else {
            holder = (MyHolder)view.getTag();
        }

        holder.name.setText(list.get(i).getName() + " " + list.get(i).getGrade());
        holder.info.setText(list.get(i).getInfo());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(storeInfoActivity, StoreReservationActivity.class);
                intent.putExtra("staff_idx", list.get(i).getStaff_idx());
                intent.putExtra("staff_name", list.get(i).getName());
                intent.putExtra("staff_grade", list.get(i).getGrade());
                intent.putExtra("staff_info", list.get(i).getInfo());
                intent.putExtra("staff_photo", list.get(i).getPhoto());
                intent.putExtra("store_idx", store_idx);
                intent.putExtra("store_name", store_name);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                storeInfoActivity.startActivity(intent);
            }
        });

        return view;
    }

    public class getItemStaffImg extends AsyncTask<Void, Void, Bitmap> {

        ImageView imageView;
        StaffVO vo;

        public getItemStaffImg(ImageView imageView, StaffVO vo) {
            this.imageView = imageView;
            this.vo = vo;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            URL url = null;
            String photo = vo.getPhoto();
            Bitmap bitmap = null;

            try {
                url = new URL(IpInfo.SERVERIP + "staff_photo/" + photo);
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

            imageView.setImageBitmap(bitmap);
        }
    }

    class MyHolder {
        ImageView imageView;
        TextView name;
        TextView info;
    }
}
