package com.rishabh.callinformation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 101;
    TextView txt;
    Button checkbtn;
    Button overlaybtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkbtn = (Button)findViewById(R.id.checkBtn);
        overlaybtn = (Button)findViewById(R.id.overlayBtn);
        txt = (TextView) findViewById(R.id.text1);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + this.getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_CONTACTS},
                1);


        checkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txt.getText().equals("Permission Granted"))
                {
                    Toast.makeText(MainActivity.this,"All permission Granted",Toast.LENGTH_SHORT).show();
                }
                else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS},
                            1);
                }
            }
        });

        overlaybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {

                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + MainActivity.this.getPackageName()));
                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                }
                else {
                    Toast.makeText(MainActivity.this,"Permission already granted",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    txt.setText("Permission Granted");


                } else {

                    txt.setText("Permission Denied ! Please Check Permissions");
                    Toast.makeText(MainActivity.this, "Accept all permission to Start", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            if (resultCode == RESULT_OK) {

                Toast.makeText(this,"over other app",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this,"Accept all permission to Start",Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
