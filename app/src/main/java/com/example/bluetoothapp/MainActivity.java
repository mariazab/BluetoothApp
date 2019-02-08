package com.example.bluetoothapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    //private int PERMISSION_REQUEST_CODE = 10;

    private BluetoothAdapter bluetoothAdapter;

    TextView listLabel;
    ListView devicesListView;

    ArrayList<String> pairedDevices = new ArrayList<String>();
    ArrayList<String> nearbyDevices = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listLabel = findViewById(R.id.listLabel);
        devicesListView = findViewById(R.id.devicesListView);


        //Check permissions
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
//        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }

        //If bluetooth is off, ask to turn it on
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);


    }


    //Find paired devices
    public void findPairedDevices(View view) {
        Set<BluetoothDevice> devicesSet = bluetoothAdapter.getBondedDevices();

        if (devicesSet.size() > 0) {
            // Get the name and address of each paired device and add to the array.
            for (BluetoothDevice device : devicesSet) {

                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                String deviceString = deviceName + " " + deviceHardwareAddress;

                Log.i("MainActivity", "Device: " + deviceString);
                pairedDevices.add(deviceString);

            }
        }

        //Set the adapter with the correct array
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, pairedDevices);


        // Assign adapter to ListView
        devicesListView.setAdapter(adapter);

        //Set label for the list
        listLabel.setText("Paired Devices:");
    }


    //Find nearby devices
    public void discoverDevices(View view) {

        //If the devices is already discovering, cancel it
        if(bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();

    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent. Add to the array
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                String deviceString = deviceName + " " + deviceHardwareAddress;

                Log.i("MainActivity", "Device: " + deviceString);
                nearbyDevices.add(deviceString);

            }

            //Set the adapter with the correct array
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_list_item_1, android.R.id.text1, nearbyDevices);


            // Assign adapter to ListView
            devicesListView.setAdapter(adapter);

            //Set label for the list
            listLabel.setText("Nearby Devices: ");

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    //Clear the lists and the arrays
    public void clearList(View view){
        pairedDevices.clear();
        nearbyDevices.clear();

        devicesListView.setAdapter(null);

    }
}
