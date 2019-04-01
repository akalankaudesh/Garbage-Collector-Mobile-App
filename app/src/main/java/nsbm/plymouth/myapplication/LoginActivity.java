package nsbm.plymouth.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText textemail;
    EditText textpassword;
    private FirebaseAuth mAuth;
    static String TAG;
    ProgressBar loginprogressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        textemail=(EditText)findViewById(R.id.txtemailaddress);
        textpassword=(EditText)findViewById(R.id.txtpword);
        final Button bunttonlogin=findViewById(R.id.btnlogin);
        final Button buttonsigin=findViewById(R.id.btnsignup);
        loginprogressBar=findViewById(R.id.progresslogin);
        bunttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String em=textemail.getText().toString();
                final String pwd=textpassword.getText().toString();
                if (em.isEmpty() || pwd.isEmpty()){
                    showMessage("Please Verify all Fields");
                    return;
                }
                SignIN(em,pwd);
                loginprogressBar.setVisibility(View.VISIBLE);
            }
        });

        buttonsigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signinintent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(signinintent);
            }
        });

    }


    private void showMessage(String message){
        Toast.makeText(LoginActivity.this,message,Toast.LENGTH_LONG).show();
    }

    private void SignIN(final String email,final String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent=new Intent(LoginActivity.this, HomePage.class);
                        startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            loginprogressBar.setVisibility(View.INVISIBLE);
                        }

                        // ...
                    }
                });

    }
    private  void  updateUI(){

    }

}
