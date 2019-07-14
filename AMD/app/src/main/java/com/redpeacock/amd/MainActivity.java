package com.redpeacock.amd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity
{
    Button operatorsButton;
    Button usersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        operatorsButton = (Button) findViewById(R.id.button);
        usersButton = (Button) findViewById(R.id.button2);
        final Intent operatorsIntent = new Intent(this,OperatorsActivity.class);
        final Intent usersIntent = new Intent(this,UsersActivity.class);
        operatorsButton.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view)
            {
                startActivity(operatorsIntent);
            }/*end onClick method*/
        }); /* end lambda function*/
        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(usersIntent);
            }/*end onClick method*/
        }); /*end lambda function*/

    }/* end onCreate method */



}/* end MainActivity class */
