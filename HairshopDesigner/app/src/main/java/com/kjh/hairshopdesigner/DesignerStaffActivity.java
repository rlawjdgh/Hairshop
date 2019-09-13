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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class DesignerStaffActivity extends AppCompatActivity {

    Button btn_staffAdd, btn_staff_add, btn_back;
    SharedPreferences pref;
    ListView listView;
    Dialog dialog;
    EditText et_staff_name, et_staff_info ,et_staff_grade;
    ImageView img_add, img_staff_photo;
    TextView tv_add, tv_noStaff;
    ItemStaffAdapter itemStaffAdapter;
    ProgressDialog progressDialog;

    StaffVO vo;
    boolean check_img = false;
    int nickName_idx;
    String img1Path, name, info, grade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_staff);

        btn_staffAdd = findViewById(R.id.button_StaffAdd);
        listView = findViewById(R.id.listView_staff);
        tv_noStaff = findViewById(R.id.textView_noStaff);
        btn_back = findViewById(R.id.button_staff_back);

        pref = PreferenceManager.getDefaultSharedPreferences( DesignerStaffActivity.this );
        nickName_idx = pref.getInt("login_idx", 0);

        progressDialog = new ProgressDialog( DesignerStaffActivity.this );
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable( false );
        progressDialog.show();

        new getItemStaff().execute();

        btn_staffAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new Dialog(DesignerStaffActivity.this);
                dialog.setContentView(R.layout.add_staff_dialog);
                dialog.show();

                tv_add = dialog.findViewById(R.id.textView_staff);
                img_add = dialog.findViewById(R.id.imageView_staff_plus);

                et_staff_name = dialog.findViewById(R.id.editText_staff_name);
                et_staff_info = dialog.findViewById(R.id.editText_staff_info);
                et_staff_grade = dialog.findViewById(R.id.editText_staff_grade);
                img_staff_photo = dialog.findViewById(R.id.imageView_staff_photo);
                btn_staff_add = dialog.findViewById(R.id.button_staff_add);

                img_staff_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        check_img = true;
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, 1111);
                    }
                });

                btn_staff_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        name = et_staff_name.getText().toString().trim();
                        info = et_staff_info.getText().toString().trim();
                        grade = et_staff_grade.getText().toString().trim();

                        if(name.equals("") && info.equals("") && grade.equals("")) {
                            Toast.makeText(DesignerStaffActivity.this, "이름, 직급, 한마디를 입력해주세요", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!check_img) {
                            Toast.makeText(DesignerStaffActivity.this, "사진을 등록해주세요", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        new InsertStaff().execute(name, info, grade);
                        dialog.dismiss();
                    }
                });

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move();
            }
        });
    }

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

            img_staff_photo.setImageBitmap(bitmap);
        }
    }

    public class InsertStaff extends AsyncTask<String, Void, String> {

        String parameter;
        String serverip = IpInfo.SERVERIP + "insertStaff.do";

        String result, name, info, grade;

        @Override
        protected String doInBackground(String... strings) {

            name = strings[0];
            info = strings[1];
            grade = strings[2];

            parameter = "nickName_idx=" + nickName_idx + "&name=" + name + "&info=" + info + "&grade=" + grade;

            try {
                String str;
                URL url = new URL(serverip);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                osw.write(parameter);
                osw.flush();

                if (conn.getResponseCode() == conn.HTTP_OK) {

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
                Log.i("MY", e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            if(s.equals("success")) {
                new UploadPhotoAsync().execute(img1Path);
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
            String urlString = IpInfo.SERVERIP + "upload_StaffPhoto.do";
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

            String parameter = "file_name=" + strings[0] + "&nickName_idx=" + nickName_idx + "&name=" + name + "&grade=" + grade;
            String serverip = IpInfo.SERVERIP + "update_StaffPhoto.do";

            String result = "";

            try {
                String str;
                URL url = new URL(serverip);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                osw.write(parameter);
                osw.flush();

                if (conn.getResponseCode() == conn.HTTP_OK) {

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
                Log.i("MY", e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equals("success")) {
                Log.d(Tag.t, "이미지 저장");
                new getItemStaff().execute();
            }
        }
    }

    public class getItemStaff extends AsyncTask<Void, Void, ArrayList<StaffVO>> {

        ArrayList<StaffVO> list;

        @Override
        protected ArrayList<StaffVO> doInBackground(Void... voids) {

            String parameter = "nickName_idx=" + nickName_idx;
            String serverip = IpInfo.SERVERIP + "getItemStaff.do";

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

                        vo = new StaffVO();

                        vo.setStaff_idx(jsonObject.getInt("staff_idx"));
                        vo.setNickName_idx(jsonObject.getInt("nickName_idx"));
                        vo.setName(jsonObject.getString("name"));
                        vo.setInfo(jsonObject.getString("info"));
                        vo.setGrade(jsonObject.getString("grade"));
                        vo.setPhoto(jsonObject.getString("photo"));

                        list.add(vo);
                    }
                }

            } catch (Exception e) {
                Log.i( "MY", e.toString());
            }

            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<StaffVO> staffVOS) {

            progressDialog.dismiss();

            if(staffVOS.size() == 0) {

                tv_noStaff.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {

                tv_noStaff.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                itemStaffAdapter = new ItemStaffAdapter(staffVOS, DesignerStaffActivity.this);
                listView.setAdapter(itemStaffAdapter);
            }
        }
    }

    public void move() {

        Intent intent = new Intent(DesignerStaffActivity.this, DesignerMainActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        move();
    }
}
