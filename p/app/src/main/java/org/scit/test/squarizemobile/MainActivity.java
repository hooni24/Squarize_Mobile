package org.scit.test.squarizemobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnClick(View v){
        switch (v.getId()){
            case R.id.logindBtn:
                break;
            case R.id.registerBtn:
                break;
        }
    }

}
