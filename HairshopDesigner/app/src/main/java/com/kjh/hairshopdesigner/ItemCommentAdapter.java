package com.kjh.hairshopdesigner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import util.Tag;

public class ItemCommentAdapter extends BaseAdapter {

    ArrayList<ReviewVO> list;
    Context context;

    Dialog reply;
    EditText et_reply;
    Button btn_save;

    public ItemCommentAdapter(ArrayList<ReviewVO> list, Context context) {
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

            view = View.inflate(context, R.layout.item_comment, null);

            holder = new MyHolder();
            holder.comment = view.findViewById(R.id.textView_storeComment);
            holder.ratingBar = view.findViewById(R.id.item_reviewRating);
            holder.user_name = view.findViewById(R.id.textView_comment_UserName);
            holder.reply = view.findViewById(R.id.textView_reply);

            view.setTag(holder);

        } else {
            holder = (MyHolder)view.getTag();
        }

        holder.ratingBar.setRating(list.get(i).getRating());
        holder.comment.setText(list.get(i).getContext());
        holder.user_name.setText("by " + list.get(i).getUser_name() + " - " + list.get(i).getStaff_name());

        if(list.get(i).getComplete() == 0 ) {

            holder.reply.setTextColor(Color.RED);
            holder.reply.setText("댓글 미등록");

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("리플을 남기시겠습니까?");

                    builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            reply = new Dialog(context);
                            reply.setContentView(R.layout.item_reply);

                            WindowManager.LayoutParams params = reply.getWindow().getAttributes();
                            params.width = 900;
                            reply.getWindow().setAttributes(params);

                            reply.show();

                            et_reply = reply.findViewById(R.id.editText_writeReply);
                            btn_save = reply.findViewById(R.id.button_reply_save);

                            btn_save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if(et_reply.getText().toString().equals("")) {
                                        Toast.makeText(context, "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                   new insertReplyAsync().execute(list.get(i).getReview_idx(), et_reply.getText().toString());
                                }
                            });
                        }
                    })
                    .setPositiveButton("아니요", null);
                    builder.show();
                }
            });

        } else {

            holder.reply.setTextColor(Color.BLUE);
            holder.reply.setText("댓글 등록");

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(context, "리플을 작성하셨습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }

    public class insertReplyAsync extends AsyncTask<Object, Void, String> {

        String parameter = "";
        String serverip = IpInfo.SERVERIP + "insertReply.do";

        int review_idx;
        String context, result;

        @Override
        protected String doInBackground(Object... objects) {

            review_idx = (int)objects[0];
            context = (String)objects[1];

            parameter = "review_idx=" + review_idx + "&context=" + context;

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
                reply.dismiss();
                notifyDataSetChanged();
                handler.sendEmptyMessageDelayed(0, 400);
            }
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {

            Toast.makeText(context, "리플을 작성했습니다.", Toast.LENGTH_SHORT).show();
        }
    };

    class MyHolder {
        TextView comment;
        TextView user_name;
        RatingBar ratingBar;
        TextView reply;
    }
}
