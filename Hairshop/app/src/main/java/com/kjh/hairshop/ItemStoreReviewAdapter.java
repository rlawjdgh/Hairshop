package com.kjh.hairshop;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RatingBar;
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

public class ItemStoreReviewAdapter extends BaseAdapter {

    ArrayList<ReviewVO> list;
    ReviewVO vo;
    StoreReviewActivity storeReviewActivity;

    MyHolder holder;

    public ItemStoreReviewAdapter(ArrayList<ReviewVO> list, StoreReviewActivity storeReviewActivity) {
        this.list = list;
        this.storeReviewActivity = storeReviewActivity;
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
    public View getView(int i, View view, ViewGroup viewGroup) {

        vo = list.get(i);

        if(view == null) {

            view = View.inflate(storeReviewActivity, R.layout.item_store_review, null);

            holder = new MyHolder();
            holder.ratingBar = view.findViewById(R.id.item_reviewRating);
            holder.context = view.findViewById(R.id.item_reviewContext);
            holder.user_name = view.findViewById(R.id.item_reviewUserName);
            holder.frameLayout = view.findViewById(R.id.item_frameLayout);
            holder.staff_name = view.findViewById(R.id.item_staff_name);
            holder.reply = view.findViewById(R.id.item_reply);

            view.setTag(holder);

        } else {
            holder = (MyHolder)view.getTag();
        }

        holder.ratingBar.setRating(list.get(i).getRating());
        holder.context.setText(list.get(i).getContext());
        holder.user_name.setText("by " + list.get(i).getUser_name() + " - " + list.get(i).getStaff_name());

        if(list.get(i).getComplete() == 0 ) {
            holder.frameLayout.setVisibility(View.GONE);
        } else {
            new getReplyAsync(holder.staff_name, holder.reply, vo).execute();
        }

        return view;
    }

    public class getReplyAsync extends AsyncTask<Integer, Void, ReplyVO> {

        String parameter = "";
        String serverip = IpInfo.SERVERIP + "getReply.do";

        TextView staffName;
        TextView replyContext;
        ReviewVO vo;
        ReplyVO reVO;

        public getReplyAsync(TextView staffName, TextView replyContext, ReviewVO vo) {
            this.staffName = staffName;
            this.replyContext = replyContext;
            this.vo = vo;
        }

        @Override
        protected ReplyVO doInBackground(Integer... integers) {

            parameter = "review_idx=" + vo.getReview_idx();

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

                    reVO = new ReplyVO();
                    reVO.setStaff_name(jsonObject.getString("staff_name"));
                    reVO.setContext(jsonObject.getString("context"));

                }

            } catch (Exception e) {
                Log.i( "MY", e.toString());
            }
            return reVO;
        }

        @Override
        protected void onPostExecute(ReplyVO replyVO) {

            staffName.setText(replyVO.getStaff_name());
            replyContext.setText(replyVO.getContext());
        }
    }

    class MyHolder {

        RatingBar ratingBar;
        TextView context;
        TextView user_name;
        FrameLayout frameLayout;
        TextView staff_name;
        TextView reply;
    }
}
