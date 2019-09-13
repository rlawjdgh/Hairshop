package com.kjh.hairshopdesigner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import util.IpInfo;
import util.Tag;

public class DesignerStoreActivity extends AppCompatActivity {

    Button btn_address, btn_save;
    EditText et_store, et_address1, et_address2, et_storeTel, et_storeOc, et_storeInfo;
    ImageView img1, img_add1, img2, img_add2;
    TextView tv_add1, tv_add2;
    ProgressDialog progressDialog;

    SharedPreferences pref;
    Intent intent;

    private static final int REQUEST_CAMERA1 = 1111;
    private static final int REQUEST_ALBUM1 = 2222;
    private static final int REQUEST_CAMERA2 = 3333;
    private static final int REQUEST_ALBUM2 = 4444;
    private String mCurrentPhotoPath;

    String img1Path = "";
    String img2Path = "";
    boolean getStoreInfo = false;
    boolean update = false;
    boolean insert = false;
    boolean check_img1 = false;
    boolean check_img2 = false;

    int nickName_idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_store);

        pref = PreferenceManager.getDefaultSharedPreferences( DesignerStoreActivity.this );
        nickName_idx = pref.getInt("login_idx", 0);

        btn_address = findViewById(R.id.button_address);
        btn_save = findViewById(R.id.button_save);
        et_store = findViewById(R.id.editText_store);
        et_address1 = findViewById(R.id.editText_address1);
        et_address2 = findViewById(R.id.editText_address2);
        et_storeTel = findViewById(R.id.editText_storeTel);
        et_storeOc = findViewById(R.id.editText_storeOc);
        et_storeInfo = findViewById(R.id.editText_storeInfo);
        img1 = findViewById(R.id.imageView1);
        img_add1 = findViewById(R.id.imageView_add1);
        img2 = findViewById(R.id.imageView2);
        img_add2 = findViewById(R.id.imageView_add2);
        tv_add1 = findViewById(R.id.textView_add1);
        tv_add2 = findViewById(R.id.textView_add2);

        img1.setOnClickListener(img);
        img2.setOnClickListener(img);
        btn_address.setOnClickListener(click);
        btn_save.setOnClickListener(click);

        progressDialog = new ProgressDialog( DesignerStoreActivity.this );
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable( false );
        progressDialog.show();

        new GetStoreInfo().execute(nickName_idx);
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.button_address:
                    et_address1.setText("");

                    Intent i = new Intent(DesignerStoreActivity.this, WebViewActivity.class);
                    startActivityForResult(i, 1);
                    break;

                case  R.id.button_save:

                    String name = et_store.getText().toString();
                    String address1 = et_address1.getText().toString();
                    String address2 = et_address2.getText().toString();
                    String tel = et_storeTel.getText().toString();
                    String openClose = et_storeOc.getText().toString();
                    String info = et_storeInfo.getText().toString();

                    if(name.isEmpty() && address1.isEmpty() && address1.isEmpty() && address2.isEmpty() && tel.isEmpty()
                            && openClose.isEmpty() && info.isEmpty()) {
                        Toast.makeText(DesignerStoreActivity.this, "가게 정보를 입력해 주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(check_img1 == false) {
                        Toast.makeText(DesignerStoreActivity.this, "이미지를 넣어주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(getStoreInfo) {
                        new ChangeStore().execute(name, address1, address2, tel, openClose, info);

                        if(check_img1) {
                            new UploadPhotoAsync().execute(img1Path, "img1");
                        }
                        if(check_img2) {
                            new UploadPhotoAsync().execute(img2Path, "img2");
                        }
                        if(update) {
                            Toast.makeText(DesignerStoreActivity.this, "매장 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                            handler_move.sendEmptyMessageDelayed(0, 800);
                        }

                    } else {
                        insert = true;
                        new InsertStore().execute(name, address1, address2, tel, openClose, info);
                    }
                    break;
            }
        }
    };

    // 이미지
    View.OnClickListener img = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            AlertDialog.Builder builder = new AlertDialog.Builder(DesignerStoreActivity.this);

            switch (view.getId()) {

                case R.id.imageView1:
                    if(check_img1 == false) {

                        builder.setTitle("갤러리에서 가져오시겠습니까?");

                        builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                startActivityForResult(intent, REQUEST_ALBUM1);
                            }
                        })
                                .setPositiveButton("사진 촬영하기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dispatchTakePictureIntent(REQUEST_CAMERA1);
                                    }
                                });

                        builder.show();
                    } else {
                        img1.setImageResource(0);
                        img1Path = "";
                        img_add1.setVisibility(View.VISIBLE);
                        tv_add1.setVisibility(View.VISIBLE);

                        check_img1 = false;
                        new UpdatePhoto1Async().execute("");
                    }
                    break;

                case R.id.imageView2:

                    if(check_img2 == false) {

                        if (check_img1 == false) {
                            Toast.makeText(DesignerStoreActivity.this, "첫 번째 사진을 저장해주세요", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        builder.setTitle("갤러리에서 가져오시겠습니까?");

                        builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                startActivityForResult(intent, REQUEST_ALBUM2);
                            }
                        })
                                .setPositiveButton("사진 촬영하기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dispatchTakePictureIntent(REQUEST_CAMERA2);
                                    }
                                });

                        builder.show();
                    } else {
                        img2.setImageResource(0);
                        img2Path = "";
                        img_add2.setVisibility(View.VISIBLE);
                        tv_add2.setVisibility(View.VISIBLE);

                        check_img2 = false;
                        new UpdatePhoto2Async().execute("");
                    }
                    break;

            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == 0000) {
            et_address1.setText(data.getStringExtra("data"));
        }
        switch (requestCode) {

            case REQUEST_CAMERA1:
                try {
                    File file = new File(mCurrentPhotoPath);

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));

                    if (bitmap == null) {
                        return;
                    }
                    ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                    img1Path = mCurrentPhotoPath;
                    Log.d(Tag.t, "ima1Path : " + img1Path);

                    Bitmap rotatedBitmap;
                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(bitmap, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(bitmap, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(bitmap, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = bitmap;
                    }
                    check_img1 = true;
                    img_add1.setVisibility(View.INVISIBLE);
                    tv_add1.setVisibility(View.INVISIBLE);

                    img1.setImageBitmap(rotatedBitmap);

                } catch (Exception e) {
                    Log.e(Tag.t, "" + e.getMessage());
                }
                break;

            case REQUEST_ALBUM1:

                Uri imageUri = data.getData();
                Cursor cursor;
                String[] proj = {MediaStore.Images.Media.DATA};
                assert imageUri != null;

                cursor = getContentResolver().query(imageUri, proj, null, null, null);

                assert cursor != null;

                int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToNext();
                img1Path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                Log.d(Tag.t, "img1Path : " + img1Path);

                File tempFile = new File(cursor.getString(idx));
                cursor.close();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inDither = false;
                Bitmap bitmap2 = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

                check_img1 = true;
                img_add1.setVisibility(View.INVISIBLE);
                tv_add1.setVisibility(View.INVISIBLE);

                img1.setImageBitmap(bitmap2);
                break;

            case REQUEST_CAMERA2:

                try {
                    File file = new File(mCurrentPhotoPath);
                    img2Path = mCurrentPhotoPath;

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));

                    if (bitmap == null) {
                        return;
                    }
                    ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap rotatedBitmap;
                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(bitmap, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(bitmap, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(bitmap, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = bitmap;
                    }
                    check_img2 = true;
                    img_add2.setVisibility(View.INVISIBLE);
                    tv_add2.setVisibility(View.INVISIBLE);

                    img2.setImageBitmap(rotatedBitmap);

                } catch (Exception e) {
                    Log.e(Tag.t, "" + e.getMessage());
                }
                break;

            case REQUEST_ALBUM2:

                Uri imageUri2 = data.getData();
                String[] proj2 = {MediaStore.Images.Media.DATA};
                assert imageUri2 != null;

                cursor = getContentResolver().query(imageUri2, proj2, null, null, null);

                assert cursor != null;

                int idx2 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToNext();
                img2Path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

                File tempFile1 = new File(cursor.getString(idx2));
                cursor.close();

                BitmapFactory.Options options2 = new BitmapFactory.Options();
                options2.inDither = false;
                Bitmap bitmap = BitmapFactory.decodeFile(tempFile1.getAbsolutePath(), options2);

                check_img2 = true;
                img_add2.setVisibility(View.INVISIBLE);
                tv_add2.setVisibility(View.INVISIBLE);

                img2.setImageBitmap(bitmap);
                break;
        }
    }

    private void dispatchTakePictureIntent(int REQUEST_CAMERA) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e(Tag.t, "" + e.getMessage());
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName(), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg",  storageDir );

        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public class GetStoreInfo extends AsyncTask<Integer, Void, JSONObject> {

        JSONObject jsonObject = null;

        String parameter;
        String serverip = IpInfo.SERVERIP + "getStoreInfo.do";

        @Override
        protected JSONObject doInBackground(Integer... integers) {

            int idx = integers[0];
            parameter = "nickName_idx=" + idx;

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
                    jsonObject = jsonArray.getJSONObject(0);
                }

            } catch (Exception e) {
                Log.i( "MY", e.toString() );
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                String result = jsonObject.getString("result");
                String photo1 = "";
                String photo2 = "";

                if(result.equals("success")) {

                    et_store.setText(jsonObject.getString("name"));
                    et_address1.setText(jsonObject.getString("address1"));
                    et_address2.setText(jsonObject.getString("address2"));
                    et_storeTel.setText(jsonObject.getString("tel"));
                    et_storeOc.setText(jsonObject.getString("openClose"));
                    et_storeInfo.setText(jsonObject.getString("info"));

                    photo1 = jsonObject.getString("photo1");
                    photo2 = jsonObject.getString("photo2");
                    getStoreInfo = true;
                    update = true;
                }

                if(!photo1.equals("null") && !photo1.equals("")) {
                    new GetStorePhotoAsync().execute( photo1, "img1" );
                }
                if(!photo2.equals("null") && !photo2.equals("")) {
                    new GetStorePhotoAsync().execute( photo2, "img2" );
                }

                progressDialog.dismiss();

            }catch (Exception e) {
                Log.e(Tag.t, e.getMessage());
            }
        }
    }

    public class GetStorePhotoAsync extends AsyncTask<String, Void, Bitmap> {

        String action = "";

        @Override
        protected Bitmap doInBackground(String... strings) {

            URL url = null;
            String photo = strings[0];
            action = strings[1];
            Bitmap bitmap = null;

            try {

                url = new URL(IpInfo.SERVERIP + "store_photo/" + photo);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);

                // 연결 종료
                is.close();
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null) {

                if ( action.equals("img1") ) {
                    check_img1 = true;
                    img1.setImageBitmap(bitmap);
                    img_add1.setVisibility(View.INVISIBLE);
                    tv_add1.setVisibility(View.INVISIBLE);

                } else if ( action.equals("img2") ) {
                    check_img2 = true;
                    img2.setImageBitmap(bitmap);
                    img_add2.setVisibility(View.INVISIBLE);
                    tv_add2.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    // store AsyncTask ( store 저장)
    public class InsertStore extends  AsyncTask<String, Void, String> {

        String result = "";
        String name, address1, address2, tel, openClose, info;

        @Override
        protected String doInBackground(String... strings) {

            name = strings[0];
            address1 = strings[1];
            address2 = strings[2];
            tel = strings[3];
            openClose = strings[4];
            info = strings[5];

            String parameter = "nickName_idx=" + nickName_idx + "&name=" + name + "&address1=" + address1 + "&address2=" + address2
                    + "&tel=" + tel + "&openClose=" + openClose + "&info=" + info;
            String serverip = IpInfo.SERVERIP + "insertStore.do";

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
                if(check_img1) {
                    new UploadPhotoAsync().execute(img1Path, "img1");
                } else if(check_img2) {
                    new UploadPhotoAsync().execute(img2Path, "img2");
                }
            }
        }
    }

    public class ChangeStore extends  AsyncTask<String, Void, String> {

        String result = "";
        String name, address1, address2, tel, openClose, info;

        @Override
        protected String doInBackground(String... strings) {

            name = strings[0];
            address1 = strings[1];
            address2 = strings[2];
            tel = strings[3];
            openClose = strings[4];
            info = strings[5];

            String parameter = "nickName_idx=" + nickName_idx + "&name=" + name + "&address1=" + address1 + "&address2=" + address2
                    + "&tel=" + tel + "&openClose=" + openClose + "&info=" + info;
            String serverip = IpInfo.SERVERIP + "changeStore.do";

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
                Log.d(Tag.t, "정보 수정");
            }
        }
    }

    // 파일 업로드
    public class UploadPhotoAsync extends AsyncTask<String, Void, JSONObject> {

        String action;

        @Override
        protected JSONObject doInBackground(String... strings) {

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            String fileName = strings[0];
            action = strings[1];
            String urlString = IpInfo.SERVERIP + "upload_storephoto.do";
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
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd );
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
                        JSONArray jsonArray = new JSONArray( stringBuffer.toString() );
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
                Log.d(Tag.t, "result : " + result);

                if( result.equals("success") ){
                    String file_name = jsonObject.getString("file_name");

                    if(action.equals("img1")) {
                        new UpdatePhoto1Async().execute(file_name);
                    }
                    if(action.equals("img2")) {
                        Log.d(Tag.t, "img2 접근");
                        new UpdatePhoto2Async().execute(file_name);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "이미지 업로드 및 저장 실패", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 포토1 Async
    public class UpdatePhoto1Async extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            String parameter = "file_name=" + strings[0] + "&nickName_idx=" + nickName_idx;
            String serverip = IpInfo.SERVERIP + "update_storephoto1.do";
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
                Log.d(Tag.t, "이미지1 저장");
                if(!check_img2) {

                    if(insert) {
                        Toast.makeText(DesignerStoreActivity.this, "매장 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        handler_move.sendEmptyMessageDelayed(0, 800);
                    }
                }
            }
        }
    }

    // 포토2 Async
    public class UpdatePhoto2Async extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            String parameter = "file_name=" + strings[0] + "&nickName_idx=" + nickName_idx;
            String serverip = IpInfo.SERVERIP + "update_storephoto2.do";
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
               Log.d(Tag.t, "이미지2 저장");

                if(insert) {
                    Toast.makeText(DesignerStoreActivity.this, "매장 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    handler_move.sendEmptyMessageDelayed(0, 800);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        
        if(!check_img1) {
            Toast.makeText(this, "사진을 저장해주세요", Toast.LENGTH_SHORT).show();
        } else {
            intent = new Intent( DesignerStoreActivity.this, DesignerMainActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            startActivity(intent);
        }
    }

    Handler handler_move = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            intent = new Intent( DesignerStoreActivity.this, DesignerMainActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            startActivity(intent);
        }
    };
}
