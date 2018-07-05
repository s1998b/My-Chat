package com.example.sahilbansal.mychat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class Alluser extends AppCompatActivity implements bind {
 private Toolbar mtoolbar;
    String id;
 private RecyclerView alluserlist;
 private DatabaseReference databaseReference;
 private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alluser);
        firebaseAuth = FirebaseAuth.getInstance();
        mtoolbar = (Toolbar)findViewById(R.id.allusertoolbar);
        id =  firebaseAuth.getCurrentUser().getUid();
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("All users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alluserlist = (RecyclerView)findViewById(R.id.allusers);
        alluserlist.setHasFixedSize(true);
        alluserlist.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<all,Alluserviewholder > firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<all, Alluserviewholder>
                        (
                                all.class , R.layout.layout_profile , Alluserviewholder.class,databaseReference
                        )
                {
            @Override
            protected void populateViewHolder(Alluserviewholder viewHolder, all model, final int position) {
                viewHolder.setUser_name(model.getUser_name());
                viewHolder.setUser_Status(model.getUser_Status());
                viewHolder.setUser_thumb_image(getApplicationContext(), model.getUser_thumb_image());
               final String name = model.getUser_name();
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();
                        Intent intent = new Intent(Alluser.this , profileactivity.class);
                        intent.putExtra("id" , visit_user_id );
                        startActivity(intent);
                    }
                });
                }
                };
        alluserlist.setAdapter(firebaseRecyclerAdapter);
    }
    public static class Alluserviewholder extends RecyclerView.ViewHolder
    {
        View mview;

        public Alluserviewholder(View itemView) {
            super(itemView);
            mview = itemView;

        }
        public void setUser_name(String user_name)
        {
            TextView name = (TextView)mview.findViewById(R.id.allusername);
            name.setText(user_name);

        }
        public void setUser_Status(String user_Status)
        {
             TextView userstat = (TextView)mview.findViewById(R.id.usersstatusinfo);
             userstat.setText(user_Status);
        }
        public void setUser_thumb_image(final Context ctx , final String user_thumb_image)
        {
           final CircleImageView thumb_image = (CircleImageView)mview.findViewById(R.id.usersimage);

            Picasso.with(ctx).load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.images)
                    .into(thumb_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(user_thumb_image).placeholder(R.drawable.images).into(thumb_image);

                        }
                    });
        }
    }
}
