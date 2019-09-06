package com.kjh.hairshopdesigner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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
    public View getView(int i, View view, ViewGroup viewGroup) {

        MyHolder holder;

        if(view == null) {

            view = View.inflate(context, R.layout.item_store_reservation, null);

            holder = new MyHolder();
            holder.user_name = view.findViewById(R.id.textView_userName);
            holder.cal_day = view.findViewById(R.id.textView_cal_day);
            holder.surgery_name = view.findViewById(R.id.textView_SurgeryName);

            view.setTag(holder);
        } else {
            holder = (MyHolder)view.getTag();
        }

        holder.user_name.setText(list.get(i).getUser_name() + " (" + list.get(i).getStaff_name() + ")");
        holder.cal_day.setText(list.get(i).getCal_day() + " / " + list.get(i).getGetTime());
        holder.surgery_name.setText(list.get(i).getSurgery_name());

        return view;
    }

    class MyHolder {
        TextView user_name;
        TextView cal_day;
        TextView surgery_name;
    }
}
