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
import util.Tag;

public class PageReservation extends Fragment {

    SharedPreferences pref;
    int store_idx;

    ItemReservationAdapter itemReservationAdapter;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LinearLayout layout =
                (LinearLayout)inflater.inflate(R.layout.viewpager_reservation, container, false);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        store_idx = pref.getInt("login_idx", 0);

        listView = container.findViewById(R.id.listView_reservation);

        new getReservation().execute();

        return layout;
    }

    public class getReservation extends AsyncTask<Void, Void, ArrayList<ReservationVO>> {

        ArrayList<ReservationVO> list;
        ReservationVO vo;

        String parameter = "";
        String serverip = IpInfo.SERVERIP + "getReservation.do";

        @Override
        protected ArrayList<ReservationVO> doInBackground(Void... voids) {

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

                        vo = new ReservationVO();
                        vo.setReservation_idx(jsonObject.getInt("reservation_idx"));
                        vo.setUser_name(jsonObject.getString("user_name"));
                        vo.setStaff_name(jsonObject.getString("staff_name"));
                        vo.setCal_day(jsonObject.getString("cal_day"));
                        vo.setGetTime(jsonObject.getString("getTime"));
                        vo.setSurgery_name(jsonObject.getString("surgery_name"));

                        list.add(vo);
                    }

                    Log.d(Tag.t, "" + list);
                }

            } catch (Exception e) {
                Log.i( "MY", e.toString());
            }

            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<ReservationVO> reservationVOS) {

            if(reservationVOS.size() == 0 ) {

            } else {

                itemReservationAdapter = new ItemReservationAdapter(reservationVOS, getContext());
                listView.setAdapter(itemReservationAdapter);
            }
        }
    }
}
