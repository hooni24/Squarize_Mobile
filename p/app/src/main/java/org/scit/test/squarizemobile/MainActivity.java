package org.scit.test.squarizemobile;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String jsontext;
    String condition;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnClick(View v){
        switch (v.getId()){
            case R.id.logindBtn:
                String id = ((EditText)findViewById(R.id.idInput)).getText().toString();
                String pw = ((EditText)findViewById(R.id.pwInput)).getText().toString();



                mProgress = ProgressDialog.show(MainActivity.this,
                        "Wait", "Downloading...");
                DownThread thread = new DownThread
                        ("http://203.233.199.20:8888/Squarize/loginSQmember.action?sq_member_id="+id+"&sq_member_pw="+pw);
                thread.start();




//                if (jsontext == null) return;
//                JSONObject json = null;
//                try {
//                    json = new JSONObject(jsontext);
//                    condition = json.get("condition").toString();
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }




                break;
            case R.id.registerBtn:
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

            Log.i("받아온 값 : ", jsontext);
            return sb.toString();
        }
    }
    Handler mAfterDown = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();
            Toast.makeText(MainActivity.this, jsontext, Toast.LENGTH_SHORT).show();
        }
    };

}
