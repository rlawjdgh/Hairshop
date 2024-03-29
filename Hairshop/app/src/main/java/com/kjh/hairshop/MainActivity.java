package com.kjh.hairshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

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

    SharedPreferences pref;
    DrawerLayout drawerLayout;
    RelativeLayout drawer;

    TextView btn_myPage, btn_search, tv_myPoint;
    Button btn_logout;
    EditText et_searchStore;
    Animation menu_visible_ani;

    BackPressed backPressed;
    LocationManager locationManager;
    ProgressDialog progressDialog;
    GetStoreAllAdapter getStoreAllAdapter;
    ListView listView;
    LinearLayout ll_myReservation, ll_myLike, ll_myProduct;

    MapView mapView;
    MapPoint myPoint, storePoint;
    MapPOIItem mapPOIItem;

    List<Address> address;
    Geocoder coder;
    double my_lat, my_lng;

    StoreVO vo;
    LocationVO loc_vo;
    Intent intent;

    int login_idx;

    @Override
    protected void onResume() {
        super.onResume();

        new getMyPointAsync().execute();
    }

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

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( MainActivity.this );
        login_idx = pref.getInt("login_idx", 0);

        listView = findViewById(R.id.listView);
        drawer = findViewById(R.id.drawer);
        drawerLayout = findViewById(R.id.drawerLayout);
        btn_myPage = findViewById(R.id.button_myPage);
        btn_logout = findViewById(R.id.button_logout);
        btn_search = findViewById(R.id.textView_search);
        ll_myReservation = findViewById(R.id.linearLayout_myReservation);
        ll_myLike = findViewById(R.id.linearLayout_myLike);
        ll_myProduct = findViewById(R.id.linearLayout_myProduct);
        tv_myPoint = findViewById(R.id.textView_myPoint);
        et_searchStore = findViewById(R.id.editText_searchStore);
        
        btn_logout.setOnClickListener(my_drawer);
        ll_myReservation.setOnClickListener(my_drawer);
        ll_myLike.setOnClickListener(my_drawer);
        ll_myProduct.setOnClickListener(my_drawer);

        btn_myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawer);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if(et_searchStore.getVisibility() == View.VISIBLE) {

                    if(!et_searchStore.getText().toString().equals("")) {

                        intent = new Intent(MainActivity.this, StoreSearchActivity.class);
                        intent.putExtra("search", et_searchStore.getText().toString());
                        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                        startActivity(intent);
                    }

                    menu_visible_ani = AnimationUtils.loadAnimation(MainActivity.this, R.anim.menu_invisible);
                    et_searchStore.startAnimation(menu_visible_ani);
                    et_searchStore.setVisibility(v.INVISIBLE);
                    et_searchStore.setText("");

                    imm.hideSoftInputFromWindow(et_searchStore.getWindowToken(), 0);
                } else {
                    menu_visible_ani = AnimationUtils.loadAnimation(MainActivity.this, R.anim.menu_visible);
                    et_searchStore.startAnimation(menu_visible_ani);
                    et_searchStore.setVisibility(v.VISIBLE);

                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        });

        coder = new Geocoder( MainActivity.this );

        drawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_UNLOCKED );

        progressDialog = new ProgressDialog( MainActivity.this );
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable( false );
        progressDialog.show();

        mapView = findViewById(R.id.map_view);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 500, locationListener);

        new getMyPointAsync().execute();
    }

    public class getMyPointAsync extends AsyncTask<Void, Void, Integer> {

        String parameter;
        String serverip = IpInfo.SERVERIP + "getMyPoint.do";

        int point;

        @Override
        protected Integer doInBackground(Void... voids) {

            parameter = "login_idx=" + login_idx;

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

                    point = jsonObject.getInt("point");
                }

            } catch (Exception e) {
                Log.i( "MY", e.toString() );
            }

            return point;
        }

        @Override
        protected void onPostExecute(Integer integer) {

            tv_myPoint.setText("포인트 : " + point + "p");
        }
    }

    View.OnClickListener my_drawer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.button_logout:

                    Toast.makeText(MainActivity.this, "로그아웃되었습니다", Toast.LENGTH_SHORT).show();

                    UserManagement.requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {

                            pref = PreferenceManager.getDefaultSharedPreferences( MainActivity.this );
                            SharedPreferences.Editor editor = pref.edit();
                            editor.clear();

                            editor.putBoolean( "save", false );
                            editor.apply();

                            handler_logout.sendEmptyMessageDelayed(0, 800);
                        }
                    });
                break;
                
                case R.id.linearLayout_myReservation:

                    intent = new Intent(MainActivity.this, MyReservationActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    startActivity(intent);
                    break;

                case R.id.linearLayout_myLike:

                    intent = new Intent(MainActivity.this, MyStoreLikeActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    startActivity(intent);
                    break;

                case R.id.linearLayout_myProduct:

                    intent = new Intent(MainActivity.this, MyProductActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    startActivity(intent);
                    break;
            }
        }
    };

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

            new getAllStoreAsync().execute();
            progressDialog.dismiss();

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

    public class getAllStoreAsync extends AsyncTask<Void, Void, ArrayList<StoreVO>> {

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
                        vo.setGood(jsonObject.getInt("good"));

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

            loc_list = new ArrayList<>();

            for(int i = 0; i < storeVOS.size(); i++) {

                try {
                    address = coder.getFromLocationName( storeVOS.get(i).getAddress1(), 5 );
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

                    loc_vo = new LocationVO(list.get(i).getNickName_idx(), list.get(i).getName(), distance, list.get(i).getPhoto1(), list.get(i).getInfo(), list.get(i).getGood());
                    loc_list.add(loc_vo);
                    Collections.sort(loc_list);

                } catch (Exception e) {
                    Log.i( "MY", e.toString() );
                }
            }

            getStoreAllAdapter = new GetStoreAllAdapter(loc_list, MainActivity.this, my_lat, my_lng);
            getStoreAllAdapter.notifyDataSetChanged();
            listView.setAdapter(getStoreAllAdapter);
            Utility.setListViewHeightBasedOnChildren(listView);
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

                        intent = new Intent(MainActivity.this, StoreInfoActivity.class);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        new getAllStoreAsync().execute();
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            startActivity(i);
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

    Handler handler_logout = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {

            intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            startActivity(intent);
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        backPressed.onBackPressed();
    }
}
