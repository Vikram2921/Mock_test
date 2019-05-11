package com.example.mock_test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Edit_profile extends AppCompatActivity {

    FirebaseDatabase fd;
    Shared_handler handler;
    DatabaseReference ref;
    FirebaseStorage str;
    StorageReference sref;
    String pro="--";
    Uri uri;
    int ischanged=0;
    ImageView select;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        handler=new Shared_handler(getApplicationContext());
        getSupportActionBar().setTitle("Edit Profile");
        final EditText fullname,username,email,password,cpassword,opassword;
        Button create;
        fd=FirebaseDatabase.getInstance();
        ref=fd.getReference("Users");
        str=FirebaseStorage.getInstance();
        sref=str.getReference("Profiles");
        select=findViewById(R.id.select);
        fullname=findViewById(R.id.fullname);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        cpassword=findViewById(R.id.cpassword);
        opassword=findViewById(R.id.opassword);
        email=findViewById(R.id.email);
        create=findViewById(R.id.create);
        fullname.setText(handler.getFrom("personal","fullname"));
        username.setText(handler.getFrom("personal","username"));
        email.setText(handler.getFrom("personal","email"));
        pro=handler.getFrom("personal","profile");
        if(!pro.equals("--"))
        {
            Glide.with(getApplicationContext()).load(pro).into(select);
        }
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mediaIntent = new Intent(Intent.ACTION_GET_CONTENT);
                mediaIntent.setType("image/*");
                startActivityForResult(mediaIntent, 1);
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String name=fullname.getText().toString();
                if(name.length()>0)
                {
                    if(isEmailValid(email.getText().toString()))
                    {
                        Account_pojo ap=new Account_pojo();
                        ap.setType(handler.getFrom("personal","type"));
                        ap.setUsername(handler.getFrom("personal","username"));
                        ap.setFullname(name);
                        ap.setPassword(handler.getFrom("personal","password"));
                        ap.setEmail(email.getText().toString());
                        ap.setProfile(handler.getFrom("personal","profile"));
                        if(password.getText().length()>0)
                        {
                            if(opassword.getText().length()>0)
                            {
                                if(opassword.getText().toString().equals(handler.getFrom("personal","password")))
                                {
                                    String cpass=cpassword.getText().toString();
                                    if(password.getText().toString().equals(cpass))
                                    {
                                        ap.setPassword(password.getText().toString());
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"Confirm password not matched !",Toast.LENGTH_LONG).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Old password not matched !",Toast.LENGTH_LONG).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Enter old password !",Toast.LENGTH_LONG).show();
                            }
                        }
                        if(ischanged==1)
                        {
                            uploadProfile(uri,ap);
                        }
                        else
                        {
                            save(ap);
                        }

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Invalid email address !",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Invalid name !",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void save(final Account_pojo ap)
    {
        ref.child(ap.getUsername()).child("Personal Details").setValue(ap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Toast.makeText(getApplicationContext(),"Profile updated !",Toast.LENGTH_LONG).show();
                handler.saveinandsave("personal","fullname",ap.getFullname());
                handler.saveinandsave("personal","email",ap.getEmail());
                handler.saveinandsave("personal","password",ap.getPassword());
                handler.saveinandsave("personal","profile",ap.getProfile());
                if(handler.getFrom("personal","type").equals("STUDENT"))
                {
                    if(!ap.getProfile().equals("--")) {
                        Glide.with(getApplicationContext()).load(ap.getProfile()).into(Student_home.profile);
                    }
                    Student_home.fullname.setText(ap.getFullname());
                    Student_home.email.setText(ap.getEmail());
                }
                else
                {
                    if(!ap.getProfile().equals("--")) {
                        Glide.with(getApplicationContext()).load(ap.getProfile()).into(Faculty_home.profile);
                    }
                    Faculty_home.fullname.setText(ap.getFullname());
                    Faculty_home.email.setText(ap.getEmail());
                }
                finish();
            }
        });
    }
    public void uploadProfile(Uri uri, final Account_pojo ap)
    {
        final ProgressDialog dialog=new ProgressDialog(Edit_profile.this,R.style.AppCompatAlertDialogStyle);
        StorageReference filereference=sref.child(ap.getUsername()+"");
        filereference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        sref.child(ap.getUsername()+"").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String profile=uri.toString();
                                ap.setProfile(profile);
                                save(ap);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        dialog.setTitle("Updating Profile");
                        dialog.setMessage("Please wait while updating your profile pic !");
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                ischanged=1;
                uri=data.getData();
                Glide.with(getApplicationContext()).load(uri).into(select);
            }
        }
    }
}
