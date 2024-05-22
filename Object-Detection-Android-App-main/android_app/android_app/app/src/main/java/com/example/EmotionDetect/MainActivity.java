package com.example.EmotionDetect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SignalStrengthUpdateRequest;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private CardView cameraCard;
    private CardView uploadCenterCard;

    private CardView signUpCard;
    private CardView logOutCard;
    BottomNavigationView bottomNavigationView;
    private TextView txtUserName;
    private final String USER_NAME_URL = "https://studev.groept.be/api/a23pt314/get_username_from_id";
    private String userName; // stores fetched username.
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
        uploadCenterCard = findViewById(R.id.uploadCenterCard);
        signUpCard = findViewById(R.id.signUpCard);
        logOutCard = findViewById(R.id.logOutCard);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        txtUserName = findViewById(R.id.txtUserName);


        // check if user is logged
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        Log.d("Shared prefs", sharedPreferences.toString());
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            int userId = Integer.parseInt(sharedPreferences.getString("userId", "-1"));
            signUpCard.setVisibility(View.GONE);
            logOutCard.setVisibility(View.VISIBLE);

            if (userId != - 1) {
                fetchAndDisplayUserName(userId);
                Log.d("Login", "User is logged in.");
            }
            else Log.e("Shared Prefs", "user id hasn't been saved correctly");
        } else {
            txtUserName.setText(R.string.welcome_message_anonymous);
            signUpCard.setVisibility(View.VISIBLE);
            logOutCard.setVisibility(View.GONE);
        }

        // setup camera listener
        cameraCard.setOnClickListener(v -> startActivity(new Intent(this, CameraActivity.class)));

        // setup upload center listener
        uploadCenterCard.setOnClickListener(v -> startActivity(new Intent(this, VideoActivity.class)));

        // setup sign up listener
        signUpCard.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

        // setup log out listener
        logOutCard.setOnClickListener(this::logOut);

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

    private void fetchAndDisplayUserName(int id) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(
                Request.Method.POST,
                USER_NAME_URL,
                response -> {
                    Log.d("Database", "Response received");
                    try {
                        JSONArray arr = new JSONArray(response);
                        if (arr.length() == 1) {
                            JSONObject user = arr.getJSONObject(0);
                            Log.d("Login", "received username: " + user.getString("user_name"));
                            userName =  String.format("Welcome %s!", user.getString("user_name"));
                        }
                        else {
                            Log.e("Database", "username couldn't be fetched for display");
                        }
                    } catch (JSONException e) {
                        Log.e("JSON", Objects.requireNonNull(e.getMessage()));
                        throw new RuntimeException(e);
                    }

                    if (id >= 0) {// if id is present
                        txtUserName.setVisibility(View.VISIBLE);
                        txtUserName.setText(userName);
                    } else {
                        txtUserName.setVisibility(View.GONE);
                    }
                },
                error -> Log.e("Database", Objects.requireNonNull(error.getMessage()))
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }
        };
        queue.add(request);
    }

    private void logOut(View v) {
        // clear prefs.
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // switch cards
        signUpCard.setVisibility(View.VISIBLE);
        logOutCard.setVisibility(View.GONE);
        txtUserName.setText(R.string.welcome_message_anonymous);
        Toast.makeText(MainActivity.this, "You are logged out.", Toast.LENGTH_LONG).show();
        Log.d("Shared Prefs", "shared preferences cleared.");
        Log.d("Log in", "User has logged out.");
    }
}