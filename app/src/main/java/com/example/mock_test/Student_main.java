package com.example.mock_test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.iwgang.countdownview.CountdownView;

import static com.example.mock_test.Student_home.isinexam;

public class Student_main extends Fragment
{
    FirebaseDatabase fd;
    DatabaseReference ref;
    String stimedef="",etimedef="";
    TextView ev,va;
    int today_count=0;
    View v;
    LinearLayout tls;
    String green="#FF00AF0B";
    static String curenteui="";
    static ArrayList<Question_pojo> ctql=new ArrayList<>();
    static ArrayList<Integer> sequence=new ArrayList<>();
    static ArrayList<Integer> osequence=new ArrayList<>();
    static ArrayList<String> Responses=new ArrayList<>();
    static mock_test_basic_pojo mtpbdef;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    String red="#FFFC1200";
    Helper h=new Helper();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.student_main, container, false);
        v=view;
        fd=FirebaseDatabase.getInstance();
        ref=fd.getReference("Exams");
        fetch();
        return view;
    }
    public void fetch()
    {
        ev=v.findViewById(R.id.exam_vis);
        va=v.findViewById(R.id.view_all);
        tls=v.findViewById(R.id.tls);
        load_today_list();
    }
    public String getTodaydateinformat()
    {
        Calendar calendar=Calendar.getInstance();
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        return convertoTwo(day)+""+convertoTwo(month)+""+year;
    }
    public void load_today_list()
    {

        ref.child(getTodaydateinformat()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(ev.getVisibility()==View.VISIBLE)
                {
                    ev.setVisibility(View.GONE);
                }
                today_count=today_count+1;
                String euid=dataSnapshot.getKey();
                mock_test_basic_pojo mtbp=dataSnapshot.child("Exam basic").getValue(mock_test_basic_pojo.class);
                addintodaylist(euid,mtbp);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
    private void addintodaylist(final String euid, final mock_test_basic_pojo mtbp)
    {
        LayoutInflater inflater= (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.today_exam_list_item,null);
        TextView tv=view.findViewById(R.id.test_name);
        TextView tv1=view.findViewById(R.id.max);
        tv.setText(mtbp.getExam_fullname());
        final CountdownView cdv=view.findViewById(R.id.counter);
        final String stime=mtbp.getExam_start_time();
        String[] sp=stime.split("]-]");
        final TextView head=view.findViewById(R.id.head);
        int sh=Integer.parseInt(sp[0]);
        final int sm=Integer.parseInt(sp[1]);
        String a="AM";
        if(sh>12)
        {
            a="PM";
            sh=sh-12;
        }
        String etime=mtbp.getExam_end_time();
        String[] ep=etime.split("]-]");
        int eh=Integer.parseInt(ep[0]);
        final int em=Integer.parseInt(ep[1]);
        String e="AM";
        if(eh>12)
        {
            e="PM";
            eh=eh-12;
        }
        final Date date1=getDate(convertoTwo(sh)+":"+convertoTwo(sm)+":00 "+a);
        final Date date4=getDate(convertoTwo(eh)+":"+convertoTwo(em)+":00 "+e);
        final Date date2=getDate(h.getCurrentTime("hh:mm:ss a"));
        tv1.setText("Max Marks : "+mtbp.getMax_marks()+"  | Starting time :: "+convertoTwo(sh)+" : "+convertoTwo(sm)+" "+a);
        final boolean[] able = {true};
        if(date2.after(date4))
        {
            able[0] =false;
            cdv.setVisibility(View.GONE);
            head.setTextColor(Color.parseColor(red));
            head.setText("Exam ended !");
        }
        else
        {
            long millis = date1.getTime() - date2.getTime();
            if (millis > 0)
            {
                cdv.start(millis);
                cdv.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                    @Override
                    public void onEnd(CountdownView cv)
                    {
                        head.setTextColor(Color.parseColor(green));
                        head.setText("Exam is currently running. Ends in ");
                        long millis0 = date4.getTime() - date2.getTime();
                        cdv.start(millis0);
                        cdv.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                            @Override
                            public void onEnd(CountdownView cv)
                            {
                                able[0] =false;
                                cdv.setVisibility(View.GONE);
                                head.setTextColor(Color.parseColor(red));
                                head.setText("Exam ended !");
                            }
                        });
                    }
                });
            }
            else
            {
                head.setTextColor(Color.parseColor(green));
                head.setText("Exam is currently running. Ends in ");
                long millis0 = date4.getTime() - date2.getTime();
                cdv.start(millis0);
                cdv.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                    @Override
                    public void onEnd(CountdownView cv)
                    {
                        able[0] =false;
                        cdv.setVisibility(View.GONE);
                        head.setTextColor(Color.parseColor(red));
                        head.setText("Exam ended !");
                    }
                });
            }
        }
        final boolean finalAble = able[0];
        final String finalA = a;
        final int finalSh = sh;
        final int finalEh = eh;
        final String finalE = e;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(finalAble ==true)
                {
                    stimedef=convertoTwo(finalSh)+":"+convertoTwo(sm)+":00 "+ finalA;
                    etimedef=convertoTwo(finalEh)+":"+convertoTwo(em)+":00 "+ finalE;
                    if(mtbp.getRequier_qr().equals("true"))
                    {
                        Intent i = new Intent(getContext(), QrCodeActivity.class);
                        startActivityForResult( i,REQUEST_CODE_QR_SCAN);
                    }
                    else
                    {
                        Intent intent=new Intent(getContext(),nexttoenter.class);
                        intent.putExtra("date",getTodaydateinformat());
                        intent.putExtra("euid",euid);
                        intent.putExtra("stime",stimedef);
                        intent.putExtra("etime",etimedef);
                        startActivity(intent);
                    }
                }
            }
        });
        tls.addView(view);
    }
    public String convertoTwo(int val)
    {
        String n=val+"";
        if(val<10)
        {
            n="0"+val;
        }
        return n;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK)
        {
            if(data==null)
                return;
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                Toast.makeText(getContext(),"Scan Error !",Toast.LENGTH_LONG).show();
            }
            return;

        }
        if(requestCode == REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Intent intent=new Intent(getContext(),nexttoenter.class);
            intent.putExtra("date",getTodaydateinformat());
            intent.putExtra("euid",result);
            intent.putExtra("stime",stimedef);
            intent.putExtra("etime",etimedef);
            startActivity(intent);

        }
    }
}
