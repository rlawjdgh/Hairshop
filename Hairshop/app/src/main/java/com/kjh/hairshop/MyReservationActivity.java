package com.kjh.hairshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import util.IpInfo;
import util.Tag;

public class MyReservationActivity extends AppCompatActivity {

    TextView tv_noReservation;
    ListView lv_myReservation;
    ItemMyReservationAdapter itemMyReservationAdapter;
    Button btn_back;

    int login_idx;

    @Override
    protected void onResume() {
        super.onResume();
        new getMyReservationAsync().execute(login_idx);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservation);

        tv_noReservation = findViewById(R.id.textView_noReservation);
        lv_myReservation = findViewById(R.id.listView_reservation);
        btn_back = findViewById(R.id.button_myReservationBack);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( MyReservationActivity.this );
        login_idx = pref.getInt("login_idx", 0);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                move();
            }
        });

        new getMyReservationAsync().execute(login_idx);
    }

    public class getMyReservationAsync extends AsyncTask<Integer, Void, ArrayList<ReservationVO>> {

        ArrayList<ReservationVO> list;
        ReservationVO vo;

        String parameter = "";
        String serverip = IpInfo.SERVERIP + "getMyReservation.do";

        @Override
        protected ArrayList<ReservationVO> doInBackground(Integer... integers) {

            parameter = "login_idx=" + integers[0];

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

                        vo = new ReservationVO();
                        vo.setReservation_idx(jsonObject.getInt("reservation_idx"));
                        vo.setStore_idx(jsonObject.getInt("store_idx"));
                        vo.setStore_name(jsonObject.getString("store_name"));
                        vo.setStaff_name(jsonObject.getString("staff_name"));
                        vo.setStaff_grade(jsonObject.getString("staff_grade"));
                        vo.setCal_day(jsonObject.getString("cal_day"));
                        vo.setGetTime(jsonObject.getString("getTime"));
                        vo.setSurgery_name(jsonObject.getString("surgery_name"));
                        vo.setComplete(jsonObject.getInt("complete"));

                        list.add(vo);
                    }
                }

            } catch (Exception e) {
                Log.i( "MY", e.toString());
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<ReservationVO> reservationVOS) {

            if(reservationVOS.size() == 0 ) {

                tv_noReservation.setVisibility(View.VISIBLE);
                lv_myReservation.setVisibility(View.GONE);
            } else {

                tv_noReservation.setVisibility(View.GONE);
                lv_myReservation.setVisibility(View.VISIBLE);

                itemMyReservationAdapter = new ItemMyReservationAdapter(reservationVOS, MyReservationActivity.this, login_idx);
                lv_myReservation.setAdapter(itemMyReservationAdapter);
            }
        }
    }

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 0) {
                new getMyReservationAsync().execute(login_idx);
            }
        }
    };

    @Override
    public void onBackPressed() {
        move();
    }

    public void move() {

        Intent intent = new Intent(MyReservationActivity.this, MainActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        startActivity(intent);
    }
}
