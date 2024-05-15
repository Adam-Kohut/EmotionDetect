package com.example.yolov8tflite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;


public class DataOutput extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data_output);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setContentView(R.layout.activity_data_output);


        int emotion = MainJava.getEmotion();
        float confidence = MainJava.getConf();
        String sConfidence = String.valueOf(confidence);

        String ConfArr = Arrays.toString(MainJava.getConfArray());
        String LabelArr = Arrays.toString(MainJava.getLabelArray());

        TextView nameView = findViewById(R.id.nameView);
        nameView.setText(String.valueOf(emotion));

        TextView confView = findViewById(R.id.confView);
        confView.setText(sConfidence);

        TextView listView = findViewById(R.id.listView);
        listView.setText(ConfArr);

        TextView labelView = findViewById(R.id.labelView);
        labelView.setText(LabelArr);
    }

    public void onBtnSubmit_Clicked(View Caller) {
        Intent intent = new Intent(this, LineGraph.class);
        startActivity(intent);
    }


}