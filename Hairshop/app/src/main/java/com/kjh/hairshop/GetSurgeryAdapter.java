package com.kjh.hairshop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
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
import util.Tag;

public class GetSurgeryAdapter extends BaseAdapter {

    StoreReservationActivity storeReservationActivity;
    ArrayList<SurgeryVO> list;

    int login_idx, staff_idx, store_idx, price;
    String cal_day, getTime;

    public GetSurgeryAdapter(StoreReservationActivity storeReservationActivity, ArrayList<SurgeryVO> list,
                             int staff_idx, String cal_day, String getTime, int store_idx) {
        this.storeReservationActivity = storeReservationActivity;
        this.list = list;
        this.staff_idx = staff_idx;
        this.cal_day = cal_day;
        this.getTime = getTime;
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
    public View getView(final int i, View view, ViewGroup parent) {

        MyHolder holder;

        BootpayAnalytics.init(storeReservationActivity, "5d511cdf0627a80027ea5b8e");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( storeReservationActivity );
        login_idx = pref.getInt("login_idx", 0);

        if(view == null) {

            view = View.inflate(storeReservationActivity, R.layout.item_surgery, null);

            holder = new MyHolder();
            holder.name = view.findViewById(R.id.item_name);
            holder.price = view.findViewById(R.id.item_price);

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

                AlertDialog.Builder builder = new AlertDialog.Builder(storeReservationActivity);
                builder.setTitle("예약하시겠습니까?");

                builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        price = list.get(i).getPrice();

                        if(getTime.equals("") || cal_day.equals("")) {
                            Toast.makeText(storeReservationActivity, "날짜와 시간을 확인해주세요", Toast.LENGTH_SHORT).show();
                        } else {

                            Bootpay.init(storeReservationActivity)
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

                                            new ReservationAsync().execute(list.get(i).getName(), price);
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
                    }
                })
                .setPositiveButton("아니요", null);
                builder.show();
            }
        });

        return view;
    }


    public class ReservationAsync extends AsyncTask<Object, Void, String> {

        String parameter;
        String serverip = IpInfo.SERVERIP + "insertReservation.do";

        String result, surgery_name = "";
        int price;

        @Override
        protected String doInBackground(Object... objects) {

            surgery_name = (String)objects[0];
            price = (Integer)objects[1];

            parameter = "login_idx=" + login_idx + "&store_idx=" + store_idx + "&staff_idx=" + staff_idx + "&cal_day=" + cal_day + "&getTime="
                    + getTime + "&surgery_name=" + surgery_name + "&price=" + price;

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
                new addPointAsync().execute();
            }
        }
    }

    public class addPointAsync extends AsyncTask<Void, Void, String> {

        String parameter;
        String serverip = IpInfo.SERVERIP + "addUserPoint.do";

        String result;

        @Override
        protected String doInBackground(Void... voids) {

            parameter = "login_idx=" + login_idx + "&price=" + price;

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
                Toast.makeText(storeReservationActivity, "예약이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(storeReservationActivity, MyReservationActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                storeReservationActivity.startActivity(intent);
            }
        }
    }

    class MyHolder {
        TextView name;
        TextView price;
    }
}
