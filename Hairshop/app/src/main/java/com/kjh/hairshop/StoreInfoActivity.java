package com.kjh.hairshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
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

import util.IpInfo;
import util.Tag;

public class StoreInfoActivity extends AppCompatActivity {

    TextView tv_storeInfo_name, tv_storeInfo_staff;
    Button btn_storeLike, btn_storeInfo, btn_back, btn_storeReview, btn_store_product;
    ListView gridView;
    ViewPager viewPager;
    Intent intent;

    Bitmap img1, img2;
    GetStoreStaffAdapter getStoreStaffAdapter;
    CheckStoreGoodParser checkStoreGoodParser;
    StaffVO staffVO;
    StoreVO storeVO;
    int login_idx;
    int store_idx;
    int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_info);

        btn_storeLike = findViewById(R.id.button_storeInfo_like);
        btn_storeInfo = findViewById(R.id.button_storeInfo_info);
        btn_storeReview = findViewById(R.id.button_storeinfo_review);
        btn_store_product = findViewById(R.id.button_storeinfo_product);
        btn_back = findViewById(R.id.button_storeinfo_back);
        tv_storeInfo_name = findViewById(R.id.textView_storeInfo_name);
        tv_storeInfo_staff = findViewById(R.id.textView_storeInfo_staff);
        viewPager = findViewById(R.id.view_pager);
        gridView = findViewById(R.id.gridView_storeInfo);

        checkStoreGoodParser = new CheckStoreGoodParser();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( StoreInfoActivity.this );
        login_idx = pref.getInt("login_idx", 0);

        intent = getIntent();
        store_idx = intent.getIntExtra("store_idx", 0);

        btn_storeInfo.setOnClickListener(click);
        btn_storeLike.setOnClickListener(click);
        btn_storeReview.setOnClickListener(click);
        btn_store_product.setOnClickListener(click);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(StoreInfoActivity.this, MainActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(intent);
            }
        });

        new getStoreInfoAsync().execute();
        new getStoreStaffAsync().execute();
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.button_storeInfo_like:
                    number = 1;
                    new StoreGoodAsync().execute(number);
                    break;

                case R.id.button_storeInfo_info:

                    intent = new Intent(StoreInfoActivity.this, StoreMoreInfoActivity.class);
                    intent.putExtra("address1", storeVO.getAddress1());
                    intent.putExtra("address2", storeVO.getAddress2());
                    intent.putExtra("tel", storeVO.getTel());
                    intent.putExtra("openClose", storeVO.getOpenClose());
                    intent.putExtra("info", storeVO.getInfo());

                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    startActivity(intent);
                    break;

                case R.id.button_storeinfo_review:

                    intent = new Intent(StoreInfoActivity.this, StoreReviewActivity.class);
                    intent.putExtra("store_idx", store_idx);

                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    startActivity(intent);
                    break;

                case R.id.button_storeinfo_product:

                    intent = new Intent(StoreInfoActivity.this, StoreProductActivity.class);
                    intent.putExtra("store_idx", store_idx);

                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    startActivity(intent);
                    break;
            }
        }
    };

    public class getStoreInfoAsync extends AsyncTask<Void, Void, StoreVO> {

        String parameter = "";
        String serverip = IpInfo.SERVERIP + "getStoreInfo.do";

        @Override
        protected StoreVO doInBackground(Void... voids) {

            parameter = "nickName_idx=" + store_idx;

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

                    storeVO = new StoreVO();

                    storeVO.setNickName_idx(jsonObject.getInt("nickName_idx"));
                    storeVO.setName(jsonObject.getString("name"));
                    storeVO.setAddress1(jsonObject.getString("address1"));
                    storeVO.setAddress2(jsonObject.getString("address2"));
                    storeVO.setTel(jsonObject.getString("tel"));
                    storeVO.setOpenClose(jsonObject.getString("openClose"));
                    storeVO.setPhoto1(jsonObject.getString("photo1"));
                    storeVO.setPhoto2(jsonObject.getString("photo2"));
                    storeVO.setInfo(jsonObject.getString("info"));
                }
            } catch (Exception e) {
                Log.i( "MY", e.toString());
            }

            return storeVO;
        }

        @Override
        protected void onPostExecute(StoreVO storeVOS) {
            tv_storeInfo_name.setText(storeVOS.getName());

            number = 0;
            new StoreGoodAsync().execute(number);
            new getItemStaffImgAsync().execute(storeVOS.getPhoto1());

        }
    }

    public class getItemStaffImgAsync extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {

            URL url = null;
            String pho = strings[0];
            Bitmap bitmap = null;

            try {
                url = new URL(IpInfo.SERVERIP + "store_photo/" + pho);
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
            img1 = bitmap;

            new getItemStaffImgAsync2().execute(storeVO.getPhoto2());
        }
    }

    public class getItemStaffImgAsync2 extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {

            URL url = null;
            String pho = strings[0];
            Bitmap bitmap = null;

            try {
                url = new URL(IpInfo.SERVERIP + "store_photo/" + pho);
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
            img2 = bitmap;

            viewPager.setAdapter(new StoreImgPagerAdapter(getSupportFragmentManager(), img1, img2));
            viewPager.setCurrentItem(0);

        }
    }

    public class getStoreStaffAsync extends AsyncTask<Void, Void, ArrayList<StaffVO>> {

        ArrayList<StaffVO> list;

        String parameter = "";
        String serverip = IpInfo.SERVERIP + "getItemStaff.do";

        @Override
        protected ArrayList<StaffVO> doInBackground(Void... voids) {

            parameter = "nickName_idx=" + store_idx;

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

                        staffVO = new StaffVO();

                        staffVO.setStaff_idx(jsonObject.getInt("staff_idx"));
                        staffVO.setNickName_idx(jsonObject.getInt("nickName_idx"));
                        staffVO.setName(jsonObject.getString("name"));
                        staffVO.setInfo(jsonObject.getString("info"));
                        staffVO.setGrade(jsonObject.getString("grade"));
                        staffVO.setPhoto(jsonObject.getString("photo"));

                        list.add(staffVO);
                    }
                }
            } catch (Exception e) {
                Log.i( "MY", e.toString());
            }

            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<StaffVO> staffVOS) {

            getStoreStaffAdapter = new GetStoreStaffAdapter(StoreInfoActivity.this, staffVOS, storeVO.getName(), storeVO.getNickName_idx());
            gridView.setAdapter(getStoreStaffAdapter);
            //Utility.setGridViewHeightBasedOnChildren(gridView, 2);
            Utility.setListViewHeightBasedOnChildren(gridView);
        }
    }

    public class StoreGoodAsync extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... integers) {
            return checkStoreGoodParser.checkStoreGood(integers[0], login_idx, store_idx);
        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equals("insert")) {

                if(number == 1) {
                    btn_storeLike.setBackgroundDrawable(ContextCompat.getDrawable(StoreInfoActivity.this, R.drawable.btn_click_like));
                } else {
                    btn_storeLike.setBackgroundDrawable(ContextCompat.getDrawable(StoreInfoActivity.this, R.drawable.btn_like));
                }
            } else {
                if(number == 1) {
                    btn_storeLike.setBackgroundDrawable(ContextCompat.getDrawable(StoreInfoActivity.this, R.drawable.btn_like));
                } else {
                    btn_storeLike.setBackgroundDrawable(ContextCompat.getDrawable(StoreInfoActivity.this, R.drawable.btn_click_like));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        intent = new Intent(StoreInfoActivity.this, MainActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        startActivity(intent);
    }
}
