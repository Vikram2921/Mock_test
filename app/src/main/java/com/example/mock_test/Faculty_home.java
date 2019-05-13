package com.example.mock_test;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

import java.util.ArrayList;

public class Faculty_home extends AppCompatActivity implements DuoMenuView.OnMenuClickListener{

    static TextView fullname,email;
    static ImageView profile;
    MenuAdapter mMenuAdapter;
    Button edit,logout;
    Shared_handler shh;
    DuoMenuView duoMenuView;
    static Helper h=new Helper();
    DuoDrawerLayout drawerLayout;
    static Context fcontext;
    Toolbar toolbar;
    ArrayList<String> menulist=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_home);
        toolbar=findViewById(R.id.toolbar);
        drawerLayout=findViewById(R.id.drawer);
        duoMenuView= (DuoMenuView) drawerLayout.getMenuView();
        getSupportActionBar().hide();
        fcontext=getApplicationContext();
        toolbar.setTitle("Home");
        shh=new Shared_handler(getApplicationContext());
        handleDrawer();
        menulist.add("Home");
        menulist.add("About us");
        menulist.add("Feedback");
        menulist.add("Contact us");
        handleMenu();
        goToFragment(new Faculty_main(), false);
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
            public void onClick(View view) {
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
                goToFragment(new Faculty_main(),false);
                break;
            case 1:
                goToFragment(new about_us(),false);
                break;
            case 2:
                goToFragment(new feedback(),false);
                break;
            case 3:
                goToFragment(new contact_us(),false);
                break;
            default:
                goToFragment(new Faculty_main(), false);
                break;
        }
        drawerLayout.closeDrawer();
    }
}
