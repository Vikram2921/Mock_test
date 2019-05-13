package com.example.mock_test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.zxing.WriterException;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;
import cn.iwgang.countdownview.CountdownView;

public class Generate_QR extends AppCompatActivity {

    Helper h=new Helper();
    ImageView qr;
    String euid,stime;
    long before=0;
    RelativeLayout it,ot;
    Button livestatus;
    CountdownView countdownView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate__qr);
        getSupportActionBar().hide();
        Intent intent=getIntent();
        euid=intent.getStringExtra("euid");
        stime=intent.getStringExtra("start_time");
        before=Long.parseLong(intent.getStringExtra("before"));
        fetch();
    }
    public Date getDate(String date)
    {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss aa");
        Date date1 = null;
        try
        {
            date1 = format.parse(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date1;
    }
    private void fetch()
    {
        it=findViewById(R.id.intime);
        ot=findViewById(R.id.other_time);
        qr=findViewById(R.id.QRCODE);
        livestatus=findViewById(R.id.live_status);
        livestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(getApplicationContext(),live_status.class);
                intent.putExtra("euid",euid);
                startActivity(intent);
            }
        });
        countdownView=findViewById(R.id.counter_view);
        Date start=getDate(stime);
        Date current=getDate(h.getCurrentTime("hh:mm:ss a"));
        long diff=start.getTime()-current.getTime();
        if(diff>1.8e+6)
        {
            double newlong=diff-1.8e+6;
            ot.setVisibility(View.VISIBLE);
            it.setVisibility(View.GONE);
            countdownView.start((long)newlong);
            countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                @Override
                public void onEnd(CountdownView cv)
                {
                    ot.setVisibility(View.GONE);
                    it.setVisibility(View.VISIBLE);
                    GenrateQRCode();
                }
            });
        }
        else
        {
            GenrateQRCode();
        }
    }
    public void GenrateQRCode()
    {
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;
        QRGEncoder qrgEncoder = new QRGEncoder(euid, null, QRGContents.Type.TEXT, smallerDimension);
        try
        {
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            Glide.with(getApplicationContext()).load(bitmap).into(qr);

        } catch (WriterException e)
        {
        }
    }
}
