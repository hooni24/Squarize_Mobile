package org.scit.test.squarizemobile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

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

    static LatLng place = new LatLng(36.265778, 127.884858);
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

        Log.i("온맵레디", " 실행");

        Marker seoul = googleMap.addMarker(new MarkerOptions().position(place)
                .title("Seoul"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(7));
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
                            }

                            Log.i("가져온 값 : ", buskingArrayList.toString());

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

            for (int i = 0; i < buskingArrayList.size(); i++) {
                double latitude = Double.parseDouble(buskingArrayList.get(i).getLatitude());
                double longitude = Double.parseDouble(buskingArrayList.get(i).getLongitude());

                //마커기능
                MarkerOptions marker = new MarkerOptions();
                marker.position(new LatLng(latitude, longitude));
                marker.draggable(true);
                marker.title(buskingArrayList.get(i).getTitle());

                //시간계산
                Date now = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date buskingDate = null;
                Date endDate = null;
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


            }


        }
    };
}
