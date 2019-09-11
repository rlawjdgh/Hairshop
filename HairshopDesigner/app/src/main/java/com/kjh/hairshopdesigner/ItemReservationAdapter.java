package com.kjh.hairshopdesigner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

public class ItemReservationAdapter extends BaseAdapter {

    ArrayList<ReservationVO> list;
    Context context;

    public ItemReservationAdapter(ArrayList<ReservationVO> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        MyHolder holder;

        if(view == null) {

            view = View.inflate(context, R.layout.item_store_reservation, null);

            holder = new MyHolder();
            holder.user_name = view.findViewById(R.id.textView_userName);
            holder.cal_day = view.findViewById(R.id.textView_cal_day);
            holder.surgery_name = view.findViewById(R.id.textView_SurgeryName);
            holder.regdate = view.findViewById(R.id.textView_regdate);
            holder.complete = view.findViewById(R.id.textView_complete);

            view.setTag(holder);
        } else {
            holder = (MyHolder)view.getTag();
        }

        holder.user_name.setText(list.get(i).getUser_nickName() + " (" + list.get(i).getStaff_name() + " " + list.get(i).getStaff_grade() + ")");
        holder.cal_day.setText(list.get(i).getCal_day() + " / " + list.get(i).getGetTime());
        holder.surgery_name.setText(list.get(i).getSurgery_name());
        holder.regdate.setText("예약시간 : " + list.get(i).getRegdate());

        if(list.get(i).getComplete() == 0 ) {
            holder.complete.setTextColor(Color.RED);
            holder.complete.setText("미완료");
        } else {

            holder.complete.setTextColor(Color.BLUE);
            holder.complete.setText("완료");
        }

        if(list.get(i).getComplete() == 0) {

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("완료하시겠습니까??");

                    builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            new reservationCompleteAsync().execute(list.get(i).getReservation_idx());
                        }
                    })
                            .setPositiveButton("아니요", null);
                    builder.show();
                }
            });
        }

        return view;
    }

    public class reservationCompleteAsync extends AsyncTask<Integer, Void, String> {

        String parameter;
        String serverip = IpInfo.SERVERIP + "reservationComplete.do";

        String result;
        int reservation_idx;

        @Override
        protected String doInBackground(Integer... integers) {

            reservation_idx = integers[0];
            parameter = "reservation_idx=" + reservation_idx;

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

                    result = jsonObject.getString("result");
                }

            } catch (Exception e) {
                Log.i( "MY", e.toString() );
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            if(s.equals("success")) {
                notifyDataSetChanged();
            }
        }
    }

    class MyHolder {
        TextView user_name;
        TextView cal_day;
        TextView surgery_name;
        TextView regdate;
        TextView complete;
    }
}
