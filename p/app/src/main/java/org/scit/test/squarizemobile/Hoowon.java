package org.scit.test.squarizemobile;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Hoowon extends AppCompatActivity {

    Spinner mileSpinner;
    ProgressDialog mProgress;
    String jsontext;
    String nowMile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoowon);


        mileSpinner = (Spinner)findViewById(R.id.mileSpin);
        ArrayAdapter mileAdapter = ArrayAdapter.createFromResource(this,
                R.array.mile, android.R.layout.simple_spinner_item);
        mileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mileSpinner.setAdapter(mileAdapter);

        nowMile = getIntent().getExtras().getString("mile");
        ((TextView)findViewById(R.id.mile)).setText("보유 마일리지 : " + nowMile);

    }


    public void hoowonGo(View v){
        String mileage = String.valueOf(mileSpinner.getSelectedItem());

        if(Integer.parseInt(nowMile) < Integer.parseInt(mileage)){
            Toast.makeText(this, "마일리지 부족", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, mileage + "원 후원함", Toast.LENGTH_SHORT).show();

        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        String id = sp.getString("loginId", "");

        String hoowonURL = "http://203.233.199.20:8888/Squarize/hoowon.action?id="+ id +"&buskerId="+ getIntent().getExtras().getString("buskerId") +"&mile="+mileage;
        mProgress = ProgressDialog.show(Hoowon.this, "후원하기", "처리중입니다...");
        Hoowon.DownThread thread = new Hoowon.DownThread(hoowonURL);
        thread.start();

        ((TextView)findViewById(R.id.mile)).setText("보유 마일리지 : " + (Integer.parseInt(nowMile) - Integer.parseInt(mileage)));
    }


    public void returnDetail(View v){
        finish();
    }



    class DownThread extends Thread {
        String mAddr;

        DownThread(String addr) {
            mAddr = addr;
        }

        @Override
        public void run() {
            String result = DownloadHtml(mAddr);
            Message message = mAfterDown.obtainMessage();
            message.obj = result;
            mAfterDown.sendMessage(message);
        }

        public String DownloadHtml(String addr) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(addr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
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
            } catch (NetworkOnMainThreadException e) {
                return "Error : 메인 스레드 네트워크 작업 에러 - " + e.getMessage();
            } catch (Exception e) {
                return "Error : " + e.getMessage();
            }
            return sb.toString();
        }
    }

    Handler mAfterDown = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();
            Toast.makeText(getApplicationContext(), "후원하였습니다", Toast.LENGTH_SHORT).show();
        }
    };


}
