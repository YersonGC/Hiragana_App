package com.example.learnhiragana;

import static com.example.learnhiragana.R.*;
import static com.example.learnhiragana.R.id.login00;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private TextView userNameTextView;
    private Button logoutButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userNameTextView = findViewById(R.id.userNameTextView);
        logoutButton = findViewById(R.id.logoutButton);


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userNameTextView.setText(currentUser.getEmail());
        }

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            // Redirige a Memory2 al cerrar la sesión
            Intent intent = new Intent(ProfileActivity.this, Memory2.class);
            startActivity(intent);
            finish(); // Cierra la actividad de perfil
        });

        Button log = findViewById(R.id.login00);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentM = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intentM);
            }
        });



    }
}