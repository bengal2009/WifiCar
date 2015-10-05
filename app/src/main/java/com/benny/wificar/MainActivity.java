package com.benny.wificar;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity implements View.OnClickListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((Button) findViewById(R.id.leftkey)).setOnClickListener(this);
        ((Button) findViewById(R.id.rightkey)).setOnClickListener(this);
        ((Button) findViewById(R.id.upkey)).setOnClickListener(this);
        ((Button) findViewById(R.id.downkey)).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.leftkey:
                RcApplication.CAR.left();
                break;
            case R.id.rightkey:
                RcApplication.CAR.right();
                break;
            case R.id.upkey:
                RcApplication.CAR.forward();
                break;
            case R.id.downkey:
                RcApplication.CAR.reverse();
                break;

        }
    }
}
