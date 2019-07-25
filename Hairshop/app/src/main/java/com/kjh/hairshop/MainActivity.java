package com.kjh.hairshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.IpInfo;
import util.Tag;

public class MainActivity extends AppCompatActivity {

    BackPressed backPressed;
    LocationManager locationManager;
    ProgressDialog progressDialog;
    GetStoreAllAdapter getStoreAllAdapter;
    ListView listView;

    MapView mapView;
    MapPoint myPoint, storePoint;
    MapPOIItem mapPOIItem;

    List<Address> address;
    Geocoder coder;
    double my_lat, my_lng;

    StoreVO vo;
    LocationVO loc_vo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backPressed = new BackPressed(this);
        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            setPermission();
            return;
        }
        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            setPermission();
            return;
        }

        coder = new Geocoder( MainActivity.this );
        listView = findViewById(R.id.listView);

        progressDialog = new ProgressDialog( MainActivity.this );
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable( false );
        progressDialog.show();

        mapView = findViewById(R.id.map_view);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 500, locationListener);
    }

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            locationManager.removeUpdates(locationListener);
            my_lat = location.getLatitude();
            my_lng = location.getLongitude();

            myPoint = MapPoint.mapPointWithGeoCoord(my_lat, my_lng);

            mapView.setMapCenterPointAndZoomLevel(myPoint, 4, true);
            mapPOIItem = new MapPOIItem();
            mapPOIItem.setItemName("현재위치");
            mapPOIItem.setMapPoint(myPoint);
            mapPOIItem.setMarkerType(MapPOIItem.MarkerType.BluePin);
            mapView.addPOIItem(mapPOIItem);

            new getAllStore().execute();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public class getAllStore extends AsyncTask<Void, Void, ArrayList<StoreVO>> {

        ArrayList<StoreVO> list;
        ArrayList<LocationVO> loc_list;

        @Override
        protected ArrayList<StoreVO> doInBackground(Void... voids) {

            String parameter = "";
            String serverip = IpInfo.SERVERIP + "getStoreAll.do";

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
                    JSONObject jsonObject = null;

                    list = new ArrayList();

                    for( int i = 0; i < jsonArray.length(); i++ ) {
                        jsonObject = jsonArray.getJSONObject(i);

                        vo = new StoreVO();

                        vo.setNickName_idx(jsonObject.getInt("nickName_idx"));
                        vo.setName(jsonObject.getString("name"));
                        vo.setAddress1(jsonObject.getString("address1"));
                        vo.setPhoto1(jsonObject.getString("photo1"));
                        vo.setInfo(jsonObject.getString("info"));

                        list.add(vo);
                    }
                }
            } catch (Exception e) {
                Log.i( "MY", e.toString());
            }

            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<StoreVO> storeVOS) {

            progressDialog.dismiss();

            loc_list = new ArrayList<>();

            for(int i = 0; i < list.size(); i++) {

                try {
                    address = coder.getFromLocationName( list.get(i).getAddress1(), 5 );
                    Address store_address = address.get(0);

                    storePoint = MapPoint.mapPointWithGeoCoord(store_address.getLatitude(), store_address.getLongitude());

                    mapPOIItem = new MapPOIItem();
                    mapPOIItem.setItemName(list.get(i).getName() + "(길찾기)");
                    mapPOIItem.setMapPoint(storePoint);
                    mapPOIItem.setTag(list.get(i).getNickName_idx());
                    mapPOIItem.setMarkerType(MapPOIItem.MarkerType.RedPin);
                    mapView.addPOIItem(mapPOIItem);
                    mapView.setPOIItemEventListener(click);

                    Location locationA = new Location("pointA");
                    locationA.setLatitude(my_lat);
                    locationA.setLongitude(my_lng);

                    Location locationB = new Location("point B");
                    locationB.setLatitude(store_address.getLatitude());
                    locationB.setLongitude(store_address.getLongitude());

                    double distance = locationA.distanceTo(locationB);

                    loc_vo = new LocationVO(list.get(i).getNickName_idx(), list.get(i).getName(), distance, list.get(i).getPhoto1(), list.get(i).getInfo());
                    loc_list.add(loc_vo);
                    Collections.sort(loc_list);

                } catch (Exception e) {
                    Log.i( "MY", e.toString() );
                }
            }

            getStoreAllAdapter = new GetStoreAllAdapter(loc_list, MainActivity.this, my_lat, my_lng);
            listView.setAdapter(getStoreAllAdapter);
        }
    }

    MapView.POIItemEventListener click = new MapView.POIItemEventListener() {
        @Override
        public void onPOIItemSelected(MapView mapView, final MapPOIItem mapPOIItem) {

            if(mapPOIItem.getTag() != 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("매장 정보를 보시겠습니까?");

                builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(MainActivity.this, StoreInfoActivity.class);
                        intent.putExtra("store_idx", mapPOIItem.getTag());
                        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                        startActivity(intent);

                    }
                })
                        .setPositiveButton("아니요", null);
                builder.show();
            }
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

            if(mapPOIItem.getTag() != 0) {

                Double latitude = mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude;
                Double longitude = mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude;

                String url = "daummaps://route?sp="+my_lat+","+my_lng+"&ep="+latitude+","+longitude+"&by=PUBLICTRANSIT";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

        }

        @Override
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

        }
    };



    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            finish();
        }
    };

    private void setPermission() {

        TedPermission.with( this )
                .setPermissionListener( permissionListener )
                .setDeniedMessage("이 앱에서 요구하는 권한이 있습니다\n[설정]->[권한]에서 해당 권한을 활성화 해주세요")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION).check();
    }

    @Override
    public void onBackPressed() {
        backPressed.onBackPressed();
    }
}
