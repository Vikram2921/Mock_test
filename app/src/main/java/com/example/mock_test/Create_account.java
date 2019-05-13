package com.example.mock_test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Create_account extends AppCompatActivity {

    FirebaseDatabase fd;
    DatabaseReference ref;
    String type="STUDENT";
    String pro="--";
    ImageView select;
    int ischanged=0;
    FirebaseStorage str;
    ProgressDialog dialog;
    StorageReference sref;
    Uri uri;
    public boolean isusernamevalid(String username)
    {
        boolean valid=true;
        if(username.contains(".")||username.contains("$")||username.contains("#")||username.contains("[")||username.contains("]"))
        {
            valid=false;
        }
        return valid;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getSupportActionBar().hide();
        fd=FirebaseDatabase.getInstance();
        ref=fd.getReference("Users");
        str=FirebaseStorage.getInstance();
        sref=str.getReference("Profiles");
        final EditText fullname,username,email,password,cpassword;
        Button create;
        select=findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mediaIntent = new Intent(Intent.ACTION_GET_CONTENT);
                mediaIntent.setType("image/*");
                startActivityForResult(mediaIntent, 1);
            }
        });
        fullname=findViewById(R.id.fullname);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        cpassword=findViewById(R.id.cpassword);
        email=findViewById(R.id.email);
        create=findViewById(R.id.create);
        dialog = new ProgressDialog(Create_account.this);
        dialog.setTitle("Creating your account ");
        dialog.setMessage("Please wait while creating your account !");
        dialog.setCanceledOnTouchOutside(false);
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
                if(isusernamevalid(user)) {
                    if (full.length() > 0) {
                        if (user.length() > 6) {
                            if (isEmailValid(emid) == true) {
                                if (cpass.equals(pass))
                                {
                                    dialog.show();
                                    Account_pojo ap = new Account_pojo();
                                    ap.setEmail(emid);
                                    ap.setFullname(full);
                                    ap.setPassword(pass);
                                    ap.setProfile(pro);
                                    ap.setType(type);
                                    ap.setUsername(user);
                                    if(ischanged==0)
                                    {
                                        save(ap);
                                    }
                                    else
                                    {
                                        uploadProfile(uri,ap);
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Password not matched !", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid Email ID. ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Username lenght should be greater than 6", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Fullname cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Invalid Username !",Toast.LENGTH_LONG).show();
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
                if(isusernamevalid(user)) {
                    if (full.length() > 0) {
                        if (user.length() > 6) {
                            if (isEmailValid(emid) == true) {
                                if (cpass.equals(pass)) {
                                    dialog.show();
                                    Account_pojo ap = new Account_pojo();
                                    ap.setEmail(emid);
                                    ap.setFullname(full);
                                    ap.setPassword(pass);
                                    ap.setProfile(pro);
                                    ap.setType(type);
                                    ap.setUsername(user);
                                    if(ischanged==0)
                                    {
                                        save(ap);
                                    }
                                    else
                                    {
                                        uploadProfile(uri,ap);
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Password not matched !", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid Email ID. ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Username lenght should be greater than 6", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Fullname cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    Toast.makeText(getApplicationContext(),"Invalid Username !",Toast.LENGTH_SHORT).show();
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
    public void save(final Account_pojo ap)
    {
        dialog.setTitle("Finialising account");
        dialog.setMessage("Setting up your account information ...");
        ref.child(ap.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                {
                    ref.child(ap.getUsername()).child("Personal Details").setValue(ap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            login_activity.user.setText(ap.getUsername() + "");
                            login_activity.pass.setText(ap.getPassword() + "");
                            dialog.dismiss();
                            finish();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Username not available !", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void uploadProfile(Uri uri, final Account_pojo ap)
    {
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
                    }
                });
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
