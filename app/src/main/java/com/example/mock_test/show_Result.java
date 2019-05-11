package com.example.mock_test;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.timqi.sectorprogressview.SectorProgressView;

import java.util.ArrayList;
import java.util.Collections;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class show_Result extends AppCompatActivity {

    String date,euid;
    FirebaseDatabase fd;
    DatabaseReference ref,ref1;
    mock_test_basic_pojo mtbp;
    Shared_handler shh;
    Helper h=new Helper();
    LinearLayout ll;
    ArrayList<String> responses=new ArrayList<>();
    ArrayList<Question_pojo> questions=new ArrayList<>();
    SectorProgressView cp,sp,wp;
    TextView ctv,stv,wtv,per,oall;
    int totalmarks=0,mymarks=0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__result);
        getSupportActionBar().hide();
        Intent intent=getIntent();
        date=intent.getStringExtra("date");
        euid=intent.getStringExtra("euid");
        shh=new Shared_handler(getApplicationContext());
        fd=FirebaseDatabase.getInstance();
        ref=fd.getReference("Users");
        ref1=fd.getReference("Exams");
        loadResponse();
        loadallQuestions();
    }
    public void addResponseView(int qno,String myres,String result,Question_pojo qp,int iskipped)
    {
        LayoutInflater inflater= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.response_item,null);
        LinearLayout block=v.findViewById(R.id.block);
        TextView qn=v.findViewById(R.id.qno);
        TextView c=v.findViewById(R.id.correct);
        TextView r=v.findViewById(R.id.response);
        ImageView eye=v.findViewById(R.id.view);
        qn.setText(qno+"");
        r.setText(myres+"");
        if(iskipped==0) {
            if (result.equals("CORRECT")) {
                block.setBackgroundColor(Color.parseColor("#26c705"));
            } else if (result.equals("INCORRECT")) {
                block.setBackgroundColor(Color.parseColor("#d81b1b"));
            }
        }
        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        c.setText(qp.getCorrect()+"");
        ll.addView(v);
    }
    private void loadResponse()
    {
        ll=findViewById(R.id.response_container);
    }
    public void loadExamDetails()
    {
        final TextView tv=findViewById(R.id.examname);
        final TextView tv1=findViewById(R.id.mm);
        final TextView tv2=findViewById(R.id.sf);
        final TextView tv3=findViewById(R.id.ea);
        ref1.child(date).child(euid).child("Exam basic").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                mtbp=dataSnapshot.getValue(mock_test_basic_pojo.class);
                totalmarks=Integer.parseInt(mtbp.getMax_marks());
                tv.setText(mtbp.getExam_fullname());
                tv1.setText("Max. Marks : "+mtbp.getMax_marks());
                String stime=mtbp.getExam_start_time();
                String etime=mtbp.getExam_end_time();
                String sp[]=stime.split("]-]");
                String ep[]=etime.split("]-]");
                int sh=Integer.parseInt(sp[0]);
                int sm=Integer.parseInt(sp[1]);
                int eh=Integer.parseInt(ep[0]);
                int em=Integer.parseInt(ep[1]);
                String a="AM",b="AM";
                if(sh>12)
                {
                    a="PM";
                    sh=sh-12;
                }
                if(eh>12)
                {
                    b="PM";
                    eh=eh-12;
                }
                tv2.setText("Start from :: "+h.convertoTwo(sh)+" : "+h.convertoTwo(sm)+" : 00 "+a);
                tv3.setText("End from :: "+h.convertoTwo(eh)+" : "+h.convertoTwo(em)+" : 00 "+b);
                loadPerformance();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void loadallQuestions()
    {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(show_Result.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading Data")
                .fadeColor(Color.DKGRAY)
                .build();
        dialog.show();
        ref1.child(date).child(euid).child("Questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot data:dataSnapshot.getChildren())
                {
                    Question_pojo qp=data.getValue(Question_pojo.class);
                    questions.add(qp);
                }
                dialog.dismiss();
                loadExamDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void loadPerformance()
    {
        cp=findViewById(R.id.correct_progress);
        wp=findViewById(R.id.wrong_progress);
        sp=findViewById(R.id.skipped_progress);
        ctv=findViewById(R.id.ctv);
        wtv=findViewById(R.id.wtv);
        stv=findViewById(R.id.stv);
        per=findViewById(R.id.percentage);
        oall=findViewById(R.id.overall);
        ref.child(shh.getFrom("personal","username")).child("My Results").child(date).child(euid).child("Response").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot data : dataSnapshot.getChildren())
                    {
                        String key=data.getKey();
                        String values=data.getValue().toString();
                        responses.add(values);
                    }
                    analysnow();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void analysnow()
    {
        int total=0,skipped=0,correct=0,wrong=0;
        int mymarks=0;
        total=responses.size();
        for(int i=0;i<total;i++)
        {
            String val=responses.get(i);
            String res=val.split("]-]")[0];
            int myresponse=5;
            double myresponse2=0.0;
            int isdrq=0;
            int is=0;
            if(!res.equals("UNANSWERED"))
            {
                if(!res.equals("SKIPPED"))
                {
                    if(!res.contains(".")) {
                        myresponse = Integer.parseInt(res);
                    }
                    else
                    {
                        isdrq=1;
                        myresponse2=Double.parseDouble(res);
                    }
                }
                else
                {
                    is=1;
                }
            }
            else
            {
              is=1;
            }
            String result=val.split("]-]")[1];
            Question_pojo qp=questions.get(i);
            if(isdrq==0) {
                addResponseView(i + 1, myresponse+"", result, qp, is);
                if (myresponse != 5) {
                    if (result.equals("CORRECT")) {
                        mymarks = mymarks + Integer.parseInt(qp.getMarks());
                        correct++;
                    } else if (result.equals("INCORRECT")) {
                        wrong++;
                    }
                } else {
                    skipped++;
                }
            }
            else
            {
                addResponseView(i + 1, myresponse2+"", result, qp, is);
                if (result.equals("CORRECT"))
                {
                    mymarks = mymarks + Integer.parseInt(qp.getMarks());
                    correct++;
                } else if (result.equals("INCORRECT")) {
                    wrong++;
                }
            }
        }
        float sper=((float)skipped/(float)total)*100;
        float cper=((float)correct/(float)total)*100;
        float wper=((float)wrong/(float)total)*100;
        cp.animate().start();
        wp.animate().start();
        sp.animate().start();
        cp.setPercent(cper);
        wp.setPercent(wper);
        sp.setPercent(sper);
        float totper=((float)mymarks/(float)totalmarks)*100;
        Log.d("TAGPERCENT", "analysnow: "+cper+" : "+wper+" : "+sper+" : "+totper);
        per.setText((int)totper+" %");
        ctv.setText("Correct Answers\n"+correct+" / "+total);
        wtv.setText("Wrong Answers\n"+wrong+" / "+total);
        stv.setText("Skipped Questions\n"+skipped+" / "+total);
        if(totper>30)
        {
            oall.setTextColor(Color.parseColor("#26c705"));
            oall.setText("PASS");
        }
        else
        {
            oall.setTextColor(Color.parseColor("#d81b1b"));
            oall.setText("FAIL");
        }
    }
}
