package com.example.mock_test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.zip.Inflater;

import static com.example.mock_test.Faculty_main.mtbp;
import static com.example.mock_test.Faculty_main.questionlist;

public class Add_questions extends AppCompatActivity {

    String date,euid;
    static LinearLayout ques;
    ImageView aq,sp;
    FirebaseDatabase fd;
    DatabaseReference ref,ref1;
    Helper h=new Helper();
    static int qno=0;
    static TextView tq,tm;
    static int mark=0;
    Shared_handler sh;
    static Context adContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);
        Intent intent=getIntent();
        getSupportActionBar().hide();
        date=intent.getStringExtra("date");
        euid=intent.getStringExtra("euid");
        fd=FirebaseDatabase.getInstance();
        ref=fd.getReference("Exams");
        ref1=fd.getReference("Users");
        sh=new Shared_handler(getApplicationContext());
        adContext=getApplicationContext();
        fetch();
    }
    private void fetch()
    {
        ques=findViewById(R.id.question_container);
        aq=findViewById(R.id.add_question);
        sp=findViewById(R.id.submit_paper);
        tq=findViewById(R.id.tques);
        tm=findViewById(R.id.tmarks);
        aq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(getApplicationContext(),Create_Question.class);
                intent.putExtra("date",date);
                intent.putExtra("euid",euid);
                startActivity(intent);
            }
        });
        sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(questionlist.size()>0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Add_questions.this);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.alert_input, null);
                    final EditText input = v.findViewById(R.id.input);
                    builder.setView(v);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String mark1 = input.getText().toString();
                            if (mark1.length() > 0 || mark1.equals("0")) {
                                dialog.dismiss();
                                mtbp.setPass_marks(mark1 + "");
                                AlertDialog.Builder builder = new AlertDialog.Builder(Add_questions.this, R.style.AppCompatAlertDialogStyle);
                                builder.setTitle("Want to start publishing your paper now ?");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, int id) {
                                        final ProgressDialog dialog1 = new ProgressDialog(Add_questions.this);
                                        dialog1.setTitle("Publishing your paper ....");
                                        dialog1.setMessage("This may take a while .");
                                        dialog1.setCanceledOnTouchOutside(false);
                                        dialog1.show();
                                        ref.child(date).child(euid).child("Exam basic").setValue(mtbp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                for (int i = 0; i < questionlist.size(); i++) {
                                                    dialog1.setMessage("Uploading Question no # " + (i + 1));
                                                    Question_pojo qp = questionlist.get(i);
                                                    final int finalI = i;
                                                    ref.child(date).child(euid).child("Questions").child((i + 1) + "").setValue(qp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (finalI == questionlist.size() - 1)
                                                            {
                                                                String [] sp=mtbp.getExamdate().split("]-]");
                                                                String dd=sp[0]+""+sp[1]+""+sp[2];
                                                                ref1.child(sh.getFrom("personal","username")).child("MYPAPERS").child(mtbp.getExamname()).setValue(h.getTodaydate("dd/MM/yyyy")+"]-]"+h.getCurrentTime("hh:mm:ss a")+"]-]"+dd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        dialog1.dismiss();
                                                                        finish();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                }
                                                dialog1.dismiss();
                                                finish();
                                            }
                                        });
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog dialog0 = builder.create();
                                dialog0.setCanceledOnTouchOutside(false);
                                dialog0.show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid passing marks !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }else
                {
                    Toast.makeText(getApplicationContext(),"Please create atleast 1 question !",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
