package com.kjh.hairshopdesigner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import util.IpInfo;
import util.Tag;

public class DesignerSurgeryActivity extends AppCompatActivity {

    Button btn_back, btn_add;
    TextView tv_cut, tv_perm, tv_chlorination, tv_clinic, tv_product , tv_noSurgery, tv_add;
    ListView listView;
    Dialog dialog;
    SharedPreferences pref;
    ProgressDialog progressDialog;

    EditText add_name;
    EditText add_price;
    ImageView add_imageView, img_add;
    Button add_surgery;

    SurgeryVO vo;
    ItemSurgeryAdapter itemSurgeryAdapter;
    ItemProductAdapter itemProductAdapter;

    int category = 0;
    int nickName_idx;
    boolean check_img = false;
    String img1Path;
    String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_surgery);

        btn_back = findViewById(R.id.button_back);
        btn_add = findViewById(R.id.button_add);
        tv_cut = findViewById(R.id.textView_cut);
        tv_perm = findViewById(R.id.textView_perm);
        tv_chlorination = findViewById(R.id.textView_chlorination);
        tv_clinic = findViewById(R.id.textView_clinic);
        tv_product = findViewById(R.id.textView_product);
        tv_noSurgery = findViewById(R.id.textView_noSurgery);
        listView = findViewById(R.id.listView_surgery);

        tv_cut.setOnClickListener(click);
        tv_perm.setOnClickListener(click);
        tv_chlorination.setOnClickListener(click);
        tv_clinic.setOnClickListener(click);
        tv_product.setOnClickListener(click);
        btn_add.setOnClickListener(btn_Add);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DesignerSurgeryActivity.this, DesignerMainActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(intent);
            }
        });

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

        progressDialog = new ProgressDialog( DesignerSurgeryActivity.this );
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable( false );
        progressDialog.show();

        pref = PreferenceManager.getDefaultSharedPreferences( DesignerSurgeryActivity.this );
        nickName_idx = pref.getInt("login_idx", 0);

        new getItemSurgery().execute();

    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.textView_cut:
                    category = 0;
                    break;

                case R.id.textView_perm:
                    category = 1;
                    break;

                case R.id.textView_chlorination:
                    category = 2;
                    break;

                case R.id.textView_clinic:
                    category = 3;
                    break;

                case R.id.textView_product:
                    category = 4;
                    break;
            }
            new getItemSurgery().execute();
        }
    };

    View.OnClickListener btn_Add = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            if(category == 4) {

                dialog = new Dialog(DesignerSurgeryActivity.this);
                dialog.setContentView(R.layout.add_product_dialog);
                dialog.show();

                img_add = dialog.findViewById(R.id.imageView_add);
                tv_add = dialog.findViewById(R.id.textView_add);

                add_name = dialog.findViewById(R.id.editText_product_name);
                add_price = dialog.findViewById(R.id.editText_product_price);
                add_imageView = dialog.findViewById(R.id.imageView_add_product);
                add_surgery = dialog.findViewById(R.id.button_add_product);

                add_imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        check_img = true;
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, 1111);
                    }
                });

                add_surgery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        productName = add_name.getText().toString().trim();
                        String price = add_price.getText().toString().trim();

                        if(productName.equals("") && price.equals("")) {
                            Toast.makeText(DesignerSurgeryActivity.this, "이름과 가격을 입력하세요", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(!check_img) {
                            Toast.makeText(DesignerSurgeryActivity.this, "사진을 등록해주세요", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new InsertItemSurgery().execute(productName, price);
                        dialog.dismiss();
                    }
                });

            } else {

                dialog = new Dialog(DesignerSurgeryActivity.this);
                dialog.setContentView(R.layout.add_surgery_dialog);
                dialog.show();

                add_name = dialog.findViewById(R.id.editText_add_name);
                add_price = dialog.findViewById(R.id.editText_add_price);
                add_surgery = dialog.findViewById(R.id.button_add_surgery);

                add_surgery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String name = add_name.getText().toString().trim();
                        String price = add_price.getText().toString().trim();

                        if(name.equals("") && price.equals("")) {
                            Toast.makeText(DesignerSurgeryActivity.this, "이름과 가격을 입력하세요", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        new InsertItemSurgery().execute(name, price);
                        dialog.dismiss();
                    }
                });
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1111) {

            Uri imageUri = data.getData();
            Cursor cursor;
            String[] proj = {MediaStore.Images.Media.DATA};
            assert imageUri != null;

            cursor = getContentResolver().query(imageUri, proj, null, null, null);

            assert cursor != null;

            int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToNext();
            img1Path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

            File tempFile = new File(cursor.getString(idx));
            cursor.close();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = false;
            Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

            img_add.setVisibility(View.INVISIBLE);
            tv_add.setVisibility(View.INVISIBLE);

            add_imageView.setImageBitmap(bitmap);
        }
    }

    public class InsertItemSurgery extends AsyncTask<Object, Void, String> {

        String parameter;
        String serverip = IpInfo.SERVERIP + "insertItemSurgery.do";

        String name;
        String price;
        String result = "";

        @Override
        protected String doInBackground(Object... objects) {

            name = (String)objects[0];
            price = (String)objects[1];

            parameter = "nickName_idx=" + nickName_idx + "&category=" + category + "&name=" + name + "&price=" + price;

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

                    result = jsonObject.getString("result");
                }

            } catch (Exception e) {
                Log.i( "MY", e.toString() );
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            if( s.equals("success") ){

                if(check_img) {
                    new UploadPhotoAsync().execute(img1Path);
                } else {
                    new getItemSurgery().execute();
                }
            }
        }
    }

    public class UploadPhotoAsync extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            String fileName = strings[0];
            String urlString = IpInfo.SERVERIP + "upload_ProductPhoto.do";
            JSONObject jsonObject = null;

            try {
                File sourceFile = new File(fileName);
                DataOutputStream dos;

                if (!sourceFile.isFile()) {
                    Log.e("MY", "Source File not exist :" + fileName);
                } else {

                    FileInputStream mFileInputStream = new FileInputStream(sourceFile);
                    URL connectUrl = new URL(urlString);

                    HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    int bytesAvailable = mFileInputStream.available();
                    int maxBufferSize = 1024 * 1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    byte[] buffer = new byte[bufferSize];
                    int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = mFileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    mFileInputStream.close();
                    dos.flush();
                    // finish upload...

                    if (conn.getResponseCode() == conn.HTTP_OK) {

                        InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                        BufferedReader reader = new BufferedReader(tmp);
                        StringBuffer stringBuffer = new StringBuffer();

                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuffer.append(line);
                        }
                        JSONArray jsonArray = new JSONArray(stringBuffer.toString());
                        jsonObject = jsonArray.getJSONObject(0);
                    }
                    mFileInputStream.close();
                    dos.close();
                }
            } catch (Exception e) {
                Log.i("MY", e.toString());
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            String result = "";

            try {
                result = jsonObject.getString("result");

                if( result.equals("success") ){
                    String file_name = jsonObject.getString("file_name");

                    new UpdatePhotoAsync().execute(file_name);

                }else{
                    Toast.makeText(getApplicationContext(), "이미지 업로드 및 저장 실패", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class UpdatePhotoAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String parameter = "file_name=" + strings[0] + "&nickName_idx=" + nickName_idx + "&category=" + category + "&name=" + productName;
            String serverip = IpInfo.SERVERIP + "update_ProductPhoto.do";

            String result = "";

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
                    result = jsonObject.getString("result");
                }
            } catch (Exception e) {
                Log.i( "MY", e.toString() );
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            if(s.equals("success")) {
                Log.d(Tag.t, "이미지 저장");
                new getItemSurgery().execute();
            }
        }
    }

    public class getItemSurgery extends AsyncTask<Void, Void, ArrayList<SurgeryVO>> {

        ArrayList<SurgeryVO> list;

        @Override
        protected ArrayList<SurgeryVO> doInBackground(Void... voids) {

            String parameter = "nickName_idx=" + nickName_idx + "&category=" + category;
            String serverip = IpInfo.SERVERIP + "getItemSurgery.do";

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
                        vo = new SurgeryVO();

                        if(category == 4) {

                            vo.setSurgery_idx(jsonObject.getInt("surgery_idx"));
                            vo.setCategory(jsonObject.getInt("category"));
                            vo.setName(jsonObject.getString("name"));
                            vo.setPrice(jsonObject.getInt("price"));
                            vo.setPhoto(jsonObject.getString("photo"));
                        } else {

                            vo.setSurgery_idx(jsonObject.getInt("surgery_idx"));
                            vo.setCategory(jsonObject.getInt("category"));
                            vo.setName(jsonObject.getString("name"));
                            vo.setPrice(jsonObject.getInt("price"));
                        }
                        list.add(vo);
                    }
                }

            } catch (Exception e) {
                Log.i( "MY", e.toString());
            }

            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<SurgeryVO> surgeryVOS) {

            progressDialog.dismiss();

            if(surgeryVOS.size() == 0 ) {
                tv_noSurgery.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {

                if(category == 4) {
                    tv_noSurgery.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);

                    itemProductAdapter = new ItemProductAdapter(surgeryVOS, DesignerSurgeryActivity.this);
                    listView.setAdapter(itemProductAdapter);
                    itemProductAdapter.notifyDataSetChanged();

                } else {
                    tv_noSurgery.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);

                    itemSurgeryAdapter = new ItemSurgeryAdapter(surgeryVOS, DesignerSurgeryActivity.this);
                    listView.setAdapter(itemSurgeryAdapter);
                    itemSurgeryAdapter.notifyDataSetChanged();

                    handler.sendEmptyMessageDelayed(0, 1250);
                }
            }
        }
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Intent i = new Intent(DesignerSurgeryActivity.this, DesignerSurgeryActivity.class);
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

        handler.removeMessages(0);

        Intent intent = new Intent(DesignerSurgeryActivity.this, DesignerMainActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        startActivity(intent);
    }

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 0) {
                new getItemSurgery().execute();
                handler.removeMessages(0);
            }
        }
    };
}
