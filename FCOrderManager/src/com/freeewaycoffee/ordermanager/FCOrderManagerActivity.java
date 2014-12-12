package com.freeewaycoffee.ordermanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class FCOrderManagerActivity extends Activity 
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fc_om_signon);
        Intent intent = new Intent();
        intent.setClassName(this, FCOrderManagerSignonActivity.class.getName());
        startActivity(intent);
        this.finish();
    }
}