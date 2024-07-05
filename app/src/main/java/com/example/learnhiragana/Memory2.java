package com.example.learnhiragana;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class Memory2 extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button signUpButton, signInButton, backButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_memory2);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        signInButton = findViewById(R.id.signInButton);
        backButton = findViewById(R.id.atras4);

        signUpButton.setOnClickListener(v -> signUp());
        signInButton.setOnClickListener(v -> signIn());
        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Verificar si el usuario ya ha iniciado sesión al iniciar la actividad
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // El usuario ya ha iniciado sesión, redirige a ProfileActivity
            startActivity(new Intent(Memory2.this, ProfileActivity.class));
            finish(); // Cierra Memory2 para evitar volver a la pantalla de inicio de sesión
        }
    }

    private void signUp() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Memory2.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        handleAuthException(task);
                    }
                });
    }

    private void signIn() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Memory2.this, "User signed in successfully!", Toast.LENGTH_SHORT).show();



                        startActivity(new Intent(Memory2.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(Memory2.this, "Sign in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Required.");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }

    private void handleAuthException(com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult> task) {
        try {
            throw task.getException();
        } catch (FirebaseAuthWeakPasswordException e) {
            passwordEditText.setError("Weak password.");
            passwordEditText.requestFocus();
        } catch (FirebaseAuthUserCollisionException e) {
            emailEditText.setError("User already exists.");
            emailEditText.requestFocus();
        } catch (Exception e) {
            Toast.makeText(Memory2.this, "Authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}