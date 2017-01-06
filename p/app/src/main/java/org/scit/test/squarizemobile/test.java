package org.scit.test.squarizemobile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class test extends AppCompatActivity {
    String jsontext;
    String buskingList;
    ProgressDialog mProgress;
    TextView textView01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textView01 = (TextView) findViewById(R.id.textview01);
    }

    public void btnClick(View v) {
        switch (v.getId()) {
            case R.id.button01:
                String buskingURL = "http://203.233.199.19:8888/Squarize/toBuskingList.action";

                mProgress = ProgressDialog.show(test.this, "Wait", "불러오는 중입니다...");
                DownThread thread = new DownThread(buskingURL);
                thread.start();

        }
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


                        JSONObject json = null;
                        try {
                            json = new JSONObject(jsontext);
                            buskingList = json.get("buskingList").toString();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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
            textView01.setText(msg.obj.toString());
        }
    };

}