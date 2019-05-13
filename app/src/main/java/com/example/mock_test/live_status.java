package com.example.mock_test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class live_status extends AppCompatActivity {

    FirebaseDatabase fd;
    DatabaseReference ref,ref1;
    LinearLayout ll;
    String euid;
    Helper h=new Helper();
    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_status);
        getSupportActionBar().setTitle("Exam Live Status");
        fd=FirebaseDatabase.getInstance();
        ref=fd.getReference("Exams");
        ref1=fd.getReference("Users");
        Intent intent=getIntent();
        euid=intent.getStringExtra("euid");
        date=h.getTodaydateinformat();
        Log.d("TAGdater", "onCreate: "+date);
        ll=findViewById(R.id.liver);
        getLiveStatus();
    }
    public void addListener(String key)
    {
        LayoutInflater inflater= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.live_tem,null);
        final ImageView pro=v.findViewById(R.id.profile);
        final TextView name=v.findViewById(R.id.name);
        final TextView status=v.findViewById(R.id.status);
        ref1.child(key).child("Personal Details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String name1=dataSnapshot.child("fullname").getValue().toString();
                String profile1=dataSnapshot.child("profile").getValue().toString();
                name.setText(name1);
                if(!profile1.equals("--"))
                {
                    Glide.with(getApplicationContext()).load(profile1).into(pro);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref.child(date).child(euid).child("Candidates").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String value=dataSnapshot.getValue().toString();
                if(value.equals("SUBMITTED"))
                {
                    status.setText("Paper Submitted   ");
                    status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_black_24dp,0,0,0);
                    setcolor(0,status);
                }
                else if(value.equals("WRITING"))
                {
                    status.setText("Writing Paper   ");
                    status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_edit_black_24dp,0,0,0);
                    setcolor(1,status);
                }
                else if(value.equals("READY"))
                {
                    status.setText("Ready To Enter   ");
                    status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_black_24dp,0,0,0);
                    setcolor(2,status);
                }
                else
                {
                    status.setText("Refreshing   ");
                    status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_refresh_black_24dp,0,0,0);
                    setcolor(3,status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ll.addView(v);
    }

    @SuppressLint("NewApi")
    private void setcolor(int i, TextView status)
    {
        if(i==0)
        {
            status.setTextColor(Color.parseColor("#4CAF50"));
            status.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }
        else if(i==1)
        {
            status.setTextColor(Color.parseColor("#FF5722"));
            status.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#FF5722")));
        }
        else if(i==2)
        {
            status.setTextColor(Color.parseColor("#3F51B5"));
            status.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
        }
        else
        {
            status.setTextColor(Color.parseColor("#000000"));
            status.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
        }
    }
    public void getLiveStatus()
    {
        ref.child(date).child(euid).child("Candidates").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                String key=dataSnapshot.getKey();
                addListener(key);
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
}
