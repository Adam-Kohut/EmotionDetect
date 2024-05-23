package com.example.EmotionDetect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class ProfileActivity extends AppCompatActivity {

    private final String USER_NAME_AND_EMAIL_URL = "https://studev.groept.be/api/a23pt314/get_username_and_email_from_id";
    private String userNameAndEmail; // stores fetched username.
    private BottomNavigationView bottomNavigationView;
    private TextView txtUserName, logOutCard;
    private ImageView profileImage;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profilePage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // setting up nav bar.
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        txtUserName = findViewById(R.id.username_textview);
        logOutCard = findViewById(R.id.logout_option);
        profileImage = findViewById(R.id.profile_image);
        setUpNavBar();
//        fetchAndDisplayUserName();

        //check if used is logged in
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        Log.d("Shared prefs", sharedPreferences.toString());
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            int userId = Integer.parseInt(sharedPreferences.getString("userId", "-1"));
            logOutCard.setVisibility(View.VISIBLE);

            if (userId != - 1) {
                fetchAndDisplayUserName(userId);
                Log.d("Login", "User is logged in.");
            }
            else Log.e("Shared Prefs", "user id hasn't been saved correctly");
        } else {
            //txtUserName.setText(R.string.welcome_message_anonymousProfile);
            logOutCard.setVisibility(View.GONE);
        }

        //setup click listener log out button
        logOutCard.setOnClickListener(this::logOut);
    }

    public void onProfileImageClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }

    private void setUpNavBar() {
        // setup nav listeners
        // set Home selected
        bottomNavigationView.setSelectedItemId(R.id.profile);
        // item selected listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // note item is of type menuItem from the navbar.
            if (item.getItemId() == R.id.files) {
                startActivity(new Intent(getApplicationContext(), FilesActivity.class));
                return true;
            } else if (item.getItemId() == R.id.profile) {
                return true;
            } else if (item.getItemId() == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;
            }
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
                USER_NAME_AND_EMAIL_URL,
                response -> {
                    Log.d("Database", "Response received");
                    try {
                        JSONArray arr = new JSONArray(response);
                        if (arr.length() == 1) {
                            JSONObject user = arr.getJSONObject(0);
                            Log.d("Login", "received username: " + user.getString("user_name") + "received email address: " + user.getString("email_address"));
                            userNameAndEmail = String.format("Username: %s!\nEmail Adress: %s", user.getString("user_name"), user.getString("email_address"));
                        } else {
                            Log.e("Database", "username couldn't be fetched for display");
                        }
                    } catch (JSONException e) {
                        Log.e("JSON", Objects.requireNonNull(e.getMessage()));
                        throw new RuntimeException(e);
                    }

                    if (id >= 0) {// if id is present
                        txtUserName.setVisibility(View.VISIBLE);
                        txtUserName.setText(userNameAndEmail);
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
//        signUpCard.setVisibility(View.VISIBLE);
        logOutCard.setVisibility(View.GONE);
        txtUserName.setText(R.string.welcome_message_anonymous);
        Toast.makeText(ProfileActivity.this, "You are logged out.", Toast.LENGTH_LONG).show();
        Log.d("Shared Prefs", "shared preferences cleared.");
        Log.d("Log in", "User has logged out.");
    }
}
