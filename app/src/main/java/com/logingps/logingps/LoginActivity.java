package com.logingps.logingps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        setTitle("Login");

        createDefaultUser();
    }

    private void createDefaultUser() {
        mAuth.createUserWithEmailAndPassword("test@gmail.com", "123456")
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i("CreateUser", "I'm here!");


                        if (task.isSuccessful()) {
                            Log.i("CreateUser", "Successfully registered User!");

                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            Log.i("CreateUser", "Erro registered User!");
                        }
                    }
                });
    }

    public void signin(View view) {
        Intent intent = new Intent(view.getContext(), HomeActivity.class);
        startActivity(intent);
    }

}
