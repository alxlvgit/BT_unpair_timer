package com.example.bluetoothtimer;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class Test_bt_connection extends AppCompatActivity {
   // private final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private UUID DEFAULT_UUID;
    private static final String TAG = null ;
    private final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
private  BluetoothSocket socket = null;
private  BluetoothDevice my_device = null;
BluetoothManager bMgr;
private InputStream is = null;
private OutputStream os = null;
Button connect;
Button reset_btn;
    TextView textView;
    AudioManager audioManager;
    BluetoothHeadset bluetoothHeadset;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_bt_connection);
        connect = findViewById(R.id.connect_btn);
        reset_btn = findViewById(R.id.reset_btn);
textView = findViewById(R.id.textView);
        getApplicationContext();




        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
  connect();

//  bluetoothHeadset.startVoiceRecognition(my_device);
//                audioManager.setMode(AudioManager.STREAM_VOICE_CALL);
//                audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
//                audioManager.setSpeakerphoneOn(false);
//                audioManager.startBluetoothSco();
//                audioManager.setBluetoothScoOn(true);
            }
        });

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  audioManager.setBluetoothScoOn(false);
                resetConnection();

            }
        });
    }


    private void connect() {
              resetConnection();
               my_device = adapter.getRemoteDevice("F8:DF:15:7E:7D:54");

       //my_device = adapter.getRemoteDevice("EB:06:EF:C4:5D:94");
        // Make an RFCOMM binding.
        BluetoothSocket tmp = null;
        DEFAULT_UUID = my_device.getUuids()[0].getUuid();
     try {
//         Method method;
//            method = my_device.getClass().getMethod("createRfcommSocket", int.class);
//            tmp = (BluetoothSocket) method.invoke(my_device, 1);

             tmp = my_device.createInsecureRfcommSocketToServiceRecord(DEFAULT_UUID);


        } catch (Exception e1) {
            Log.d("log", "connect(): Failed to bind to RFCOMM by UUID." + e1.getMessage());
        }

      socket = tmp;

            try {
                adapter.cancelDiscovery();

                socket.connect();


            } catch (Exception e) {
                Log.d("tag", "Not connected" + e.getMessage());
            }
        Log.d ("tag","connect(): CONNECTED!");

        try {
            is  = socket.getInputStream();
            os = socket.getOutputStream();
        }
        catch (IOException e) {
            Log.d("tag","connect(): Error attaching i/o streams to socket." + e.getMessage());
        }
    }
        private void resetConnection() {
//        if (is != null) {
//            try {is.close();} catch (IOException e) {
//                e.getMessage();
//            }
//            is = null;
//        }
//
//        if (os != null) {
//            try {
//                os.close();
//            }
//            catch (IOException e) {
//                e.getMessage();
//            }
//            os = null;
//        }

        if (socket != null) {
            try {
                //Thread.sleep(1000);
                socket.close();
            } catch (Exception e) {
                e.getMessage();
            }
            socket = null;
        }
        adapter.cancelDiscovery();
        }
}