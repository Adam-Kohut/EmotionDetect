package com.example.yolov8tflite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


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

    public void on2BtnSubmit_Clicked(View Caller) {

        String ConfArr = Arrays.toString(MainJava.getConfArray());
        String LabelArr = Arrays.toString(MainJava.getLabelArray());

        int[] labelArray =  MainJava.getLabelArray();
        int angerQuant = 0;
        int disgustQuant = 0;
        int fearQuant = 0;
        int happyQuant = 0;
        int sadQuant = 0;
        int surpriseQuant = 0;


        int arrLength = labelArray.length;
        for(int i = 0; i < arrLength; i++){
            if(labelArray[i] == 0){
                angerQuant += 1;
            }
        }
        for(int i = 0; i < arrLength; i++){
            if(labelArray[i] == 1){
                disgustQuant += 1;
            }
        }
        for(int i = 0; i < arrLength; i++){
            if(labelArray[i] == 2){
                fearQuant += 1;
            }
        }
        for(int i = 0; i < arrLength; i++){
            if(labelArray[i] == 3){
                happyQuant += 1;
            }
        }
        for(int i = 0; i < arrLength; i++){
            if(labelArray[i] == 4){
                sadQuant += 1;
            }
        }
        for(int i = 0; i < arrLength; i++){
            if(labelArray[i] == 5){
                surpriseQuant += 1;
            }
        }

        float angerPerc = (float) angerQuant / arrLength;
        float disgustPerc = (float) disgustQuant / arrLength;
        float fearPerc = (float) fearQuant / arrLength;
        float happyPerc = (float) happyQuant / arrLength;
        float sadPerc = (float) sadQuant / arrLength;
        float surprisePerc = (float) surpriseQuant / arrLength;



        final String POST_URL = "https://studev.groept.be/api/a23PT314/AddMovieFile";

        ProgressDialog progressDialog = new ProgressDialog(DataOutput.this);
        progressDialog.setMessage("Uploading, please wait...");

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest submitRequest = new StringRequest(
                Request.Method.POST,
                POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(
                                DataOutput.this,
                                "Post request executed",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(
                                DataOutput.this,
                                "Unable to place the order" + error,
                                Toast.LENGTH_LONG).show();
                    }
                }
        ) { //NOTE THIS PART: here we are passing the POST parameters to the webservice
            @Override
            protected Map<String, String> getParams() {
                /* Map<String, String> with key value pairs as data load */
                Map<String, String> params = new HashMap<>();
                //params.put("file_name", "Test");
                params.put("userid", String.valueOf(1));
                params.put("logtitle", "Test");
                params.put("labelarray", LabelArr);
                params.put("confidencearray", ConfArr);
                params.put("happinesspercentage", String.valueOf(happyPerc));
                params.put("sadnesspercentage", String.valueOf(sadPerc));
                params.put("fearpercentage", String.valueOf(fearPerc));
                params.put("angerpercentage", String.valueOf(angerPerc));
                params.put("disgustpercentage", String.valueOf(disgustPerc));
                params.put("surprisepercentage",  String.valueOf(surprisePerc));



                //params.put("LabelArray", Arrays.toString(MainJava.getLabelArray()));
                //params.put("ConfidenceArray", Arrays.toString(MainJava.getConfArray()));
                return params;


            }
        };
        progressDialog.show();
        requestQueue.add(submitRequest);
    }


}