package com.kjh.hairshopdesigner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import util.IpInfo;
import util.Tag;

public class LoginActivity extends AppCompatActivity {

    SessionCallback callback;
    SharedPreferences pref;
    LoginParser loginParser;

    String idx;
    String nickName;
    String email;
    int division;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = PreferenceManager.getDefaultSharedPreferences( LoginActivity.this );

        loginParser = new LoginParser();
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        String check = pref.getString("login_nickName", nickName);
        if(check != null) {
            move(pref.getInt("login_division", division));
        }
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    Log.e("SessionCallback :: ", "onFailure : " + errorResult.getErrorMessage());
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.e("SessionCallback :: ", "onSessionClosed : " + errorResult.getErrorMessage());
                }

                @Override
                public void onNotSignedUp() {
                    Log.e("SessionCallback :: ", "onNotSignedUp");
                }

                @Override
                public void onSuccess(final UserProfile userProfile) {

                    idx = String.valueOf(userProfile.getId());
                    nickName = userProfile.getNickname();
                    email = userProfile.getEmail();

                    Log.d(Tag.t, "" + idx);

                    new CheckMember().execute(String.valueOf(userProfile.getId()));
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("SessionCallback :: ", "onFailure : " + exception.getMessage());
        }
    }

    public class CheckMember extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            return loginParser.checkMember(strings[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            try{
                String result = jsonObject.getString("result");

                if(result.equals("fail")) {
                    division = 1;
                    new InsertMemberAsync().execute(idx, nickName, email, division);

                } else {

                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt( "login_idx", jsonObject.getInt("idx"));
                    editor.putString("login_nickName", jsonObject.getString("nickName"));
                    editor.putString( "login_email", jsonObject.getString("email"));
                    editor.putInt("login_division", jsonObject.getInt("division"));

                    Log.i(Tag.t, "" + jsonObject.getString("nickName"));
                    editor.apply();

                    move(jsonObject.getInt("division"));
                }

            } catch (Exception e) {
                Log.e(Tag.t, "" + e.getMessage());
            }
        }
    }

    public class InsertMemberAsync extends AsyncTask<Object, Void, String> {

        String parameter;
        String serverip = IpInfo.SERVERIP + "insertMember.do";

        String result = "";
        String nickName = "";
        String email = "";

        @Override
        protected String doInBackground(Object... objects) {

            idx = (String)objects[0];
            nickName = (String)objects[1];
            email = (String)objects[2];
            division = (Integer)objects[3];

            parameter = "idx=" + idx + "&nickName=" + nickName + "&email=" + email + "&division=" + division;

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

                Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = pref.edit();
                editor.putInt( "login_idx", Integer.parseInt(idx));
                editor.putString("login_nickName", nickName);
                editor.putString( "login_email", email);
                editor.putInt("login_division", division);
                editor.apply();

                move(division);
            }
        }
    }

    public void move(int division) {

        if(division == 1) {
            Intent i = new Intent( LoginActivity.this, DesignerMainActivity.class );
            i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            startActivity(i);
            finish();
        }
    }
}
