package com.example.sahilbansal.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.UpdateLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Settings extends AppCompatActivity {

    private static final int VALUE_INTENT = 1;
    private TextView displayname;
    private TextView displaystatus;
    private CircleImageView displayimage;
    private Button changepic;
    private Button changestatusbar;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    Bitmap thumb_bitmap = null;
    private StorageReference thumbimageref;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth = FirebaseAuth.getInstance();
        String Users_online = mAuth.getCurrentUser().getUid();
        displayimage = (CircleImageView)findViewById(R.id.circleImageView);
        displayname = (TextView) findViewById(R.id.username);
        displaystatus = (TextView)findViewById(R.id.status);
        changepic = (Button)findViewById(R.id.settingschangeprifile);
        changestatusbar = (Button)findViewById(R.id.settingschangestatus);
        progressDialog = new ProgressDialog(this);
        thumbimageref = FirebaseStorage.getInstance().getReference().child("Thumb_images");
        changestatusbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldstatus = displaystatus.getText().toString().trim();
                Intent intent = new Intent(Settings.this , changestatus.class);
                intent.putExtra("status" , oldstatus);
                startActivity(intent);
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Users_online);
        databaseReference.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile_images");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("User_name").getValue().toString();
                String status = dataSnapshot.child("User_Status").getValue().toString();
               final String image = dataSnapshot.child("User_image").getValue().toString();
                String thumb_image = dataSnapshot.child("User_thumb_image").getValue().toString();
                displayname.setText(name);
                displaystatus.setText(status);
                Picasso.with(Settings.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.images)
                        .into(displayimage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(Settings.this).load(image).placeholder(R.drawable.images).into(displayimage);

                            }
                        });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        changepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent , VALUE_INTENT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== VALUE_INTENT && resultCode == RESULT_OK && data!=null)
        {
            Uri imageuri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                progressDialog.setTitle("Updating Profile image");
                progressDialog.setMessage("Please wait");
                progressDialog.show();

                Uri resultUri = result.getUri();
                File  thumb_filepath = new File(resultUri.getPath());
                try
                {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_filepath);
                }
                catch (IOException e)
                {
                     e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG , 50 , byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();

                String user_id = mAuth.getCurrentUser().getUid();
                StorageReference filepath = storageReference.child(user_id + ".jpg");
                final StorageReference thumb_path = thumbimageref.child(user_id + ".jpg");
              // final StorageReference thumb_storage = storageReference.child(user_id + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(Settings.this, "Saving image", Toast.LENGTH_SHORT).show();

                           final String downloadurl = task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_path.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                  String thumb_downloadurl =  thumb_task.getResult().getDownloadUrl().toString();
                                  if(task.isSuccessful())
                                  {
                                       Map upadae_image = new HashMap();
                                       upadae_image.put("User_image" , downloadurl);
                                       upadae_image.put("User_thumb_image",thumb_downloadurl);
                                      databaseReference.updateChildren(upadae_image).addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                              if(task.isSuccessful())
                                              {
                                                  Toast.makeText(Settings.this, "Image is updated successfully", Toast.LENGTH_SHORT).show();
                                                  progressDialog.dismiss();
                                              }
                                              else
                                              {
                                                  Toast.makeText(Settings.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                  progressDialog.dismiss();
                                              }
                                          }
                                      });
                                  }
                                }
                            });


                        }
                        else
                        {
                            Toast.makeText(Settings.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                });

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
