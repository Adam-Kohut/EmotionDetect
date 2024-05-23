package com.example.EmotionDetect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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


        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        Log.d("Shared prefs", sharedPreferences.toString());
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            int userId = Integer.parseInt(sharedPreferences.getString("userId", "-1"));

            if (userId != - 1) {
                Log.d("Login", "User is logged in.");
            }
            else Log.e("Shared Prefs", "user id hasn't been saved correctly");
        } else {
            Toast.makeText(this, "Register or login to be able to see send data logs.",
                    Toast.LENGTH_LONG).show();
        }


        int emotion = MainJava.getEmotion();
        float confidence = MainJava.getConf();
        String sConfidence = String.valueOf(confidence);


        String ConfArr = Arrays.toString(MainJava.getConfArray());
        String LabelArr = Arrays.toString(MainJava.getLabelArray());


        TextView listView = findViewById(R.id.listView);
        if(ConfArr == "[]"){
            listView.setText("No Data Entered");
        }
        else{
            listView.setText("Confidence Values Verified");
        }

        TextView labelView = findViewById(R.id.labelView);
        if(LabelArr == "[]"){
            labelView.setText("No Data Entered");
        }
        else{
            labelView.setText("Label Values Verified");
        }



    }

    public void onBtnSubmit_Clicked(View Caller) {
        Intent intent = new Intent(this, LineGraph.class);
        startActivity(intent);
    }

    public void on2BtnSubmit_Clicked(View Caller) {

        String ConfArr = Arrays.toString(MainJava.getConfArray());
        String LabelArr = Arrays.toString(MainJava.getLabelArray());

        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        Log.d("Shared prefs", sharedPreferences.toString());
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        int userId = Integer.parseInt(sharedPreferences.getString("userId", "-1"));



        int[] labelArray =  MainJava.getLabelArray();
        int angerQuant = 0;
        int disgustQuant = 0;
        int fearQuant = 0;
        int happyQuant = 0;
        int sadQuant = 0;
        int surpriseQuant = 0;

        String filename = MainJava.getFilename();

        EditText editText = findViewById(R.id.editText);
        String inputText = editText.getText().toString();

        if(!inputText.isEmpty()){
            filename = inputText;
            filename = inputText.replace(" ", "");
        }


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

        String finalFilename = filename;
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
                params.put("userid", String.valueOf(userId));
                params.put("logtitle", finalFilename);
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
        if (isLoggedIn) {
            if (userId != - 1) {
                requestQueue.add(submitRequest);
            }
            else Log.e("Shared Prefs", "user id hasn't been saved correctly");
        } else {
            Toast.makeText(this, "Unable to send data logs without account",
                    Toast.LENGTH_LONG).show();
        }
    }


}