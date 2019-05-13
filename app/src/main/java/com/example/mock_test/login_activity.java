package com.example.mock_test;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.firebase.database.*;
import in.myinnos.imagesliderwithswipeslibrary.SliderLayout;
import in.myinnos.imagesliderwithswipeslibrary.SliderTypes.BaseSliderView;
import in.myinnos.imagesliderwithswipeslibrary.SliderTypes.TextSliderView;

import java.util.Random;

public class login_activity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener
{
    Runnable r;
    SliderLayout slider;
    Handler handler;
    int i=0;
    Button create,login;
    static EditText user,pass;
    CheckBox rem;
    FirebaseDatabase fd;
    Shared_handler sh;
    DatabaseReference ref;
    String url1="https://firebasestorage.googleapis.com/v0/b/mocktest-f6d02.appspot.com/o/accounting.jpg?alt=media&token=d4cd8bce-2483-4994-93fa-74f2533810b7";
    String url2="https://firebasestorage.googleapis.com/v0/b/mocktest-f6d02.appspot.com/o/Best-Study-Material-for-NEET.webp?alt=media&token=3e1ee76d-bea7-4700-8fbf-aee81169f02c";
    String url3="https://firebasestorage.googleapis.com/v0/b/mocktest-f6d02.appspot.com/o/hero-accounting-degrees-and-certification-what-youll-study-in-an-accounting-program.jpg?alt=media&token=0ac977f5-8bf7-425d-a8c3-4e01ff328b19";
    String url4="https://firebasestorage.googleapis.com/v0/b/mocktest-f6d02.appspot.com/o/images.jpeg?alt=media&token=8d85a354-e1b1-4300-a81c-3882ddec46ce";
    public String getRandomUrl()
    {
        String number="";
        for(int i=0;i<6;i++)
        {
            number=number+getRandomNumber(0,9);
        }
        String url4="https://images.pexels.com/photos/"+number+"/pexels-photo-"+number+".jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260";
        return url4;
    }
    public int getRandomNumber(int min,int max)
    {
        return (new Random()).nextInt((max - min) + 1) + min;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        fd=FirebaseDatabase.getInstance();
        ref=fd.getReference("Users");
        CheckforPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,1);
        sh=new Shared_handler(getApplicationContext());
        if(sh.getFrom("personal","remember").equals("true"))
        {
            String type=sh.getFrom("personal","type");
            if(type.equals("STUDENT"))
            {
                Intent intent=new Intent(getApplicationContext(),Student_home.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Intent intent=new Intent(getApplicationContext(),Faculty_home.class);
                startActivity(intent);
                finish();
            }
        }
        else
        {
            sh.Clear("personal");
            fetch();
        }
    }
    public void fetch()
    {
        slider = findViewById(R.id.slider);
        login = findViewById(R.id.login);
        create = findViewById(R.id.create);
        user = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        rem = findViewById(R.id.rember);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final ProgressDialog dialog=new ProgressDialog(login_activity.this);
                dialog.setMessage("Logging you in .....");
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);
                String username=user.getText().toString();
                final String password= pass.getText().toString();
                if(username.length()>0)
                {

                    if(password.length()>0)
                    {
                        ref.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    Account_pojo ap=dataSnapshot.child("Personal Details").getValue(Account_pojo.class);
                                    String pass=ap.getPassword();
                                    if(pass.equals(password))
                                    {
                                        String type=ap.getType();
                                        sh.saveinandsave("personal","fullname",ap.getFullname());
                                        sh.saveinandsave("personal","email",ap.getEmail());
                                        sh.saveinandsave("personal","password",ap.getPassword());
                                        sh.saveinandsave("personal","profile",ap.getProfile());
                                        sh.saveinandsave("personal","type",ap.getType());
                                        sh.saveinandsave("personal","username",ap.getUsername());
                                        sh.saveinandsave("personal","remember",rem.isChecked()+"");
                                        if(type.equals("STUDENT"))
                                        {
                                            Intent intent=new Intent(getApplicationContext(),Student_home.class);
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            Intent intent=new Intent(getApplicationContext(),Faculty_home.class);
                                            startActivity(intent);
                                        }
                                        dialog.dismiss();
                                        finish();
                                    }
                                    else
                                    {
                                        dialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Password not matched !",Toast.LENGTH_LONG).show();
                                    }
                                }
                                else
                                {
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"User not exists ! create account first .",Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Invalid password !",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please enter username !",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Create_account.class);
                startActivity(intent);
            }
        });
        loadImages();
        //setSliderViews();
    }
    public void loadImages()
    {
        for(int i=0;i<4;i++)
        {
            String url="";
            if(i==0)
            {
                url=url1;
            }
            else if(i==1)
            {
                url=url2;
            }
            else if(i==2)
            {
                url=url3;
            }
            else if(i==3)
            {
                url=url4;
            }
            //url=getRandomUrl();
            TextSliderView textSliderView = new TextSliderView(login_activity.this);
            textSliderView
                    .description("")
                    .descriptionSize(20)
                    .image(url)
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(login_activity.this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra", "");
            slider.addSlider(textSliderView);
        }
    }
    @Override
    protected void onStop() {
        slider.stopAutoCycle();
        super.onStop();
    }
    @Override
    public void onSliderClick(BaseSliderView slider)
    {
    }
    private void CheckforPermission(String permission,int code)
    {
        int per = ContextCompat.checkSelfPermission(this,permission);
        if (per != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission))
            {
                requestpermission(permission,code);
            }
            else
            {
                requestpermission(permission,code);
            }
        }
    }
    public void requestpermission(String permission,int code)
    {
        ActivityCompat.requestPermissions(this,
                new String [] { permission }, code);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String [] permissions, @NonNull int [] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 1:
            {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    CheckforPermission(Manifest.permission.CAMERA,2);
                }
                else
                {
                    CheckforPermission(Manifest.permission.CAMERA,2);
                }
                return;
            }
            case 2:
            {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    CheckforPermission(Manifest.permission.VIBRATE,3);
                }
                else
                {
                    CheckforPermission(Manifest.permission.VIBRATE,3);
                }
                return;
            }
            case 3:
            {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    CheckforPermission(Manifest.permission.READ_PHONE_STATE,4);
                }
                else
                {
                    CheckforPermission(Manifest.permission.READ_PHONE_STATE,4);
                }
                return;
            }
            case 4:
            {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    CheckforPermission(Manifest.permission.READ_EXTERNAL_STORAGE,5);
                }
                else
                {
                    CheckforPermission(Manifest.permission.READ_EXTERNAL_STORAGE,5);
                }
                return;
            }
            case 5:
            {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                }
                else
                {
                }
                return;
            }
            default:
                break;
        }
    }
}
