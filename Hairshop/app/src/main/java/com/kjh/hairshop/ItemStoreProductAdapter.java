package com.kjh.hairshop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

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

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
import kr.co.bootpay.CancelListener;
import kr.co.bootpay.CloseListener;
import kr.co.bootpay.ConfirmListener;
import kr.co.bootpay.DoneListener;
import kr.co.bootpay.ErrorListener;
import kr.co.bootpay.ReadyListener;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;
import util.IpInfo;

public class ItemStoreProductAdapter extends BaseAdapter {

    ArrayList<SurgeryVO> list;
    StoreProductActivity storeProductActivity;
    SurgeryVO vo;
    int login_idx;

    public ItemStoreProductAdapter(ArrayList<SurgeryVO> list, StoreProductActivity storeProductActivity) {
        this.list = list;
        this.storeProductActivity = storeProductActivity;
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

        BootpayAnalytics.init(storeProductActivity, "5d511cdf0627a80027ea5b8e");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( storeProductActivity );
        login_idx = pref.getInt("login_idx", 0);

        if(view == null) {
            view = View.inflate(storeProductActivity, R.layout.item_store_product, null);

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
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(storeProductActivity);
                builder.setTitle("구매하시겠습니까?");

                builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Bootpay.init(storeProductActivity)
                                .setApplicationId("5d511cdf0627a80027ea5b8e")
                                .setPG(PG.KAKAO)
                                .setMethod(Method.EASY)
                                .setName(list.get(i).getName())
                                .setOrderId(list.get(i).getName())
                                .setPrice(list.get(i).getPrice())
                                .onConfirm(new ConfirmListener() {
                                    @Override
                                    public void onConfirm(@Nullable String message) {
                                        if (0 < 10) Bootpay.confirm(message);
                                        else Bootpay.removePaymentWindow();
                                        Log.d("confirm", message);
                                    }
                                })
                                .onDone(new DoneListener() {
                                    @Override
                                    public void onDone(@Nullable String message) {

                                        new addStoreProductAsync().execute(list.get(i).getSurgery_idx());
                                    }
                                })
                                .onReady(new ReadyListener() {
                                    @Override
                                    public void onReady(@Nullable String message) {
                                        Log.d("ready", message);
                                    }
                                })
                                .onCancel(new CancelListener() {
                                    @Override
                                    public void onCancel(@Nullable String message) {
                                        Log.d("cancel", message);
                                    }
                                })
                                .onError(new ErrorListener() {
                                    @Override
                                    public void onError(@Nullable String message) {
                                        Log.d("error", message);
                                    }
                                })
                                .onClose(new CloseListener() {
                                    @Override
                                    public void onClose(String message) {
                                        Log.d("close", "close");
                                    }
                                })
                                .show();

                        }
                    })
                    .setPositiveButton("아니요", null);
                    builder.show();
            }
        });

        return view;
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

    public class addStoreProductAsync extends AsyncTask<Integer, Void, String> {

        String parameter;
        String serverip = IpInfo.SERVERIP + "addStoreProduct.do";

        String result;

        @Override
        protected String doInBackground(Integer... integers) {

            parameter = "login_idx=" + login_idx + "&surgery_idx=" + integers[0];

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

            if(s.equals("success")) {

                handler.sendEmptyMessageDelayed(0, 900);
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 0) {
                Toast.makeText(storeProductActivity, "구매 완료하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    class MyHolder {

        ImageView imageView;
        TextView name;
        TextView price;
    }
}
