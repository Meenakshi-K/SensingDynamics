package com.example.corey.smelly;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by corey on 2015-10-17.
 */
public class CloudNose implements Runnable {

    private static final String TAG = "CloudNose";

    private BufferedWriter out = null;
    private BufferedReader  in = null;

    private ArrayList<String> dataSet = null;

    public CloudNose(ArrayList<String> d) {
        dataSet = new ArrayList<String>();

        for (String s : d) {
            dataSet.add(s);
        }
    }

    @Override
    public void run() {
        String host = "192.168.20.106";
        InetAddress addr = null;
        Socket s = null;

        Log.d(TAG, "run()");

        try {
            addr = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            Log.d(TAG, String.format("Could not find address of server %s", host));
            e.printStackTrace();
            return;
        }

        try {
            s = new Socket(addr, 8220);

            out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

            in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        } catch (IOException e) {
            Log.d(TAG, "Failed to create a socket");
            e.printStackTrace();
            return;
        }

        if (s.isConnected())
        {
            Log.d(TAG, "connected!");
        }

        //Check if we are connected to the right server
        String r = null;
        try {
            out.write("wemake");
            out.newLine();
            out.flush();

            r = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(r.contains("sense!")) {
            Log.d(TAG, "Communication with server established!");
        }
        else {
            return;
        }



        try {

            //try to send our data
            for(String d : dataSet) {
                Log.d(TAG, String.format("write(%s)", d));
                out.write(d);
                out.newLine();
            }
            out.write("compute");
            out.newLine();
            out.flush();



        } catch (IOException e) {
            e.printStackTrace();
        }



        try {
            int cnt = 0;
            int maxSeconds = 60;
            while (cnt < maxSeconds) //wait for a maximum of 60s
            {
                if (in.ready()) {
                    r = in.readLine();
                    Log.d(TAG, String.format("got [%s] from server", r));

                    if(r.contains("result:"))
                    {
                        // Here we should do something with the result
                        // Like showing the user a picture of fruit...
                        Log.d(TAG, "got our result from the server");
                        Log.d(TAG, r);
                        break;
                    }
                }
                else {
                    Log.d(TAG, String.format("sleeping... %d/%d seconds", cnt, maxSeconds));
                    Thread.sleep(1000, 0);
                }
                cnt++;
            }

            s.close();
            Log.d(TAG, "close()");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "finish run()");
    }
}
