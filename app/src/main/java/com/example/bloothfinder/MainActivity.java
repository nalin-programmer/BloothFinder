package com.example.bloothfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    TextView statusTextView;
    Button searchButton;
    ArrayList<String> bluetoothDevices = new ArrayList<>(  );
    ArrayList<String>Addresses = new ArrayList<>(  );
    ArrayAdapter arrayAdapter;

    BluetoothAdapter bluetoothAdapter;

    public void searchClicked(View view){
        statusTextView.setText( "Searching...." );
        searchButton.setEnabled( false );
        bluetoothDevices.clear();
        Addresses.clear();
        bluetoothAdapter.startDiscovery();

    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("Action",action);

            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals( action )){
                statusTextView.setText( "Finished" );
                searchButton.setEnabled( true );
            }else if(BluetoothDevice.ACTION_FOUND.equals( action )){
                BluetoothDevice device = intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );
                String name =  device.getName();
                String address = device.getAddress();
                String rssi = Integer.toString( intent.getShortExtra( BluetoothDevice.EXTRA_RSSI ,Short.MIN_VALUE) );
                Log.i("Device Found","Name: "+name+" Address: "+address+" rssi "+rssi);

                if(!Addresses.contains( address )){
                    Addresses.add(address);
                    String deviceString="";
                    if(name==null || name.equals( "" )){

                        deviceString = (address+" - RSSI "+ rssi+"dBm");
                    }else{
                        deviceString = (name+" - RSSI "+ rssi+"dBm");
                    }

                    bluetoothDevices.add(deviceString);
                    arrayAdapter.notifyDataSetChanged();
                }


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        listView=findViewById( R.id.listView );
        statusTextView=findViewById( R.id.statusTextView );
        searchButton=findViewById( R.id.searchButton );

        arrayAdapter=new ArrayAdapter( this,android.R.layout.simple_list_item_1,bluetoothDevices  );
        listView.setAdapter( arrayAdapter );

        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        IntentFilter intentFilter = new IntentFilter(  );
        intentFilter.addAction( BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED );
        intentFilter.addAction( BluetoothDevice.ACTION_FOUND );
        intentFilter.addAction( BluetoothAdapter.ACTION_DISCOVERY_STARTED );
        intentFilter.addAction( BluetoothAdapter.ACTION_DISCOVERY_FINISHED );
        registerReceiver( broadcastReceiver,intentFilter );


    }
}