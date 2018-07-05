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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class loginactivity extends AppCompatActivity {
private Toolbar toolbar;
private EditText loginemail;
private EditText loginpassword;
private Button loginbutton;
private FirebaseAuth mAuth;
private ProgressDialog progressDialog;
private DatabaseReference userreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);
        mAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.logintoolbar);
        loginbutton = (Button)findViewById(R.id.login_button);
        loginemail = (EditText)findViewById(R.id.login_email);
        progressDialog = new ProgressDialog(this);
        loginpassword = (EditText)findViewById(R.id.login_password);
        userreference = FirebaseDatabase.getInstance().getReference().child("Users");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sing In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginemail.getText().toString().trim();
                String password = loginpassword.getText().toString().trim();
                Loginuser(email,password);
            }
        });
    }

    private void Loginuser(String email, String password) {

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
            progressDialog.setTitle("Login into your account");
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
             mAuth.signInWithEmailAndPassword(email,password)
                     .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task)
                 {
                     if(task.isSuccessful())
                     {
                         String online_user_id = mAuth.getCurrentUser().getUid();
                         String device_tocken = FirebaseInstanceId.getInstance().getToken();
                         userreference.child(online_user_id).child("device_tocken").setValue(device_tocken)
                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void aVoid) {
                                         Intent intent = new Intent(loginactivity.this , MainActivity.class);
                                         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                         startActivity(intent);
                                         finish();
                                     }
                                 });

                     }
                     else
                     {
                         Toast.makeText(loginactivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                     }
                       progressDialog.dismiss();
                 }
             });
        }
    }
}
