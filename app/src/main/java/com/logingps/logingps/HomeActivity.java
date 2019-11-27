package com.logingps.logingps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    TextView displayName;

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        setTitle("Home");

        displayName = findViewById(R.id.display_name);
    }

    public void getCoordinates(View view) {

    }

    public void showReport(View view) {

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(null, currentUser);
    }

    public void signOut(View view) {
        mAuth.signOut();
        updateUI(view, null);
    }

    private void updateUI(View view, FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            displayName.setText(user.getEmail());
        } else {
            Intent intent = new Intent(view.getContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
