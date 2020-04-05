package com.example.myapplication5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.database.Cursor;
import android.media.MediaPlayer;

import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;
import android.content.Intent;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import  androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.OnFailureListener;

import android.net.Uri;


import android.widget.MediaController;
public class uplodvideo extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 3;
    private VideoView videoView;
    private Uri VideoUri;
    private MediaController mc;
    private StorageReference mStorageRef;
    private String videoname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uplodvideo);
        videoView = findViewById(R.id.videoview);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads").child("videos");

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener(){
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp ,int width,int height){
                        mc = new MediaController(uplodvideo.this);
                        videoView.setMediaController(mc);
                        mc.setAnchorView(videoView);
                    }
                });
            }
        });

        videoView.start();

        //videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        // @Override
        //public void onPrepared(MediaPlayer mp) {
        // mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
        // @Override
        //public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        //   mc = new MediaController(uploadvideo.this);
        //    videoView.setMediaController(mc);
        //  mc.setAnchorView(videoView);
        //     }
        //});
        // }
        //});



    }

    public void uploadvideoo(View view) {

        Intent i = new Intent();
        i.setType("video/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select a Video"), PICK_VIDEO_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            VideoUri = data.getData();
            videoView.setVideoURI(VideoUri);
            //videoname = getFileName(VideoUri);

        }
    }

    //all above right
//--------------------------------------------------------------------------------
    public void videouploadtoserver (View view ){




        //StorageReference riversRef = mStorageRef.child("images/rivers.jpg");
        mStorageRef.child("videos/.").putFile(VideoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // Uri downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        Toast.makeText(uplodvideo.this, "video uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(uplodvideo.this,exception.toString(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                }
            } finally {
                cursor.close();
            }
        }

        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
















}
