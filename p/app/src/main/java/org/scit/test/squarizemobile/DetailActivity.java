package org.scit.test.squarizemobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.scit.test.squarizemobile.vo.SQ_busking;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    //사용되는 값들 : title, buskingdate, teamname, genre, location, runningtime, description
    //버튼 : goodock

    TextView title, buskingdate, teamname, genre, location, description;
    ImageView gallery;
    boolean isGoodock;
    SQ_busking busking;
    ProgressDialog mProgress;
    String jsontext;

    String command = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //해야하는 일들 : 공연에 대한 넘어온 값 뿌리기
        busking = (SQ_busking) getIntent().getSerializableExtra("busking");

        title = (TextView) findViewById(R.id.title);
        buskingdate = (TextView) findViewById(R.id.buskingdate);
        teamname = (TextView) findViewById(R.id.teamname);
        genre = (TextView) findViewById(R.id.genre);
        location = (TextView) findViewById(R.id.location);
        description = (TextView) findViewById(R.id.description);
        gallery = (ImageView) this.findViewById(R.id.gallery);

        title.setText(busking.getTitle());
        buskingdate.setText(busking.getBuskingdate());
        teamname.setText(busking.getTeamname());
        genre.setText(busking.getGenre());
        location.setText(busking.getLocation());
        description.setText(busking.getDescription());

            String gallery2 = busking.getGallery();

            gallery.setImageResource(R.drawable.busking2);

            //구독여부에 따라 버튼텍스트설정
            isGoodock = busking.isGoodock();

            if (isGoodock) {
            ((Button) findViewById(R.id.goodock)).setText("구독취소");
        } else {
            ((Button) findViewById(R.id.goodock)).setText("구독하기");
        }


    }


    /**
     * 구독하기 클릭
     * 로그인한사람 ID랑 해당 버스킹ID값을 action으로 넘겨서 sq_goodock테이블 수정
     */
    public void goodock(View v) {
        //구독대상 버스커 아이디
        String buskingId = busking.getId();
        //구독자 (로그인한사람) 아이디
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        String id = sp.getString("loginId", "");

        if (isGoodock) {
            ((Button) findViewById(R.id.goodock)).setText("구독하기");
            command = "구독취소 완료";

            String removeGoodockURL = "http://203.233.199.20:8888/Squarize/removeGoodock.action?id=" + id + "&buskingId=" + buskingId;
            mProgress = ProgressDialog.show(DetailActivity.this, "구독취소", "구독취소 중입니다...");
            DetailActivity.DownThread thread = new DetailActivity.DownThread(removeGoodockURL);
            thread.start();
        } else {
            ((Button) findViewById(R.id.goodock)).setText("구독취소");
            command = "구독신청 완료";

            String removeGoodockURL = "http://203.233.199.20:8888/Squarize/addGoodock.action?id=" + id + "&buskingId=" + buskingId;
            mProgress = ProgressDialog.show(DetailActivity.this, "구독하기", "구독신청 중입니다...");
            DetailActivity.DownThread thread = new DetailActivity.DownThread(removeGoodockURL);
            thread.start();
        }
        isGoodock = !isGoodock;
    }


    /**
     * 후원하기 클릭
     */
    public void hoowon(View v){
        Intent intent = new Intent(this, Hoowon.class);
        intent.putExtra("mile", getIntent().getExtras().getString("mile"));
        intent.putExtra("buskerId", busking.getId());
        startActivity(intent);
    }

    /**
     * 돌아가기 클릭
     */
    public void returnMap(View v) {
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
            Toast.makeText(getApplicationContext(), command, Toast.LENGTH_SHORT).show();
        }
    };


}
