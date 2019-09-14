package com.kjh.hairshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class StoreReviewActivity extends AppCompatActivity {

    Button btn_back;
    TextView tv_noReview;
    ListView listView;

    ItemStoreReviewAdapter itemStoreReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_review);

        btn_back = findViewById(R.id.button_reviewBack);
        tv_noReview = findViewById(R.id.textView_noReview);
        listView = findViewById(R.id.listView_review);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StoreReviewActivity.this, StoreInfoActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        new getStroeReviewAsync().execute(intent.getIntExtra("store_idx", 0));
    }

    public class getStroeReviewAsync extends AsyncTask<Integer, Void, ArrayList<ReviewVO>> {

        ArrayList<ReviewVO> list;
        ReviewVO vo;

        String parameter = "";
        String serverip = IpInfo.SERVERIP + "getStoreComment.do";

        @Override
        protected ArrayList<ReviewVO> doInBackground(Integer... integers) {

            int store_idx = integers[0];
            parameter = "store_idx=" + store_idx;

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

                        vo = new ReviewVO();
                        vo.setReview_idx(jsonObject.getInt("review_idx"));
                        vo.setUser_name(jsonObject.getString("user_name"));
                        vo.setStaff_name(jsonObject.getString("staff_name"));
                        vo.setContext(jsonObject.getString("context"));
                        vo.setRating(jsonObject.getInt("rating"));
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
        protected void onPostExecute(ArrayList<ReviewVO> reviewVOS) {

            if(reviewVOS.size() == 0) {
                tv_noReview.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {

                tv_noReview.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                itemStoreReviewAdapter = new ItemStoreReviewAdapter(reviewVOS, StoreReviewActivity.this);
                listView.setAdapter(itemStoreReviewAdapter);
            }
        }
    }
}
