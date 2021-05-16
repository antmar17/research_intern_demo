package com.example.ub_intern_demo;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    private EditText email_et, password_et,name_et;
    private Button register_btn, backButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //find all UI elements
        name_et = findViewById(R.id.name_edittxt);
        email_et = findViewById(R.id.email_edittxt);
        password_et = findViewById(R.id.password_edittxt);
        register_btn = findViewById(R.id.register_btn);
        progressBar = findViewById(R.id.progress_bar);
        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(v->{

            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        });
        mAuth = FirebaseAuth.getInstance();

        register_btn.setOnClickListener( v->{
            CheckInput();
        });


    }

    private void registerUser(String name,String email,String password) {

        //show progress bar
        progressBar.setVisibility(View.VISIBLE);

        //create user
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Create Java object and put it into firebase database
                            User user = new User(name,email,"");
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "User has been registerd!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    else{

                                        Toast.makeText(RegisterActivity.this, "Failed to be  registerd!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);

                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Failed to be  registerd!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
        );
    }
    private void CheckInput() {
        //get strings entered by users
        String name = name_et.getText().toString().trim();
        String email = email_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();

        //check if all info is entered amd valid
        if(name.isEmpty()){
            name_et.setError("Name is requied");
            name_et.requestFocus();
            return;
        }

        if(email.isEmpty()){
            email_et.setError("Email is requied");
            email_et.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_et.setError("Please provide a valid email");
            email_et.requestFocus();
            return;
        }
        if(password.isEmpty()){
            password_et.setError("Password is required");
            password_et.requestFocus();
            return;
        }

        if(password.length() < 6){
            password_et.setError("Password too short!");
            password_et.requestFocus();
            return;
        }

        registerUser(name,email,password);





    }
}


