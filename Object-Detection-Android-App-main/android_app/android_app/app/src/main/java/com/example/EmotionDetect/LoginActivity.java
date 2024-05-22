package com.example.EmotionDetect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextView txtGoRegister;
    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private final String USER_CHECK_URL = "https://studev.groept.be/api/a23pt314/is_account_present";
    TextView txtForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // fetch views-
        txtGoRegister = findViewById(R.id.txtNoAccountClickable);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);

        // setup going to register page
        txtGoRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        // TODO: setup forgot password

        // super btnLogin listener.
        btnLogin.setOnClickListener(v -> {
            Log.d("Login", "Login button pressed");
            String email = String.valueOf(inputEmail.getText());
            String password = String.valueOf(inputPassword.getText());
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    USER_CHECK_URL,
                    response -> {
                        Log.d("Database", "Response received");
                        try {
                            JSONArray arr = new JSONArray(response);
                            if (arr.length() == 1) {
                                // getting user JSON object from JSON array here...
                                JSONObject user = arr.getJSONObject(0);
                                String userId = user.getString("id");
                                Log.d("Login", "received user id: " + userId);

//                                // Save login state and userId to SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.putString("userId", userId);
                                editor.apply();

                                // start main activity again
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), "Welcome, login successful!", Toast.LENGTH_SHORT).show();
                            }
                            else { // login unsuccessful
                                Log.d("Login", "Login failed: invalid credentials");
                                Toast.makeText(getApplicationContext(), "Please fill in your email and password", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.d("Login", "JSON parsing error: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "Login failed: JSON parsing error", Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }
                    },
                    error -> {
                        Log.e("Database", Objects.requireNonNull(error.getMessage()));
                        Toast.makeText(getApplicationContext(), "Login failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("password", password);
                    params.put("email", email);
                    return params;
                }
            };
            queue.add(request);
        });
    }
}