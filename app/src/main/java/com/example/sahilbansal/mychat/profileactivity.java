package com.example.sahilbansal.mychat;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.cert.TrustAnchor;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class profileactivity extends AppCompatActivity {

    private Button acceptrequest;
    private Button declinerequest;
    private TextView profilename;
    private TextView profilestatus;
    private ImageView ProfileImage;
    String name;
    String status;
    String receiver_user_id;
    String image;
    String sender_user_id;
    private String current_state;
    private DatabaseReference usersreference;
    private DatabaseReference friendrequesrtreference;
    private FirebaseAuth mauth;
    private DatabaseReference friendsreference;
    private DatabaseReference Notificationreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileactivity);
        usersreference = FirebaseDatabase.getInstance().getReference().child("Users");
        friendrequesrtreference = FirebaseDatabase.getInstance().getReference().child("friend_request");
        friendrequesrtreference.keepSynced(true);
        friendsreference = FirebaseDatabase.getInstance().getReference().child("Friends");
        friendsreference.keepSynced(true);
        Notificationreference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        Notificationreference.keepSynced(true);
        mauth = FirebaseAuth.getInstance();
        sender_user_id = mauth.getCurrentUser().getUid();
        acceptrequest = (Button) findViewById(R.id.sendrequest);
        declinerequest = (Button) findViewById(R.id.declinerequest);
        profilename = (TextView) findViewById(R.id.username);
        profilestatus = (TextView) findViewById(R.id.status);
        ProfileImage = (ImageView) findViewById(R.id.image);
        current_state = "not friend";
        receiver_user_id = getIntent().getExtras().get("id").toString();
        usersreference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("User_name").getValue().toString();
                status = dataSnapshot.child("User_Status").getValue().toString();
                image = dataSnapshot.child("User_image").getValue().toString();
                profilename.setText(name);
                profilestatus.setText(status);
                Picasso.with(profileactivity.this).load(image).placeholder(R.drawable.images).into(ProfileImage);
                friendrequesrtreference.child(sender_user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild(receiver_user_id)) {
                                        String rea_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();

                                        if (rea_type.equals("sent")) {
                                            current_state = "request_sent";
                                            acceptrequest.setText("Cancel friend request");
                                            declinerequest.setVisibility(View.INVISIBLE);
                                            declinerequest.setEnabled(false);
                                        } else if (rea_type.equals("received")) {
                                            current_state = "request_received";
                                            acceptrequest.setText("Accept friend request");
                                            declinerequest.setVisibility(View.VISIBLE);
                                            declinerequest.setEnabled(true);
                                            declinerequest.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    declinefriendrequest();
                                                }
                                            });
                                        }

                                    }
                                } else {
                                    friendsreference.child(sender_user_id)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(receiver_user_id)) {
                                                        current_state = "friends";
                                                        acceptrequest.setText("unfriend");
                                                        declinerequest.setVisibility(View.INVISIBLE);
                                                        declinerequest.setEnabled(false);

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        declinerequest.setVisibility(View.INVISIBLE);
        declinerequest.setEnabled(false);
        if (!sender_user_id.equals(receiver_user_id)) {
            acceptrequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptrequest.setEnabled(false);

                    if (current_state.equals("not friend")) {

                        SendFreiendRequest();

                    }
                    if (current_state.equals("request_sent")) {
                        cancelfriendrequest();

                    }
                    if (current_state.equals("request_received")) {
                        acceptfriendrequest();
                    }
                    if (current_state.equals("friends")) {
                        unfriendafriend();
                    }

                }


            });
        }
        else
        {
            acceptrequest.setVisibility(View.INVISIBLE);
            declinerequest.setVisibility(View.INVISIBLE);

        }
    }

    private void declinefriendrequest() {


        friendrequesrtreference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendrequesrtreference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                acceptrequest.setEnabled(true);
                                                current_state = "not_friends";
                                                acceptrequest.setText("Send friend request");
                                                declinerequest.setVisibility(View.INVISIBLE);
                                                declinerequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void unfriendafriend() {

      friendsreference.child(sender_user_id).child(receiver_user_id).removeValue()
              .addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful())
                      {
                           friendsreference.child(receiver_user_id).child(sender_user_id).removeValue()
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if(task.isSuccessful())
                                           {
                                                acceptrequest.setEnabled(true);
                                                current_state = "not_friends";
                                                acceptrequest.setText("send friend request");
                                               declinerequest.setVisibility(View.INVISIBLE);
                                               declinerequest.setEnabled(false);
                                           }
                                       }
                                   });
                      }
                  }
              });


    }





    private void acceptfriendrequest() {

        Calendar callfordate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String savesrrentdate = currentdate.format(callfordate.getTime());
        friendsreference.child(sender_user_id).child(receiver_user_id).setValue(savesrrentdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                     public void onSuccess(Void aVoid) {
                      friendsreference.child(receiver_user_id).child(sender_user_id).setValue(savesrrentdate)
                              .addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void aVoid) {
                                      friendrequesrtreference.child(sender_user_id).child(receiver_user_id).removeValue()
                                              .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<Void> task) {
                                                      if (task.isSuccessful()) {
                                                          friendrequesrtreference.child(receiver_user_id).child(sender_user_id).removeValue()
                                                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                      @Override
                                                                      public void onComplete(@NonNull Task<Void> task) {
                                                                          if(task.isSuccessful())
                                                                          {
                                                                              acceptrequest.setEnabled(true);
                                                                              current_state = "friends";
                                                                              acceptrequest.setText("Unfriend ");
                                                                              declinerequest.setVisibility(View.INVISIBLE);
                                                                              declinerequest.setEnabled(false);
                                                                          }
                                                                      }
                                                                  });
                                                      }
                                                  }
                                              });
                                  }
                              });
                    }
                });


    }

    private void cancelfriendrequest() {

        friendrequesrtreference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendrequesrtreference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful())
                                               {
                                                    acceptrequest.setEnabled(true);
                                                    current_state = "not_friends";
                                                    acceptrequest.setText("Send friend request");
                                                   declinerequest.setVisibility(View.INVISIBLE);
                                                   declinerequest.setEnabled(false);
                                               }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendFreiendRequest() {

        friendrequesrtreference.child(sender_user_id).child(receiver_user_id).child("request_type").
                setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                     friendrequesrtreference.child(receiver_user_id).child(sender_user_id).child("request_type")
                             .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful())
                           {
                               HashMap<String,String> notifications = new HashMap<>();
                               notifications.put("from",sender_user_id);
                               notifications.put("type","request");
                               Notificationreference.child(receiver_user_id).push().setValue(notifications)
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()) {
                                                   acceptrequest.setEnabled(true);
                                                   current_state = "request_send";
                                                   acceptrequest.setText("Cancel friend request");
                                                   declinerequest.setVisibility(View.INVISIBLE);
                                                   declinerequest.setEnabled(false);
                                               }
                                           }
                                       });

                           }
                         }
                     });
                }
            }
        });

    }
}
