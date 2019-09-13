package com.kjh.hairshopdesigner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.util.List;

public class DesignerMainActivity extends AppCompatActivity {

    Button btn_store, btn_reservation, btn_employee, btn_surgery;
    TextView textView_logout, textView_nickName;

    SharedPreferences pref;
    BackPressed backPressed;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_main);

        backPressed = new BackPressed(this);

        textView_logout = findViewById(R.id.textView_logout);
        textView_nickName = findViewById(R.id.textView_nickName);
        btn_store = findViewById(R.id.btn_store);
        btn_reservation = findViewById(R.id.btn_reservation);
        btn_employee = findViewById(R.id.btn_employee);
        btn_surgery = findViewById(R.id.btn_surgery);

        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            setPermission();
            return;
        }
        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            setPermission();
            return;
        }
        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            setPermission();
            return;
        }

        btn_store.setOnClickListener(click);
        btn_reservation.setOnClickListener(click);
        btn_employee.setOnClickListener(click);
        btn_surgery.setOnClickListener(click);

        pref = PreferenceManager.getDefaultSharedPreferences( DesignerMainActivity.this );
        String nickName = pref.getString("login_nickName", "");
        textView_nickName.setText("'" + nickName + "' 님 안녕하세요!");

        textView_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(DesignerMainActivity.this, "로그아웃되었습니다", Toast.LENGTH_SHORT).show();

                UserManagement.requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {

                        pref = PreferenceManager.getDefaultSharedPreferences( DesignerMainActivity.this );
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear();

                        editor.putBoolean( "save", false );
                        editor.apply();

                        handler_logout.sendEmptyMessageDelayed(0, 800);
                    }
                });
            }
        });
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.btn_store:

                    intent = new Intent(DesignerMainActivity.this, DesignerStoreActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    startActivity(intent);
                    break;

                case R.id.btn_surgery:
                    intent = new Intent(DesignerMainActivity.this, DesignerSurgeryActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    startActivity(intent);
                    break;

                case R.id.btn_employee:
                    intent = new Intent(DesignerMainActivity.this, DesignerStaffActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    startActivity(intent);
                    break;

                case R.id.btn_reservation:
                    intent = new Intent(DesignerMainActivity.this, DesignerReservationActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    startActivity(intent);
                    break;

            }
        }
    };

    Handler handler_logout = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {

            intent = new Intent(DesignerMainActivity.this, LoginActivity.class);
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            startActivity(intent);
            finish();
        }
    };

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Intent i = new Intent(DesignerMainActivity.this, DesignerMainActivity.class);
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
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).check();
    }

    @Override
    public void onBackPressed() {
        backPressed.onBackPressed();
    }
}
