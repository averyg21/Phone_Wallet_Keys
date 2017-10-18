package com.example.averygrimes.phone_wallet_keys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class Setting extends AppCompatActivity implements View.OnClickListener
{

    Button btn_Settings_Themes, btn_Settings_Notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btn_Settings_Themes = (Button) findViewById(R.id.btn_Settings_Themes);
        btn_Settings_Notifications = (Button) findViewById(R.id.btn_Settings_Notifications);

        btn_Settings_Themes.setOnClickListener(this);
        btn_Settings_Notifications.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_Settings_Themes:
            {

                break;
            }
            case R.id.btn_Settings_Notifications:
            {

                break;
            }
        }
    }
}
