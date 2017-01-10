package org.scit.test.squarizemobile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.scit.test.squarizemobile.vo.SQ_busking;

public class DetailActivity extends AppCompatActivity {

    //사용되는 값들 : title, buskingdate, teamname, genre, location, runningtime, description
    //버튼 : goodock

    TextView title, buskingdate, teamname, genre, location, description;
    boolean isGoodock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //해야하는 일들 : 공연에 대한 넘어온 값 뿌리기

        SQ_busking busking = (SQ_busking) getIntent().getSerializableExtra("busking");
        title = (TextView) findViewById(R.id.title);
        buskingdate = (TextView) findViewById(R.id.buskingdate);
        teamname = (TextView) findViewById(R.id.teamname);
        genre = (TextView) findViewById(R.id.genre);
        location = (TextView) findViewById(R.id.location);
        description = (TextView) findViewById(R.id.description);

        title.setText(busking.getTitle());
        buskingdate.setText(busking.getBuskingdate());
        teamname.setText(busking.getTeamname());
        genre.setText(busking.getGenre());
        location.setText(busking.getLocation());
        description.setText(busking.getDescription());


        //구독여부에 따라 버튼텍스트설정

        isGoodock = busking.isGoodock();

        if(isGoodock){
            ((Button)findViewById(R.id.goodock)).setText("구독취소");
        }else {
            ((Button)findViewById(R.id.goodock)).setText("구독하기");
        }

    }


    /**
     * 구독하기 클릭
     * 로그인한사람 ID랑 해당 버스킹ID값을 action으로 넘겨서 sq_goodock테이블 수정
     */
    public void goodock(View v){

        if(isGoodock){
            ((Button)findViewById(R.id.goodock)).setText("구독취소");
        }else {
            ((Button)findViewById(R.id.goodock)).setText("구독하기");
        }

        isGoodock = !isGoodock;
    }


    /**
     * 후원하기 클릭
     */
    public void hoowon(View v){

    }

    /**
     * 돌아가기 클릭
     */
    public void returnMap(View v){
        finish();
    }
}
