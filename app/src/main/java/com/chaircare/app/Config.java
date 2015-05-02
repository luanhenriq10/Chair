package com.chaircare.app;

import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;


public class Config extends Activity{

    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private ImageButton btn_home=null;
    private ImageButton btn_config=null;
    private Switch btToggle=null;
    private Button bt_find_device=null;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private  BluetoothAdapter mBluetoothAdapter = null;
    private IntentFilter filter=null;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private Context context=this;
    private Thread workerThread;
    private Activity activity=this;
    private Switch switch_alarm=null;
    private boolean conectado = false;
    public static Activity parent;



    private LinearLayout device_list_header=null;
    private ListView device_list=null;

    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

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
        btn_config=(ImageButton)findViewById(R.id.btn_config);
        btn_config.setImageResource(R.drawable.settings_gray);
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

                Toast.makeText(context, "Buscando", Toast.LENGTH_SHORT).show();

            }
        });

        switch_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                try {
                    int resID=getResources().getIdentifier("alarm", "raw", getPackageName());

                    MediaPlayer mediaPlayer=MediaPlayer.create(context,resID);



                    mediaPlayer.start();

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });


    }



    private void callHome()
    {

        Intent config = new Intent(this, MainActivity.class);
        config.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(config);


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

        try{
            if(mmDevice != null && conectado == false)
                openBT();
        }catch(Exception e){Toast.makeText(context, "An error occurrs", Toast.LENGTH_LONG).show();}
    }

    private void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        Toast.makeText(context, "Conectado ao " + mmDevice.getName(), Toast.LENGTH_LONG).show();
    }


    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 32; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];

        this.parent = this;
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                conectado = true;
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                sendData();
                                int bytesAvailable = mmInputStream.available();
                                if (bytesAvailable > 0) {
                                    byte[] packetBytes = new byte[bytesAvailable];
                                    mmInputStream.read(packetBytes);
                                    for (int i = 0; i < bytesAvailable; i++) {
                                        byte b = packetBytes[i];
                                        if (b == delimiter) {
                                            byte[] encodedBytes = new byte[readBufferPosition];
                                            System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                            final String data = new String(encodedBytes, "US-ASCII");
                                            readBufferPosition = 0;

                                            //System.out.println("Luaaan " + data);
                                            handler.post(new Runnable() {
                                                public void run() {
                                                    //Toast.makeText(context, ""+data, Toast.LENGTH_SHORT).show();
                                                    ShareData.data = data;

                                                }

                                                public String getData() {
                                                    return data;
                                                }
                                            });
                                        } else {
                                            readBuffer[readBufferPosition++] = b;
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                try {
                                    mmOutputStream.close();
                                    mmInputStream.close();
                                } catch (Exception e) {
                                }

                                Toast.makeText(parent, "Foi perdido a conexão", Toast.LENGTH_LONG).show();
                                stopWorker = true;
                            }
                        }
                    });
                    try {
                        Thread.currentThread().sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData() throws IOException
    {
        mmOutputStream.write("*\n".getBytes());
        //myLabel.setText("Data Sent");
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
                    if(device.getName().equals("HC-06")) {
                        Toast.makeText(context,"Encontrado o dispositivo", Toast.LENGTH_SHORT).show();
                        mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                        mNewDevicesArrayAdapter.notifyDataSetChanged();
                        mmDevice = device;
                        boundDevice(mmDevice);
                        if(mmDevice.getBondState() == 1)
                            Toast.makeText(context, "Pareado com o " + mmDevice.getName(),Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "Pareado com o " + mmDevice.getName(),Toast.LENGTH_SHORT).show();


                    }
                }
                else if(device.getBondState() == BluetoothDevice.BOND_BONDED)
                {
                    if(device.getName().equals("HC-06")) {
                        mmDevice = device;
                        try{
                            if(conectado == false)
                                openBT();
                        }catch(Exception e){
                            Toast.makeText(context,"Erro ao conectar",Toast.LENGTH_SHORT).show();
                        }

                    }
                }

            }
        }
    };

    public void boundDevice(BluetoothDevice device){
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
