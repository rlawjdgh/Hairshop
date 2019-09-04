package com.kjh.hairshopdesigner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Button;

public class DesignerReservationActivity extends AppCompatActivity {

    ViewPager viewPager;
    Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_reservation);

        btn_back = findViewById(R.id.button_reservationBack);
        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(0);
    }
}
