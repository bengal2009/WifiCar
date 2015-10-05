package com.benny.wificar;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class Wificar extends ActionBarActivity implements View.OnClickListener  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wificar);
        ((Button)findViewById(R.id.leftkey)).setOnClickListener(this);
        ((Button)findViewById(R.id.rightkey)).setOnClickListener(this);
        ((Button)findViewById(R.id.upkey)).setOnClickListener(this);
        ((Button)findViewById(R.id.downkey)).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.leftkey:
                Toast.makeText(getApplicationContext(), "Left! ",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.rightkey:
                Toast.makeText(getApplicationContext(), "Right! ",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.upkey:
                Toast.makeText(getApplicationContext(), "UP! ",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.downkey:
                Toast.makeText(getApplicationContext(), "DOWN! ",
                        Toast.LENGTH_SHORT).show();
                break;

        }
    }



}


