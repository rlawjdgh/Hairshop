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

public class StoreSearchActivity extends AppCompatActivity {

    ListView listView;
    Button btn_back;
    TextView tv_noSearh;
    Intent intent;

    ItemSearchStoreAdapter itemSearchStoreAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_search);

        listView = findViewById(R.id.listView_search);
        btn_back = findViewById(R.id.button_searchBack);
        tv_noSearh = findViewById(R.id.textView_noSearchStore);

        intent = getIntent();
        String search = intent.getStringExtra("search");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(StoreSearchActivity.this, MainActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(intent);
            }
        });

        new getSearchStoreAsync().execute(search);
    }

    public class getSearchStoreAsync extends AsyncTask<String, Void, ArrayList<StoreVO>> {

        ArrayList<StoreVO> list;
        StoreVO vo;

        String parameter = "";
        String serverip = IpInfo.SERVERIP + "getSearchStore.do";

        @Override
        protected ArrayList<StoreVO> doInBackground(String... strings) {

            parameter = "search=" + strings[0];

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

                        vo = new StoreVO();
                        vo.setNickName_idx(jsonObject.getInt("nickName_idx"));
                        vo.setName(jsonObject.getString("name"));
                        vo.setPhoto1(jsonObject.getString("photo1"));
                        vo.setInfo(jsonObject.getString("info"));
                        vo.setGood(jsonObject.getInt("good"));

                        list.add(vo);
                    }
                }

            } catch (Exception e) {
                Log.i( "MY", e.toString());
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<StoreVO> storeVOS) {

            if(storeVOS.size() == 0) {

                tv_noSearh.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {

                tv_noSearh.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                itemSearchStoreAdapter = new ItemSearchStoreAdapter(storeVOS, StoreSearchActivity.this);
                listView.setAdapter(itemSearchStoreAdapter);
            }
        }
    }
}
