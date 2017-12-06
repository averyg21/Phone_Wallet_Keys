package com.example.averygrimes.phone_wallet_keys;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import android.app.AlertDialog;
import android.widget.Switch;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;


public class DeviceSettings extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "DeviceSettings";
    Button btn_DeviceSettings_Delete, btn_DeviceSettings_EditName, btn_DeviceSettings_Notification, btn_DeviceSettings_SnoozeTimer;


    BluetoothAdapter bluetoothAdapter;
    String deviceAddress;
    Thread myThread;
    String connectedDeviceAddress;

    // Used to connect to the bluetooth device
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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

        if(extrasForDeviceSettings.containsKey("ConnectedDeviceAddress"))
        {
            connectedDeviceAddress = extrasForDeviceSettings.getString("ConnectedDeviceAddress");
        }

        final BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

        getSupportActionBar().setTitle(bluetoothDevice.getName());

        btn_DeviceSettings_Delete = (Button) findViewById(R.id.btn_DeviceSettings_Delete);
        btn_DeviceSettings_EditName = (Button) findViewById(R.id.btn_DeviceSettings_EditName);
        btn_DeviceSettings_Notification = (Button) findViewById(R.id.btn_DeviceSettings_Notification);

        btn_DeviceSettings_Delete.setOnClickListener(this);
        btn_DeviceSettings_EditName.setOnClickListener(this);
        btn_DeviceSettings_Notification.setOnClickListener(this);
    }

    public Runnable connectToDevice = new Runnable()
    {
        @Override
        public void run()
        {
            final BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

            while (true)
            {
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
                    Thread.sleep(5000);
                } catch (Exception ex)
                {
                    return;
                }
            }
        }

    };

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

                if(bluetoothAdapter.isEnabled())
                {
                    if(isChecked)
                    {
                        myThread = new Thread(connectToDevice);
                        myThread.start();
                        connectedDeviceAddress = deviceAddress;
                    }
                    else
                    {
                        myThread.interrupt();
                        connectedDeviceAddress = null;
                    }
                }
                }
                //else
                //{

                //}
            //}
        });
        return true;
    }

    //used to generate notification
    public void createNotification()
    {
        String content = "";
        File file = getFileStreamPath("NotificationSound.txt");
        String[] notificationList = new String[1];

        try
        {
            if (!file.exists())
            {
                uriSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            else
            {
                FileInputStream reader = openFileInput(file.getName());

                byte[] input = new byte[reader.available()];
                while (reader.read(input) != -1) {}

                content += new String(input);

                notificationList = content.split(",");

                String[] temp;
                for(int i = 0; i < notificationList.length; i++)
                {
                    temp = notificationList[i].split("\\|");

                    if(temp[0].equals(deviceAddress))
                    {
                        uriSound = Uri.parse(temp[1]);
                    }
                }
            }

        }
        catch (IOException e)
        {
            Log.e("Exception", "File Read failed: " + e.toString());
        }

        Intent intent = new Intent(this,DeviceSettings.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, 0);

        //Build the large notification
        Drawable drawable= ContextCompat.getDrawable(this,R.drawable.logoteam);

        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
        notification.setSound(uriSound);

        //Build the notification
        notification.setSmallIcon(R.drawable.oreo);
        notification.setTicker("This is the ticker");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Lost Device Title");
        notification.setContentText("Body of the notification");
        notification.setLargeIcon(bitmap);
        notification.setContentIntent(pendingIntent);
        long[] pattern = {500,500,500,500,500,500,500,500,500};
        notification.setVibrate(pattern);

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

                                final Switch sw = (Switch) findViewById(R.id.action_switch);

                                Context context = getBaseContext();
                                Intent intent = new Intent(context, MainActivity.class);

                                if(connectedDeviceAddress != null)
                                {
                                    //intent.putExtra("ConnectedDeviceAddress", connectedDeviceAddress);
                                    context.startActivity(intent);
                                }
                                else
                                {
                                    context.startActivity(intent);
                                }
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
            intent.putExtra("ConnectedDeviceAddress", connectedDeviceAddress);
            context.startActivity(intent);
        }
        else
        {
            finish();
        }
    }
}


