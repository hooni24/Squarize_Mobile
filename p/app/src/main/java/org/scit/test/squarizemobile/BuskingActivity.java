package org.scit.test.squarizemobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scit.test.squarizemobile.vo.SQ_busking;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class BuskingActivity extends FragmentActivity implements OnMapReadyCallback {

    static final LatLng PLACE = new LatLng(37.552756, 126.928317);
    private GoogleMap googleMap;
    String jsontext;
    //String buskingList;
    ProgressDialog mProgress;
    ArrayList<SQ_busking> buskingArrayList;
    //ClusterManager<SQ_busking> mClusterManager;
    String mile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busking);

        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        String id = sp.getString("loginId", "");
        Toast.makeText(this, id + "님 환영합니다.", Toast.LENGTH_SHORT).show();

        ((TextView) findViewById(R.id.loginId)).setText(id + "님 환영합니다!");


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String buskingURL = "http://203.233.199.20:8888/Squarize/toBuskingList.action?id=" + id;

        mProgress = ProgressDialog.show(BuskingActivity.this, "Wait", "불러오는 중입니다...");
        BuskingActivity.DownThread thread = new BuskingActivity.DownThread(buskingURL);
        thread.start();
    }





    public void charge(View v){
        //버튼 누르면 이 메소드 실행됨. 결제창으로 연결되도록
    }





    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;

        //Marker seoul = googleMap.addMarker(new MarkerOptions().position(place).title("Seoul"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(PLACE));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        /*// 클러스터 매니저 생성
        mClusterManager = new ClusterManager<>(this, googleMap);
        googleMap.setOnCameraChangeListener(mClusterManager);*/
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
                        JSONArray jarray = null;
                        buskingArrayList = new ArrayList<>();
                        try {
                            json = new JSONObject(jsontext);
                            jarray = json.getJSONArray("buskingList");

                            mile = json.getString("mile");

                            for (int i = 0; i < jarray.length(); i++) {
                                SQ_busking busking = new SQ_busking();
                                busking.setSq_busking_id((Integer) jarray.getJSONObject(i).get("sq_busking_id"));
                                busking.setId((String) jarray.getJSONObject(i).get("id"));
                                busking.setTitle((String) jarray.getJSONObject(i).get("title"));
                                busking.setLocation((String) jarray.getJSONObject(i).get("location"));
                                busking.setLatitude((String) jarray.getJSONObject(i).get("latitude"));
                                busking.setLongitude((String) jarray.getJSONObject(i).get("longitude"));
                                busking.setUrl((String) jarray.getJSONObject(i).get("url"));
                                busking.setGenre((String) jarray.getJSONObject(i).get("genre"));
                                busking.setTeamname((String) jarray.getJSONObject(i).get("teamname"));
                                busking.setGallery((String) jarray.getJSONObject(i).get("gallery"));
                                busking.setGallery2((String) jarray.getJSONObject(i).get("gallery2"));
                                busking.setGallery3((String) jarray.getJSONObject(i).get("gallery3"));
                                busking.setGallery4((String) jarray.getJSONObject(i).get("gallery4"));
                                busking.setGallery5((String) jarray.getJSONObject(i).get("gallery5"));
                                busking.setBuskingdate((String) jarray.getJSONObject(i).get("buskingdate"));
                                busking.setRunningtime((Integer) jarray.getJSONObject(i).get("runningtime"));
                                busking.setDescription((String) jarray.getJSONObject(i).get("description"));
                                busking.setEnd((String) jarray.getJSONObject(i).get("end"));
                                busking.setIsGoodock((String) jarray.getJSONObject(i).get("isGoodock"));

                                buskingArrayList.add(busking);
                            }


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

    Marker[] markerArray;


    Handler mAfterDown = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();

            markerArray = new Marker[buskingArrayList.size()];

            Date now = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date buskingDate = null;
            Date endDate = null;



            ((TextView) findViewById(R.id.mileage)).setText(mile);

            for (int i = 0; i < buskingArrayList.size(); i++) {
                double latitude = Double.parseDouble(buskingArrayList.get(i).getLatitude());
                double longitude = Double.parseDouble(buskingArrayList.get(i).getLongitude());


                //마커기능
                MarkerOptions marker = new MarkerOptions();

                marker.position(new LatLng(latitude, longitude));
                marker.draggable(true);
                marker.title(buskingArrayList.get(i).getTeamname());
                marker.snippet(buskingArrayList.get(i).getTitle());


                //시간계산
                try {
                    buskingDate = formatter.parse(buskingArrayList.get(i).getBuskingdate());
                    endDate = formatter.parse(buskingArrayList.get(i).getEnd());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //마커 종류 확인
                if (endDate.getTime() - now.getTime() < 0) {
                    continue;
                }
                if (86400000 < buskingDate.getTime() - now.getTime() && buskingDate.getTime() - now.getTime() < 86400000 * 3) {
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue));
                } else if (0 < buskingDate.getTime() - now.getTime() && buskingDate.getTime() - now.getTime() <= 86400000) {
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red));
                } else {
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_purple));
                }

                //마커찍기
                markerArray[i] = googleMap.addMarker(marker);
                //mClusterManager.addItem(buskingArrayList.get(i));
                markerArray[i].setTag(buskingArrayList.get(i));


                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                        intent.putExtra("busking", (SQ_busking) marker.getTag());
                        intent.putExtra("mile", mile);
                        startActivity(intent);
                    }
                });
            } //for문

        }
    };
}