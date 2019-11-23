package com.logingps.logingps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        setTitle("Login");
    }

    public void signin(View view) {
        Intent intent = new Intent(view.getContext(), HomeActivity.class);
        startActivity(intent);
    }

}
