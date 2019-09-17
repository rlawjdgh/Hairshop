package com.kjh.hairshop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import util.IpInfo;

public class ItemMyLikeStoreAdapter extends BaseAdapter {

    ArrayList<StoreVO> list;
    StoreVO vo;
    MyStoreLikeActivity myStoreLikeActivity;

    public ItemMyLikeStoreAdapter(ArrayList<StoreVO> list, MyStoreLikeActivity myStoreLikeActivity) {
        this.list = list;
        this.myStoreLikeActivity = myStoreLikeActivity;
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
        vo = list.get(i);

        if(view == null) {

            view = View.inflate(myStoreLikeActivity, R.layout.item_my_like_store, null);

            holder = new MyHolder();
            holder.imageView = view.findViewById(R.id.item_storeLikeImg);
            holder.store_name = view.findViewById(R.id.item_storeLike_name);
            holder.info = view.findViewById(R.id.item_storeLike_info);
            holder.good = view.findViewById(R.id.textView_storeLike_good);

            new getStoreImg(holder.imageView, vo).execute();

            view.setTag(holder);

        } else {
            holder = (MyHolder)view.getTag();
        }

        holder.store_name.setText(list.get(i).getName());
        holder.info.setText(list.get(i).getInfo());
        holder.good.setText("" + list.get(i).getGood());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(myStoreLikeActivity, StoreInfoActivity.class);
                intent.putExtra("store_idx", list.get(i).getNickName_idx());
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                myStoreLikeActivity.startActivity(intent);
            }
        });


        return view;
    }

    public class getStoreImg extends AsyncTask<Void,Void, Bitmap> {

        ImageView store_image;
        StoreVO vo;

        public getStoreImg(ImageView imageView, StoreVO vo) {
            this.store_image = imageView;
            this.vo = vo;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            URL url = null;
            String photo = vo.getPhoto1();
            Bitmap bitmap = null;

            try {
                url = new URL(IpInfo.SERVERIP + "store_photo/" + photo);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);

                is.close();
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            store_image.setImageBitmap(bitmap);
        }
    }

    class MyHolder {

        ImageView imageView;
        TextView store_name;
        TextView info;
        TextView good;
    }
}
