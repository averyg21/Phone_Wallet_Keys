package com.example.averygrimes.phone_wallet_keys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DeviceSettings extends AppCompatActivity implements View.OnClickListener
{
    Button btn_DeviceSettings_OnOff, btn_DeviceSettings_Delete, btn_DeviceSettings_EditName, btn_DeviceSettings_Sound, btn_DeviceSettings_SetColor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_settings);

        btn_DeviceSettings_OnOff = (Button) findViewById(R.id.btn_DeviceSettings_OnOff);
        btn_DeviceSettings_Delete = (Button) findViewById(R.id.btn_DeviceSettings_Delete);
        btn_DeviceSettings_EditName = (Button) findViewById(R.id.btn_DeviceSettings_EditName);
        btn_DeviceSettings_Sound = (Button) findViewById(R.id.btn_DeviceSettings_Sound);
        btn_DeviceSettings_SetColor = (Button) findViewById(R.id.btn_DeviceSettings_SetColor);

        btn_DeviceSettings_OnOff.setOnClickListener(this);
        btn_DeviceSettings_Delete.setOnClickListener(this);
        btn_DeviceSettings_EditName.setOnClickListener(this);
        btn_DeviceSettings_Sound.setOnClickListener(this);
        btn_DeviceSettings_SetColor.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_DeviceSettings_OnOff:
            {

                break;
            }
            case R.id.btn_DeviceSettings_Delete:
            {

                break;
            }
            case R.id.btn_DeviceSettings_EditName:
            {


                break;
            }
            case R.id.btn_DeviceSettings_Sound:
            {


                break;
            }
            case R.id.btn_DeviceSettings_SetColor:
            {


                break;
            }
        }
    }
}
