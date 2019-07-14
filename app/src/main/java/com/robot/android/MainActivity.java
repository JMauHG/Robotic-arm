package com.robot.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.physicaloid.lib.Boards;
import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.programmer.avr.UploadErrors;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    private Button btOpen, btClose, btLeft, btRight, btRead, btVel;
    private TextView tvRead;
    private Spinner spPWM;

    private RecyclerView rvRead;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> list;

    private int counter = 0;

    private Physicaloid mPhysicaloid; 		// initialising library

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = this.getAllList();

        btOpen  = (Button) findViewById(R.id.btOpen);
        btClose = (Button) findViewById(R.id.btClose);

        btLeft = (Button) findViewById(R.id.btLeft);
        btRight = (Button) findViewById(R.id.btRight);
        btRead = (Button) findViewById(R.id.btRead);
        btVel = (Button) findViewById(R.id.btVel);

        tvRead  = (TextView) findViewById(R.id.tvRead);
        rvRead = findViewById(R.id.rvRead);

        spPWM = (Spinner) findViewById(R.id.spPWM);

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MyAdapter(list, R.layout.text_view, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String data, int position) {
                deleteData(position);
            }
        });

        rvRead.setHasFixedSize(true);
        rvRead.setItemAnimator(new DefaultItemAnimator());

        rvRead.setLayoutManager(mLayoutManager);
        rvRead.setAdapter(mAdapter);

        mPhysicaloid = new Physicaloid(this);
        mPhysicaloid.upload(Boards.ARDUINO_UNO, "/sdcard/arduino/Blink.hex");

        setEnabledUi(false);

        btLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    String left = "l";	//get text from EditText
                    byte[] buf = left.getBytes();	//convert string to byte array
                    mPhysicaloid.write(buf, buf.length);	//write data to arduinotextView.setText("Button Pressed");
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    String left = "s";	//get text from EditText
                    byte[] buf = left.getBytes();	//convert string to byte array
                    mPhysicaloid.write(buf, buf.length);	//write data to arduinotextView.setText("Button Pressed");
                }
                return true;
            }
        });

        btRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    String left = "r";	//get text from EditText
                    byte[] buf = left.getBytes();	//convert string to byte array
                    mPhysicaloid.write(buf, buf.length);	//write data to arduinotextView.setText("Button Pressed");
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    String left = "s";	//get text from EditText
                    byte[] buf = left.getBytes();	//convert string to byte array
                    mPhysicaloid.write(buf, buf.length);	//write data to arduinotextView.setText("Button Pressed");
                }
                return true;
            }
        });

        btRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // read listener, When new data is received from Arduino add it to Text view
                mPhysicaloid.addReadListener(new ReadLisener() {
                    @Override
                    public void onRead(int size) {
                        byte[] buf = new byte[size];
                        mPhysicaloid.read(buf, size);
                        tvAppend(tvRead, new String(buf)); 		// add data to text viiew
                    }
                });

            }
        });

        btVel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vel = spPWM.getSelectedItem().toString(); 	// get the text from spinner
                //switch statement to check for baud rate
                switch (vel) {
                    case "25%":
                        String vela = "p63";	//get text from EditText
                        byte[] bufa = vela.getBytes();	//convert string to byte array
                        mPhysicaloid.write(bufa, bufa.length);	//write data to arduino;
                        break;
                    case "50%":
                        String velb = "p127";	//get text from EditText
                        byte[] bufb = velb.getBytes();	//convert string to byte array
                        mPhysicaloid.write(bufb, bufb.length);	//write data to arduino;
                        break;
                    case "75%":
                        String velc = "p190";	//get text from EditText
                        byte[] bufc = velc.getBytes();	//convert string to byte array
                        mPhysicaloid.write(bufc, bufc.length);	//write data to arduino;
                        break;
                    case "100%":
                        String veld = "p255";	//get text from EditText
                        byte[] bufd = veld.getBytes();	//convert string to byte array
                        mPhysicaloid.write(bufd, bufd.length);	//write data to arduino;
                        break;
                }
            }
        });

        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhysicaloid.setBaudrate(9600);

                if(mPhysicaloid.open()) { 	// tries to connect to device and if device was connected
                    setEnabledUi(true);

                } else {
                    //Error while connecting
                    Toast.makeText(MainActivity.this, "Cannot open", Toast.LENGTH_LONG).show();
                }
            }
        });

        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPhysicaloid.close()) { 	//close the connection to arduino
                    setEnabledUi(false);	// set UI accordingly
                    //****************************************************************
                    // TODO : clear read callback
                    mPhysicaloid.clearReadListener();
                    //****************************************************************
                }
            }
        });

    }
    //****************************************************************


    //setEnabledUi method to set UI elements on screen
    private void setEnabledUi(boolean on) {
        if(on) {                            // if connected to device
            btOpen.setEnabled(false);       //hide open button (already opened)
            btClose.setEnabled(true);       // display close button
            btLeft.setEnabled(true);       // display left button
            btRight.setEnabled(true);       // display right button
            btRead.setEnabled(true);       // display read button
            btVel.setEnabled(true);      // hide choose speed button
            spPWM.setEnabled(true);      // hide speed spinner

        } else {                            // if not connected to device
            btOpen.setEnabled(true);        //display open button
            btClose.setEnabled(false);      // hide close button
            btLeft.setEnabled(false);      // hide left button
            btRight.setEnabled(false);      // hide right button
            btRead.setEnabled(false);      // hide read button
            btVel.setEnabled(false);      // hide choose speed button
            spPWM.setEnabled(false);      // hide speed spinner
        }
    }


    private List<String> getAllList() {
        return new ArrayList<String>() {{
        }};
    }
    Handler mHandler = new Handler();
    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                addData(list.size(),"Angular position = " + ftext + "\n");
                //ftv.append("Angular position = " + ftext + "°" + "\n"); 	// add text to Text view
            }
        });
    }

    private void deleteData(int position) {
        list.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    private void addData(int position, String str) {
        list.add(position, str );
        mAdapter.notifyItemInserted(position);
        mLayoutManager.scrollToPosition(position);

    }
}
