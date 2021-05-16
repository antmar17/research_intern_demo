package com.example.ub_intern_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class QuestionActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton freshRb,sophRb,junRb,senRb;
    Button submitBtn,backButton;
    ProgressBar progressBar;
    FirebaseAuth mauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        radioGroup = findViewById(R.id.radio_group);
        freshRb = findViewById(R.id.fresh_chkBx);
        sophRb= findViewById(R.id.soph_chkBx);
        junRb= findViewById(R.id.jun_chkBx);
        senRb= findViewById(R.id.sen_chkBx);
        submitBtn = findViewById(R.id.submit_btn);
        progressBar = findViewById(R.id.progress_bar);
        backButton = findViewById(R.id.back_btn);


        submitBtn.setOnClickListener(v->{
            submitAnswer();
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),MenuActivity.class);
            startActivity(intent);
        });


    }

    //Checks that input is valid
    private boolean checkInput(){
        if (freshRb.isChecked() || sophRb.isChecked() || junRb.isChecked() || senRb.isChecked()){
            return true;
        }
        else{

            Toast.makeText(QuestionActivity.this, "Please select an answer", Toast.LENGTH_LONG).show();
            return false;
        }
    }


    private void submitAnswer(){
        if(!checkInput()){
            return;
        }
        String grade = "";
        if(freshRb.isChecked()){
            grade = "Freshman";
        }
        if(sophRb.isChecked()){

            grade = "Sophmore";

        }
        if(junRb.isChecked()){

            grade = "Junior";

        }
        if(senRb.isChecked()){
            grade = "Senior";
        }
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Grade").setValue(grade).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(QuestionActivity.this, "Answer has been recorded", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);
                }
                else{
                    Toast.makeText(QuestionActivity.this, "Failed to record answer", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


    }

}