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
    StaffVO vo;

    public GetStoreStaffAdapter(StoreInfoActivity storeInfoActivity, ArrayList<StaffVO> list) {
        this.storeInfoActivity = storeInfoActivity;
        this.list = list;
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
            holder.grade = view.findViewById(R.id.item_storeInfo_grade);
            holder.info = view.findViewById(R.id.item_storeInfo_info);

            new getItemStaffImg(holder.imageView, vo).execute();

            view.setTag(holder);

        } else {
            holder = (MyHolder)view.getTag();
        }

        holder.name.setText(list.get(i).getName());
        holder.grade.setText(" (" + list.get(i).getGrade() + ")");
        holder.info.setText(list.get(i).getInfo());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(storeInfoActivity);
                builder.setTitle("예약하시겠습니까?");

                builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // 예약 페이지

                    }
                })
                .setPositiveButton("아니요", null);
                builder.show();
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
        TextView grade;
        TextView info;
    }
}
