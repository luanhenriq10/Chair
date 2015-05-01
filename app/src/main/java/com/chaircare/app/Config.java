package com.chaircare.app;

import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class Config extends Activity{

    private ImageButton btn_home=null;
    private Switch btToggle=null;
    private Button bt_find_device=null;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private  BluetoothAdapter mBluetoothAdapter = null;
    private IntentFilter filter=null;
    private Context context=this;
    private Activity activity=this;
    private Switch switch_alarm=null;
    private MediaPlayer player = new MediaPlayer();

    private LinearLayout device_list_header=null;
    private ListView device_list=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        setDefaultListeners();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listBTDevices();
    }

    @Override
    public void onBackPressed()
    {
        callHome();

    }

    private void setDefaultListeners()
    {

        btn_home=(ImageButton)findViewById(R.id.btn_home);
        btToggle= (Switch)findViewById(R.id.switch_en_dis);
        bt_find_device=(Button)findViewById(R.id.btn_search_devices);
        switch_alarm=(Switch)findViewById(R.id.switch_alarm);
        device_list_header=(LinearLayout)findViewById(R.id.list_device_layout_header);
        device_list_header.setVisibility(View.INVISIBLE);

        device_list=(ListView)findViewById(R.id.bt_devices_list);
        device_list.setVisibility(View.INVISIBLE);

        setOnClickListeners();

    }

    private void setOnClickListeners()
    {
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callHome();

            }
        });

        btToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBluetoothAdapter.isEnabled() && (!btToggle.isChecked())) {
                    mBluetoothAdapter.disable();
                }

                if ((!mBluetoothAdapter.isEnabled()) && (btToggle.isChecked())) {
                    mBluetoothAdapter.enable();

                }
            }
        });

        bt_find_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothAdapter.startDiscovery();
                listBTDevices();
                device_list_header.setVisibility(View.VISIBLE);
                device_list.setVisibility(View.VISIBLE);

                Toast.makeText(context, "Buscando", Toast.LENGTH_LONG).show();

            }
        });

        switch_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




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

    private void listBTDevices()
    {

        mNewDevicesArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,0);
        ListView newDevicesListView = (ListView) findViewById(R.id.bt_devices_list);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        // newDevicesListView.setOnItemClickListener(mDeviceClickListener);


        // Register for broadcasts when a device is discovered
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);


        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }



    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    mNewDevicesArrayAdapter.notifyDataSetChanged();
                }


            }
        }
    };

    static MediaPlayer getMediaPlayer(Context context){

        MediaPlayer mediaplayer = new MediaPlayer();

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return mediaplayer;
        }

        try {
            Class<?> cMediaTimeProvider = Class.forName( "android.media.MediaTimeProvider" );
            Class<?> cSubtitleController = Class.forName( "android.media.SubtitleController" );
            Class<?> iSubtitleControllerAnchor = Class.forName( "android.media.SubtitleController$Anchor" );
            Class<?> iSubtitleControllerListener = Class.forName( "android.media.SubtitleController$Listener" );

            Constructor constructor = cSubtitleController.getConstructor(new Class[]{Context.class, cMediaTimeProvider, iSubtitleControllerListener});

            Object subtitleInstance = constructor.newInstance(context, null, null);

            Field f = cSubtitleController.getDeclaredField("mHandler");

            f.setAccessible(true);
            try {
                f.set(subtitleInstance, new Handler());
            }
            catch (IllegalAccessException e) {return mediaplayer;}
            finally {
                f.setAccessible(false);
            }

            Method setsubtitleanchor = mediaplayer.getClass().getMethod("setSubtitleAnchor", cSubtitleController, iSubtitleControllerAnchor);

            setsubtitleanchor.invoke(mediaplayer, subtitleInstance, null);
            //Log.e("", "subtitle is setted :p");
        } catch (Exception e) {}

        return mediaplayer;
    }


}
