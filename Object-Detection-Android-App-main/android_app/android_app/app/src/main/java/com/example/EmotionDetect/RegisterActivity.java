package com.example.EmotionDetect;

import android.content.Intent;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

// TODO: DISPLAY PROGRESS BAR WITH ERROR MESSAGE + create table + modify all to incorporate user_id

public class RegisterActivity extends AppCompatActivity {

    private EditText txtName, txtEmail, txtPassword, txtPasswordConfirmed;
    private Button btnRegister;
    private TextView txtClickHere;
    private static final String URL = "https://studev.groept.be/api/a23PT314/create_new_account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // fetch views
        txtName = findViewById(R.id.inputName);
        txtEmail = findViewById(R.id.inputEmail);
        txtPassword = findViewById(R.id.inputPassword);
        txtPasswordConfirmed = findViewById(R.id.inputPasswordConfirmation);
        btnRegister = findViewById(R.id.btnRegister);
        txtClickHere = findViewById(R.id.txtClickHere);

        // button click listener
        btnRegister.setOnClickListener(v -> {
            String name, email, password, passwordConfirmation;
            name = String.valueOf(txtName.getText());
            email = String.valueOf(txtEmail.getText());
            password = String.valueOf(txtPassword.getText());
            passwordConfirmation = String.valueOf(txtPasswordConfirmed.getText());

            if (
                password.equals(passwordConfirmation) &&
                !name.isEmpty() &&
                !password.isEmpty() &&
                !email.isEmpty()
            )
                {
                    // create account (DB push
                    createAccountDB(name, email, password, passwordConfirmation);

                }
            else
                {
                    // clear password and display error message.
                    txtPassword.getText().clear();
                    txtPasswordConfirmed.getText().clear();
                    Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_LONG).show();
                }
        });

        // set up "click here" event.
        txtClickHere.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void createAccountDB(String name, String email, String password, String passwordConfirmation) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Register", String.valueOf(response));
                        // based on response value, account will be made or not made.
                        pushToDB(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                RegisterActivity.this,
                                "Account already exists or unable to communicate with the server",
                                Toast.LENGTH_LONG).show();
                        Log.d("database", error.toString());
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", name);
                        params.put("password", password);
                        params.put("email", email);
                        return params;
            }
        };
        requestQueue.add(request);
    }

    private void pushToDB(String response) {
        // note: bad practice as DB can be changed but more efficient
        // than having to fetch and search through the whole userlist!
        response = response.toLowerCase();
        if (response.equals("[]")) { // on success
            Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else { // duplicate present.
            if(response.contains("user_name") && response.contains("duplicate")) {
                Toast.makeText(getApplicationContext(), "User name already exists. Choose another.", Toast.LENGTH_SHORT).show();
            }
            if(response.contains("email_address") && response.contains("duplicate")) {
                Toast.makeText(getApplicationContext(), "Email address already exists. Choose another.", Toast.LENGTH_SHORT).show();
            }

            txtName.getText().clear();
            txtEmail.getText().clear();
            txtPassword.getText().clear();
            txtPasswordConfirmed.getText().clear();

        }
    }
}