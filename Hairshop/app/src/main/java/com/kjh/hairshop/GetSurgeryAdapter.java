package com.kjh.hairshop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GetSurgeryAdapter extends BaseAdapter {

    StoreReservationActivity storeReservationActivity;
    ArrayList<SurgeryVO> list;

    int staff_idx;
    String cal_day;
    String getTime;

    public GetSurgeryAdapter(StoreReservationActivity storeReservationActivity, ArrayList<SurgeryVO> list, int staff_idx, String cal_day, String getTime) {
        this.storeReservationActivity = storeReservationActivity;
        this.list = list;
        this.staff_idx = staff_idx;
        this.cal_day = cal_day;
        this.getTime = getTime;
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

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(storeReservationActivity);
                builder.setTitle("예약하시겠습니까?");

                builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(storeReservationActivity, KakaoPayActivity.class);
                    intent.putExtra("staff_idx", staff_idx);
                    intent.putExtra("regdate", cal_day);
                    intent.putExtra("time", getTime);
                    intent.putExtra("surgery_name", list.get(i).getName());
                    intent.putExtra("price", list.get(i).getPrice());

                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    storeReservationActivity.startActivity(intent);

                    }
                })
                .setPositiveButton("아니요", null);
                builder.show();
            }
        });

        return view;
    }

    class MyHolder {
        TextView name;
        TextView price;
    }
}
