package com.rishabh.callinformation;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class CallerIDForegroundService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";



    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        assert intent.getAction() != null;
        if ("ACTION_CALLER_ID".equals(intent.getAction())) {
            showNotification(1001);

            int callState = intent.hasExtra("callState") ? intent.getIntExtra("callState", -1) : -1;
            String incomingNumber = intent.hasExtra("incomingNumber") ? intent.getStringExtra("incomingNumber") : "";

//            Log.d("Ringing", "CallerIdForegroundService, callState :" + callState);
//            Log.d("Ringing", "CallerIdForegroundService, incomingNumber:" + incomingNumber);


            final Intent intentCallerID = new Intent(this.getApplicationContext(), IncomingCallActivity.class);
            intentCallerID.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intentCallerID.putExtras(intentCallerID);
            intentCallerID.putExtra("callState", callState);
            intentCallerID.putExtra("incomingNumber", incomingNumber);
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    CallerIDForegroundService.this.startActivity(intentCallerID);
                }
            }, 500);

            return START_STICKY;
        }else if ("ACTION_CALLER_ID_ALWAYS".equals(intent.getAction())) {
            showNotification(1002);
            return START_STICKY;
        }
        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification(int notificationId) {

        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText("Call it is Active")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        startForeground(notificationId, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}

