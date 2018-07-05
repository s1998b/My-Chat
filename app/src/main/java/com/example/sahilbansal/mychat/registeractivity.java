package com.example.sahilbansal.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class registeractivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    private DatabaseReference databaseReference;
    private Toolbar rtoolbar;
    private EditText registerusername;
    private EditText registerpassword;
    private EditText registeremail;
    private Button createaccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeractivity);
        mAuth = FirebaseAuth.getInstance();
        rtoolbar = (Toolbar)findViewById(R.id.registertoolbar);
        setSupportActionBar(rtoolbar);
        getSupportActionBar().setTitle("Register Here");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        registeremail = (EditText)findViewById(R.id.register_email);
        registerpassword = (EditText)findViewById(R.id.register_pass);
        registerusername = (EditText)findViewById(R.id.register_name);
        createaccount = (Button)findViewById(R.id.register_create);
        loadingbar = new ProgressDialog(this);
        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = registerusername.getText().toString().trim();
                String email = registeremail.getText().toString().trim();
                String password = registerpassword.getText().toString().trim();
                Registeraccount(name , email, password);
            }
        });
    }

    private void Registeraccount(final String name, String email, String password) {

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please write your name", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please write your Email", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Creating Account");
            loadingbar.setMessage("Please wait....");
            loadingbar.show();
             mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                  if(task.isSuccessful())
                  {
                      String device_tocken = FirebaseInstanceId.getInstance().getToken();

                      String current_user_id = mAuth.getCurrentUser().getUid();
                      databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
                      databaseReference.child("User_name").setValue(name);
                      databaseReference.child("User_Status").setValue("Hey i am using mychat application");
                      databaseReference.child("Decive_tocken").setValue(device_tocken);
                      databaseReference.child("User_image").setValue("default_image");
                      databaseReference.child("User_thumb_image").setValue("default_image").addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful())
                              {
                                  Toast.makeText(registeractivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                  Intent intent = new Intent(registeractivity.this , MainActivity.class);
                                  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                  startActivity(intent);
                                  finish();
                              }
                              else
                              {
                                  Toast.makeText(registeractivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                              }
                          }
                      });

                  }
                  else
                  {
                      Toast.makeText(registeractivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                  }
                  loadingbar.dismiss();
                 }
             });
        }
    }
}
