package com.kjh.hairshop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import util.IpInfo;

public class ItemBuyProductAdapter extends BaseAdapter {

    ArrayList<SurgeryVO> list;
    SurgeryVO vo;
    MyProductActivity myProductActivity;

    public ItemBuyProductAdapter(ArrayList<SurgeryVO> list, MyProductActivity myProductActivity) {
        this.list = list;
        this.myProductActivity = myProductActivity;
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

        final MyHolder holder;
        vo = new SurgeryVO();
        vo = list.get(i);


        if(view == null) {
            view = View.inflate(myProductActivity, R.layout.item_store_product, null);

            holder = new MyHolder();
            holder.imageView = view.findViewById(R.id.image_product);
            holder.name  = view.findViewById(R.id.item_ProductName);
            holder.regdate = view.findViewById(R.id.item_ProductPrice);

            new getItemProductImg(holder.imageView, vo).execute();

            view.setTag(holder);

        } else {
            holder = (MyHolder)view.getTag();
        }

        String str = list.get(i).getRegdate();

        holder.name.setText(list.get(i).getName());
        holder.regdate.setText("구매날짜 : " + str.substring(5, 10));

        return view;
    }

    public class getItemProductImg extends AsyncTask<Void, Void, Bitmap> {

        ImageView imageView;
        SurgeryVO vo;

        public getItemProductImg(ImageView imageView, SurgeryVO vo) {
            this.imageView = imageView;
            this.vo = vo;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            URL url = null;
            String photo = vo.getPhoto();
            Bitmap bitmap = null;

            try {
                url = new URL(IpInfo.SERVERIP + "product_photo/" + photo);
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
        protected void onPostExecute(final Bitmap bitmap) {

            imageView.setImageBitmap(bitmap);
        }
    }

    class MyHolder {

        ImageView imageView;
        TextView name;
        TextView regdate;
    }
}
