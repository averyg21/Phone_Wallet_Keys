package com.example.averygrimes.phone_wallet_keys;

import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;


public class History extends AppCompatActivity {

    Database mydb;
    ArrayList<User> userList;
    ListView listView;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mydb = new Database(this);

        userList = new ArrayList<>();
        Cursor data = mydb.getListContents();
        int numRows = data.getCount();

        if(numRows == 0){
            Toast.makeText(History.this, "There is nothing in this database", Toast.LENGTH_LONG).show();
        }
        else {
            while (data.moveToNext()) {
                user = new User(data.getString(0),data.getString(1),data.getString(2));
                userList.add(user);
            }
            ThreeColum_ListAdapter adapter = new ThreeColum_ListAdapter(this, R.layout.device_view_layout, userList);
            listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);
        }

    }


}
