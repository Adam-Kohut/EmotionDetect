package com.example.EmotionDetect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.navigation.NavigationBarView;

public class UploadCenterActivity extends AppCompatActivity {
    private Button btnAudioPicker;
    private NavigationBarView navigationBar;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_center);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // fetch views
        btnAudioPicker = findViewById(R.id.btnFilePicker);
        navigationBar = findViewById(R.id.bottom_navigation);

        // navbar setup
        navigationBar.setSelectedItemId(R.id.profile);
        // deselect selected option.
        @SuppressLint("RestrictedApi") BottomNavigationItemView itemSelected = (BottomNavigationItemView) findViewById(navigationBar.getSelectedItemId());
        itemSelected.setChecked(false);
//         setup navbar listeners
        navigationBar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.files) {
                startActivity(new Intent(this, FilesActivity.class));
                return true;
            }
            else if (item.getItemId() == R.id.home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            }
            else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            else
                return false;
        });
    }
}