package com.example.sahilbansal.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.*;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Set;

public class changestatus extends AppCompatActivity {
    private EditText statuschange;
    private Button save;
    private Toolbar tolbar;
    private ProgressDialog progressDialog;
    private DatabaseReference changestautsreference;
    private FirebaseAuth mauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changestatus);
        mauth = FirebaseAuth.getInstance();
        String userid =  mauth.getCurrentUser().getUid();
        changestautsreference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        statuschange = (EditText)findViewById(R.id.enter_status);
        save = (Button)findViewById(R.id.save_changes);
        tolbar = (Toolbar)findViewById(R.id.statustoolbar);
        progressDialog = new ProgressDialog(this);
        String newstatus = getIntent().getExtras().getString("status").toString();
        statuschange.setText(newstatus);
        setSupportActionBar(tolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = statuschange.getText().toString().trim();
                changeprofilestatus(status);
            }
        });
    }

    private void changeprofilestatus(String status) {
        if(TextUtils.isEmpty(status))
        {
            Toast.makeText(this, "Please write a new status", Toast.LENGTH_SHORT).show();
        }
        else
        {
            progressDialog.setMessage("Please wait");
            progressDialog.setTitle("Change Profile Status");
            progressDialog.show();
                  changestautsreference.child("User_Status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          if(task.isSuccessful())
                          {
                              Toast.makeText(changestatus.this, "status is updated", Toast.LENGTH_SHORT).show();
                              Intent intent = new Intent(changestatus.this , Settings.class);
                              startActivity(intent);
                          }
                          else
                          {
                              Toast.makeText(changestatus.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                          }
                          progressDialog.dismiss();
                      }
                  });
        }
    }
}
