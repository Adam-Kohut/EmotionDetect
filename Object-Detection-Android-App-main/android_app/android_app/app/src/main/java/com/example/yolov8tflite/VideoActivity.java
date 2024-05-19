package com.example.yolov8tflite;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class VideoActivity extends AppCompatActivity {

    private static final int REQUEST_VIDEO_PICK = 1;
    private ImageView imageView;
    private Button pickVideoButton;
    private Handler handler;
    private Runnable updateFrameRunnable;
    private long frameInterval = 1; // 1 second
    private MediaMetadataRetriever retriever;
    private long duration;
    private long currentFrameTime;
    private int totalFrames;
    private int currentFrameIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        imageView = findViewById(R.id.imageView);
        pickVideoButton = findViewById(R.id.pickVideoButton);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        pickVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickVideoFile();
            }
        });

        handler = new Handler();
    }

    private void pickVideoFile() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_VIDEO_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_PICK && resultCode == RESULT_OK && data != null) {
            Uri videoUri = data.getData();
            if (videoUri != null) {
                try {
                    processVideo(videoUri);
                } catch (IOException e) {
                    Toast.makeText(this, "Failed to process video: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void processVideo(Uri videoUri) throws IOException {
        retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, videoUri);

            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            duration = Long.parseLong(durationStr);
            currentFrameIndex = 0;
            totalFrames = (int) (duration / frameInterval);


            startSlideshow();

        } catch (RuntimeException e) {
            throw new IOException("Error processing video frames", e);
        }
    }

    private void startSlideshow() {
        updateFrameRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentFrameTime < totalFrames) {
                    Bitmap bitmap = null; // time in microseconds
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            bitmap = retriever.getFrameAtIndex((int) currentFrameTime);
                        }
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle the exception, for example, stop the slideshow if an error occurs
                        handler.removeCallbacks(updateFrameRunnable);
                        return;
                    }
                    currentFrameTime += (frameInterval);
                    handler.postDelayed(this, frameInterval);
                } else {
                    handler.removeCallbacks(updateFrameRunnable); // Stop the slideshow
                }
            }
        };
        handler.post(updateFrameRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && updateFrameRunnable != null) {
            handler.removeCallbacks(updateFrameRunnable);
        }
        if (retriever != null) {
            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}