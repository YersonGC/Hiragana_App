package com.example.learnhiragana;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log; // Importa la clase Log
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learnhiragana.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Memory5 extends AppCompatActivity {

    TextView textViewPregunta, textViewResultado, textViewPuntaje, textViewTimer;
    Button boton1, boton2, boton3;

    String[] hiragana = {"あ", "え", "い", "お", "う"};

    Map<String, String> hiraganaRomaji = new HashMap<>();

    int puntaje = 0;
    CountDownTimer countDownTimer;

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS_KEY = "MySharedPrefs";
    private static final String BEST_SCORE_KEY_PREFIX = "BestScore_";

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory5);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        textViewPuntaje = findViewById(R.id.textViewPuntaje);
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewPregunta = findViewById(R.id.textViewPregunta);
        textViewResultado = findViewById(R.id.textViewResultado);
        boton1 = findViewById(R.id.boton1);
        boton2 = findViewById(R.id.boton2);
        boton3 = findViewById(R.id.boton3);

        hiraganaRomaji.put("あ", "a");
        hiraganaRomaji.put("え", "e");
        hiraganaRomaji.put("い", "i");
        hiraganaRomaji.put("お", "o");
        hiraganaRomaji.put("う", "u");

        generarPregunta();

        boton1.setOnClickListener(this::verificarRespuesta);
        boton2.setOnClickListener(this::verificarRespuesta);
        boton3.setOnClickListener(this::verificarRespuesta);

        sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);

        countDownTimer = new CountDownTimer(6000, 1000) {
            public void onTick(long millisUntilFinished) {
                textViewTimer.setText("Tiempo: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                onGameFinished();
            }
        }.start();
    }

    private void generarPregunta() {
        Random random = new Random();
        int indiceCorrecto = random.nextInt(5);
        String hiraganaPregunta = hiragana[indiceCorrecto];
        textViewPregunta.setText(hiraganaPregunta);

        List<String> opcionesRomaji = Arrays.asList(hiraganaRomaji.values().toArray(new String[0]));
        Collections.shuffle(opcionesRomaji);

        if (!opcionesRomaji.subList(0, 3).contains(hiraganaRomaji.get(hiraganaPregunta))) {
            opcionesRomaji.set(random.nextInt(3), hiraganaRomaji.get(hiraganaPregunta));
        }

        boton1.setText(opcionesRomaji.get(0));
        boton2.setText(opcionesRomaji.get(1));
        boton3.setText(opcionesRomaji.get(2));
    }

    private void verificarRespuesta(View view) {
        Button botonPresionado = (Button) view;
        String respuestaUsuario = botonPresionado.getText().toString();

        String respuestaCorrecta = hiraganaRomaji.get(textViewPregunta.getText().toString());

        if (respuestaUsuario.equals(respuestaCorrecta)) {
            textViewResultado.setText("¡Correcto!");
            textViewResultado.setTextColor(Color.GREEN);
            puntaje += 100;
        } else {
            textViewResultado.setText("¡Incorrecto!");
            textViewResultado.setTextColor(Color.RED);
        }

        textViewPuntaje.setText("Puntaje: " + puntaje);
        generarPregunta();
    }

    private void onGameFinished() {
        textViewTimer.setVisibility(View.GONE);
        textViewPuntaje.setVisibility(View.GONE);
        textViewPregunta.setVisibility(View.GONE);
        boton1.setVisibility(View.GONE);
        boton2.setVisibility(View.GONE);
        boton3.setVisibility(View.GONE);

        if (currentUser != null) {
            saveBestScore(puntaje);
            int bestScore = getBestScore();
            String userEmail = currentUser.getEmail();
            saveScoreToFirestore(userEmail, puntaje);

            textViewResultado.setText("Tiempo terminado\n" +
                    "Puntaje final: " + puntaje + "\n" +
                    "Mejor puntuación: " + bestScore);
        } else {
            textViewResultado.setText("Tiempo terminado\n" +
                    "Puntaje final: " + puntaje);
        }

        textViewResultado.setTextColor(Color.BLACK);

        Button retakeButton = new Button(Memory5.this);
        retakeButton.setText("Volver a jugar");

        LinearLayout mainLayout = findViewById(R.id.main_layout);
        mainLayout.addView(retakeButton);

        retakeButton.setOnClickListener(view -> recreate());
    }

    private void saveScoreToFirestore(String userEmail, int score) {
        Map<String, Object> userScoreUpdate = new HashMap<>();
        userScoreUpdate.put("score", score); // Actualiza el puntaje en el campo "score"

        db.collection("score").document(userEmail).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            // El documento existe, usar update
                            db.collection("score").document(userEmail).update(userScoreUpdate)
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Score actualizado exitosamente"))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Error al actualizar el score", e));
                        } else {
                            // El documento no existe, usar set
                            db.collection("score").document(userEmail).set(userScoreUpdate)
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Score guardado exitosamente"))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Error al guardar el score", e));
                        }
                    } else {
                        Log.e("Firestore", "Error al verificar la existencia del documento", task.getException());
                    }
                });
    }

    private void saveBestScore(int score) {
        if (currentUser != null) {
            int currentBestScore = getBestScore();
            if (score > currentBestScore) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(BEST_SCORE_KEY_PREFIX + currentUser.getUid(), score);
                editor.apply();
            }
        }
    }

    private int getBestScore() {
        if (currentUser != null) {
            return sharedPreferences.getInt(BEST_SCORE_KEY_PREFIX + currentUser.getUid(), 0);
        } else {
            return 0;
        }
    }
}
