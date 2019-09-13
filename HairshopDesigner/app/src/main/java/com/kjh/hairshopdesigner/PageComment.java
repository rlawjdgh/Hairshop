package com.kjh.hairshopdesigner;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import util.IpInfo;

public class PageComment extends Fragment {

    SharedPreferences pref;

    ItemCommentAdapter itemCommentAdapter;
    TextView tv_noComment;
    ListView listView;
    int store_idx;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LinearLayout layout =
                (LinearLayout)inflater.inflate(R.layout.viewpager_comment, container, false);
        listView = layout.findViewById(R.id.listView_comment);
        tv_noComment = layout.findViewById(R.id.textView_noComment);

        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        store_idx = pref.getInt("login_idx", 0);

        new getStoreCommentAsync().execute();

        return layout;
    }

    public class getStoreCommentAsync extends AsyncTask<Void, Void, ArrayList<ReviewVO>> {

        ArrayList<ReviewVO> list;
        ReviewVO vo;

        String parameter = "";
        String serverip = IpInfo.SERVERIP + "getStoreComment.do";

        @Override
        protected ArrayList<ReviewVO> doInBackground(Void... voids) {

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
                tv_noComment.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);

            } else {

                tv_noComment.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                itemCommentAdapter = new ItemCommentAdapter(reviewVOS, getContext());
                listView.setAdapter(itemCommentAdapter);
            }
        }
    }
}
