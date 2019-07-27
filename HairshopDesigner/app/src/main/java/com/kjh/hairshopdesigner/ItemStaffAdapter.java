package com.kjh.hairshopdesigner;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import java.util.BitSet;

import util.IpInfo;
import util.Tag;

public class ItemStaffAdapter extends BaseAdapter {

    ArrayList<StaffVO> list;
    DesignerStaffActivity designerStaffActivity;
    StaffVO vo;

    public ItemStaffAdapter(ArrayList<StaffVO> list, DesignerStaffActivity designerStaffActivity) {
        this.list = list;
        this.designerStaffActivity = designerStaffActivity;
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

            view = View.inflate(designerStaffActivity, R.layout.item_staff, null);

            holder = new MyHolder();
            holder.imageView = view.findViewById(R.id.imageView_item_staffImg);
            holder.name = view.findViewById(R.id.textView_item_staffName);
            holder.grade = view.findViewById(R.id.textView_item_staffGrade);
            holder.info = view.findViewById(R.id.textView_item_staffInfo);

            new getItemStaffImg(holder.imageView, vo).execute();

            view.setTag(holder);

        } else {
            holder = (MyHolder)view.getTag();
        }

        holder.name.setText("이름 : " + list.get(i).getName());
        holder.grade.setText("직급 : " + list.get(i).getGrade());
        holder.info.setText("한마디 : " + list.get(i).getInfo());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(designerStaffActivity);

                builder.setTitle("삭제하시겠습니까?")
                        .setNegativeButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                new removeItemStaff().execute(list.get(i).getStaff_idx());
                                notifyDataSetChanged();
                            }
                        })
                        .setPositiveButton("아니요", null)
                        .setCancelable(false)
                        .show();
            }
        });

        return view;
    }

    public class removeItemStaff extends AsyncTask<Integer, Void, String> {

        String parameter;
        String serverip = IpInfo.SERVERIP + "removeItemStaff.do";

        int staff_idx;
        String result;

        @Override
        protected String doInBackground(Integer... integers) {

            staff_idx = integers[0];
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
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    result = jsonObject.getString("result");
                }

            } catch (Exception e) {
                Log.i( "MY", e.toString() );
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
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
        TextView grade;
        TextView info;
    }
}
