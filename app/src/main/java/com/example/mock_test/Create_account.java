package com.example.mock_test;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Create_account extends AppCompatActivity {

    FirebaseDatabase fd;
    DatabaseReference ref;
    String type="STUDENT";
    String pro="--";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getSupportActionBar().hide();
        fd=FirebaseDatabase.getInstance();
        ref=fd.getReference("Users");
        final EditText fullname,username,email,password,cpassword;
        Button create;
        fullname=findViewById(R.id.fullname);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        cpassword=findViewById(R.id.cpassword);
        email=findViewById(R.id.email);
        create=findViewById(R.id.create);
        create.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view)
            {
                type="FACULTY";
                final String full=fullname.getText().toString();
                final String user=username.getText().toString();
                final String pass=password.getText().toString();
                String cpass=cpassword.getText().toString();
                final String emid=email.getText().toString();
                if(full.length()>0)
                {
                    if(user.length()>6)
                    {
                        if(isEmailValid(emid)==true)
                        {
                            if(cpass.equals(pass))
                            {
                                final ProgressDialog dialog=new ProgressDialog(Create_account.this);
                                dialog.setTitle("Creating your account ");
                                dialog.setMessage("Please wait while creating your account !");
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();
                                ref.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        if(!dataSnapshot.exists())
                                        {
                                            Account_pojo ap=new Account_pojo();
                                            ap.setEmail(emid);
                                            ap.setFullname(full);
                                            ap.setPassword(pass);
                                            ap.setProfile(pro);
                                            ap.setType(type);
                                            ap.setUsername(user);
                                            ref.child(user).child("Personal Details").setValue(ap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    login_activity.user.setText(user+"");
                                                    login_activity.pass.setText(pass+"");
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            });
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),"Username not available !",Toast.LENGTH_LONG).show();
                                            username.setText("");
                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Password not matched !",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Invalid Email ID. ",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Username lenght should be greater than 6",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Fullname cannot be empty",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final String full=fullname.getText().toString();
                final String user=username.getText().toString();
                final String pass=password.getText().toString();
                String cpass=cpassword.getText().toString();
                final String emid=email.getText().toString();
                if(full.length()>0)
                {
                    if(user.length()>6)
                    {
                        if(isEmailValid(emid)==true)
                        {
                            if(cpass.equals(pass))
                            {
                                final ProgressDialog dialog=new ProgressDialog(Create_account.this);
                                dialog.setTitle("Creating your account ");
                                dialog.setMessage("Please wait while creating your account !");
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();
                                ref.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        if(!dataSnapshot.exists())
                                        {
                                            Account_pojo ap=new Account_pojo();
                                            ap.setEmail(emid);
                                            ap.setFullname(full);
                                            ap.setPassword(pass);
                                            ap.setProfile(pro);
                                            ap.setType(type);
                                            ap.setUsername(user);
                                            ref.child(user).child("Personal Details").setValue(ap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    login_activity.user.setText(user+"");
                                                    login_activity.pass.setText(pass+"");
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            });
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),"Username not available !",Toast.LENGTH_LONG).show();
                                            username.setText("");
                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Password not matched !",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Invalid Email ID. ",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Username lenght should be greater than 6",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Fullname cannot be empty",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public static boolean isEmailValid(String email)
    {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
