package com.example.EmotionDetect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.EmotionDetect.model.MovieFile;
import com.example.EmotionDetect.model.MovieFileAdapter;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilesActivity extends AppCompatActivity implements FilterBottomSheet.OnClickListener {
    private NavigationBarView navigationBar; // nav bar on UI
    private static final String USER_FILES_URL = "https://studev.groept.be/api/a23PT314/getFiles";
    private List<MovieFile> files = new ArrayList<>();
    private  List<MovieFile> filesFull;
    private SearchView searchView;
    private MovieFileAdapter fileAdapter;
    private ImageButton btnFilter;
//    private FrameLayout overlayLayout;

    private RecyclerView filesView;
    private ImageButton btnRefreshFiles;
    private int userId;
    private CircularProgressIndicator progressIndicator;
    private boolean startUpFlagProgressBar = true; // flag to display toast refresh files only when user refreshes.
    private boolean firstFilterSetupFlag = true; // flag to determine whether this is the first x setting up filter.

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
        btnFilter = findViewById(R.id.btnFilter);
        btnRefreshFiles = findViewById(R.id.btnRefreshFiles);
        progressIndicator = findViewById(R.id.progressIndicator);

        //check if user is logged in
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        Log.d("Shared prefs", sharedPreferences.toString());
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            userId = Integer.parseInt(sharedPreferences.getString("userId", "-1"));

            if (userId != -1) {
                Log.d("Login", "Files can be displayed. User is logged in.");
                // files request.
                requestUserFiles(userId);
                // set up search view.
                setUpSearchView();
                // set up filter button to start filter fragment
                setUpFilterBtn();
                // setup refresh button
                setUpBtnRefreshFiles();
            } else Log.e("Shared Prefs", "user id hasn't been saved correctly");
        } else {
            Toast.makeText(this, "Register or login to be able to see your files",
                    Toast.LENGTH_LONG).show();
            hideProgressIndicator();
            Log.d("Login", "Files cannot be displayed as User isn't logged in.");
        }

        // set up navigation bar
        setUpNavBar();

//        setUpOverlayLayout();
    }

    private void setUpSearchView() {
        // making view visible as user is logged in
        searchView.setVisibility(View.VISIBLE);
        // changing color of text and X button in searchview
        try {
            Field mSearchSrcTextViewField = SearchView.class.getDeclaredField("mSearchSrcTextView");
            mSearchSrcTextViewField.setAccessible(true);
            EditText mSearchSrcTextView = (EditText) mSearchSrcTextViewField.get(searchView);
            // Change text color to white
            assert mSearchSrcTextView != null;
            mSearchSrcTextView.setTextColor(Color.WHITE);
            mSearchSrcTextView.setHintTextColor(Color.LTGRAY);

            // I have to change the color of X in search bar as well
            ImageView closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
            closeButton.setColorFilter(Color.WHITE);
            // Align hint text to the left
            mSearchSrcTextView.setGravity(android.view.Gravity.START | android.view.Gravity.CENTER_VERTICAL);

            // Ensure no padding is causing the hint to be centered
            mSearchSrcTextView.setPadding(0, 0, 0, 0);
            // note: either above or below change. both worked
//            mSearchSrcTextView.setLayoutParams(new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT));

            // used this to try and exit the search view when clicked outside of it
//            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (hasFocus) {
//                        overlayLayout.setVisibility(View.VISIBLE);
//                        // setting overlay layout to detect whenever a click outside of the search has been made.
//                    } else {
//                        overlayLayout.setVisibility(View.GONE);
//                    }
//                }
//            });
        } catch (Exception e) {
            Log.d("search view setup error", Objects.requireNonNull(e.getMessage()));
        }

        // setup search change listener
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

//    private void setUpOverlayLayout() {
//        overlayLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (searchView.isIconified()) {
//                    // do nothing
//                } else {
//                    // searchview is open, so close it and hide framelayout.
//                    searchView.setIconified(true);
//                    overlayLayout.setVisibility(View.GONE);
//                }
//            }
//        });
//    }

    private void setUpFilterBtn() {
        // making filter button visible as the user is logged in.
        btnFilter.setVisibility(View.VISIBLE);
        btnFilter.setOnClickListener(v -> {
            FilterBottomSheet filterFragment = new FilterBottomSheet();
            filterFragment.setOnClickListener(FilesActivity.this);
            filterFragment.show(getSupportFragmentManager(), "FilterBottomSheet");
        });
    }

    private void requestUserFiles(int userId) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // loading progress indicator
        showProgressIndicator();
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                USER_FILES_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONArray response) {
                        // processing files fetching.
                        processJSONResponse(response, userId);
                        // creating file adapter here in order to always include the original list elements (both for search and filter)
                        fileAdapter = new MovieFileAdapter(files, new MovieFileAdapter.onFileCardClickListener() {
                            @Override
                            public void onFileCardClick(String fileName) {
                                startFileActivity(fileName);
                            }
                        });
                        //set up recyclerView.
                        setUpRecycleView();
                        Objects.requireNonNull(filesView.getAdapter()).notifyDataSetChanged();
                        // assigning filesfull value of full list. Required for preserving list during filter stage.
                        filesFull = new ArrayList<>(files);
                        // hiding progress indicator
                        hideProgressIndicator();
                    }
                },
                error -> {
                    hideProgressIndicator();
                    Toast.makeText(
                            FilesActivity.this,
                            "Unable to communicate with the server",
                            Toast.LENGTH_LONG).show();
                });
        requestQueue.add(queueRequest);
    }

    private void showProgressIndicator() {
        progressIndicator.setVisibility(View.VISIBLE);
        filesView.setVisibility(View.GONE);
        if (!startUpFlagProgressBar)
            Toast.makeText(this, "Refreshing files", Toast.LENGTH_SHORT).show();
        else
            startUpFlagProgressBar = false;
    }

    private void hideProgressIndicator() {
        progressIndicator.setVisibility(View.GONE);
        filesView.setVisibility(View.VISIBLE);
    }

    private void setUpRecycleView() {
        filesView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        filesView.setAdapter(fileAdapter);
    }


    private void processJSONResponse(JSONArray response, int userId) {
        for (int i = 0; i < response.length(); i++) {
            try {
                MovieFile file = new MovieFile(response.getJSONObject(i), userId);
                Log.d("Movie file", String.valueOf(file.getHappinessPercentage() * 100));
                Log.d("Database", file.toString());
                files.add(file);
            } catch (JSONException e) {
                Log.d("JSON error", Objects.requireNonNull(e.getMessage()));
            }
        }
    }

    private void setUpNavBar() {
        navigationBar.setSelectedItemId(R.id.files);
        navigationBar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.files) {
                Toast.makeText(this, "already at files page.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onFilterApplied(FilterCriteria filterCriteria) {// Setting up list as stream.
        Stream<MovieFile> fileStream = filesFull.stream(); // note: we have to start with files full in order to take into account all files when filtering
        Log.d("Debug", "size of filesfull before stream: " + String.valueOf(filesFull.size()));
        // refreshing fileMovieListFull such that it is filtering based on all values again!
        // Step 1: Sort the list based on the sort criteria
        String sortCriteria = filterCriteria.getSortCriteria();
        switch (sortCriteria) {
            case "A-Z":
                fileStream = fileStream.sorted(Comparator.comparing(MovieFile::getFileNameToLower));
                break;
            case "Z-A":
                fileStream = fileStream.sorted(Comparator.comparing(MovieFile::getFileNameToLower).reversed());
                break;
            case "None":
                // No sorting needed
                break;
            default:
                // Handle invalid sort criteria
                throw new IllegalArgumentException("Invalid sort criteria: " + filterCriteria.getSortCriteria());
        }

        // Step 2: Filter the list based on emotion thresholds
        fileStream = fileStream.filter(file ->
                file.getHappinessPercentage() >= filterCriteria.getHappinessThreshold() &&
                        file.getSadnessPercentage() >= filterCriteria.getSadnessThreshold() &&
                        file.getFearPercentage() >= filterCriteria.getFearThreshold() &&
                        file.getAngerPercentage() >= filterCriteria.getAngerThreshold() &&
                        file.getDisgustPercentage() >= filterCriteria.getDisgustThreshold() &&
                        file.getSurprisePercentage() >= filterCriteria.getSurpriseThreshold()
        );

        Log.d("Debug", "size of filesfull after stream: " + String.valueOf(filesFull.size()));
        Log.d("Debug", "size of files before assignment: " + String.valueOf(files.size()));
        // Step 3: Store everything back into an ArrayList
        files = fileStream.collect(Collectors.toList());
        Log.d("Debug", "size of files after assignment: " + String.valueOf(files.size()));
//        fileAdapter.setMovieFileList(files);
        fileAdapter.setMovieFileListFull(files);
        fileAdapter.getFilter().filter("");
        fileAdapter.notifyDataSetChanged();
        for (MovieFile file: files) {
            Log.d("Debug", file.getFileName());
        }

        Log.d("Debug", String.valueOf(files.size()));

    }

    /**
     * Function used to refresh the file list whenever refresh button has been triggered.
     */
    private void setUpBtnRefreshFiles() {
        // SHOWING refresh button as user is logged in
        btnRefreshFiles.setVisibility(View.VISIBLE);
        btnRefreshFiles.setOnClickListener(v -> {
            Log.d("Database", "Requesting user files again due to a refresh clicked");
            requestUserFiles(userId);
        });
    }

    @Override
    public boolean isFirstTimeFiltering() {
        return firstFilterSetupFlag;
    }

    @Override
    public void setFirstTimeFilteringFlag(boolean value) {
        this.firstFilterSetupFlag = value;
    }

    private void startFileActivity(String fileName) {
        Intent intent = new Intent(this, FileActivity.class);
        intent.putExtra("fileName", fileName);
        startActivity(intent);
    }
}
