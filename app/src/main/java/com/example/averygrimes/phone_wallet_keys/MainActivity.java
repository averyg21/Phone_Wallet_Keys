package com.example.averygrimes.phone_wallet_keys;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "test";  //Show on the android monitor
    BluetoothAdapter bluetooth;

    //Create a BroadcastReceiver
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //when discovery finds a device
            if (action.equals(bluetooth.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetooth.ERROR);
                switch(state){
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mReceiver: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mReceiver: STATE TURNING OFF");
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.b);

        bluetooth = BluetoothAdapter.getDefaultAdapter();

        button.setOnClickListener(new View.OnClickListener() {  //will go to method enableDisable
            @Override
            public void onClick(View view) {
                enableDisable();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu dot){
        getMenuInflater().inflate(R.menu.main, dot);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id =item.getItemId();

        if (id==R.id.id_setting){
            Intent intentsetting = new Intent(MainActivity.this,Setting.class);
            startActivity(intentsetting);
            return true;
        }
        if (id==R.id.id_help){
            return true;
        }
        if (id==R.id.id_history){
            Intent intenthistory = new Intent(MainActivity.this,History.class);
            startActivity(intenthistory);
            return true;
        }
        return true;
    }

    public void enableDisable(){
        if(bluetooth == null){
            Log.d(TAG, "bluetooth is not compatible with this device");
        }
        if(!bluetooth.isEnabled()){         //When Bluetooth is OFF will turn on bluetooth
            Intent Try_enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(Try_enable);
            Button button = (Button) findViewById(R.id.b);
            button.setText("OFF");

            IntentFilter BT = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, BT);
        }
        if(bluetooth.isEnabled()){          //When Bluetooth is ON will turn off bluetooth
            bluetooth.disable();
            Button button = (Button) findViewById(R.id.b);
            button.setText("ON");

            IntentFilter BT = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, BT);
        }
    }
}
