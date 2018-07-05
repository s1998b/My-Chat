package com.example.sahilbansal.mychat;

import android.content.Intent;
import android.os.TransactionTooLargeException;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ViewPager myviewPager;
    private TabLayout myTablayout;
    private TabsPagerAdaptor mytabspageradaptor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        myviewPager = (ViewPager)findViewById(R.id.main_view_pager);
        mytabspageradaptor = new TabsPagerAdaptor(getSupportFragmentManager());
        myviewPager.setAdapter(mytabspageradaptor);
        myTablayout = (TabLayout)findViewById(R.id.main_tabs);
        myTablayout.setupWithViewPager(myviewPager);
        mtoolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("mychat");
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        if(currentuser==null)
        {
           Logoutuser();
        }
    }

    private void Logoutuser() {
        Intent intent = new Intent(MainActivity.this,startpageactivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mychat_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.main_logout_button)
        {
             mAuth.signOut();
             Logoutuser();
        }
        if(item.getItemId()==R.id.mainaccountsettings_button)
        {
             Intent intent = new Intent(MainActivity.this , Settings.class);
             startActivity(intent);
        }
        if(item.getItemId()==R.id.main_allusers)
        {
             Intent intent = new Intent(MainActivity.this , Alluser.class);
             startActivity(intent);
        }
        return true;
    }
}
