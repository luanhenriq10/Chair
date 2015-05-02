package com.chaircare.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


public class Profile extends Activity {
    private ImageButton btn_home=null;
    private ImageButton btn_config=null;
    private ImageButton btn_back=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setDefaultListeners();
    }


    private void setDefaultListeners() {

        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_config=(ImageButton)findViewById(R.id.btn_config);
        btn_back=(ImageButton)findViewById(R.id.btn_back);
        setOnClickListeners();

    }

    private void setOnClickListeners() {
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callHome();

            }
        });

        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callConfig();

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callHome();
            }
        });
    }

    private void callHome()
    {
        Intent config = new Intent(this, MainActivity.class);
        config.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(config);
        this.finish();
    }

    private void callConfig()
    {
        Intent config = new Intent(this, Config.class);
        config.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(config);

        this.finish();
    }

    @Override
    public void onBackPressed() {
        callHome();
    }
}

