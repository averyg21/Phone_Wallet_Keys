package com.example.averygrimes.phone_wallet_keys;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import android.app.AlertDialog;

public class DeviceSettings extends AppCompatActivity implements View.OnClickListener
{
    Button btn_DeviceSettings_OnOff, btn_DeviceSettings_Delete, btn_DeviceSettings_EditName, btn_DeviceSettings_Notification, btn_DeviceSettings_SnoozeTimer;

    BluetoothAdapter bluetoothAdapter;
    private ArrayList<DeviceModel> connectedDeviceList;
    Bundle extrasForDeviceSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_settings);

        connectedDeviceList = new ArrayList<>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        extrasForDeviceSettings = getIntent().getExtras();








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

    @Override
    public void onClick(View view)
    {
        String deviceAddress = extrasForDeviceSettings.getString("DeviceAddress");
        final BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

        switch (view.getId())
        {
            case R.id.btn_DeviceSettings_OnOff:
            {

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
        Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(startIntent);
    }
}
