package com.example.averygrimes.phone_wallet_keys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class Themes extends AppCompatActivity{
    Button btn_UHCL, btn_Dark, btn_Beach, btn_Fall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);



        btn_UHCL =(Button)findViewById(R.id.btn_UHCL);
        btn_Dark =(Button)findViewById(R.id.btn_Dark);
        btn_Beach=(Button)findViewById(R.id.btn_Beach);
        btn_Fall =(Button)findViewById(R.id.btn_Fall);


        btn_UHCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String t = "1";
                int b = Integer.parseInt(t);
                Intent myIntent = new Intent(Themes.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

        btn_Dark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String t = "2";
                int b = Integer.parseInt(t);
                Intent intent = new Intent(Themes.this, MainActivity.class);
                intent.putExtra("name", b);
                startActivity(intent);

            }
        });

        btn_Beach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String t = "3";
                int b = Integer.parseInt(t);
                Intent intent = new Intent(Themes.this, MainActivity.class);
                intent.putExtra("name", b);
                startActivity(intent);

            }
        });

        btn_Fall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String t = "4";
                int b = Integer.parseInt(t);
                Intent intent = new Intent(Themes.this, MainActivity.class);
                intent.putExtra("name", b);
                startActivity(intent);

            }
        });


    }


}

