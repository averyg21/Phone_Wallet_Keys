package com.example.averygrimes.phone_wallet_keys;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import com.daimajia.swipe.util.Attributes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import android.widget.LinearLayout;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
//NEW
import java.util.Date;
import java.util.Calendar;

import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.bluetooth.BluetoothDevice;
import android.widget.ListView;
import android.app.Dialog;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "MainActivity";

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

    //Empty References for changing color
    LinearLayout linearLayout;
    ConstraintLayout constraintLayout;
    ActionBar actionBar;
    View view;
    int themeclick;

    //Access the database
    Database myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //access database class
        myDb = new Database(this);

        // Connect References
        btn_AddDevice = (Button) findViewById(R.id.Btn_AddDevice);
        btn_BluetoothSwitch = (Button) findViewById(R.id.Btn_BluetoothSwitch);
        tvEmptyTextView = (TextView) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        connectedDeviceList = new ArrayList<>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        for(BluetoothDevice bt : pairedDevices)
        {
            try {
                Method method = bt.getClass().getMethod("getAliasName");
                if(method != null)
                {
                    connectedDeviceList.add(new DeviceModel((String)method.invoke(bt), "Unpaired"));
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }



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

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        // If buttons are clicked, go to onclick method
        btn_AddDevice.setOnClickListener(this);
        btn_BluetoothSwitch.setOnClickListener(this);


        //changes the color of background depending on theme class
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        constraintLayout = (ConstraintLayout) findViewById(R.id.activity_main_layout);
        actionBar = getSupportActionBar();
        Intent intent = getIntent();
        themeclick = intent.getIntExtra("name", 0);

        if (themeclick == 1)
        {
            linearLayout.setBackgroundResource(R.color.colordefault);
            constraintLayout.setBackgroundResource(R.color.colordefault2);
        }
        if (themeclick == 2){
            linearLayout.setBackgroundResource(R.color.colorBlack);
            constraintLayout.setBackgroundResource(R.color.colorRed);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#800000")));
        }
        if (themeclick == 3){
            linearLayout.setBackgroundResource(R.color.colorTan);
            constraintLayout.setBackgroundResource(R.color.colorBeach1);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4da6ff")));
        }
        if (themeclick == 4){
            linearLayout.setBackgroundResource(R.color.colorFall1);
            constraintLayout.setBackgroundResource(R.color.colorFall2);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3d0099")));
        }

        //New
        myDb = new Database(this);
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
                            Log.d(TAG, "btnDiscover: Looking for unpaired devices.");
                            mBTDevices = new ArrayList<>();

                            if(bluetoothAdapter.isDiscovering()){
                                bluetoothAdapter.cancelDiscovery();
                                Log.d(TAG, "btnDiscover: Canceling discovery.");

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

                            bluetooth_ScanList.setOnItemClickListener(new OnItemClickListener()
                            {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    //first cancel discovery because its very memory intensive.
                                    bluetoothAdapter.cancelDiscovery();

                                    Log.d(TAG, "onItemClick: You Clicked on a device.");
                                    String deviceName = mBTDevices.get(i).getName();
                                    String deviceAddress = mBTDevices.get(i).getAddress();

                                    Log.d(TAG, "onItemClick: deviceName = " + deviceName);
                                    Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

                                    //when device is added will show up on the database table
                                    String Dname = deviceName.toString();
                                    //String Dstatus = "";
                                    Date d=new Date();
                                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                                    String currentDateTimeString = sdf.format(d);
                                    String Dtime = currentDateTimeString;

                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                    String formattedDate = df.format(c.getTime());
                                    String Ddate = formattedDate;

                                    AddData(Dname,Dtime,Ddate);

                                    //create the bond.
                                    //NOTE: Requires API 17+? I think this is JellyBean
                                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                                        Log.d(TAG, "Trying to pair with " + deviceName);
                                        mBTDevices.get(i).createBond();
                                    }
                                }
                            });


                        }
                    });

                    btn_MakeDiscoverable.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

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
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
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
                        Log.d(TAG, "onReceive: STATE OFF");
                        btn_BluetoothSwitch.setText("Bluetooth On");
                        btn_AddDevice.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        Toast.makeText(getApplicationContext(), "Turning off Bluetooth", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        btn_BluetoothSwitch.setText("Bluetooth Off");
                        btn_AddDevice.getBackground().setColorFilter(null);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
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
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
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
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND))
            {
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                bluetooth_ScanList.setAdapter(mDeviceListAdapter);
            }
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");

                    Intent startIntent = new Intent(getApplicationContext(), DeviceSettings.class);
                    Bundle extrasForDeviceSettings = new Bundle();

                    extrasForDeviceSettings.putString("DeviceAddress",mDevice.getAddress());
                    startIntent.putExtras(extrasForDeviceSettings);
                    startActivity(startIntent);

                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
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
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    public void enableDisableBT()
    {
        if(bluetoothAdapter == null)
        {
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
            Toast.makeText(getApplicationContext(), "Something is wrong with the Bluetooth", Toast.LENGTH_LONG).show();
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(bluetoothAdapter.isEnabled())
        {
            Log.d(TAG, "enableDisableBT: disabling BT.");
            bluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
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

    //The menu bar will show on the top right
    @Override
    public boolean onCreateOptionsMenu(Menu dot){
        getMenuInflater().inflate(R.menu.main, dot);
        return true;
    }

    //Selection for the menu bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id =item.getItemId();

        if (id==R.id.id_theme){
            Intent intentsetting = new Intent(MainActivity.this, Themes.class);
            startActivity(intentsetting);
            return true;
        }
        if (id==R.id.id_help){
            Intent intentHelp = new Intent(MainActivity.this,Help.class);
            startActivity(intentHelp);
            return true;
        }
        if (id==R.id.id_history){
            Intent intenthistory = new Intent(MainActivity.this,History.class);
            startActivity(intenthistory);
            return true;
        }
        return true;
    }

    public void AddData(String bName, String bTime, String bDate){
        boolean insertData = myDb.addData(bName,bTime,bDate);

        if(insertData==true){
            Toast.makeText(MainActivity.this,"Successfully Entered Data!",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.this,"Something went wrong :(",Toast.LENGTH_LONG).show();
        }
    }



}
