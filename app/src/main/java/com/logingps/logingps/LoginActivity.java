package com.logingps.logingps;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        setTitle("Login");

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        createDefaultUser();
    }

    private void createDefaultUser() {
        mAuth.createUserWithEmailAndPassword("test2@gmail.com", "123456")
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

    public void signIn(final View view) {
        String strEmail = email.getText().toString();
        String strPassword = password.getText().toString();

        Log.d("SingIn", "signIn:" + strEmail);
        if (validateField(strEmail, strPassword)) {
            mAuth.signInWithEmailAndPassword(strEmail, strPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("SingIn", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(view, user);
                            } else {
                                Log.w("SingIn", "signInWithEmail:failure", task.getException());
                                Toast.makeText(view.getContext(), "Login failed!", Toast.LENGTH_SHORT).show();
                            }
                            if (!task.isSuccessful()) {
                                Log.w("SingIn", "signInWithEmail:failure", task.getException());
                                Toast.makeText(view.getContext(), "Login failed!", Toast.LENGTH_SHORT).show();
                            }
                            hideProgressDialog();
                        }
                    });
        } else {
            alertDialog();
        }
    }

    private void updateUI(View view, FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            Intent intent = new Intent(view.getContext(), HomeActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(view.getContext(), "Login failed!", Toast.LENGTH_SHORT).show();
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Valida se os campos de login foram preenchidos
     *
     * @return true se tudo estiver preenchido
     * false se algum campo estiver vazio
     */
    private boolean validateField(String email, String password) {
        if (email.isEmpty()) {
            return false;
        }

        if (password.isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * Popup de alerta. Utilizada quando algum campo do formulário não estiver preenchido
     */
    private void alertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Required Fields")
                .setMessage("Verify that you filled in all fields")

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
}
