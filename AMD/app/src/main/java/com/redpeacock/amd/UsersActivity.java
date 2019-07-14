package com.redpeacock.amd;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class UsersActivity extends AppCompatActivity
{
    Button scanButton;
    Context context;
    private final int CAMERA_CODE = 102;
    private Intent scanIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        scanButton =  (Button) findViewById(R.id.scanButton);
        context = this;
         scanIntent =  new Intent(context,ScanActivity.class);
        scanButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                askPermission(scanIntent);

            }/*end onClick*/
        });// end lambda expression


    }/*end onCreate method*/

    private void askPermission(Intent intent)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);
        }/*end if*/
        else

            startActivity(scanIntent);
    }/*end askPermission method*/

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case (CAMERA_CODE):

                if (permissions[0].equals(Manifest.permission.CAMERA))
                {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)return;
                        startActivity(scanIntent);
                    }/*end if*/
                }/*end if*/
                break;

            default:
                super.onRequestPermissionsResult(requestCode,permissions,grantResults);
                break;
        }/*end switch*/
    }

}/*end UsersActivity class */
