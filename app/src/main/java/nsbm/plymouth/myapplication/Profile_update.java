package nsbm.plymouth.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Profile_update extends AppCompatActivity {

    FirebaseUser getcurrentuser= FirebaseAuth.getInstance().getCurrentUser();
    String name;
    String email;
    Uri photoUrl;
    Uri pickedimg;
    ImageView updateduserimg;
    static int PreqCode=1;
    static int REQUESTCODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        final TextView txtupdateusername=(TextView)findViewById(R.id.txtupdateusername);
        final TextView txtoldpword=(TextView)findViewById(R.id.txtoldpword);
        final TextView txtnewpword=(TextView)findViewById(R.id.txtnewpword);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Name, email address, and profile photo Url
            name = user.getDisplayName();
            email = user.getEmail();
            photoUrl = user.getPhotoUrl();

        }


        updateduserimg=(ImageView)findViewById(R.id.imguserupdated);

        Glide.with(getApplicationContext()).load(photoUrl).into(updateduserimg);

        updateduserimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=22){
                    checkAndRequestForPermission();
                }
                else {
                    openGallery();
                }
            }
        });



        Button updatecancel=(Button)findViewById(R.id.btncancelupdate);
        updatecancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancel=new Intent(Profile_update.this,HomePage.class);
                startActivity(cancel);
            }
        });


        Button updateuserbtn=(Button)findViewById(R.id.btnupdateuser);
        final ProgressBar progressBarupdate=(ProgressBar)findViewById(R.id.progressupdate);
        updateuserbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String em=txtupdateusername.getText().toString();
                final String pwd=txtoldpword.getText().toString();
                final String pwd2=txtnewpword.getText().toString();
                if (em.isEmpty() || pwd.isEmpty() ||pwd2.isEmpty() ){
                    showMessage("Please Verify all Fields");
                    return;
                }
                progressBarupdate.setVisibility(View.VISIBLE);

                AuthCredential credential = EmailAuthProvider
                        .getCredential(email, pwd);
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    showMessage("User re-authenticated");
                                    user.updatePassword(pwd2)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        showMessage("User password updated.");
                                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                .setDisplayName(em)
                                                                .setPhotoUri(pickedimg)
                                                                .build();
                                                        user.updateProfile(profileUpdates)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            showMessage("User profile updated");
                                                                            Intent intent=new Intent(Profile_update.this,HomePage.class);
                                                                            startActivity(intent);
                                                                        }
                                                                    }
                                                                });

                                                    }

                                                }
                                            });



                                }else {
                                    showMessage("User re-authentication failed");
                                   return;
                                }
                            }
                        });




            }
        });



    }

    private void showMessage(String message){
        Toast.makeText(Profile_update.this,message,Toast.LENGTH_LONG).show();
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(Profile_update.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Profile_update.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(Profile_update.this,"Please Allow Permission",Toast.LENGTH_SHORT).show();
            }else {
                ActivityCompat.requestPermissions(Profile_update.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PreqCode);
            }
        }else {

            openGallery();
        }
    }
    private void openGallery() {

        Intent GalleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        GalleryIntent.setType("image/*");
        startActivityForResult(GalleryIntent,REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUESTCODE && resultCode==RESULT_OK && data != null){
            pickedimg=data.getData();
            updateduserimg.setImageURI(pickedimg);
        }

    }
}
