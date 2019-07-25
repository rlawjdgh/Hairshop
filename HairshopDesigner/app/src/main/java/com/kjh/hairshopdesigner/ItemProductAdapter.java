package com.kjh.hairshopdesigner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import util.IpInfo;
import util.Tag;

public class ItemProductAdapter extends BaseAdapter {

    ArrayList<SurgeryVO> list;
    DesignerSurgeryActivity designerSurgeryActivity;
    SurgeryVO vo;

    public ItemProductAdapter(ArrayList<SurgeryVO> list, DesignerSurgeryActivity designerSurgeryActivity) {
        this.list = list;
        this.designerSurgeryActivity = designerSurgeryActivity;
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

        final MyHolder holder;
        vo = new SurgeryVO();
        vo = list.get(i);

        Log.d(Tag.t, "" + list.size());

        if(view == null) {
            view = View.inflate(designerSurgeryActivity, R.layout.item_product_surgery, null);

            holder = new MyHolder();
            holder.imageView = view.findViewById(R.id.image_product);
            holder.name  = view.findViewById(R.id.item_ProductName);
            holder.price = view.findViewById(R.id.item_ProductPrice);

            new getItemProductImg(holder.imageView, vo).execute();

            view.setTag(holder);

        } else {
            holder = (MyHolder)view.getTag();
        }

        String rePrice = new DecimalFormat("###,###").format(list.get(i).getPrice());


        holder.name.setText(list.get(i).getName());
        holder.price.setText(rePrice + "원");

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(designerSurgeryActivity);

                builder.setTitle("삭제하시겠습니까?")
                        .setNegativeButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                new removeItemProduct().execute(list.get(i).getSurgery_idx());
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

    public class removeItemProduct extends AsyncTask<Integer, Void, String> {

        String parameter;
        String serverip = IpInfo.SERVERIP + "removeItemSurgery.do";

        int surgery_idx;
        String result;

        @Override
        protected String doInBackground(Integer... integers) {

            surgery_idx = integers[0];
            parameter = "surgery_idx=" + surgery_idx;

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

            if( s.equals("success") ){
                Log.d(Tag.t, "삭제완료");
            }
        }
    }

    public class getItemProductImg extends AsyncTask<Void, Void, Bitmap> {

        ImageView imageView;
        SurgeryVO vo;

        public getItemProductImg(ImageView imageView, SurgeryVO vo) {
            this.imageView = imageView;
            this.vo = vo;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            URL url = null;
            String photo = vo.getPhoto();
            Bitmap bitmap = null;

            try {
                url = new URL(IpInfo.SERVERIP + "product_photo/" + photo);
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
        protected void onPostExecute(final Bitmap bitmap) {

            imageView.setImageBitmap(bitmap);
        }
    }

    class MyHolder {
        ImageView imageView;
        TextView name;
        TextView price;
    }
}
