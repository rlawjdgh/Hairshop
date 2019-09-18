package com.kjh.hairshop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import util.IpInfo;

public class ItemMyReservationAdapter extends BaseAdapter {

    ArrayList<ReservationVO> list;
    MyReservationActivity myReservationActivity;

    Dialog review;
    EditText et_writeContext;
    RatingBar ratingBar;
    Button btn_save;

    int ratingNum;
    int login_idx;

    int reservation_idx, store_idx;
    String staff_name;

    public ItemMyReservationAdapter(ArrayList<ReservationVO> list, MyReservationActivity myReservationActivity, int login_idx) {
        this.list = list;
        this.myReservationActivity = myReservationActivity;
        this.login_idx = login_idx;
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
    public View getView(final int i, View view, ViewGroup parent) {

        MyHolder holder;

        if(view == null) {

            holder = new MyHolder();
            view = View.inflate(myReservationActivity, R.layout.item_my_reservation, null);

            holder.staff_name = view.findViewById(R.id.textView_StaffName);
            holder.cal_day = view.findViewById(R.id.textView_cal_day);
            holder.surgery_name = view.findViewById(R.id.textView_SurgeryName);
            holder.complete = view.findViewById(R.id.textView_complete);

            view.setTag(holder);
        } else {
            holder = (MyHolder)view.getTag();
        }

        holder.staff_name.setText("헤어숍 : " + list.get(i).getStore_name() + " (" + list.get(i).getStaff_name() + " " + list.get(i).getStaff_grade() + ")");
        holder.cal_day.setText("예약날짜 : " + list.get(i).getCal_day() + " " + list.get(i).getGetTime());
        holder.surgery_name.setText(list.get(i).getSurgery_name());

        if(list.get(i).getComplete() == 0 ) {
            holder.complete.setTextColor(Color.RED);
            holder.complete.setText("미완료");

        } else {

            if(list.get(i).getComplete() == 1) {

                holder.complete.setTextColor(Color.BLUE);
                holder.complete.setText("리뷰를 작성해주세요!");

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        reservation_idx = list.get(i).getReservation_idx();
                        store_idx = list.get(i).getStore_idx();
                        staff_name = list.get(i).getStaff_name() + " " + list.get(i).getStaff_grade();

                        AlertDialog.Builder builder = new AlertDialog.Builder(myReservationActivity);
                builder.setTitle("리뷰를 남기시겠습니까?");

                builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        review = new Dialog(myReservationActivity);
                        review.setContentView(R.layout.item_write_review);
                        review.show();

                        et_writeContext = review.findViewById(R.id.editText_writeContext);
                        ratingBar = review.findViewById(R.id.ratingBar);
                        btn_save = review.findViewById(R.id.button_review_save);

                        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                                ratingNum = (int)ratingBar.getRating();
                            }
                        });

                        btn_save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(et_writeContext.equals("")) {
                                    Toast.makeText(myReservationActivity, "후기를 입력해주세요", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(ratingBar.getRating() == 0) {
                                    Toast.makeText(myReservationActivity, "별점을 선택해주세요", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                new insertReviewAsync().execute(et_writeContext.getText().toString());
                            }
                        });
                    }
                })
                        .setPositiveButton("아니요", null);
                builder.show();
            }
        });

            } else {

                holder.complete.setTextColor(Color.BLUE);
                holder.complete.setText("완료");

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(myReservationActivity, "리뷰를 작성하셨습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        return view;
    }

    public class insertReviewAsync extends AsyncTask<String, Void, String> {

        String parameter = "";
        String serverip = IpInfo.SERVERIP + "insertReview.do";

        String context, result;

        @Override
        protected String doInBackground(String... strings) {

            context = strings[0];

            parameter = "login_idx=" + login_idx + "&reservation_idx=" + reservation_idx + "&store_idx=" + store_idx + "&staff_name="
                    + staff_name + "&context=" + context + "&ratingNum=" + ratingNum;

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
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    result = jsonObject.getString("result");
                }

            } catch (Exception e) {
                Log.i("MY", e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            if(s.equals("success")) {
                review.dismiss();
                handler.sendEmptyMessageDelayed(0, 500);
                myReservationActivity.handler.sendEmptyMessage(0);
            }
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {

            notifyDataSetChanged();
            myReservationActivity.handler.removeMessages(0);
            Toast.makeText(myReservationActivity, "리뷰를 저장했습니다.", Toast.LENGTH_SHORT).show();
        }
    };

    class MyHolder {

        TextView staff_name;
        TextView cal_day;
        TextView surgery_name;
        TextView complete;
    }
}
