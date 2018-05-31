package com.example.rteav1;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class TriggerOption extends Service {

    WindowManager windowManager;
    private ImageView triggerImg;
    WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);

        triggerImg = new ImageView(this);
        triggerImg.setImageResource(R.drawable.triggerpngimage);

        params = new WindowManager.LayoutParams(
                200,200,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |   WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        |   WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        |   WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        |   WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        |   WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        //this code is to drag the emergency icon.
        triggerImg.setOnTouchListener(new View.OnTouchListener() {
            long lastTouchTime = -1;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                            long thisTime = System.currentTimeMillis();
                            if (thisTime - lastTouchTime < 250) {
                                // Double click
                                lastTouchTime = -1;
                                Log.d("Double",String.valueOf(thisTime - lastTouchTime));

                                startService(new Intent(TriggerOption.this, Trigger.class));
                                stopSelf();

                            } else {
                                // too slow
                                lastTouchTime = thisTime;
                            }

                        initialX = params.x;
                        initialY = params.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        break;

                    case MotionEvent.ACTION_UP:
                        break;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX +
                                (int)(event.getRawX() - initialTouchX);

                        params.y = initialY +
                                (int)(event.getRawY() - initialTouchY);

                        Log.d("Touch", initialTouchX +"::"+initialTouchY);


                        windowManager.updateViewLayout(triggerImg, params);
                        break;
                }
                return false;
            }
        });

        windowManager.addView(triggerImg, params);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(triggerImg != null){
            windowManager.removeView(triggerImg);
        }
    }
}
