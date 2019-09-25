package com.kjh.hairshop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
            holder.staffNum = view.findViewById(R.id.item_staffNum);

            new getItemStaffImg(holder.imageView, vo).execute();
            new getRatingNumAsync(holder.staffNum, list.get(i).getStaff_idx()).execute();

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

    public class getRatingNumAsync extends AsyncTask<Void, Void, ArrayList<ReviewVO>> {

        String parameter = "";
        String serverip = IpInfo.SERVERIP + "getRatingNum.do";

        ArrayList<ReviewVO> list;
        TextView textView;
        int staff_idx;

        public getRatingNumAsync(TextView textView, int staff_idx) {
            this.textView = textView;
            this.staff_idx = staff_idx;
        }

        @Override
        protected ArrayList<ReviewVO> doInBackground(Void... voids) {

            parameter = "staff_idx=" + staff_idx;

            try {
                String str;
                URL url = new URL(serverip);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                osw.write( parameter );
                osw.flush();


                if( conn.getResponseCode() == conn.HTTP_OK ) {

                    InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(isr);

                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }

                    JSONArray jsonArray = new JSONArray(buffer.toString());
                    JSONObject jsonObject = null;

                    list = new ArrayList();

                    for( int i = 0; i < jsonArray.length(); i++ ) {
                        jsonObject = jsonArray.getJSONObject(i);

                        ReviewVO vo = new ReviewVO();
                        vo.setRating(jsonObject.getInt("rating"));

                        list.add(vo);
                    }
                }
            } catch (Exception e) {
                Log.i( "MY", e.toString());
            }

            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<ReviewVO> reviewVOS) {

            if(reviewVOS.size() == 0) {
                textView.setText("0/5");
            } else {

                int hap = 0;

                for(int i = 0; i < reviewVOS.size(); i++) {

                    hap += reviewVOS.get(i).getRating();
                }

                String avg = String.format("%.1f", (float)hap / reviewVOS.size());
                textView.setText(avg + "/5");

            }
        }
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
        TextView staffNum;
    }
}
