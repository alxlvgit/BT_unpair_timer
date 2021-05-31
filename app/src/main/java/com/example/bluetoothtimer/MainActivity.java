package com.example.bluetoothtimer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
   // private  final  static long START_TIME_IN_MILLIS = 600000;
    private Button test;
    private Button startPauseButton;
    private Button selectBt;
    private Button reset;
    private TextView countdown;
    private CountDownTimer countDownTimer;
    private EditText input;
    private Switch time_format;
    boolean timerRunning;
    //private long millis = START_TIME_IN_MILLIS;
    private  long preset;
    private  long millis;
    final BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
    private String deviceToUnpair;
   private List<String> myBondedDevices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        input = findViewById(R.id.minutesInput);
        startPauseButton = findViewById(R.id.start_pause);
        reset = findViewById(R.id.reset_btn);
        countdown = findViewById(R.id.textview_countdown);
        selectBt = findViewById(R.id.select_bt_btn);
        time_format = findViewById(R.id.switch_format);
        ListView listView = (ListView)findViewById(R.id.devices_list);
test = findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Test_bt_connection.class);
                 startActivity(intent);
            }
        });

// list all bt devices in a listview
        selectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bAdapter.isEnabled()){
                    bAdapter.enable();
                }
             myBondedDevices = listDevicesNames();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,myBondedDevices);
                    listView.setVisibility(View.VISIBLE);
listView.setAdapter(adapter);

//assign selected device to be unpaired once timer done
listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        deviceToUnpair = myBondedDevices.get(position);
        Toast.makeText(getApplicationContext(),"You've selected "+ myBondedDevices.get(position), Toast.LENGTH_SHORT ).show();
        listView.setVisibility(View.INVISIBLE);
    }
});
            }
        });


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });


        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerRunning) {
                    pauseTimer();
                }
                else {
                    startTimer();
                }
            }
        });



time_format.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            updateCountdownTextHours();
        }
        else {
            updateCountdownTextMinutes();
        }
    }
});
    }


    @SuppressLint("SetTextI18n")
    public void startTimer() {
        if (!presetTime()) {
            return;
        }
         if (myBondedDevices == null || deviceToUnpair == null){
         Toast.makeText(getApplicationContext(), "Please, choose a bluetooth device you want to unpair",Toast.LENGTH_SHORT).show();
             return;
       }
        //countdown.setText(input.getText().toString() + ":00");
            countDownTimer = new CountDownTimer(millis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    millis = millisUntilFinished;
                    if(time_format.isChecked()){
                        updateCountdownTextHours();
                    }
                    else {
                        updateCountdownTextMinutes();
                    }
                }

                @Override
                public void onFinish() {
                    timerRunning = false;
                    startPauseButton.setText("Start");
                    startPauseButton.setVisibility(View.INVISIBLE);
                    reset.setVisibility(View.VISIBLE);

                    //    find bluetooth device by name and unpair it if exists
                    Set<BluetoothDevice> pairedDevices = bAdapter.getBondedDevices();
                    for (BluetoothDevice bt : pairedDevices) {
                        if (bt.getName().contains(deviceToUnpair)) {
                            unpairDevice(bt);
                            deviceToUnpair = null;
                        }
                    }

                    // disable bluetooth adapter
//              if (bAdapter.isEnabled()) {
//                  bAdapter.disable();
//            }
//                Toast.makeText(getApplicationContext(),"Bluetooth Turned OFF", Toast.LENGTH_SHORT).show();

                }
            }.start();
            timerRunning = true;
            startPauseButton.setText("pause");
            reset.setVisibility(View.INVISIBLE);

    }

    @SuppressLint("SetTextI18n")
    public void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        startPauseButton.setText("start");
        reset.setVisibility(View.VISIBLE);
    }


    public void resetTimer() {
        millis = preset;
        if(time_format.isChecked()){
            updateCountdownTextHours();
        }
        else {
            updateCountdownTextMinutes();
        }
         reset.setVisibility(View.INVISIBLE);
         startPauseButton.setVisibility(View.VISIBLE);
    }

// min/sec format
    public void updateCountdownTextMinutes() {
            int minutes = (int) (millis/1000) / 60;
            int seconds =  (int) (millis/1000)  % 60;
            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            countdown.setText(timeLeftFormatted);
    }

    //hr/min/sec format
public void updateCountdownTextHours(){
        int hours = (int) (millis/3600000);
    int minutes = (int) ((millis/1000)%3600) / 60;
    int seconds =  (int) (millis/1000)  % 60;
    String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes,seconds);
    countdown.setText(timeLeftFormatted);
}

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean presetTime() {
        if (input!=null && !(input.length() ==0) && !input.getText().toString().equals("0") ){
            preset = (Long.parseLong(input.getText().toString()))*60000;


            if (preset!=0) {
                millis = preset;
                if (timerRunning) {
                    startPauseButton.setText("start");
                    countDownTimer.cancel();
                    timerRunning = false;
                    resetTimer();
                }
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Please, enter the preset value for timer", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //list all paired bt devices
public ArrayList<String> listDevicesNames (){
    ArrayList <String> devices = new ArrayList<String>();
    Set<BluetoothDevice> pairedDevices = bAdapter.getBondedDevices();
    for (BluetoothDevice bt : pairedDevices) {
      devices.add(bt.getName());
    }
    return devices;
}

}