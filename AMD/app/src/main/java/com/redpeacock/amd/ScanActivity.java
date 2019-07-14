package com.redpeacock.amd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler
{

    private ZXingScannerView zXingScannerView;
    private Intent listViewActivityIntent;
    String medData;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        listViewActivityIntent= new Intent(getApplicationContext(),ListViewActivity.class);
        scan(this);


    }//end onCreate

   public void scan(ScanActivity view)
   {
        zXingScannerView = new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
       zXingScannerView.setResultHandler(this);
       zXingScannerView.startCamera();
   }//end scan

    @Override
    public void handleResult(Result result)
    {
        medData=result.getText();
        listViewActivityIntent.putExtra("Medication Data",medData);
        startActivity(listViewActivityIntent);
        //Toast.makeText(getApplicationContext(),result.getText(),Toast.LENGTH_SHORT).show();

    }//end handleResult method
    @Override
    protected void onPause()
    {
        super.onPause();
        zXingScannerView.stopCamera();
        zXingScannerView.resumeCameraPreview(this);
    }//end onPause method
}//end ScanActivity Class
