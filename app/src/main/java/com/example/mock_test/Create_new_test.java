package com.example.mock_test;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.mock_test.Faculty_home.h;
import static com.example.mock_test.Faculty_main.mtbp;

public class Create_new_test extends AppCompatActivity
{
    FirebaseDatabase fd;
    DatabaseReference ref;
    ImageView cst,cdu,cd;
    Shared_handler shh;
    TextView stoe,dtoe,doe;
    EditText exam_name,exam_fullname;
    CheckBox ac,rqr;
    Calendar calendar;
    int hour=0,minute=0;
    TextView td;
    Button next;
    int sh=0,sm=0,eh=0,em=0,dh=0,dm=0;
    int dyear=0,dmonth=0,dday=0,eday=00,emonth=00,eyear=0000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_test);
        getSupportActionBar().hide();
        fd=FirebaseDatabase.getInstance();
        ref=fd.getReference("Exams");
        calendar=Calendar.getInstance();
        shh=new Shared_handler(getApplicationContext());
        fetch();
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
    private void fetch()
    {
        cst=findViewById(R.id.choose_time);
        cdu=findViewById(R.id.choose_duration);
        stoe=findViewById(R.id.time_of_exam);
        dtoe=findViewById(R.id.duration_of_exam);
        td=findViewById(R.id.total_duration);
        cd=findViewById(R.id.choose_date);
        doe=findViewById(R.id.date_of_exam);
        exam_name=findViewById(R.id.examname);
        exam_fullname=findViewById(R.id.examnamefull);
        exam_name.setText(h.GetnewEUID()+"");
        ac=findViewById(R.id.ac);
        rqr=findViewById(R.id.rqr);
        next=findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final String examname=exam_fullname.getText().toString();
                if(examname.length()>0)
                {
                    if(eday!=0)
                    {
                        if(sh!=0)
                        {
                            if(eh!=0)
                            {
                                mtbp=new mock_test_basic_pojo();
                                mtbp.setAllow_calc(ac.isChecked()+"");
                                mtbp.setEmam_total_duration(dh+"]-]"+dm);
                                mtbp.setExam_end_time(eh+"]-]"+em);
                                mtbp.setExam_start_time(sh+"]-]"+sm);
                                mtbp.setExamdate(convertoTwo(eday)+"]-]"+convertoTwo(emonth)+"]-]"+eyear);
                                mtbp.setExamname(exam_name.getText().toString());
                                mtbp.setMax_marks("0");
                                mtbp.setExam_fullname(examname);
                                mtbp.setRequier_qr(rqr.isChecked()+"");
                                mtbp.setPass_marks("0");
                                mtbp.setExam_creator(shh.getFrom("personal","username"));
                                Intent intent=new Intent(getApplicationContext(),Add_questions.class);
                                intent.putExtra("date",convertoTwo(eday)+""+convertoTwo(emonth)+""+eyear+"");
                                intent.putExtra("euid",exam_name.getText().toString());
                                startActivity(intent);
                                finish();

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Select exam end time !",Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Select exam start time !",Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Select exam date !",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Enter valid exam name !",Toast.LENGTH_LONG).show();
                }
            }
        });
        cst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(hour==0)
                {
                    hour=calendar.get(Calendar.HOUR_OF_DAY);
                    minute=calendar.get(Calendar.MINUTE);
                }
                TimePickerDialog timePickerDialog=new TimePickerDialog(Create_new_test.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1)
                    {
                        hour=i;
                        minute=i1;
                        sh=i;
                        sm=i1;
                        String a="AM";
                        if(i>12)
                        {
                            i=i-12;
                            a="PM";
                        }
                        stoe.setText(convertoTwo(i)+" : "+convertoTwo(i1)+" : 00 "+a);
                    }
                },hour,minute,false);
                timePickerDialog.setTitle("Select starting time");
                timePickerDialog.show();
            }
        });
        cdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(hour==0)
                {
                    hour=calendar.get(Calendar.HOUR_OF_DAY);
                    minute=calendar.get(Calendar.MINUTE);
                }
                TimePickerDialog timePickerDialog=new TimePickerDialog(Create_new_test.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1)
                    {
                        hour=i;
                        eh=i;
                        em=i1;
                        minute=i1;
                        String a="AM";
                        if(i>12)
                        {
                            i=i-12;
                            a="PM";
                        }
                        dtoe.setText(convertoTwo(i)+" : "+convertoTwo(i1)+" : 00 "+a);
                        if(sh!=0)
                        {
                            if(eh>sh) {
                                dh = eh - sh;
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Invalid time !",Toast.LENGTH_LONG).show();
                            }
                        }
                        if(sm!=0)
                        {
                            dm=em-sm;
                        }
                        td.setText("Exam Duration :: "+convertoTwo(dh)+" Hour "+convertoTwo(dm)+" Minutes 00 Seconds");
                    }
                },hour,minute,false);
                timePickerDialog.setTitle("Select starting time");
                timePickerDialog.show();
            }
        });
        cd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(dday==0)
                {
                    dday=calendar.get(Calendar.DAY_OF_MONTH);
                    dyear=calendar.get(Calendar.YEAR);
                    dmonth=calendar.get(Calendar.MONTH);
                }
                DatePickerDialog datePickerDialog=new DatePickerDialog(Create_new_test.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                    {
                        doe.setText(convertoTwo(i2)+" / "+convertoTwo((i1+1))+" / "+i);
                        eday=i2;
                        emonth=i1;
                        eyear=i;
                        dyear=eyear;
                        dmonth=emonth;
                        dday=eday;
                    }
                },dyear,dmonth,dday);
                datePickerDialog.setTitle("Select exam date");
                datePickerDialog.show();
            }
        });
    }
}
