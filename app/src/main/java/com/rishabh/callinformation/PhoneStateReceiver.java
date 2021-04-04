package com.rishabh.callinformation;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static android.os.Looper.getMainLooper;

public class PhoneStateReceiver extends BroadcastReceiver {

     Context mContext;
    String incomingNumber;
    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context.getApplicationContext();
        try {
//            System.out.println("Receiver start");
//            Toast.makeText(context, " Receiver start ", Toast.LENGTH_SHORT).show();

            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            incomingNumber = intent.getStringExtra("incoming_number");
            if (incomingNumber != null)
            {
                if (incomingNumber.length() > 10) {
                    int startIdx = incomingNumber.length() - 10;
                    incomingNumber = incomingNumber.substring(startIdx);
                }
            }
            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
//                incomingNumber = intent.getStringExtra("incoming_number");
                if(incomingNumber != null)
                {

//                    Toast.makeText(context,"Call From - "+incomingNumber,Toast.LENGTH_SHORT).show();

                    Intent serviceIntent = new Intent(mContext, CallerIDForegroundService.class);
                    serviceIntent.setAction("ACTION_CALLER_ID");
                    serviceIntent.putExtra("callState", TelephonyManager.CALL_STATE_RINGING);
                    serviceIntent.putExtra("incomingNumber", incomingNumber);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(mContext, serviceIntent);
                    } else {
                        mContext.startService(serviceIntent);
                    }
                }
            }
            else if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
//                Toast.makeText(context,"Received State",Toast.LENGTH_SHORT).show();

                final Intent intentCallerID = new Intent(mContext, IncomingCallActivity.class);
                intentCallerID.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intentCallerID.putExtras(intentCallerID);
                intentCallerID.putExtra("callState", TelephonyManager.CALL_STATE_OFFHOOK);
                intentCallerID.putExtra("incomingNumber", incomingNumber);
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mContext.startActivity(intentCallerID);
                    }
                }, 500);
            }


            else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
//                Toast.makeText(context,"Call cut |Rejected",Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "onReceive:  REJECTED ");


                cancelCallerIDService();
                final Intent intentCallerID = new Intent(mContext, IncomingCallActivity.class);
                intentCallerID.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intentCallerID.putExtras(intentCallerID);
                intentCallerID.putExtra("callState", TelephonyManager.CALL_STATE_IDLE);
                intentCallerID.putExtra("incomingNumber", incomingNumber);
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mContext.startActivity(intentCallerID);
                    }
                }, 500);
                incomingNumber = "";
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void cancelCallerIDService() {
        Intent serviceIntent = new Intent(mContext, CallerIDForegroundService.class);
        mContext.stopService(serviceIntent);
        NotificationManager nMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(1001);
    }
}
