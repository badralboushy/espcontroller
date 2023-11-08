package com.example.espcontrollerfinal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothDevices extends AppCompatActivity {
    private final static String TAG ="BluetoothDevicesTAG" ;
    private static final String MESSAG_ID = "BT_MESSAGE" ;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Toolbar toolbar;
    TextView txt_bt_status;
    ImageView img_bt_status;
    BluetoothDevice device;
    BluetoothSocket sock;
    BluetoothSocket sockFallback ;
  
   public static String DeviceAddress ;
    ArrayList deviceStrs ;
    final ArrayList devices = new ArrayList();
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private String bluetoothKey ="bluetoothKey";

   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);
        Log.d(TAG, "onCreate: start");
        toolbar= (Toolbar)findViewById(R.id.BluetoothDevices_toolbar);
        setSupportActionBar(toolbar);
        txt_bt_status = (TextView)findViewById(R.id.txt_bt_status);
        img_bt_status = (ImageView)findViewById(R.id.img_bt_status);
        deviceStrs = new ArrayList();

        Description();

        getAllDeviceAddress();

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bluetooth_devices_list, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_Bluetooth:{
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
                return true; }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getAllDeviceAddress(){

               Set<BluetoothDevice> pairedDevices=bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                deviceStrs.add(device.getName() +    "\n"+device.getAddress());
                devices.add(device.getAddress());
            }

        }

        showDeviceSelecterDialog(deviceStrs, devices);
    }


    private void showDeviceSelecterDialog(ArrayList deviceStrs
            , ArrayList devices) {
        // show list
        final AlertDialog.Builder alertDialog =
                new AlertDialog.Builder(this);

        ArrayAdapter adapter =
                new ArrayAdapter(this,
                        android.R.layout.select_dialog_singlechoice,
                        deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialog.setSingleChoiceItems(adapter, -1,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        int position = ((AlertDialog) dialog)
                                .getListView()
                                .getCheckedItemPosition();
                        new BluetoothDevices().DeviceAddress = (String) devices.get(position);

//                        SharedPreferences.Editor editor = getSharedPreferences(MESSAG_ID,MODE_PRIVATE).edit();
//                        editor.putString(bluetoothKey, deviceAddress).apply();
//                         SharedPreferences sharedPreferences = getSharedPreferences(MESSAG_ID,MODE_PRIVATE);
//                        String temp =sharedPreferences.getString(bluetoothKey,null);
//                        Log.d(TAG, "onClick: "+temp);
                        try {
                            new BluetoothDevices().startConnection();
                        } catch (IOException e) {
                            Log.d(TAG, "onClick: after startConnection: "+ e.toString()); 
                        }
                    }
                });

        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();
    }

   
   
    private  void startConnection() throws IOException {

        // get the remote Bluetooth device

//        SharedPreferences getSharedData = getSharedPreferences(MESSAG_ID,MODE_PRIVATE);
//
//        if ( getSharedData == null)
//        {
//            Log.d(TAG, "startConnection: sharedPref is null ");
//        }
        Log.d(TAG, "startConnection: 2");

        String remoteDevice = DeviceAddress;
        Log.d(TAG, "startConnection: DeviceAddress:" + remoteDevice);
        if (remoteDevice == null || "".equals(remoteDevice)) {
            
            // log error
            Log.d(TAG, "No Bluetooth device has been selected.");

        } else {


            device = bluetoothAdapter.getRemoteDevice(remoteDevice);

            /* * Establish Bluetooth connection * */
            Log.d(TAG, "startConnection:Stopping Bluetooth discovery.");
            bluetoothAdapter.cancelDiscovery();

            try {
                Log.d(TAG, "startConnection: befor sock connect");
                // Instantiate a BluetoothSocket for the remote
                // device and connect it.
                sock = device.createRfcommSocketToServiceRecord(MY_UUID);
                sock.connect();


            } catch (Exception e1) {
                Log.d("startConnection", "There was an error"+
                        ",Falling back..", e1);
                Class<?> clazz = sock.getRemoteDevice().getClass();
                Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
                try {

                    /************Fallback method 1*********************/
                    Method m = clazz.getMethod(
                            "createRfcommSocket"
                            , paramTypes
                    );
                    Object[] params = new Object[]{Integer.valueOf(1)};
                    sockFallback = (BluetoothSocket) m.invoke(
                            sock.getRemoteDevice()
                            , params
                    );
                    sockFallback.connect();
                    sock = sockFallback;

                    Log.d("", "Connected");

                } catch (Exception e2) {
                    Log.d("startConnection", "Stopping app..", e2);
                    throw new IOException();
                }
            }
            Log.d(TAG, "connected");
            
            for ( int i = 0 ; i< 10 ; i++)
            {
                write("hello world \n");
                Log.d(TAG, "startConnection: inside loop");
                
            }

            String read = readRawData(sock.getInputStream(),'.') ;
            Log.d(TAG, "startConnection: recivedData"+read);
        }
    }

    private void write(String data) throws IOException {
        sock.getOutputStream().write(data.getBytes());
    }


    public String readRawData(InputStream in, char c ) throws IOException {
        byte b = 0;
        StringBuilder res = new StringBuilder();
        Log.d(TAG, "readRawData: before while true");

        while(true)
        {
            b = (byte) in.read();
            if(b == -1)  break;                 // reach the end of the stream

            if((char)b == c)  break;
            res.append((char)b);
            Log.d(TAG, "readRawData: inside loop " + res );
}
return res.toString();
        }


    private void Description(){
        if (bluetoothAdapter.isEnabled()){
            txt_bt_status.setText("paired devices :" );
            img_bt_status.setImageResource(R.drawable.ic_bluetooth_enable);

        }
        else {
            txt_bt_status.setText("Bluetooth is Not Enabled" );
            img_bt_status.setImageResource(R.drawable.ic_bluetooth_disable);
        }
    }

}