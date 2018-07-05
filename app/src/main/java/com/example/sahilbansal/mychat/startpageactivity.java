package com.example.sahilbansal.mychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class startpageactivity extends AppCompatActivity {
private Button NeedNewAccountButton;
private Button AlreadyHaveAccountButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startactivity);
        NeedNewAccountButton = (Button)findViewById(R.id.need_account_button);
        AlreadyHaveAccountButton = (Button)findViewById(R.id.already_have_account_button);
        NeedNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(startpageactivity.this , registeractivity.class);
                startActivity(intent);
            }
        });
        AlreadyHaveAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(startpageactivity.this , loginactivity.class);
                startActivity(intent);
            }
        });
    }
}
