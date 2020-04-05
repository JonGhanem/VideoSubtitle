package com.example.myapplication5;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.os.Build;
import android.os.Bundle;


import android.database.Cursor;
import android.media.MediaPlayer;

import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
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

import java.util.Objects;

public class UplodVideo extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 3;
    private VideoView videoView;
    private Uri VideoUri , uploadedUri;
    private MediaController mc;
    private StorageReference mStorageRef;
    private StorageReference fileReference;
    private String videoname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uplodvideo);
        videoView = findViewById(R.id.videoview);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads").child("Videos");

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener(){
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp ,int width,int height){
                        mc = new MediaController(UplodVideo.this);
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

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //all above right
//--------------------------------------------------------------------------------
    public void firebaseUploading(View view ){
        if (VideoUri != null) {
            fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(VideoUri));
            fileReference.putFile(VideoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uploadedUri = uri;
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UplodVideo.this, e.toString(), Toast.LENGTH_SHORT).show();

                }
            });
        }else{
            Toast.makeText(this, "NO Video Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getFileName(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                }
            }
        }

        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
















}
