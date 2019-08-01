package com.kjh.hairshop;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GetSurgeryAdapter extends BaseAdapter {

    StoreReservationActivity storeReservationActivity;
    ArrayList<SurgeryVO> list;

    public GetSurgeryAdapter(StoreReservationActivity storeReservationActivity, ArrayList<SurgeryVO> list) {
        this.storeReservationActivity = storeReservationActivity;
        this.list = list;
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
    public View getView(int i, View view, ViewGroup parent) {

        MyHolder holder;

        if(view == null) {

            view = View.inflate(storeReservationActivity, R.layout.item_surgery, null);

            holder = new MyHolder();
            holder.name = view.findViewById(R.id.item_name);
            holder.price = view.findViewById(R.id.item_price);

            view.setTag(holder);

        } else {
            holder = (MyHolder)view.getTag();
        }
        String rePrice = new DecimalFormat("###,###").format(list.get(i).getPrice());
        holder.name.setText(list.get(i).getName());
        holder.price.setText(rePrice + "원");

        return view;
    }

    class MyHolder {
        TextView name;
        TextView price;
    }
}
