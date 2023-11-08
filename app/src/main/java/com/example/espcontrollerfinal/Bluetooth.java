package com.example.espcontrollerfinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class Bluetooth extends AppCompatActivity {
    

    Toolbar BluetoothToolBar;
    BluetoothAdapter bluetoothAdapter;
    ToggleButton tgl_Btn_On_Of;
    Button btn_lst_devices;
    TextView txt_status , txt_lst ;
    ImageView im_status ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        // get the element from xml view
        BluetoothToolBar = (Toolbar) findViewById(R.id.Bluetooth_toolbar);
        setSupportActionBar(BluetoothToolBar);
        tgl_Btn_On_Of = (ToggleButton) findViewById(R.id.tglbtn_bt_onof);
        btn_lst_devices = (Button) findViewById(R.id.btn_lst);
        txt_status = (TextView) findViewById(R.id.txt_Bt_status);
        txt_lst = (TextView) findViewById(R.id.txt_lst_bt);
        im_status = (ImageView) findViewById(R.id.IM_status);

        // Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //check whether bluetooth is available or not 
        if (bluetoothAdapter == null) {
            txt_status.setText("Bluetooth Is Not Available");
        } else {
            txt_status.setText("Bluetooth Is Available");
        }

        // set Image according to Bluetooth status

        if (bluetoothAdapter.isEnabled()){
            im_status.setImageResource(R.drawable.ic_bluetooth_enable);
            tgl_Btn_On_Of.setChecked(false);

        }
        else {
            im_status.setImageResource(R.drawable.ic_bluetooth_disable);
            tgl_Btn_On_Of.setChecked(true);
        }
        tgl_Btn_On_Of.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tgl_Btn_On_Of.isChecked()){
                    bluetoothAdapter.disable();
                    im_status.setImageResource(R.drawable.ic_bluetooth_disable);
                    showToast("Disabling Bluetooth");
                }
                else{
                    bluetoothAdapter.enable();
                    im_status.setImageResource(R.drawable.ic_bluetooth_enable);
                    showToast("Enabling Bluetooth");
                }

            }
        });

        btn_lst_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });







    }

    private void showToast(String msg){

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bluetooth_actions, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_bluetooth:
                startActivity(new Intent(this, MainActivity.class));

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}