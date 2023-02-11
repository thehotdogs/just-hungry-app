package com.example.just_hungry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailField;
    private TextInputLayout passwordField;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        emailField = (TextInputLayout) findViewById(R.id.emailField);
        passwordField = (TextInputLayout) findViewById(R.id.passwordField);
        loginButton = (Button) findViewById(R.id.loginButton);
        String email = emailField.getEditText().getText().toString();
        String password = passwordField.getEditText().getText().toString();
        Log.d("login",email+ password);
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

//        if (preferences.getBoolean("logged_in", false)) {
//            startHomeActivity();
//        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getEditText().getText().toString();
                String password = passwordField.getEditText().getText().toString();
                Log.d("login",email+ password);
                System.out.println("Email: " + email);
                System.out.println("Password: " + password);
                // Add logic to check if the username and password are correct

                // If the username and password are correct, start the home activity and
                // set the "logged_in" preference to true
                editor.putBoolean("logged_in", true);
                editor.apply();
                startHomeActivity();
            }
        });
    }

    private void startHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
