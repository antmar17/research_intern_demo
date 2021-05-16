package com.example.ub_intern_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {

    private Button questionBtn,videoBtn,recordBtn,logoutbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        //find buttons
        questionBtn = findViewById(R.id.answer_question_btn);
        videoBtn = findViewById(R.id.watch_video_btn);
        recordBtn = findViewById(R.id.record_btn);
        logoutbtn = findViewById(R.id.logout);

 //       GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        questionBtn.setOnClickListener(v->{
            Intent intent = new Intent(MenuActivity.this,QuestionActivity.class);
            startActivity(intent);
        });


        videoBtn.setOnClickListener(v->{
            Intent intent = new Intent(MenuActivity.this,VideoActivity.class);
            startActivity(intent);
        });

        recordBtn.setOnClickListener(v->{
            Intent intent = new Intent(MenuActivity.this,RecordActivity.class);
            startActivity(intent);
        });

        logoutbtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        });

    }
}