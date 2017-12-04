package com.example.averygrimes.phone_wallet_keys;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.AlertDialog;

public class DeviceSettings extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "DeviceSettings";
    Button btn_DeviceSettings_OnOff, btn_DeviceSettings_Delete, btn_DeviceSettings_EditName, btn_DeviceSettings_Notification, btn_DeviceSettings_SnoozeTimer;

    BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothDevice> connectedDevice;
    int connectedDevicesCounter;
    String deviceAddress;

    // Used to connect to the bluetooth device
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("0000110E-0000-1000-8000-00805F9B34FB");

    Handler connectToDevice;

    // Used for sending notification to phone
    NotificationCompat.Builder notification;
    private static final int uniqueID = 45612;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_settings);

        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Bundle extrasForDeviceSettings = getIntent().getExtras();

        connectedDevice = new ArrayList<>();
        connectedDevicesCounter = extrasForDeviceSettings.getInt("connectedDevicesCounter");
        connectedDevice = extrasForDeviceSettings.getParcelableArrayList("connectedDevice");
        deviceAddress = extrasForDeviceSettings.getString("deviceAddress");

        btn_DeviceSettings_OnOff = (Button) findViewById(R.id.btn_DeviceSettings_OnOff);
        btn_DeviceSettings_Delete = (Button) findViewById(R.id.btn_DeviceSettings_Delete);
        btn_DeviceSettings_EditName = (Button) findViewById(R.id.btn_DeviceSettings_EditName);
        btn_DeviceSettings_Notification = (Button) findViewById(R.id.btn_DeviceSettings_Notification);
        btn_DeviceSettings_SnoozeTimer = (Button) findViewById(R.id.btn_DeviceSettings_SnoozeTimer);

        btn_DeviceSettings_OnOff.setOnClickListener(this);
        btn_DeviceSettings_Delete.setOnClickListener(this);
        btn_DeviceSettings_EditName.setOnClickListener(this);
        btn_DeviceSettings_Notification.setOnClickListener(this);
        btn_DeviceSettings_SnoozeTimer.setOnClickListener(this);
    }

    //used to generate notification
    public void createNotification()
    {
        //Build the notification
        //notification.setSmallIcon(R.drawable.oreo);
        notification.setTicker("This is the ticker");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Lost Device Title");
        notification.setContentText("Body of the notification");

        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
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
            case R.id.btn_DeviceSettings_OnOff:
            {
                try
                {
                    connectToDevice.removeCallbacksAndMessages(null);
                }
                catch (Exception ex)
                {
                    Log.d(TAG, "Post Delay Error: " + ex);
                }

                connectToDevice = new Handler();

                connectToDevice.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {

                        if(bluetoothDevice.getBondState()==bluetoothDevice.BOND_BONDED)
                        {
                            Log.d(TAG, bluetoothDevice.getName());
                            BluetoothSocket socket = null;

                            try
                            {
                                socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                            }
                            catch (IOException e1)
                            {
                                // TODO Auto-generated catch block
                                Log.d(TAG, "socket not created");
                                e1.printStackTrace();
                            }

                            try
                            {
                                socket.connect();
                                Log.e("","Connected");
                                socket.close();
                            }
                            catch (IOException e)
                            {
                                Log.e("",e.getMessage());

                                try
                                {
                                    Log.e("","trying fallback...");

                                    socket =(BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(bluetoothDevice,1);
                                    socket.connect();

                                    Log.e("","Connected");
                                    socket.close();
                                }
                                catch (Exception e2)
                                {
                                    Log.e("", "Couldn't establish Bluetooth connection!");
                                    createNotification();
                                    connectToDevice.removeCallbacks(this);
                                }
                            }
                        }
                        connectToDevice.postDelayed(this, 5000);
                    }
                }, 5000);

                break;
            }
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
                Intent startIntent = new Intent(getApplicationContext(), NotificationsList.class);
                startActivity(startIntent);

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
        Context context = this.getApplicationContext();
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("connectedDevices", connectedDevice);
        intent.putExtra("connectedDevicesCounter", connectedDevicesCounter);
        context.startActivity(intent);
    }
}
