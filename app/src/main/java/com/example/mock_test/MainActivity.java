package com.example.mock_test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.blikoon.qrcodescanner.QrCodeActivity;

import cn.iwgang.countdownview.CountdownView;

public class MainActivity extends AppCompatActivity {

    String LOGTAG="LOGDTAG";
    Helper h=new Helper();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CountdownView mCvCountdownView = findViewById(R.id.counter_view);
        mCvCountdownView.start(h.ConvertToMillis(1,30,0));
    }
}
