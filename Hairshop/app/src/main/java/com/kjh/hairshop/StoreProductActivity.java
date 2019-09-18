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
import util.Tag;

public class StoreProductActivity extends AppCompatActivity {

    TextView tv_noProduct;
    ListView listView;
    Button btn_back;

    ItemStoreProductAdapter itemStoreProductAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_product);

        btn_back = findViewById(R.id.button_storeProductBack);
        listView = findViewById(R.id.listView_storeProduct);
        tv_noProduct = findViewById(R.id.textView_noProduct);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StoreProductActivity.this, StoreInfoActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        new getStoreProductAsync().execute(intent.getIntExtra("store_idx", 0));
    }

    public class getStoreProductAsync extends AsyncTask<Integer, Void, ArrayList<SurgeryVO>> {

        ArrayList<SurgeryVO> list;
        SurgeryVO vo;

        String parameter = "";
        String serverip = IpInfo.SERVERIP + "getStoreProduct.do";

        @Override
        protected ArrayList<SurgeryVO> doInBackground(Integer... integers) {

            parameter = "store_idx=" + integers[0];

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

                        vo = new SurgeryVO();
                        vo.setSurgery_idx(jsonObject.getInt("surgery_idx"));
                        vo.setName(jsonObject.getString("name"));
                        vo.setPrice(jsonObject.getInt("price"));
                        vo.setPhoto(jsonObject.getString("photo"));

                        list.add(vo);
                    }
                }

            } catch (Exception e) {
                Log.i( "MY", e.toString());
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<SurgeryVO> surgeryVOS) {

            if(surgeryVOS.size() == 0) {

                tv_noProduct.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {

                tv_noProduct.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                itemStoreProductAdapter = new ItemStoreProductAdapter(surgeryVOS, StoreProductActivity.this);
                listView.setAdapter(itemStoreProductAdapter);
            }
        }
    }
}
