package com.example.averygrimes.phone_wallet_keys;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.averygrimes.phone_wallet_keys.DeviceModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class SwipeRecyclerViewAdapter extends RecyclerSwipeAdapter<SwipeRecyclerViewAdapter.SimpleViewHolder>
{
    
    private Context mContext;
    private ArrayList<DeviceModel> deviceList;
    
    public SwipeRecyclerViewAdapter(Context context, ArrayList<DeviceModel> objects)
    {
        this.mContext = context;
        this.deviceList = objects;
    }
    
    
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_layout, parent, false);
        return new SimpleViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final DeviceModel item = deviceList.get(position);
        
        viewHolder.tvName.setText(item.getName());
        viewHolder.tvStatus.setText(item.getStatus());
        
        
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        
        //drag from left
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));
        
        //drag from right
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wraper));
        
        
        //handling different event when swiping
        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }
            
            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
            }
            
            @Override
            public void onStartClose(SwipeLayout layout) {
                
            }
            
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }
            
            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }
            
            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });
        
        viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(mContext, " onClick : " + item.getName() + " \n" + item.getStatus(), Toast.LENGTH_SHORT).show();
            }
        });

        //Settings
        viewHolder.btn_Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Toast.makeText(v.getContext(), "Clicked on Map " + viewHolder.tvName.getText().toString(), Toast.LENGTH_SHORT).show();

                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                Intent startIntent = new Intent(mContext, DeviceSettings.class);
                Bundle extrasForDeviceSettings = new Bundle();

                for(BluetoothDevice bt : pairedDevices)
                {
                    try {
                        Method method = bt.getClass().getMethod("getAliasName");
                        if(method != null)
                        {
                            if(((String)method.invoke(bt)).equals(viewHolder.tvName.getText().toString()))
                            {
                                extrasForDeviceSettings.putString("deviceAddress", bt.getAddress());
                                startIntent.putExtras(extrasForDeviceSettings);
                                mContext.startActivity(startIntent);
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        
        viewHolder.tvSnooze.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if(viewHolder.tvStatus.getText().toString().equals("Connected"))
                {
                    viewHolder.tvStatus.setText("Snoozed");
                    viewHolder.tvSnooze.setText("Unsnooze");
                }

            }
        });
        
        mItemManger.bindView(viewHolder.itemView, position);
    }
    
    @Override
    public int getItemCount()
    {
        return deviceList.size();
    }
    
    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
    
    public static class SimpleViewHolder extends RecyclerView.ViewHolder{
        public SwipeLayout swipeLayout;
        public TextView tvName;
        public TextView tvStatus;
        public TextView tvSnooze;
        public ImageButton btn_Settings;
        public SimpleViewHolder(View itemView)
        {
            super(itemView);
            
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvSnooze = (TextView) itemView.findViewById(R.id.tvSnooze);
            btn_Settings = (ImageButton) itemView.findViewById(R.id.btn_Settings);
        }
    }
}
