package com.example.EmotionDetect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private CardView cameraCard;
    private CardView uploadCenterCard;

    private CardView LoginCard;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // query views
        cameraCard = findViewById(R.id.cameraCard);
        uploadCenterCard = findViewById(R.id.videoPlaybackCard);
        LoginCard = findViewById(R.id.retrieveLogCard);
        bottomNavigationView = findViewById(R.id.bottom_navigation);


        // setup camera listener
        cameraCard.setOnClickListener(v -> startActivity(new Intent(this, CameraActivity.class)));

        // setup upload center listener
        uploadCenterCard.setOnClickListener(v -> startActivity(new Intent(this, VideoActivity.class)));


        LoginCard.setOnClickListener(v -> startActivity(new Intent(this, CameraActivity.class)));
        // setup nav listeners
        // set Home selected
        bottomNavigationView.setSelectedItemId(R.id.home);
        // item selected listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // note item is of type menuItem from the navbar.
            if (item.getItemId() == R.id.files){
                startActivity(new Intent(getApplicationContext(), FilesActivity.class));
                return true;
            }
            else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                return true;
            }
            else if (item.getItemId() == R.id.home)

                return true;
            else
                return false;
        });

        bottomNavigationView.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                Toast.makeText(this, "Already at home page", Toast.LENGTH_SHORT).show();
            }
        });
    }
}