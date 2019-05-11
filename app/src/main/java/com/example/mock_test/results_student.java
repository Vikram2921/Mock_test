package com.example.mock_test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class results_student extends Fragment
{
    FirebaseDatabase fd;
    DatabaseReference ref,ref1;
    LinearLayout ll;
    Shared_handler sh;
    long lasteuid=0;
    View v;
    Helper h=new Helper();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.result_student, container, false);
        v=view;
        fd=FirebaseDatabase.getInstance();
        ref=fd.getReference("Users");
        ref1=fd.getReference("Exams");
        sh=new Shared_handler(getContext());
        start();
        return view;
    }
    public void start()
    {
        ll=v.findViewById(R.id.lister);
        ref.child(sh.getFrom("personal","username")).child("My Results").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                String date=dataSnapshot.getKey();
                String euid="";
                for(DataSnapshot data:dataSnapshot.getChildren())
                {
                    euid=data.getKey();
                }
                addinlist(date,euid);
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

    private void addinlist(final String date, final String euid)
    {
        LayoutInflater inflater= (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.result_item,null);
        final TextView en=v.findViewById(R.id.examname);
        final TextView mm=v.findViewById(R.id.maxmarks);
        final TextView time=v.findViewById(R.id.time);
        ref1.child(date).child(euid).child("Exam basic").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                mock_test_basic_pojo mtbp=dataSnapshot.getValue(mock_test_basic_pojo.class);
                en.setText(mtbp.getExam_fullname());
                mm.setText("Max. marks : "+mtbp.getMax_marks());
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
                time.setText("From :: "+h.convertoTwo(sh)+" : "+h.convertoTwo(sm)+" : 00 "+a+"      To :: "+h.convertoTwo(eh)+" : "+h.convertoTwo(em)+" : 00 "+b);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),show_Result.class);
                intent.putExtra("date",date);
                intent.putExtra("euid",euid);
                startActivity(intent);

            }
        });
        long thisid=Long.parseLong(euid);
        if(thisid<lasteuid) {
            ll.addView(v);
        }
        else
        {
            lasteuid=thisid;
            ll.addView(v,0);
        }
    }
}
