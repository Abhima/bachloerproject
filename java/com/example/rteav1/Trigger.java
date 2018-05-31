package com.example.rteav1;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Trigger extends Service implements View.OnClickListener{

    WindowManager windowManager;
    WindowManager.LayoutParams params;
    LayoutInflater mInflater;

    Button btnClose, btnTrigger;

    ConnectivityManager connectivityManager;

    Method dataMtd;

    Intent intent;

    View tView;

    SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_FILENAME, MODE_PRIVATE);
        connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        tView = mInflater.inflate(R.layout.trigger_layout, null);
        btnClose = (Button)tView.findViewById(R.id.close);
        btnTrigger = (Button)tView.findViewById(R.id.helpMeTrigger);

        btnClose.setOnClickListener(this);
        btnTrigger.setOnClickListener(this);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |   WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        |   WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        |   WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        |   WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        |   WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;

        windowManager.addView(tView, params);

        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManager.removeView(tView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:

                LayoutInflater layoutInflater = LayoutInflater.from(Trigger.this);
                View promptView = layoutInflater.inflate(R.layout.pin_check_disable_trigger, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Trigger.this);
                alertDialogBuilder.setView(promptView);

                final EditText pinText = (EditText)promptView.findViewById(R.id.txtDisableTrigger);

                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String pinSP = sharedPreferences.getString(Config.SP_PinCode, "");


                                if (pinText.getText().toString().equals(pinSP)){
                                    startService(new Intent(Trigger.this, TriggerOption.class));
                                    stopService(new Intent(Trigger.this, LocationService.class));
                                    stopSelf();
                                    turnOffGPSandDaya();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Invalid Pin code. Close Again!!", Toast.LENGTH_LONG).show();
                                }



                            }
                        });
                alertDialogBuilder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                //Showing the alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
                alertDialog.show();





            break;

            case R.id.helpMeTrigger:

                turnOnGPSandData();


                Toast.makeText(getApplicationContext(), "Trigger pressed", Toast.LENGTH_SHORT).show();
                startService(new Intent(this, LocationService.class));

                break;
        }
    }

    public void turnOnGPSandData(){

       /* intent=new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        sendBroadcast(intent);*/


        try {
            dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled",
                    boolean.class);

            dataMtd.setAccessible(true);
            dataMtd.invoke(connectivityManager, true);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public void turnOffGPSandDaya(){

        /*intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", false);
        sendBroadcast(intent);*/

        try {
            dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled",
                    boolean.class);

            dataMtd.setAccessible(false);
            dataMtd.invoke(connectivityManager, false);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }

}
