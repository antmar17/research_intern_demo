package com.example.ub_intern_demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 0;
    private DatabaseReference mDatabase;
    final String TAG = "MainActivity";
    private Button emailLogBtn;
    private SignInButton googlebtn;
    private EditText email_et, password_et;
    private TextView register_txt;

    GoogleSignInClient mGoogleSignInClient;


    private FirebaseAuth mAuth;

   // @Override
   // protected void onStart() {
   //     super.onStart();
   //     FirebaseUser user =mAuth.getCurrentUser();
   //     if(user != null){
   //         Intent intent = new Intent(getApplicationContext(),MenuActivity.class);
   //         startActivity(intent);
   //     }
   // }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(MainActivity.this);

        //instance all UI elements
        emailLogBtn = findViewById(R.id.email_login_btn);
        googlebtn = findViewById(R.id.sign_in_button);
        register_txt = findViewById(R.id.register_txt);
        email_et = findViewById(R.id.email_edittxt);
        password_et = findViewById(R.id.password_edittxt);

        //Authp for firebase
        mAuth = FirebaseAuth.getInstance();
        createRequest();


        googlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.sign_in_button:
                        googleLogin();
                        break;
                    // ...
                }

            }
        });
        //set on click for UI elements
        emailLogBtn.setOnClickListener(v -> {
            loginUser();
        });

        register_txt.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    public boolean checkInput() {
        String email = email_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();


        if (email.isEmpty()) {
            email_et.setError("Email is requied");
            email_et.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_et.setError("Please provide a valid email");
            email_et.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            password_et.setError("Password is required");
            password_et.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            password_et.setError("Password too short!");
            password_et.requestFocus();
            return false;
        }
        return true;


    }

    //Email Login
    public void loginUser() {
        if (!checkInput()) {
            return;
        }
        String email = email_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Logged in Succesfully!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to log in", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void createRequest() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
    }

    public void googleLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(),MenuActivity.class);

                            String name = user.getDisplayName();
                            String Email = user.getEmail();

                            //put in firebase on completion
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(user.getUid()).child("name").setValue(name);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(user.getUid()).child("email").setValue(Email);


                            startActivity(intent);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "google sign in failed", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });


    }
}