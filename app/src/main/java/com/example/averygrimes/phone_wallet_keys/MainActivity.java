package com.example.averygrimes.phone_wallet_keys;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private static final String TAG = "test";  //Show on the android monitor
    BluetoothAdapter bluetooth;
    Button addDevice, bluetoothSwitch; // Button references
    int deviceTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetooth = BluetoothAdapter.getDefaultAdapter();
        deviceTotal = 0;

        // Point references to the actual button
        addDevice = (Button) findViewById(R.id.addDevice);
        bluetoothSwitch = (Button) findViewById(R.id.bluetoothSwitch);

        // If buttons are clicked, go to onclick method
        addDevice.setOnClickListener(this);
        bluetoothSwitch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.addDevice:
            {
                TableLayout deviceLayout = (TableLayout) findViewById(R.id.deviceLayout);

                TableRow row= new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                row.setBackgroundColor(Color.GREEN);
                TextView tv = new TextView(this);
                tv.setText("Device " + deviceTotal);
                Space space = new Space(this);
                space.setMinimumWidth(790);
                Button addBtn = new Button(this);
                addBtn.setText("Snooze");
                row.addView(tv);
                row.addView(space);
                row.addView(addBtn);
                deviceLayout.addView(row,0);

                TableRow row2= new TableRow(this);
                row2.setLayoutParams(lp);
                Space space2 = new Space(this);
                space2.setMinimumHeight(50);
                row2.addView(space2);
                deviceLayout.addView(row2,1);

                deviceTotal++;
                break;
            }
            case R.id.bluetoothSwitch:
            {
                enableDisable();
                break;
            }
        }
    }

    //Create a BroadcastReceiver
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
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

    public void enableDisable()
    {
        if(bluetooth == null)
        {
            Log.d(TAG, "bluetooth is not compatible with this device");
        }

        if(!bluetooth.isEnabled()){         //When Bluetooth is OFF will turn on bluetooth
            Intent Try_enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(Try_enable);
            Button button = (Button) findViewById(R.id.bluetoothSwitch);
            button.setText("OFF");

            IntentFilter BT = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, BT);
        }

        if(bluetooth.isEnabled()){          //When Bluetooth is ON will turn off bluetooth
            bluetooth.disable();
            Button button = (Button) findViewById(R.id.bluetoothSwitch);
            button.setText("ON");

            IntentFilter BT = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, BT);
        }
    }
}
