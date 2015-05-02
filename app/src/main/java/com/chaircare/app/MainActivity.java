package com.chaircare.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;


import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;





public class MainActivity extends BaseActivity {

    private ImageButton btn_config=null;
    private ImageButton btn_main_menu=null;
    private ImageButton btn_home=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        //mDrawerList.setItemChecked(position, true);
        //setTitle(listArray[position]);
        setDefaultListeners();



    }
    @Override
    public void onBackPressed()
    {
        if(onBackVerify())
            mainMenuAction();
        else
            this.finish();
    }

    private void setDefaultListeners()
    {

        btn_config=(ImageButton)findViewById(R.id.btn_config);
        btn_main_menu=(ImageButton)findViewById(R.id.btn_main_menu);
        btn_home=(ImageButton)findViewById(R.id.btn_home);
        btn_home.setImageResource(R.drawable.home_gray);

        setOnClickListeners();

    }

    private void setOnClickListeners()
    {
        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callConfig();

            }
        });

        btn_main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainMenuAction();
            }
        });

    }

    private void callConfig()
    {

        Intent config = new Intent(this, Config.class);
        config.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(config);
        this.finish();
    }


}
