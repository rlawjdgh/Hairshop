package com.kjh.hairshop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.Calendar;
import java.util.GregorianCalendar;

import util.IpInfo;

public class StoreReservationActivity extends AppCompatActivity {


    ImageView img_staffPhoto, img_regdateCheck, img_timeCheck;
    TextView tv_staffName, tv_staffInfo, tv_resRegdate, tv_resTime, tv_store_name, tv_cut, tv_perm, tv_chlorination, tv_clinic;
    TextView tv_dhwjs, tv_dhgn, tv_tltnf, tv_noSurgery;
    Button btn_back;
    Button[] btn_time;
    ListView listView;

    Intent intent;
    GetSurgeryAdapter getSurgeryAdapter;
    DatePickerDialog.OnDateSetListener dateListener;
    int year;
    int month;
    int day;
    boolean check_regdate = false;
    boolean check_time = false;
    boolean check_btn_time = false;
    String getTime;
    String cal_day;
    int staff_idx;
    int store_idx;
    int category = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_reservation);

        img_staffPhoto = findViewById(R.id.imageView_reservation_photo);
        img_regdateCheck = findViewById(R.id.img_regdate_check);
        img_timeCheck = findViewById(R.id.img_time_check);
        tv_staffName = findViewById(R.id.textView_reservation_name);
        tv_staffInfo = findViewById(R.id.textView_reservation_info);
        tv_resRegdate = findViewById(R.id.textView_reservation_regdate);
        tv_resTime = findViewById(R.id.textView_reservation_time);
        tv_store_name = findViewById(R.id.textView_reservation_storeName);
        tv_cut = findViewById(R.id.textView_cut);
        tv_perm = findViewById(R.id.textView_perm);
        tv_chlorination = findViewById(R.id.textView_chlorination);
        tv_clinic = findViewById(R.id.textView_clinic);
        tv_dhwjs = findViewById(R.id.textView_dhwjs);
        tv_dhgn = findViewById(R.id.textView_dhgn);
        tv_tltnf = findViewById(R.id.textView_tltnf);
        tv_noSurgery = findViewById(R.id.textView_noSurgery);
        btn_back = findViewById(R.id.button_reservation_back);
        listView = findViewById(R.id.listView_reservation);

        tv_resRegdate.setOnClickListener(click);
        tv_resTime.setOnClickListener(click);
        tv_cut.setOnClickListener(sur_click);
        tv_perm.setOnClickListener(sur_click);
        tv_chlorination.setOnClickListener(sur_click);
        tv_clinic.setOnClickListener(sur_click);

        btn_time = new Button[8];
        for(int i = 0; i < btn_time.length; i++) {
            try {
                int fvb = getResources().getIdentifier("button_reservation_"+i, "id", getPackageName());
                btn_time[i] = findViewById(fvb);
                btn_time[i].setOnClickListener(click_time);
            } catch (Exception e) {
                Log.e("MY", e.getMessage());
            }
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(StoreReservationActivity.this, StoreInfoActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(intent);
            }
        });

        intent = getIntent();
        staff_idx = intent.getIntExtra("staff_idx", 0);
        String staff_name = intent.getStringExtra("staff_name");
        String staff_grade = intent.getStringExtra("staff_grade");
        String staff_info = intent.getStringExtra("staff_info");
        String staff_photo = intent.getStringExtra("staff_photo");
        String store_name = intent.getStringExtra("store_name");
        store_idx = intent.getIntExtra("store_idx", 0);

        tv_store_name.setText(store_name);
        tv_staffName.setText(staff_name + " " + staff_grade);
        tv_staffInfo.setText(staff_info);
        new getItemStaffImg().execute(staff_photo);

        Calendar cal = GregorianCalendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

    }

     View.OnClickListener click = new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             switch (v.getId()) {
                 case R.id.textView_reservation_regdate:

                     Calendar minDate = Calendar.getInstance();
                     minDate.set(year, month, day);

                     DatePickerDialog dialog = new DatePickerDialog(StoreReservationActivity.this,
                             R.style.Theme_AppCompat_Light_Dialog_Alert, dateListener, year, month, day);
                     dialog.getDatePicker().setMinDate(minDate.getTime().getTime());
                     dialog.setCancelable(false);
                     dialog.show();

                     dateListener = new DatePickerDialog.OnDateSetListener() {

                         @Override
                         public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                             cal_day = (month+1) + "월 " + dayOfMonth + "일";

                             tv_resRegdate.setText(cal_day);
                             img_regdateCheck.setVisibility(View.VISIBLE);
                             check_regdate = true;

                             if(check_regdate && check_btn_time) {

                                 tv_tltnf.setVisibility(View.VISIBLE);
                                 tv_cut.setVisibility(View.VISIBLE);
                                 tv_perm.setVisibility(View.VISIBLE);
                                 tv_chlorination.setVisibility(View.VISIBLE);
                                 tv_clinic.setVisibility(View.VISIBLE);

                                 new getItemSurgery().execute();
                             }
                         }
                     };
                     break;

                 case R.id.textView_reservation_time:

                     if(!check_time) {

                         check_time = true;

                         if(check_btn_time) {

                             tv_dhwjs.setVisibility(View.GONE);
                             tv_dhgn.setVisibility(View.GONE);

                             for(int i = 0; i < btn_time.length; i++) {
                                 btn_time[i].setVisibility(View.GONE);
                             }
                         } else {

                             tv_dhwjs.setVisibility(View.VISIBLE);
                             tv_dhgn.setVisibility(View.VISIBLE);

                             for(int i = 0; i < btn_time.length; i++) {
                                 btn_time[i].setVisibility(View.VISIBLE);
                             }
                         }
                     } else {

                         check_time = false;

                         if(check_btn_time) {

                             tv_dhwjs.setVisibility(View.VISIBLE);
                             tv_dhgn.setVisibility(View.VISIBLE);
                             img_timeCheck.setVisibility(View.VISIBLE);

                             for(int i = 0; i < btn_time.length; i++) {
                                 btn_time[i].setVisibility(View.VISIBLE);
                             }
                         } else {

                             tv_dhwjs.setVisibility(View.GONE);
                             tv_dhgn.setVisibility(View.GONE);
                             img_timeCheck.setVisibility(View.GONE);

                             for(int i = 0; i < btn_time.length; i++) {
                                 btn_time[i].setVisibility(View.GONE);
                             }
                         }
                     }
                     break;
             }
         }
     };
    
    View.OnClickListener click_time = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.button_reservation_0:
                    getTime = btn_time[0].getText().toString();
                    break;
                case R.id.button_reservation_1:
                    getTime = btn_time[1].getText().toString();
                    break;
                case R.id.button_reservation_2:
                    getTime = btn_time[2].getText().toString();
                    break;
                case R.id.button_reservation_3:
                    getTime = btn_time[3].getText().toString();
                    break;
                case R.id.button_reservation_4:
                    getTime = btn_time[4].getText().toString();
                    break;
                case R.id.button_reservation_5:
                    getTime = btn_time[5].getText().toString();
                    break;
                case R.id.button_reservation_6:
                    getTime = btn_time[6].getText().toString();
                    break;
                case R.id.button_reservation_7:
                    getTime = btn_time[7].getText().toString();
                    break;
            }

            check_btn_time = true;
            img_timeCheck.setVisibility(View.VISIBLE);
            tv_dhwjs.setVisibility(View.GONE);
            tv_dhgn.setVisibility(View.GONE);

            for(int i = 0; i < btn_time.length; i++) {
                btn_time[i].setVisibility(View.GONE);
            }

            tv_resTime.setText(getTime);

            if(check_regdate && check_btn_time) {

                tv_tltnf.setVisibility(View.VISIBLE);
                tv_cut.setVisibility(View.VISIBLE);
                tv_perm.setVisibility(View.VISIBLE);
                tv_chlorination.setVisibility(View.VISIBLE);
                tv_clinic.setVisibility(View.VISIBLE);

                new getItemSurgery().execute();
            }
        }
    };

    View.OnClickListener sur_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.textView_cut:
                    category = 0;
                    break;

                case R.id.textView_perm:
                    category = 1;
                    break;

                case R.id.textView_chlorination:
                    category = 2;
                    break;

                case R.id.textView_clinic:
                    category = 3;
                    break;
            }

            new getItemSurgery().execute();
        }
    };

    public class getItemStaffImg extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {

            URL url = null;
            String photo = strings[0];
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
            img_staffPhoto.setImageBitmap(bitmap);
        }
    }

    public class getItemSurgery extends AsyncTask<Void, Void, ArrayList<SurgeryVO>> {

        ArrayList<SurgeryVO> list;
        SurgeryVO vo;

        @Override
        protected ArrayList<SurgeryVO> doInBackground(Void... voids) {

            String parameter = "nickName_idx=" + store_idx + "&category=" + category;
            String serverip = IpInfo.SERVERIP + "getItemSurgery.do";

            try {
                String str;
                URL url = new URL(serverip);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                osw.write(parameter);
                osw.flush();


                if (conn.getResponseCode() == conn.HTTP_OK) {

                    InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(isr);

                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }

                    JSONArray jsonArray = new JSONArray(buffer.toString());
                    JSONObject jsonObject = null;

                    list = new ArrayList();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        jsonObject = jsonArray.getJSONObject(i);
                        vo = new SurgeryVO();

                        vo.setSurgery_idx(jsonObject.getInt("surgery_idx"));
                        vo.setCategory(jsonObject.getInt("category"));
                        vo.setName(jsonObject.getString("name"));
                        vo.setPrice(jsonObject.getInt("price"));

                        list.add(vo);
                    }
                }
            } catch (Exception e) {
                Log.i("MY", e.toString());
            }

            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<SurgeryVO> surgeryVOS) {

            if(surgeryVOS.size() == 0) {

                listView.setVisibility(View.GONE);
                tv_noSurgery.setVisibility(View.VISIBLE);
            } else {

                tv_noSurgery.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                getSurgeryAdapter = new GetSurgeryAdapter(StoreReservationActivity.this, surgeryVOS, staff_idx, cal_day, getTime);
                listView.setAdapter(getSurgeryAdapter);
                Utility.setListViewHeightBasedOnChildren(listView);
            }
        }
    }
}
