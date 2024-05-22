package com.example.EmotionDetect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import com.example.EmotionDetect.model.MovieFile;
import com.example.EmotionDetect.model.MovieFileAdapter;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilesActivity extends AppCompatActivity {
    private NavigationBarView navigationBar; // nav bar on UI
    private static final String USER_FILES_URL = "https://studev.groept.be/api/a23PT314/getFiles";
    private List<MovieFile> files = new ArrayList<MovieFile>();
    private SearchView searchView;
    private MovieFileAdapter fileAdapter;

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
        searchView = findViewById(R.id.searchView);

        // changing color of text in searchview
        try {
            Field mSearchSrcTextViewField = SearchView.class.getDeclaredField("mSearchSrcTextView");
            mSearchSrcTextViewField.setAccessible(true);
            EditText mSearchSrcTextView = (EditText) mSearchSrcTextViewField.get(searchView);
            // Change text color to white
            mSearchSrcTextView.setTextColor(Color.WHITE);
            mSearchSrcTextView.setHintTextColor(Color.LTGRAY);

            // Align hint text to the left
            mSearchSrcTextView.setGravity(android.view.Gravity.START | android.view.Gravity.CENTER_VERTICAL);

            // Ensure no padding is causing the hint to be centered
            mSearchSrcTextView.setPadding(0, 0, 0, 0);
            // note: either above or below change. both worked
//            mSearchSrcTextView.setLayoutParams(new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //check if user is logged in
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        Log.d("Shared prefs", sharedPreferences.toString());
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            int userId = Integer.parseInt(sharedPreferences.getString("userId", "-1"));

            if (userId != - 1) {
                // files request.
                //set up recyclerView.
                filesView.setLayoutManager(new LinearLayoutManager(this));
                requestUserFiles(userId);
                Log.d("Login", "Files can be displayed. User is logged in.");

                // setup searchview.
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        fileAdapter.getFilter().filter(newText);
                        return false;
                    }
                });
            }
            else Log.e("Shared Prefs", "user id hasn't been saved correctly");
        } else {
            Toast.makeText(this, "Register or login to be able to see your files",
                    Toast.LENGTH_LONG).show();
            Log.d("Login", "Files cannot be displayed as User isn't logged in.");
        }
        // set up navigation bar
        setUpNavBar();

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
    private void requestUserFiles(int userId) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                USER_FILES_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONArray response) {
                        // iteration 1
                        processJSONResponse(response, userId);
                        fileAdapter = new MovieFileAdapter(files);
                        filesView.setAdapter(fileAdapter);
                        Objects.requireNonNull(filesView.getAdapter()).notifyDataSetChanged();
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


    private void processJSONResponse(JSONArray response, int userId) {
        for (int i = 0; i < response.length(); i++) {
            try {
                MovieFile file = new MovieFile(response.getJSONObject(i), userId);
                Log.d("Movie file", String.valueOf(file.getHappinessPercentage()*100) );
                Log.d("Database", file.toString());
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

//    private void filterList(String textInputted) {
//        List<MovieFile> filteredList = new ArrayList<>();
//        for (MovieFile file: files) {
//            if (file.getFileName().toLowerCase().contains(textInputted.toLowerCase())) {
//                filteredList.add(file);
//            }
//        }
//
//        if (filteredList.isEmpty()) {
//            Toast.makeText(this, "No file found.", Toast.LENGTH_SHORT).show();
//        } else { // sending data to adapter class.
//            fileAdapter.setFilteredList(filteredList);
//        }
//    }
}
