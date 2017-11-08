package com.example.averygrimes.phone_wallet_keys;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import com.daimajia.swipe.util.Attributes;
import java.util.ArrayList;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.bluetooth.BluetoothDevice;
import android.widget.ListView;
import android.app.Dialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    // Empty References
    private Button btn_AddDevice, btn_BluetoothSwitch, btn_Scan, btn_MakeDiscoverable;
    BluetoothAdapter bluetoothAdapter;

    // Empty References for connected bluetooth devices list
    private TextView tvEmptyTextView;
    private RecyclerView mRecyclerView;
    private ArrayList<DeviceModel> connectedDeviceList;
    SwipeRecyclerViewAdapter SwipeAdapter;

    // Empty References for scanned bluetooth devices list
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView bluetooth_ScanList;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect References
        btn_AddDevice = (Button) findViewById(R.id.Btn_AddDevice);
        btn_BluetoothSwitch = (Button) findViewById(R.id.Btn_BluetoothSwitch);
        tvEmptyTextView = (TextView) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        connectedDeviceList = new ArrayList<>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        loadData(); // Get list of previous connected bluetooth devices. MAY NOT NEED
        createConnectedList(); // Display loadData stuff

        // Ask to turn on bluetooth if it is off at the start
        if(!bluetoothAdapter.isEnabled())
        {
            btn_AddDevice.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(bluetoothAdapter.isEnabled())
        {
            btn_BluetoothSwitch.setText("Bluetooth Off");
        }

        // If buttons are clicked, go to onclick method
        btn_AddDevice.setOnClickListener(this);
        btn_BluetoothSwitch.setOnClickListener(this);
    }
    


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.Btn_AddDevice:
            {
                if(!bluetoothAdapter.isEnabled())
                {
                    Toast.makeText(getApplicationContext(), "Turn bluetooth on first!", Toast.LENGTH_LONG).show();
                }
                else if(bluetoothAdapter.isEnabled())
                {
                    dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.popup_window);
                    dialog.setTitle("Title...");
                    bluetooth_ScanList= (ListView) dialog.findViewById(R.id.Bluetooth_ScanList);
                    dialog.show();

                    btn_Scan = (Button) dialog.findViewById(R.id.Btn_Scan);
                    btn_MakeDiscoverable = (Button) dialog.findViewById(R.id.Btn_MakeDiscoverable);

                    btn_Scan.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {

                            mBTDevices = new ArrayList<>();

                            if(bluetoothAdapter.isDiscovering()){
                                bluetoothAdapter.cancelDiscovery();

                                //check BT permissions in manifest
                                checkBTPermissions();

                                bluetoothAdapter.startDiscovery();
                                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
                            }
                            if(!bluetoothAdapter.isDiscovering()){

                                //check BT permissions in manifest
                                checkBTPermissions();

                                bluetoothAdapter.startDiscovery();
                                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
                            }
                        }
                    });

                    btn_MakeDiscoverable.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            //Toast.makeText(getApplicationContext(), "Making device discoverable for 300 seconds", Toast.LENGTH_LONG).show();

                            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                            startActivity(discoverableIntent);

                            IntentFilter intentFilter = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                            registerReceiver(mBroadcastReceiver2,intentFilter);
                        }
                    });

                    //connectedDeviceList.add(new DeviceModel("Device " + deviceTotal, "Status"));
                    mRecyclerView.setAdapter(SwipeAdapter);
                }

                break;
            }
            case R.id.Btn_BluetoothSwitch:
            {
                enableDisableBT();
                break;
            }
            case R.id.btn_Settings:
            {
                break;
            }
        }
    }


    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        btn_BluetoothSwitch.setText("Bluetooth On");
                        btn_AddDevice.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(getApplicationContext(), "Turning off Bluetooth", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        btn_BluetoothSwitch.setText("Bluetooth Off");
                        btn_AddDevice.getBackground().setColorFilter(null);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        break;
                }

            }
        }
    };

    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND))
            {
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);

                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                bluetooth_ScanList.setAdapter(mDeviceListAdapter);
            }
        }
    };

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            //Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    public void enableDisableBT()
    {
        if(bluetoothAdapter == null)
        {
            Toast.makeText(getApplicationContext(), "Something is wrong with the Bluetooth", Toast.LENGTH_LONG).show();
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        //mBluetoothAdapter.cancelDiscovery();
    }
    
    public void createConnectedList()
    {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));

        if(connectedDeviceList.isEmpty()){
            mRecyclerView.setVisibility(View.GONE);
            tvEmptyTextView.setVisibility(View.VISIBLE);
        }else{
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmptyTextView.setVisibility(View.GONE);
        }

        //creating adapter object
        SwipeAdapter = new SwipeRecyclerViewAdapter(this, connectedDeviceList);


        // Setting Mode to Single to reveal bottom View for one item in List
        // Setting Mode to Mutliple to reveal bottom Views for multile items in List
        ((SwipeRecyclerViewAdapter) SwipeAdapter).setMode(Attributes.Mode.Single);

        mRecyclerView.setAdapter(SwipeAdapter);

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
    }

    // load initial data
    public void loadData()
    {

        for (int i = 1; i < 11; i++)
        {
            connectedDeviceList.add(new DeviceModel("Device " + i, "Status"));
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
}
