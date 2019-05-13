package com.example.mock_test;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmaltais.calcdialog.CalcDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.iwgang.countdownview.CountdownView;

import static com.example.mock_test.Student_main.Responses;
import static com.example.mock_test.Student_main.ctql;
import static com.example.mock_test.Student_main.curenteui;
import static com.example.mock_test.Student_main.mtpbdef;
import static com.example.mock_test.Student_main.osequence;
import static com.example.mock_test.Student_main.sequence;

public class Start_mock_test extends AppCompatActivity {

    TextView exam_name,maxmark;
    CountdownView counter_view;
    Helper h=new Helper();
    LinearLayout qc;
    static ArrayList<String> skipped=new ArrayList<>();
    static ArrayList<String> answerd=new ArrayList<>();
    static ArrayList<String> unanswerd=new ArrayList<>();
    ViewPager vp;
    BigDecimal value=null;
    MainPagerAdapter pagerAdapter;
    CalcDialog calcDialog;
    int tot=20;
    FirebaseDatabase fd;
    DatabaseReference ref,ref1;
    Shared_handler shh;
    String date,euid,stime,etime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_VOLUME_UP)
        {
            if(mtpbdef.getAllow_calc().equals("true")) {
                calcDialog.show(getSupportFragmentManager(), "calc_dialog");
            }
        }
        else if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(Start_mock_test.this,R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Are you sure ?");
            builder.setMessage("If you go back you will not allowed to enter in this test again . Your response will be submitted .");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, int id)
                {
                    //savresponse
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        else if(keyCode==KeyEvent.KEYCODE_HOME)
        {

        }
        return super.onKeyDown(keyCode, event);
    }
    public void loadMTPB()
    {
        ref.child(date).child(euid).child("Exam basic").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                mtpbdef=dataSnapshot.getValue(mock_test_basic_pojo.class);
                if(!curenteui.equals(euid))
                {
                    prepareList();
                }
                else {
                    loadComponents();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_mock_test);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Intent intent=getIntent();
        date=intent.getStringExtra("date");
        euid=intent.getStringExtra("euid");
        stime=intent.getStringExtra("stime");
        etime=intent.getStringExtra("etime");
        fd=FirebaseDatabase.getInstance();
        ref=fd.getReference("Exams");
        shh=new Shared_handler(getApplicationContext());
        ref1=fd.getReference("Users");
        loadMTPB();
    }
    private void prepareList()
    {
        final ProgressDialog dialog=new ProgressDialog(Start_mock_test.this);
        dialog.setTitle("Please wait....");
        dialog.setMessage("Preparing resources for exam");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ctql.clear();
        sequence.clear();
        ref.child(date).child(euid).child("Questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot data:dataSnapshot.getChildren())
                {
                    int qno=Integer.parseInt(data.getKey());
                    Question_pojo qp=data.getValue(Question_pojo.class);
                    sequence.add(qno);
                    osequence.add(qno);
                    Responses.add("UNANSWERED]-]INCORRECT");
                    ctql.add(qp);
                }
                Collections.shuffle(sequence);
                curenteui=euid;
                dialog.hide();
                loadComponents();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void Submit_Paper()
    {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(Start_mock_test.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Submitting")
                .fadeColor(Color.DKGRAY)
                .build();
        dialog.show();
        for(int i=0;i<Responses.size();i++)
        {
            final int finalI = i;
            ref1.child(shh.getFrom("personal", "username")).child("My Results").child(date).child(euid).child("Response").child((i+1)+"").setValue(Responses.get(i)+"").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(finalI ==(Responses.size()-1))
                    {
                        ref.child(date).child(euid).child("Candidates").child(shh.getFrom("personal","username")).setValue("SUBMITTED").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                Toast.makeText(getApplicationContext(),"Response submitted !",Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(getApplicationContext(),show_Result.class);
                                intent.putExtra("date",date);
                                intent.putExtra("euid",euid);
                                intent.putExtra("ftime","true");
                                startActivity(intent);
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                }
            });
        }
    }
    public void loadComponents()
    {
        maxmark=findViewById(R.id.max_marks);
        calcDialog=new CalcDialog();
        calcDialog.setValue(value);
        qc=findViewById(R.id.question_container);
        counter_view=findViewById(R.id.counter_view);
        long remtime=0;
        Button submit=findViewById(R.id.submit);
        Date ed=h.getDate(etime);
        Date cd=h.getDate(h.getTodaydate("hh:mm:ss a"));
        remtime=ed.getTime()-cd.getTime();
        counter_view.start(remtime);
        counter_view.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv)
            {
               Submit_Paper();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(Start_mock_test.this,R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Submit your paper now ?");
                builder.setMessage("Your response will be submitted and you are not allowed to attempt this paper again.");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id)
                    {
                        Submit_Paper();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
        vp=findViewById(R.id.viewpager);
        pagerAdapter = new MainPagerAdapter();
        vp.setAdapter (pagerAdapter);
        maxmark.setText("Max. marks : "+mtpbdef.getMax_marks());
        for(int i=0;i<sequence.size();i++)
        {
            int qno=sequence.get(i);
            int index=osequence.indexOf(qno);
            Question_pojo qp=ctql.get(index);
            unanswerd.add(i+"");
            addQuestion(i,(qno-1),qp);
        }
    }
    public void addQuestion(final int no, final int ono, final Question_pojo qp)
    {
        LayoutInflater inflater= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View v=inflater.inflate(R.layout.question_conatiner_view,null);
        final TextView button=v.findViewById(R.id.question_button);
        button.setText((no+1)+"");
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"NewApi", "ResourceAsColor"})
            @Override
            public void onClick(View view)
            {
                vp.setCurrentItem(no);
            }
        });
        int dr=1;
        if(qp.getType().equals("MCQ"))
        {
            dr=0;
        }
        FrameLayout v0=null;
        if(dr==1)
        {
            v0 = (FrameLayout) inflater.inflate(R.layout.direct_reply_question_style, null);
            final EditText reply=v0.findViewById(R.id.answer);
            TextView question_no=v0.findViewById(R.id.question_no);
            TextView question_marks=v0.findViewById(R.id.question_marks);
            question_marks.setText("Marks : "+qp.getMarks());
            question_no.setText("Question "+(no+1)+"");
            TextView question=v0.findViewById(R.id.question);
            question.setText(qp.getQuestion());
            Button submit=v0.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener()
            {
                @SuppressLint({"NewApi", "ResourceAsColor"})
                @Override
                public void onClick(View view)
                {
                    int next=getNextUnAnsweredorSkippedQuestion(no+"");
                    if(next==-1)
                    {
                        Toast.makeText(getApplicationContext(),"Answered All Questions !",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        vp.setCurrentItem(next);
                    }
                }
            });
            reply.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                    if(reply.length()>0)
                    {
                        if(reply.getText().toString().equals(qp.getCorrect()))
                        {
                            Responses.set(ono,reply.getText().toString()+"]-]CORRECT");
                        }
                        else
                        {
                            Responses.set(ono,reply.getText().toString()+"]-]INCORRECT");
                        }
                        if(skipped.contains(no+""))
                        {
                            skipped.remove(no+"");
                        }
                        if(unanswerd.contains(no+""))
                        {
                            unanswerd.remove(no+"");
                        }
                        if(!answerd.contains(no+"")) {
                            answerd.add(no+"");
                        }
                        button.setBackgroundColor(Color.parseColor("#26C705"));
                    }
                    else
                    {
                        Responses.set(ono,"SKIPPED]-]INCORRECT");
                        if(!skipped.contains(no+"")) {
                            skipped.add(no+"");
                        }
                        if(answerd.contains(no+""))
                        {
                            answerd.remove(no+"");
                        }
                        button.setBackgroundColor(Color.parseColor("#D81B1B"));
                    }
                }
                @Override
                public void afterTextChanged(Editable editable)
                {

                }
            });
        }
        else
        {
            v0 = (FrameLayout) inflater.inflate(R.layout.multiple_choice_question_style, null);
            TextView question_no=v0.findViewById(R.id.question_no);
            question_no.setText("Question "+(no+1)+"");
            TextView question=v0.findViewById(R.id.question);
            question.setText(qp.getQuestion());
            TextView question_marks=v0.findViewById(R.id.question_marks);
            question_marks.setText("Marks : "+qp.getMarks());
            String options=qp.getOptions();
            String sp[] =options.split("]-VKAPPS-]");
            final String correct=sp[Integer.parseInt(qp.getCorrect())-1];
            ArrayList<String> oplist=new ArrayList<>();
            final ArrayList<String> colist=new ArrayList<>();
            for(int i=0;i<sp.length;i++)
            {
                oplist.add(sp[i]+"");
                colist.add(sp[i]+"");
            }
            Collections.shuffle(oplist);
            final int[] checked = {0};
            final CheckBox op1=v0.findViewById(R.id.op1);
            final CheckBox op2=v0.findViewById(R.id.op2);
            final CheckBox op3=v0.findViewById(R.id.op3);
            final CheckBox op4=v0.findViewById(R.id.op4);
            op1.setText(oplist.get(0)+"");
            op2.setText(oplist.get(1)+"");
            op3.setText(oplist.get(2)+"");
            op4.setText(oplist.get(3)+"");
            op1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                {
                    if(b==true)
                    {
                        String selected=op1.getText().toString();
                        if(correct.equals(selected))
                        {
                            Responses.set(ono,(colist.indexOf(selected)+1)+"]-]CORRECT");
                        }
                        else
                        {
                            Responses.set(ono,(colist.indexOf(selected)+1)+"]-]INCORRECT");
                        }
                        checked[0] =1;
                        if(skipped.contains(no+""))
                        {
                            skipped.remove(no+"");
                        }
                        if(unanswerd.contains(no+""))
                        {
                            unanswerd.remove(no+"");
                        }
                        if(!answerd.contains(no+"")) {
                            answerd.add(no+"");
                        }
                        button.setBackgroundColor(Color.parseColor("#26C705"));
                        op2.setChecked(false);
                        op3.setChecked(false);
                        op4.setChecked(false);
                    }
                    else
                    {
                        if(checked[0]==1)
                        {
                            checked[0] = 0;
                            Responses.set(ono,"SKIPPED]-]INCORRECT");
                            if (!skipped.contains(no + "")) {
                                skipped.add(no + "");
                            }
                            if (answerd.contains(no + "")) {
                                answerd.remove(no + "");
                            }
                            button.setBackgroundColor(Color.parseColor("#D81B1B"));
                        }
                    }
                }
            });
            op2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                {
                    if(b==true)
                    {
                        String selected=op2.getText().toString();
                        if(correct.equals(selected))
                        {
                            Responses.set(ono,(colist.indexOf(selected)+1)+"]-]CORRECT");
                        }
                        else
                        {
                            Responses.set(ono,(colist.indexOf(selected)+1)+"]-]INCORRECT");
                        }
                        if(skipped.contains(no+""))
                        {
                            skipped.remove(no+"");
                        }
                        if(unanswerd.contains(no+""))
                        {
                            unanswerd.remove(no+"");
                        }
                        if(!answerd.contains(no+"")) {
                            answerd.add(no+"");
                        }
                        button.setBackgroundColor(Color.parseColor("#26C705"));
                        checked[0] =2;
                        op1.setChecked(false);
                        op3.setChecked(false);
                        op4.setChecked(false);
                    }
                    else
                    {
                        if(checked[0]==2) {
                            checked[0] = 0;
                            Responses.set(ono,"SKIPPED]-]INCORRECT");
                            if (!skipped.contains(no + "")) {
                                skipped.add(no + "");
                            }
                            if (answerd.contains(no + "")) {
                                answerd.remove(no + "");
                            }
                            button.setBackgroundColor(Color.parseColor("#D81B1B"));
                        }
                    }
                }
            });
            op3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                {
                    if(b==true)
                    {
                        String selected=op3.getText().toString();
                        if(correct.equals(selected))
                        {
                            Responses.set(ono,(colist.indexOf(selected)+1)+"]-]CORRECT");
                        }
                        else
                        {
                            Responses.set(ono,(colist.indexOf(selected)+1)+"]-]INCORRECT");
                        }
                        if(skipped.contains(no+""))
                        {
                            skipped.remove(no+"");
                        }
                        if(unanswerd.contains(no+""))
                        {
                            unanswerd.remove(no+"");
                        }
                        if(!answerd.contains(no+"")) {
                            answerd.add(no+"");
                        }
                        button.setBackgroundColor(Color.parseColor("#26C705"));
                        checked[0] =3;
                        op2.setChecked(false);
                        op1.setChecked(false);
                        op4.setChecked(false);
                    }
                    else
                    {
                        if(checked[0]==3) {
                            checked[0] = 0;
                            Responses.set(ono,"SKIPPED]-]INCORRECT");
                            if (!skipped.contains(no + "")) {
                                skipped.add(no + "");
                            }
                            if (answerd.contains(no + "")) {
                                answerd.remove(no + "");
                            }
                            button.setBackgroundColor(Color.parseColor("#D81B1B"));
                        }
                    }
                }
            });
            op4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                {
                    if(b==true)
                    {
                        String selected=op4.getText().toString();
                        if(correct.equals(selected))
                        {
                            Responses.set(ono,(colist.indexOf(selected)+1)+"]-]CORRECT");
                        }
                        else
                        {
                            Responses.set(ono,(colist.indexOf(selected)+1)+"]-]INCORRECT");
                        }
                        if(skipped.contains(no+""))
                        {
                            skipped.remove(no+"");
                        }
                        if(unanswerd.contains(no+""))
                        {
                            unanswerd.remove(no+"");
                        }
                        if(!answerd.contains(no+"")) {
                            answerd.add(no+"");
                        }
                        button.setBackgroundColor(Color.parseColor("#26C705"));
                        checked[0] =4;
                        op2.setChecked(false);
                        op3.setChecked(false);
                        op1.setChecked(false);
                    }
                    else
                    {
                        if(checked[0]==4)
                        {
                            Responses.set(ono,"SKIPPED]-]INCORRECT");
                            checked[0] = 0;
                            if (!skipped.contains(no + "")) {
                                skipped.add(no + "");
                            }
                            if (answerd.contains(no + "")) {
                                answerd.remove(no + "");
                            }
                            button.setBackgroundColor(Color.parseColor("#D81B1B"));
                        }
                    }
                }
            });
            Button submit=v0.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener()
            {
                @SuppressLint({"NewApi", "ResourceAsColor"})
                @Override
                public void onClick(View view)
                {
                    int next=getNextUnAnsweredorSkippedQuestion(no+"");
                    if(next==-1)
                    {
                        Toast.makeText(getApplicationContext(),"Answered All Questions !",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        vp.setCurrentItem(next);
                    }
                }
            });
        }
        pagerAdapter.addView (v0);
        pagerAdapter.notifyDataSetChanged();
        qc.addView(v);
    }

    private int getNextUnAnsweredorSkippedQuestion(String no)
    {
        int next=-1;
        int no0=Integer.parseInt(no);
        if(unanswerd.contains((no0+1)+""))
        {
            next=no0+1;
        }
        else
        {
            for(int i=0;i<unanswerd.size();i++)
            {
                int tm=Integer.parseInt(unanswerd.get(i));
                if(tm>no0)
                {
                    next=tm;
                    break;
                }
                else
                {
                    next=-2;
                }
            }
            if(next==-2)
            {
                if(unanswerd.size()>0)
                {
                    next=Integer.parseInt(unanswerd.get(0));
                }
                else
                {
                    next=-1;
                }
            }
        }
        return next;
    }
    public void addView (View newPage)
    {
        int pageIndex = pagerAdapter.addView (newPage);
        vp.setCurrentItem (pageIndex, true);
    }
    public void removeView (View defunctPage)
    {
        int pageIndex = pagerAdapter.removeView (vp, defunctPage);
        if (pageIndex == pagerAdapter.getCount())
            pageIndex--;
        vp.setCurrentItem (pageIndex);
    }
    public View getCurrentPage ()
    {
        return pagerAdapter.getView (vp.getCurrentItem());
    }
    public void setCurrentPage (View pageToShow)
    {
        vp.setCurrentItem (pagerAdapter.getItemPosition (pageToShow), true);
    }

}
