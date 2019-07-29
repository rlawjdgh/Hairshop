package com.kjh.hairshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.List;

public class StoreMoreInfoActivity extends AppCompatActivity {

    Button btn_back;
    TextView tv_oC, tv_tel, tv_loc, tv_info;
    MapView mapView2;

    Intent intent;
    List<Address> address;
    Geocoder coder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_more_info);

        btn_back = findViewById(R.id.button_back);
        tv_oC = findViewById(R.id.store_more_openClose);
        tv_tel = findViewById(R.id.store_more_tel);
        tv_loc = findViewById(R.id.store_more_location);
        tv_info = findViewById(R.id.store_more_info);
        mapView2 = findViewById(R.id.map_view2);

        coder = new Geocoder( StoreMoreInfoActivity.this );

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(StoreMoreInfoActivity.this, StoreInfoActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(intent);
            }
        });

        intent = getIntent();
        String openClose = intent.getStringExtra("openClose");
        String tel = intent.getStringExtra("tel");
        String address1 = intent.getStringExtra("address1");
        String address2 = intent.getStringExtra("address2");
        String info = intent.getStringExtra("info");

        tv_oC.setText(openClose);
        tv_tel.setText(tel);
        tv_loc.setText(address1 + " " + address2);
        tv_info.setText(info);

        try {
            address = coder.getFromLocationName(address1, 5);
            Address store_address = address.get(0);

            MapPoint storePoint = MapPoint.mapPointWithGeoCoord(store_address.getLatitude(), store_address.getLongitude());

            mapView2.setMapCenterPointAndZoomLevel(storePoint, 2, true);
            MapPOIItem mapPOIItem = new MapPOIItem();
            mapPOIItem.setMapPoint(storePoint);
            mapPOIItem.setMarkerType(MapPOIItem.MarkerType.BluePin);
            mapView2.addPOIItem(mapPOIItem);

        } catch (Exception e) {
            Log.e("MY", e.getMessage());
        }

    }
}
