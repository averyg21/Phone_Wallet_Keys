package com.example.averygrimes.phone_wallet_keys;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;


import com.daimajia.swipe.util.Attributes;
import com.example.averygrimes.phone_wallet_keys.DeviceModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private BluetoothAdapter bluetooth;
    private Button addDevice, bluetoothSwitch; // Button references
    private int deviceTotal = 0;

    private TextView tvEmptyTextView;
    private RecyclerView mRecyclerView;
    private ArrayList<DeviceModel> mDataSet;
    SwipeRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetooth = BluetoothAdapter.getDefaultAdapter();
        deviceTotal = 1;

        // Point references to the actual button
        addDevice = (Button) findViewById(R.id.addDevice);
        bluetoothSwitch = (Button) findViewById(R.id.bluetoothSwitch);
        tvEmptyTextView = (TextView) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        
        mDataSet = new ArrayList<>();
        
        loadData();
        
        if(mDataSet.isEmpty()){
            mRecyclerView.setVisibility(View.GONE);
            tvEmptyTextView.setVisibility(View.VISIBLE);
        }else{
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmptyTextView.setVisibility(View.GONE);
        }
        
        
        //creating adapter object
        mAdapter = new SwipeRecyclerViewAdapter(this, mDataSet);
        
        
        // Setting Mode to Single to reveal bottom View for one item in List
        // Setting Mode to Mutliple to reveal bottom Views for multile items in List
        ((SwipeRecyclerViewAdapter) mAdapter).setMode(Attributes.Mode.Single);
        
        mRecyclerView.setAdapter(mAdapter);
        
        /**Scroll listener**/
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("RecyclerView", "onScrollStateChanged");
            }
            
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        // If buttons are clicked, go to onclick method
        addDevice.setOnClickListener(this);
        bluetoothSwitch.setOnClickListener(this);
    }
    
    // load initial data
    public void loadData()
    {
        
        for (int i = 1; i < 11; i++)
        {
            mDataSet.add(new DeviceModel("Device " + i, "Status"));
            deviceTotal++;
        }
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

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.addDevice:
            {
                mDataSet.add(new DeviceModel("Device " + deviceTotal, "Status"));
                mRecyclerView.setAdapter(mAdapter);

                deviceTotal++;
                break;
            }
            case R.id.bluetoothSwitch:
            {
                enableDisable();
                break;
            }
            case R.id.btn_Settings:
            {

                break;
            }
        }
        
    }
    
    
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
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    public void enableDisable()
    {
        if(bluetooth == null)
        {
            //Log.d(TAG, "bluetooth is not compatible with this device");
        }

        if(!bluetooth.isEnabled()){         //When Bluetooth is OFF will turn on bluetooth
            Intent Try_enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(Try_enable);
            Button button = (Button) findViewById(R.id.bluetoothSwitch);
            button.setText("OFF");

            IntentFilter BT = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        }

        if(bluetooth.isEnabled()){          //When Bluetooth is ON will turn off bluetooth
            bluetooth.disable();
            Button button = (Button) findViewById(R.id.bluetoothSwitch);
            button.setText("ON");

            IntentFilter BT = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        }
    }
}
