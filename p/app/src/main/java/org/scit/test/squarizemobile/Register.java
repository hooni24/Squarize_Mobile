package org.scit.test.squarizemobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Register extends AppCompatActivity {
    String jsontext;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Spinner genreSpinner = (Spinner)findViewById(R.id.genreRegister);
        ArrayAdapter genreAdapter = ArrayAdapter.createFromResource(this,
                R.array.genre, android.R.layout.simple_spinner_item);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(genreAdapter);
    }

    public void registerBtn(View v){
        switch(v.getId()){
            case R.id.okRegister:
                String id = ((EditText)findViewById(R.id.idRegister)).getText().toString();
                String pw = ((EditText)findViewById(R.id.pwRegister)).getText().toString();
                String pwConfirm = ((EditText)findViewById(R.id.pwConfirmRegister)).getText().toString();
                String name = ((EditText)findViewById(R.id.nameRegister)).getText().toString();
                String mail = ((EditText)findViewById(R.id.mailRegister)).getText().toString();
                String genre = ((Spinner)findViewById(R.id.genreRegister)).getSelectedItem().toString();

                if(!pw.equals(pwConfirm)) {
                    Toast.makeText(this, "비밀번호 확인이 틀립니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String registerURL = "http://203.233.199.20:8888/Squarize/registerSQmember.action?" +
                        "sq_member.sq_member_id=" + id +
                        "&sq_member.sq_member_pw=" + pw +
                        "&sq_member.sq_member_name=" + name +
                        "&sq_member.sq_member_email=" + mail +
                        "&sq_member.sq_member_favorite=" + genre;


                mProgress = ProgressDialog.show(Register.this, "회원가입", "가입 중입니다...");
                Register.DownThread thread = new Register.DownThread(registerURL);
                thread.start();

                break;
            case R.id.cancelRegister:
                finish();
                break;
        }
    }





    class DownThread extends Thread {
        String mAddr;

        DownThread(String addr){
            mAddr = addr;
        }

        @Override
        public void run() {
            String result = DownloadHtml(mAddr);
            Message message = mAfterDown.obtainMessage();
            message.obj = result;
            mAfterDown.sendMessage(message);
        }

        public String DownloadHtml(String addr){
            StringBuilder sb = new StringBuilder();
            try{
                URL url = new URL(addr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        InputStreamReader in
                                = new InputStreamReader(conn.getInputStream());
                        int ch;
                        while ((ch = in.read()) != -1) {
                            sb.append((char) ch);
                        }
                        in.close();
                        jsontext = sb.toString();

                    }
                    conn.disconnect();
                }
            } catch (NetworkOnMainThreadException e){
                return "Error : 메인 스레드 네트워크 작업 에러 - " + e.getMessage();
            } catch (Exception e) {
                return "Error : " + e.getMessage();
            }
            return sb.toString();
        }
    }
    Handler mAfterDown = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();
            Toast.makeText(Register.this, "이메일 인증을 진행해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }
    };


}
