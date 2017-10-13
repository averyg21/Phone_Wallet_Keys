package com.example.averygrimes.phone_wallet_keys;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private static final String TAG = "test";  //Show on the android monitor
    private BluetoothAdapter bluetooth;
    private Button addDevice, bluetoothSwitch; // Button references
    private int deviceTotal = 0;

    private SwipeMenuListView listView;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetooth = BluetoothAdapter.getDefaultAdapter();
        deviceTotal = 0;

        listView = (SwipeMenuListView) findViewById(R.id.listView);

        list = new ArrayList<>();
        adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, list);

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
            {//
                listView.setAdapter(adapter);
                list.add("Device " + deviceTotal);

                adapter.notifyDataSetChanged();

                SwipeMenuCreator creator = new SwipeMenuCreator()
                {
                    @Override
                    public void create(SwipeMenu menu) {
                        // create "open" item
                        SwipeMenuItem snoozeItem = new SwipeMenuItem(
                                getApplicationContext());
                        // set item background
                        snoozeItem.setBackground(new ColorDrawable(Color.rgb(0x00, 0x66,
                                0xff)));
                        // set item width
                        snoozeItem.setWidth(200);
                        // set item title
                        snoozeItem.setIcon(R.drawable.icon_snooze);

                        snoozeItem.setId(0);
                        // add to menu
                        menu.addMenuItem(snoozeItem);

                        // create "delete" item
                        SwipeMenuItem settingsItem = new SwipeMenuItem(
                                getApplicationContext());
                        // set item background
                        settingsItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                                0x3F, 0x25)));
                        // set item width
                        settingsItem.setWidth(200);
                        // set a icon
                        settingsItem.setIcon(R.drawable.icon_settings);
                        // add to menu
                        menu.addMenuItem(settingsItem);
                    }
                };

                listView.setMenuCreator(creator);

                listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                        switch (index) {
                            case 0: // Snooze
                                Log.d(TAG, "onMenuItemClick: clicked item " + index);

                                break;
                            case 1: // Settings
                                Log.d(TAG, "onMenuItemClick: clicked item " + index);
                                break;
                        }
                        // false : close the menu; true : not close the menu
                        return false;
                    }
                });

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