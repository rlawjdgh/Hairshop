package com.kjh.hairshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class MyProductActivity extends AppCompatActivity {

    Button btn_back;
    ListView listView;
    TextView no_product;
    ItemBuyProductAdapter itemBuyProductAdapter;

    int login_idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_product);

        btn_back = findViewById(R.id.button_myProductBack);
        listView = findViewById(R.id.listView_myProduct);
        no_product = findViewById(R.id.textView_noMyProduct);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( MyProductActivity.this );
        login_idx = pref.getInt("login_idx", 0);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MyProductActivity.this, MainActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(intent);
            }
        });

        new buyProductAsync().execute(login_idx);
    }

    public class buyProductAsync extends AsyncTask<Integer, Void, ArrayList<SurgeryVO>> {

        ArrayList<SurgeryVO> list;
        SurgeryVO vo;

        String parameter = "";
        String serverip = IpInfo.SERVERIP + "buyProduct.do";

        @Override
        protected ArrayList<SurgeryVO> doInBackground(Integer... integers) {

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

                        vo = new SurgeryVO();
                        vo.setName(jsonObject.getString("name"));
                        vo.setPhoto(jsonObject.getString("photo"));
                        vo.setRegdate(jsonObject.getString("regdate"));

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
                no_product.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {

                no_product.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                itemBuyProductAdapter = new ItemBuyProductAdapter(surgeryVOS, MyProductActivity.this);
                listView.setAdapter(itemBuyProductAdapter);
            }
        }
    }
}
