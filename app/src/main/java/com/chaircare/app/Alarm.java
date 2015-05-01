package com.chaircare.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by root on 30/04/15.
 */
public class Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context k1, Intent k2) {
        // TODO Auto-generated method stub
        Toast.makeText(k1, "Alarm received!", Toast.LENGTH_LONG).show();


    }

}