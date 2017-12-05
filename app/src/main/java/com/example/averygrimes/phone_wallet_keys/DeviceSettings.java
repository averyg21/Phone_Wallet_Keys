package com.example.averygrimes.phone_wallet_keys;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;
import java.util.UUID;
import android.app.AlertDialog;
import android.widget.Switch;


public class DeviceSettings extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "DeviceSettings";
    Button btn_DeviceSettings_OnOff, btn_DeviceSettings_Delete, btn_DeviceSettings_EditName, btn_DeviceSettings_Notification, btn_DeviceSettings_SnoozeTimer;


    BluetoothAdapter bluetoothAdapter;
    int connectedDevicesCounter;
    String deviceAddress;
    Stack<String> stack;
    Thread myThread;
    boolean shouldContinue;

    // Used to connect to the bluetooth device
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Handler handler;
    Uri uriSound;

    // Used for sending notification to phone
    NotificationCompat.Builder notification;
    private static final int uniqueID = 45612;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_settings);


        myThread = new Thread(connectToDevice);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Bundle extrasForDeviceSettings = getIntent().getExtras();

        deviceAddress = extrasForDeviceSettings.getString("deviceAddress");

        if(extrasForDeviceSettings.containsKey("NotificationSound"))
        {
            uriSound = extrasForDeviceSettings.getParcelable("NotificationSound");
        }

        final BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

        getSupportActionBar().setTitle(bluetoothDevice.getName());

        btn_DeviceSettings_Delete = (Button) findViewById(R.id.btn_DeviceSettings_Delete);
        btn_DeviceSettings_EditName = (Button) findViewById(R.id.btn_DeviceSettings_EditName);
        btn_DeviceSettings_Notification = (Button) findViewById(R.id.btn_DeviceSettings_Notification);
        btn_DeviceSettings_SnoozeTimer = (Button) findViewById(R.id.btn_DeviceSettings_SnoozeTimer);

        btn_DeviceSettings_Delete.setOnClickListener(this);
        btn_DeviceSettings_EditName.setOnClickListener(this);
        btn_DeviceSettings_Notification.setOnClickListener(this);
        btn_DeviceSettings_SnoozeTimer.setOnClickListener(this);

        shouldContinue = true;
    }

    public Runnable connectToDevice = new Runnable()
    {
        @Override
        public void run() {
            final BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

            while (shouldContinue) {
                Log.d(TAG, bluetoothDevice.getName());
                BluetoothSocket socket = null;

                try {
                    socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                } catch (IOException e1) {
                    Log.d(TAG, "socket not created");
                    e1.printStackTrace();
                }

                try {
                    socket.connect();
                    Log.e("", "Connected");
                } catch (IOException e) {
                    Log.e("", e.getMessage());

                    try {
                        Log.e("", "trying fallback...");

                        socket = (BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(bluetoothDevice, 1);
                        socket.connect();
                        Log.e("", "Connected");
                    } catch (Exception e2) {
                        createNotification();

                        Log.d("", "Couldn't establish Bluetooth connection!: " + e2);
                        return;
                    }
                }


                try {
                    socket.close();
                } catch (Exception ex) {

                }

                try {
                    Thread.sleep(10000);
                } catch (Exception ex)
                {
                    return;
                }
            }
        }

    };

    public void StopThread()
    {
        myThread.stop();
    }

    //The menu bar will show on the top right
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu, menu);

        MenuItem itemSwitch = menu.findItem(R.id.myswitch);
        itemSwitch.setActionView(R.layout.use_switch);

        final Switch sw = (Switch) menu.findItem(R.id.myswitch).getActionView().findViewById(R.id.action_switch);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                final BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

                if(isChecked)
                {



                    //Intent startIntent = new Intent(getApplicationContext(), Connect.class);
                    //startIntent.putExtra("deviceAddress", deviceAddress);
                    //getApplicationContext().startActivity(startIntent);
                    myThread = new Thread(connectToDevice);
                    myThread.start();
                }
                else
                {

                }
            }
        });
        return true;
    }

    //used to generate notification
    public void createNotification()
    {
        if(uriSound == null)
        {
            uriSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        else
        {

        }

        Intent intent = new Intent(this,DeviceSettings.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, 0);

        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
        notification.setSound(uriSound);

        //Build the notification
        notification.setSmallIcon(R.drawable.oreo);
        notification.setTicker("This is the ticker");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Lost Device Title");
        notification.setContentText("Body of the notification");

        notification.setContentIntent(pendingIntent);

        //Build notification and issues it
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());
    }

    @Override
    public void onClick(View view)
    {
        final BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

        switch (view.getId())
        {
            case R.id.btn_DeviceSettings_Delete:
            {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
                a_builder.setMessage("Are you sure you want to delete this device?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                try {
                                    Method method = bluetoothDevice.getClass().getMethod("removeBond", (Class[]) null);
                                    method.invoke(bluetoothDevice, (Object[]) null);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(startIntent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                dialog.cancel();
                            }
                        });
                AlertDialog alert = a_builder.create();
                alert.show();

                break;
            }
            case R.id.btn_DeviceSettings_EditName:
            {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(DeviceSettings.this);
                View promptsView = li.inflate(R.layout.input_dialog, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        DeviceSettings.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id)
                                    {
                                        String result = userInput.getText().toString();


                                        try {
                                            Method method = bluetoothDevice.getClass().getMethod("setAlias", String.class);
                                            if(method != null) {
                                                method.invoke(bluetoothDevice, result);
                                            }
                                        } catch (NoSuchMethodException e) {
                                            e.printStackTrace();
                                        } catch (InvocationTargetException e) {
                                            e.printStackTrace();
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                break;
            }
            case R.id.btn_DeviceSettings_Notification:
            {
                Context context = this.getApplicationContext();
                Intent startIntent = new Intent(getApplicationContext(), NotificationsList.class);
                startIntent.putExtra("deviceAddress", deviceAddress);
                context.startActivity(startIntent);

                break;
            }
            case R.id.btn_DeviceSettings_SnoozeTimer:
            {


                break;
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        final Switch sw = (Switch) findViewById(R.id.action_switch);

        Context context = this.getApplicationContext();
        Intent intent = new Intent(context, MainActivity.class);

        if(sw.isChecked())
        {
            intent.putExtra("deviceAddress", deviceAddress);
        }

        if(uriSound != null)
        {
            intent.putExtra("NotificationSound", uriSound);
        }

        context.startActivity(intent);
        //finish();
    }
}


