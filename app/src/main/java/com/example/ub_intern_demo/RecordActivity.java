package com.example.ub_intern_demo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RecordActivity extends AppCompatActivity {
    private String TAG = "RecordActivity";
    private TextView statusTxt;
    private ToggleButton recordButton;
    private MediaRecorder mediaRecorder;
    private Button playButton, submitButton,backButton;
    private String fileName;
    private String recordPath;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        playButton = findViewById(R.id.play_btn);
        submitButton = findViewById(R.id.submit_btn);
        recordButton = findViewById(R.id.toggle_record_btn);
        statusTxt = findViewById(R.id.status_txt);
        backButton = findViewById(R.id.back_btn);

        fileName = "myaudio.3gp";
        recordPath = getExternalFilesDir("/").getAbsolutePath();


        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);


        //set on click listeners
        recordButton.setOnClickListener(v -> {
            record_onClick();
        });

        playButton.setOnClickListener(v -> {
            playAudio();
        });

        submitButton.setOnClickListener(v->{
            uploadFile();
        });
        backButton.setOnClickListener(v -> {

            Intent intent = new Intent(getApplicationContext(),MenuActivity.class);
            startActivity(intent);
        });
    }

    private void record_onClick() {

        if (recordButton.isChecked()) {
            startRecording();
        } else {
            stopRecording();

        }
    }

    private void startRecording() {


        RequestPermissions();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        // File path =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        // File file = new File(path,"/recorded.3gp");

        mediaRecorder.setOutputFile(recordPath + "/" + fileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        try {

            mediaRecorder.prepare();
            statusTxt.setText("Recording your voice....");
            statusTxt.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            Toast.makeText(RecordActivity.this, "something went wrong while trying to record the audio", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        statusTxt.setVisibility(View.GONE);
    }

    private void playAudio() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Toast.makeText(RecordActivity.this, "something went wrong while trying to play the audio", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        statusTxt.setText("Playing your voice....");
        statusTxt.setVisibility(View.VISIBLE);
    }


    private void uploadFile() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        Uri file = Uri.fromFile(new File(recordPath+"/"+fileName));
        StorageReference riversRef = storageRef.child(userID+"/" + file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(RecordActivity.this, "Uploaded Sucessfully!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(RecordActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);


    }
}