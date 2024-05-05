package com.example.yolov8tflite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.yolov8tflite.model.MovieFile;
import com.example.yolov8tflite.model.MovieFileAdapter;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class FilesActivity extends AppCompatActivity {
    private NavigationBarView navigationBar; // nav bar on UI
    private static final String USER_FILES_URL = "https://studev.groept.be/api/a23PT314/getFiles";
    private List<MovieFile> files = new ArrayList<MovieFile>();

    private RecyclerView filesView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_files);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // fetch views
        navigationBar = findViewById(R.id.bottom_navigation);
        filesView = findViewById(R.id.filesDisplayView);

        // set up navigation bar
        setUpNavBar();
        // files request.
        //set up recyclerView.
        MovieFileAdapter fileAdapter = new MovieFileAdapter(files);
        filesView.setAdapter(fileAdapter);
        filesView.setLayoutManager(new LinearLayoutManager(this));
        requestUserFiles(); // TODO: add user_id later perhaps.
    }

    // TODO: extend this to include request based on filters
//    private void requestUserFiles() {
//        // make request queue
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//        // make a request for the files.
//        Log.d("database", "Making request");
//        JsonArrayRequest filesRequest = new JsonArrayRequest(
//                Request.Method.GET,
//                USER_FILES_URL,
//                null,
//                response -> {
//                    Log.d("database", "Request is being processed");
//                    processJSONResponse(response);
//                    TextView txtView = (TextView) findViewById(R.id.viewFilesDisplay);
//                    txtView.setText(files.size());
//                },
//                error -> {
//                    Log.e("database", "couldn't make request" + error.getLocalizedMessage(), error);
//                    Toast.makeText(this,
//                            "Unable to communicate with the server",
//                            Toast.LENGTH_LONG).show();
//                }
//        );
//        requestQueue.add(filesRequest);
//        Log.d("database", "request added to queue");
//
//    }
    private void requestUserFiles() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                USER_FILES_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // iteration 1
                        processJSONResponse(response);
                        filesView.getAdapter().notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                FilesActivity.this,
                                "Unable to communicate with the server",
                                Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(queueRequest);
    }


    private void processJSONResponse(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                MovieFile file = new MovieFile(response.getJSONObject(i));
                files.add(file);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpNavBar() {
        navigationBar.setSelectedItemId(R.id.files);
        navigationBar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.files) {
                return true;
            } else if (item.getItemId() == R.id.home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else
                return false;
        });
    }
}
