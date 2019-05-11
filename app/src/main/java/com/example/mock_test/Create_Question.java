package com.example.mock_test;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.mock_test.Add_questions.adContext;
import static com.example.mock_test.Add_questions.mark;
import static com.example.mock_test.Add_questions.qno;
import static com.example.mock_test.Add_questions.ques;
import static com.example.mock_test.Add_questions.tm;
import static com.example.mock_test.Add_questions.tq;
import static com.example.mock_test.Faculty_main.mtbp;
import static com.example.mock_test.Faculty_main.questionlist;

public class Create_Question extends AppCompatActivity {

    CheckBox mcq,drq;
    EditText marks,question,answer,op1,op2,op3,op4;
    Button sac,san;
    RelativeLayout mcqview;
    String breaker="]-VKAPPS-]";
    String type="MCQ";
    String isedit="false";
    int qno=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__question);
        getSupportActionBar().hide();
        Intent intent=getIntent();
        if(intent.hasExtra("edit"))
        {
            isedit = intent.getStringExtra("edit");
            qno=Integer.parseInt(intent.getStringExtra("Qno"));
        }
        fetch();
    }
    public void loadQuestion()
    {
        Question_pojo qp=questionlist.get(qno);
        marks.setText(qp.getMarks()+"");
        question.setText(qp.getQuestion()+"");
        answer.setText(qp.getCorrect());
        if(qp.getType().equals("MCQ"))
        {
            mcq.setChecked(true);
            String[] sp=qp.getOptions().split(breaker);
            op1.setText(sp[0]);
            op2.setText(sp[1]);
            op3.setText(sp[2]);
            op4.setText(sp[3]);
        }
        else
        {
            if(mcq.isChecked())
            {
                mcq.setChecked(false);
            }
            drq.setChecked(true);
        }
    }
    private void fetch()
    {
        mcq=findViewById(R.id.mcq);
        drq=findViewById(R.id.drq);
        mcqview=findViewById(R.id.MCQView);
        marks=findViewById(R.id.marks);
        question=findViewById(R.id.question);
        answer=findViewById(R.id.answer);
        op1=findViewById(R.id.answer1);
        op2=findViewById(R.id.answer2);
        op3=findViewById(R.id.answer3);
        op4=findViewById(R.id.answer4);
        sac=findViewById(R.id.sac);
        san=findViewById(R.id.san);
        if(isedit.equals("true"))
        {
            loadQuestion();
        }
        mcq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if(b==true)
                {
                    drq.setChecked(false);
                    type="MCQ";
                    if(mcqview.getVisibility()==View.GONE)
                    {
                        mcqview.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    if(mcqview.getVisibility()==View.VISIBLE)
                    {
                        mcqview.setVisibility(View.GONE);
                    }
                }
            }
        });
        drq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if(b==true)
                {
                    type="DRQ";
                    mcq.setChecked(false);
                }
            }
        });
        sac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(question.getText().toString().length()>0)
                {
                    if(answer.getText().toString().length()>0)
                    {
                        if(Integer.parseInt(marks.getText().toString())>0)
                        {
                            Question_pojo qp=getQuestionPojo();
                            if(isedit.equals("true"))
                            {
                                questionlist.set(qno,qp);
                            }
                            else {
                                questionlist.add(qp);
                                addinList(qp);
                            }
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Marks should be grater than zero !",Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Enter Answer for the question",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Question feild cannot be empty !",Toast.LENGTH_LONG).show();
                }
            }
        });
        san.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(question.getText().toString().length()>0)
                {
                    if(answer.getText().toString().length()>0)
                    {
                        if(Integer.parseInt(marks.getText().toString())>0)
                        {
                            Question_pojo qp=getQuestionPojo();
                            if(isedit.equals("true"))
                            {
                                questionlist.set(qno,qp);
                            }
                            else {
                                questionlist.add(qp);
                                addinList(qp);
                            }
                            question.setText("");
                            answer.setText("");
                            op1.setText("");
                            op2.setText("");
                            op3.setText("");
                            op4.setText("");
                            mcq.setChecked(true);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Marks should be grater than zero !",Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Enter Answer for the question",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Question field cannot be empty !",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public Question_pojo getQuestionPojo()
    {
        Question_pojo qp=new Question_pojo();
        qp.setType(type);
        qp.setCorrect(answer.getText().toString()+"");
        qp.setMarks(marks.getText().toString());
        qp.setQuestion(question.getText().toString());
        if(type.equals("MCQ"))
        {
            qp.setOptions(op1.getText().toString() + breaker + op2.getText().toString() + breaker + op3.getText().toString() + breaker + op4.getText().toString());
        }else
        {
            qp.setOptions("NONE");
        }
        return qp;
    }
    public void addinList(final Question_pojo qp)
    {
        LayoutInflater inflater= (LayoutInflater) adContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View v=inflater.inflate(R.layout.add_question_list_item,null);
        final TextView q=v.findViewById(R.id.question);
        final TextView mat=v.findViewById(R.id.markandtype);
        ImageView del=v.findViewById(R.id.delete_question);
        ImageView edit=v.findViewById(R.id.edit_question);
        q.setText(qp.getQuestion());
        mat.setText(qp.getMarks()+" marks       |       "+qp.getType());
        ques.addView(v);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ques.removeView(v);
                mark=mark-Integer.parseInt(qp.getMarks());
                questionlist.remove(qp);
                tq.setText("Total Question : "+questionlist.size()+"");
                tm.setText("Total marks : "+mark+"");
                mtbp.setMax_marks(mark+"");
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                int pos=questionlist.indexOf(qp);
                Intent intent=new Intent(adContext,Create_Question.class);
                intent.putExtra("Qno",pos+"");
                intent.putExtra("edit","true");
                startActivity(intent);
            }
        });
        mark=mark+Integer.parseInt(qp.getMarks());
        tq.setText("Total Question : "+questionlist.size()+"");
        tm.setText("Total marks : "+mark+"");
        mtbp.setMax_marks(mark+"");
    }
}
