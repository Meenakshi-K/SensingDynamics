package com.example.corey.smelly;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.corey.smelly.ElectronicNose;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */


                ElectronicNose enose = new ElectronicNose("98:76:B6:00:6A:BA");

                int con = enose.connect();

                if (con == -1) {
                    Log.d(TAG, "Failed to connect");
                    return;
                }

                String v = enose.getVersionString();

                Log.d(TAG, "Got version string : [" + v + "]");

                //before we collect the data, lets configure the timing
                enose.setBaselineTime(1000);
                enose.setSampleTime(1000);
                enose.setPurgeTime(1000);
                enose.setSettleTime(1000);

                //a list of strings containing all the data points we are interested in.
                ArrayList<String> td = enose.collectData();

                Log.d(TAG, String.format("Collected %d samples", td.size()));

                enose.disconnect();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
