package org.scit.test.squarizemobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scit.test.squarizemobile.vo.SQ_busking;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BuskingActivity extends FragmentActivity implements OnMapReadyCallback {

    static LatLng place = new LatLng(37.56, 126.97);
    private GoogleMap googleMap;
    String jsontext;
    String buskingList;
    ProgressDialog mProgress;
    ArrayList<SQ_busking> buskingArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busking);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String buskingURL = "http://203.233.199.19:8888/Squarize/toBuskingList.action";

        mProgress = ProgressDialog.show(BuskingActivity.this, "Wait", "불러오는 중입니다...");
        BuskingActivity.DownThread thread = new BuskingActivity.DownThread(buskingURL);
        thread.start();
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;

        Marker seoul = googleMap.addMarker(new MarkerOptions().position(place)
                .title("Seoul"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
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

                            for (int i = 0; i < jarray.length(); i++) {
                                SQ_busking busking = new SQ_busking();
                                Log.v("jarry", jarray.toString());
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

                                buskingArrayList.add(busking);
                                Log.v("busking", busking.toString());
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

//    Marker[] markerArray;

    Handler mAfterDown = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();
            Log.v("ㅋㅋㅋㅋ", msg.obj.toString());

//            markerArray = new Marker[buskingArrayList.size()];

            for (int i = 0; i < buskingArrayList.size(); i++) {
                Log.v("들어오나", buskingArrayList.get(i).toString());
                double longitude = Double.parseDouble(buskingArrayList.get(i).getLongitude());
                double latitude = Double.parseDouble(buskingArrayList.get(i).getLatitude());

                place = new LatLng(latitude, longitude);

                googleMap.addMarker(new MarkerOptions().position(place)
                        .title(buskingArrayList.get(i).getTeamname())
                        .snippet("시간 : " + buskingArrayList.get(i).getBuskingdate()
                                    +"\n주제 : " + buskingArrayList.get(i).getTitle()));

                final SQ_busking b = buskingArrayList.get(i);

                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(BuskingActivity.this, DetailActivity.class);
                        intent.putExtra("busking", b);
                        startActivity(intent);
                    }
                });
            }

//            //클릭리스너 달기 (작동 안할수 있음.. 작동 하면 일단은 해당마커 타이틀이 토스트로뜸)
//            //근데 누른 마커가 어떤건지 어떻게 알지? 객체를 넘기면 좋고 안돼면 buskingid라도 알아야함
//            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick(Marker marker) {
//                    Toast.makeText(getApplicationContext(), "클릭됨" + marker.getTitle(),
//                            Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//            });

        }
    };
}