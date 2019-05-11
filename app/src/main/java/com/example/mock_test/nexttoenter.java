package com.example.mock_test;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Date;

import cn.iwgang.countdownview.CountdownView;

import static com.example.mock_test.Faculty_main.mtbp;
import static com.example.mock_test.Faculty_main.questionlist;
import static com.example.mock_test.Student_main.ctql;
import static com.example.mock_test.Student_main.curenteui;
import static com.example.mock_test.Student_main.sequence;

public class nexttoenter extends AppCompatActivity
{
    String date,euid,stime,etime;
    Shared_handler shh;
    CountdownView countdownView;
    Helper h=new Helper();
    FirebaseDatabase fd;
    CardView hidden;
    DatabaseReference ref;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(nexttoenter.this,R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Are you sure ?");
            builder.setMessage("You will need to rescan the QR Code( if Required ) for entering in this exam again .");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, int id)
                {
                    ref.child(date).child(euid).child("Candidates").child(shh.getFrom("personal","username")).setValue("NOTREADY").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            finish();
                        }
                    });
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nexttoenter);
        getSupportActionBar().hide();
        Intent intent=getIntent();
        date=intent.getStringExtra("date");
        euid=intent.getStringExtra("euid");
        stime=intent.getStringExtra("stime");
        etime=intent.getStringExtra("etime");
        fd=FirebaseDatabase.getInstance();
        hidden=findViewById(R.id.hidden);
        ref=fd.getReference("Exams");
        shh=new Shared_handler(getApplicationContext());
        ref.child(date).child(euid).child("Candidates").child(shh.getFrom("personal","username")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String value=dataSnapshot.getValue().toString();
                    if(value.equals("SUBMITTED"))
                    {
                        hidden.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        ref.child(date).child(euid).child("Candidates").child(shh.getFrom("personal","username")).setValue("READY").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                start();
                            }
                        });
                    }
                }
                else
                {
                    ref.child(date).child(euid).child("Candidates").child(shh.getFrom("personal","username")).setValue("READY").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                               start();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void start()
    {
        final Date start=h.getDate(stime);
        countdownView=findViewById(R.id.counter_view);
        Date now=h.getDate(h.getTodaydate("hh:mm:ss a"));
        if(now.after(start))
        {
            Intent intent1=new Intent(getApplicationContext(),Start_mock_test.class);
            intent1.putExtra("euid",euid);
            intent1.putExtra("date",date);
            intent1.putExtra("stime",stime);
            intent1.putExtra("etime",etime);
            startActivity(intent1);
            finish();
        }
        else
        {
            long millis=start.getTime()-now.getTime();
            countdownView.start(millis);
            countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                @Override
                public void onEnd(CountdownView cv)
                {
                    Intent intent1=new Intent(getApplicationContext(),Start_mock_test.class);
                    intent1.putExtra("euid",euid);
                    intent1.putExtra("date",date);
                    intent1.putExtra("stime",stime);
                    intent1.putExtra("etime",etime);
                    startActivity(intent1);
                    finish();
                }
            });
        }
    }
}
