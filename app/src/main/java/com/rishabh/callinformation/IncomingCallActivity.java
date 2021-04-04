package com.rishabh.callinformation;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.rishabh.callinformation.databinding.ActivityIncomingCallBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IncomingCallActivity extends AppCompatActivity {


    ActivityIncomingCallBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setWindowParams();

        binding = ActivityIncomingCallBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();


        int callState = getIntent().hasExtra("callState") ? getIntent().getIntExtra("callState", -1) : -1;
        String phNumber = getIntent().hasExtra("incomingNumber") ? getIntent().getStringExtra("incomingNumber") : "";

//        Log.d("Ringing", "IncomingCallActivity, callState :" + callState);
//        Log.d("Ringing", "IncomingCallActivity, incomingNumber :" + phNumber);


        binding.tvName.setText("searching");
        binding.tvClose.setVisibility(View.INVISIBLE);


        settingValuesToUI(callState, phNumber);

        setContentView(view);


        binding.tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IncomingCallActivity.this.finish();
            }
        });


    }


    public void setWindowParams() {
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.dimAmount = 0;
        wlp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        getWindow().setAttributes(wlp);
    }


    private void settingValuesToUI(int callState, String phNumber) {
        boolean isPhoneNoValid = phNumber == null || phNumber.equals("") || phNumber.length() != 10;

        if (isPhoneNoValid) {
            finish();
            return;
        }

//        binding.tvMsg.setText("Incoming call from +91 " + phNumber);

        if (callState == TelephonyManager.CALL_STATE_RINGING) {

            binding.tvMsg.setText("Incoming call from +91 " + phNumber);
            setvalue(this, phNumber);

        } else if (callState == TelephonyManager.CALL_STATE_OFFHOOK) {
            binding.tvMsg.setText("Calling  +91 " + phNumber);
            setvalue(this, phNumber);

        } else if (callState == TelephonyManager.CALL_STATE_IDLE) {

            binding.tvMsg.setText("Call from +91 " + phNumber);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setShowWhenLocked(false);
            }

            setvalue(this, phNumber);

        } else {
            finish();
        }

    }


    private void setvalue(final Context mContext, final String phNumber) {

        ExecutorService executors = Executors.newSingleThreadExecutor();
        executors.execute(new Runnable() {
            @Override
            public void run() {
                final String name = getContactName(IncomingCallActivity.this, phNumber) + "";

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (IncomingCallActivity.this.isContactExists(mContext, phNumber)) {
                            binding.tvStamp.setText(IncomingCallActivity.this.splitUsernameFromData(name));
                            binding.tvName.setText(name);
                        } else {
                            binding.tvStamp.setText("n/a");
                            binding.tvName.setText("New Call");
                        }
                        binding.tvClose.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

    }


    private String splitUsernameFromData(String username) {

        if (username.equals("") || username.equals(" ")) {
            return "n/a";
        } else {
            String name = username.replaceFirst("^\\s*(?:M(?:iss|rs?|s)|Dr|Rev)\\b[\\s.]*", "");
            if (name.equals("") || name.equals(" ")) {
                return "n/a";
            }
            return String.valueOf(name.charAt(0)).toUpperCase();
        }
    }


    public boolean isContactExists(Context context, String number) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        try (Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null)) {
            assert cur != null;
            if (cur.moveToFirst()) {
                return true;
            }
        }
        return false;
    }


    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }


    public boolean isAllPermissionsGranted() {

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)) {

            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }

        return true;
    }
}