package com.example.mock_test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class Student_home extends AppCompatActivity implements DuoMenuView.OnMenuClickListener {

    static TextView fullname,email;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    static ImageView profile;
    MenuAdapter mMenuAdapter;
    static int isinexam=0;
    static Helper h=new Helper();
    Button edit,logout;
    Shared_handler shh;
    DuoMenuView duoMenuView;
    DuoDrawerLayout drawerLayout;
    Toolbar toolbar;
    FirebaseDatabase fd;
    DatabaseReference ref;
    ArrayList<String> menulist=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
        toolbar=findViewById(R.id.toolbar);
        drawerLayout=findViewById(R.id.drawer);
        duoMenuView= (DuoMenuView) drawerLayout.getMenuView();
        getSupportActionBar().hide();
        toolbar.setTitle("Home");
        toolbar.inflateMenu(R.menu.student_home);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                int id=menuItem.getItemId();
                if(id==R.id.scanner)
                {
                    Intent i = new Intent(getApplicationContext(), QrCodeActivity.class);
                    startActivityForResult( i,REQUEST_CODE_QR_SCAN);
                }
                return false;
            }
        });
        shh=new Shared_handler(getApplicationContext());
        handleDrawer();
        menulist.add("Home");
        menulist.add("Results");
        menulist.add("About us");
        menulist.add("Feedback");
        menulist.add("Contact us");
        fd=FirebaseDatabase.getInstance();
        ref=fd.getReference("Exams");
        handleMenu();
        goToFragment(new Student_main(), false);
        fetchD();
    }


    private void fetchD()
    {
        fullname=findViewById(R.id.name);
        email=findViewById(R.id.email);
        profile=findViewById(R.id.profile);
        edit=findViewById(R.id.edit);
        logout=findViewById(R.id.logout);
        String pro=shh.getFrom("personal","profile");
        if(!pro.equals("--"))
        {
            Glide.with(getApplicationContext()).load(pro).into(profile);
        }
        fullname.setText(""+ shh.getFrom("personal","fullname"));
        email.setText(""+ shh.getFrom("personal","email"));
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(getApplicationContext(),Edit_profile.class);
                startActivity(intent);

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shh.Clear("personal");
                Intent intent=new Intent(getApplicationContext(),login_activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void handleDrawer()
    {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();
    }
    private void handleMenu()
    {
        mMenuAdapter = new MenuAdapter(menulist);
        duoMenuView.setOnMenuClickListener(this);
        duoMenuView.setAdapter(mMenuAdapter);
    }

    @Override
    public void onFooterClicked()
    {

    }

    @Override
    public void onHeaderClicked() {

    }
    private void goToFragment(Fragment fragment, boolean addToBackStack)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (addToBackStack)
        {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container, fragment).commit();
    }
    @Override
    public void onOptionClicked(int position, Object objectClicked)
    {
        toolbar.setTitle(menulist.get(position).toString());
        mMenuAdapter.setViewSelected(position, true);
        switch (position)
        {
            case 0:
                    goToFragment(new Student_main(),false);
                    break;
            case 1:
                goToFragment(new results_student(),false);
                break;
            case 2:
                goToFragment(new about_us(),false);
                break;
            case 3:
                goToFragment(new feedback(),false);
                break;
            case 4:
                goToFragment(new contact_us(),false);
                break;
            default:
                goToFragment(new Student_main(), false);
                break;
        }
        drawerLayout.closeDrawer();
    }
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if(resultCode != Activity.RESULT_OK)
        {
            if(data==null)
                return;
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                Toast.makeText(getApplicationContext(),"Scan Error !",Toast.LENGTH_LONG).show();
            }
            return;

        }
        if(requestCode == REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;
            final ProgressDialog dialog=new ProgressDialog(Student_home.this);
            dialog.setTitle("Please wait ....");
            dialog.setMessage("Verify exam details please wait ....");
            dialog.show();
            final String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            if(result.contains("/")||result.contains(":"))
            {
                Toast.makeText(getApplicationContext(),"Invalid Qr code !",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
            else {
                final String td = getTodaydateinformat();
                ref.child(td).child(result).child("Exam basic").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            mock_test_basic_pojo mtbp = dataSnapshot.getValue(mock_test_basic_pojo.class);
                            final String stime = mtbp.getExam_start_time();
                            String[] sp = stime.split("]-]");
                            int sh = Integer.parseInt(sp[0]);
                            final int sm = Integer.parseInt(sp[1]);
                            String a = "AM";
                            if (sh > 12) {
                                a = "PM";
                                sh = sh - 12;
                            }
                            String etime = mtbp.getExam_end_time();
                            String[] ep = etime.split("]-]");
                            int eh = Integer.parseInt(ep[0]);
                            final int em = Integer.parseInt(ep[1]);
                            String e = "AM";
                            if (eh > 12) {
                                e = "PM";
                                eh = eh - 12;
                            }
                            String stimedef = convertoTwo(sh) + ":" + convertoTwo(sm) + ":00 " + a;
                            String etimedef = convertoTwo(eh) + ":" + convertoTwo(em) + ":00 " + e;
                            Intent intent = new Intent(getApplicationContext(), nexttoenter.class);
                            intent.putExtra("date", td);
                            intent.putExtra("euid", result);
                            intent.putExtra("stime", stimedef);
                            intent.putExtra("etime", etimedef);
                            startActivity(intent);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong QR code !", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
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
    public String getTodaydateinformat()
    {
        Calendar calendar=Calendar.getInstance();
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        return convertoTwo(day)+""+convertoTwo(month)+""+year;
    }
}
