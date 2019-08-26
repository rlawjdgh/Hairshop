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
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;
import kr.co.bootpay.enums.UX;
import kr.co.bootpay.listener.CancelListener;
import kr.co.bootpay.listener.CloseListener;
import kr.co.bootpay.listener.ConfirmListener;
import kr.co.bootpay.listener.DoneListener;
import kr.co.bootpay.listener.ErrorListener;
import kr.co.bootpay.listener.ReadyListener;
import util.IpInfo;

public class GetSurgeryAdapter extends BaseAdapter {

    StoreReservationActivity storeReservationActivity;
    ArrayList<SurgeryVO> list;

    int staff_idx;
    String cal_day;
    String getTime;
    int login_idx;

    public GetSurgeryAdapter(StoreReservationActivity storeReservationActivity, ArrayList<SurgeryVO> list, int staff_idx, String cal_day, String getTime) {
        this.storeReservationActivity = storeReservationActivity;
        this.list = list;
        this.staff_idx = staff_idx;
        this.cal_day = cal_day;
        this.getTime = getTime;
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

                    /*Intent intent = new Intent(storeReservationActivity, KakaoPayActivity.class);
                    intent.putExtra("staff_idx", staff_idx);
                    intent.putExtra("regdate", cal_day);
                    intent.putExtra("time", getTime);
                    intent.putExtra("surgery_name", list.get(i).getName());
                    intent.putExtra("price", list.get(i).getPrice());

                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    storeReservationActivity.startActivity(intent);*/

                    new ReservationAsync().execute(staff_idx, cal_day, getTime, list.get(i).getName(), list.get(i).getPrice());

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

        String result;
        String staff_idx, cal_day, getTiem, surgery_name = "";
        int price;

        @Override
        protected String doInBackground(Object... objects) {

            staff_idx = (String)objects[0];
            cal_day = (String)objects[1];
            getTiem = (String)objects[2];
            surgery_name = (String)objects[3];
            price = (Integer)objects[4];

            parameter = "login_idx=" + login_idx + "&staff_idx=" + staff_idx + "&cal_day=" + cal_day + "&getTime=" 
                    + getTiem + "&surgery_name=" + surgery_name + "&price=" + price;

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

                // 페이지 이동
            }
        }
    }

    class MyHolder {
        TextView name;
        TextView price;
    }
}
