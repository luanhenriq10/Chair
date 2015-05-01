package com.chaircare.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


public class About extends Activity {
    private ImageButton back_btn=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setDefaultListeners();
    }

    private void setDefaultListeners()
    {
        back_btn=(ImageButton)findViewById(R.id.btn_back_about);
        setOnClickListeners();

    }

    private void setOnClickListeners()
    {
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callHome();
            }
        });
    }

    @Override
    public void onBackPressed() {
        callHome();
    }

    private void callHome()
    {
        Intent config = new Intent(this, MainActivity.class);
        config.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(config);

        this.finish();
    }

}
