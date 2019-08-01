package com.kjh.hairshop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

import util.IpInfo;

public class StoreReservationActivity extends AppCompatActivity {


    ImageView img_staffPhoto, img_regdate, img_time, img_regdateCheck, img_timeCheck;
    TextView tv_staffName, tv_staffInfo, tv_resRegdate, tv_resTime, tv_store_name, tv_cut, tv_perm, tv_chlorination, tv_clinic;
    Button btn_back;
    Button[] btn_time;
    RelativeLayout rel_resTime, rel_surgery;
    ListView listView;

    Intent intent;

    DatePickerDialog.OnDateSetListener dateListener;
    int year;
    int month;
    int day;
    boolean check_regdate = false;
    boolean check_time = false;
    String getTime;
    int staff_idx;
    int store_idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_reservation);

        img_staffPhoto = findViewById(R.id.imageView_reservation_photo);
        img_regdate = findViewById(R.id.img_regdate);
        img_regdateCheck = findViewById(R.id.img_regdate_check);
        img_timeCheck = findViewById(R.id.img_time_check);
        img_time = findViewById(R.id.img_time);
        tv_staffName = findViewById(R.id.textView_reservation_name);
        tv_staffInfo = findViewById(R.id.textView_reservation_info);
        tv_resRegdate = findViewById(R.id.textView_reservation_regdate);
        tv_resTime = findViewById(R.id.textView_reservation_time);
        tv_store_name = findViewById(R.id.textView_reservation_storeName);
        tv_cut = findViewById(R.id.textView_cut);
        tv_perm = findViewById(R.id.textView_perm);
        tv_chlorination = findViewById(R.id.textView_chlorination);
        tv_clinic = findViewById(R.id.textView_clinic);
        btn_back = findViewById(R.id.button_reservation_back);
        rel_resTime = findViewById(R.id.relative_time);
        rel_surgery = findViewById(R.id.relative_surgery);
        listView = findViewById(R.id.listView_reservation);

        img_regdate.setOnClickListener(click);
        img_time.setOnClickListener(click);

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
                 case R.id.img_regdate:

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
                             tv_resRegdate.setText((month+1) + "월 " + dayOfMonth + "일");
                             img_regdateCheck.setVisibility(View.VISIBLE);
                             check_regdate = true;
                         }
                     };

                     if(check_regdate && check_regdate) {

                         // 시술 async
                     }
                     break;

                 case R.id.img_time:
                     rel_resTime.setVisibility(View.VISIBLE);
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

            check_time = true;
            img_timeCheck.setVisibility(View.VISIBLE);
            rel_resTime.setVisibility(View.GONE);
            tv_resTime.setText(getTime);

            if(check_regdate && check_regdate) {

                // 시술 async
            }
        }
    };

    View.OnClickListener sur_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int category;

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

            // 시술 async
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
}
