package com.example.learnhiragana;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhiragana.adapters.EmparejamientoAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Memory4 extends AppCompatActivity {

    private RecyclerView recyclerEmparejamiento;
    private TextView tvFeedback, textViewPuntajeEmp, textViewTimerEmp;
    private List<String> items = new ArrayList<>();
    private Map<String, String> emparejamientos = new HashMap<>();
    private String primeraSeleccion = null;
    private int primeraPosicion = -1;
    int puntajeEmp = 0;
    CountDownTimer countDownTimer;

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS_KEY = "MySharedPrefs";
    private static final String BEST_SCORE_KEY_PREFIX = "BestScore_";

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_memory4);

        db = FirebaseFirestore.getInstance(); // Inicializa Firestore
        currentUser = FirebaseAuth.getInstance().getCurrentUser(); // Obtiene el usuario actual de FirebaseAuth

        textViewPuntajeEmp = findViewById(R.id.PuntajeEmp); // Obtiene el TextView del puntaje
        textViewTimerEmp = findViewById(R.id.textViewTimerEmp); // Obtiene el TextView del temporizador

        tvFeedback = findViewById(R.id.tv_feedback); // Obtiene el TextView de retroalimentación
        recyclerEmparejamiento = findViewById(R.id.recycler_emparejamiento); // Obtiene el RecyclerView de emparejamiento

        recyclerEmparejamiento.setLayoutManager(new GridLayoutManager(this, 4)); // Configura el LayoutManager del RecyclerView

        inicializarTarjetas(); // Inicializa las tarjetas

        sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE); // Inicializa SharedPreferences

        // Inicia el temporizador del juego
        countDownTimer = new CountDownTimer(6000, 1000) {
            public void onTick(long millisUntilFinished) {
                textViewTimerEmp.setText("Tiempo: " + millisUntilFinished / 1000); // Actualiza el temporizador cada segundo
            }
            public void onFinish() {
                onGameFinished(); // Llama a onGameFinished() cuando el temporizador termina
            }
        }.start();

        // Configura el RecyclerView con el adaptador
        EmparejamientoAdapter adapter = new EmparejamientoAdapter(items, new EmparejamientoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                manejarSeleccion(position); // Maneja la selección del ítem
            }
        });
        recyclerEmparejamiento.setAdapter(adapter); // Establece el adaptador al RecyclerView
    }

    private void inicializarTarjetas() {
        // Mapa de emparejamientos de Hiragana y romaji
        emparejamientos.put("お", "o");
        emparejamientos.put("は", "ha");
        emparejamientos.put("よ", "yo");
        emparejamientos.put("う", "u");
        emparejamientos.put("か", "ka");
        emparejamientos.put("き", "ki");

        // Agrega los items (Hiragana y romaji)
        for (Map.Entry<String, String> entry : emparejamientos.entrySet()) {
            items.add(entry.getKey());
            items.add(entry.getValue());
        }
        // Mezcla las tarjetas
        Collections.shuffle(items);
    }

    private void onGameFinished() {
        textViewTimerEmp.setVisibility(View.GONE);
        textViewPuntajeEmp.setVisibility(View.GONE);
        tvFeedback.setVisibility(View.GONE);
        recyclerEmparejamiento.setVisibility(View.GONE);

        if (currentUser != null) {
            saveBestScore(puntajeEmp); // Guarda el mejor puntaje si el usuario está logueado
            int bestScore = getBestScore2(); // Obtiene el mejor puntaje de score2
            String userEmail = currentUser.getEmail();
            saveScoreToFirestore(userEmail, puntajeEmp); // Guarda el puntaje en Firestore
            tvFeedback.setText("Tiempo terminado\n" +
                    "Puntaje final: " + puntajeEmp + "\n" +
                    "Mejor puntuación: " + bestScore); // Muestra el puntaje final y el mejor puntaje
        } else {
            tvFeedback.setText("Tiempo terminado\n" +
                    "Puntaje final: " + puntajeEmp); // Muestra solo el puntaje final si no hay usuario logueado
        }

        tvFeedback.setTextColor(Color.BLACK);
        tvFeedback.setVisibility(View.VISIBLE);

        Button retakeButton = new Button(Memory4.this);
        retakeButton.setText("Volver a jugar");

        LinearLayout mainLayout = findViewById(R.id.main_layout);
        mainLayout.addView(retakeButton);

        retakeButton.setOnClickListener(view -> recreate());
    }

    private void saveScoreToFirestore(String userEmail, int scoreEmp) {
        Map<String, Object> userScoreUpdate = new HashMap<>();
        userScoreUpdate.put("score2", scoreEmp);

        db.collection("score").document(userEmail).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            // El documento existe, usar update
                            db.collection("score").document(userEmail).update(userScoreUpdate)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Score actualizado exitosamente");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error al actualizar el score", e);
                                    });
                        } else {
                            // El documento no existe, usar set
                            db.collection("score").document(userEmail).set(userScoreUpdate)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Score guardado exitosamente");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error al guardar el score", e);
                                    });
                        }
                    } else {
                        Log.e("Firestore", "Error al verificar la existencia del documento", task.getException());
                    }
                });
    }

    private void saveBestScore(int scoreEmp) {
        if (currentUser != null) {
            int currentBestScore = getBestScore2(); // Obtiene el mejor puntaje actual de score2
            if (scoreEmp > currentBestScore) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(BEST_SCORE_KEY_PREFIX + "2_" + currentUser.getUid(), scoreEmp); // Guarda el nuevo mejor puntaje en score2
                editor.apply();
            }
        }
    }

    private int getBestScore2() {
        if (currentUser != null) {
            return sharedPreferences.getInt(BEST_SCORE_KEY_PREFIX + "2_" + currentUser.getUid(), 0); // Devuelve el mejor puntaje del campo score2
        } else {
            return 0;
        }
    }

    private void manejarSeleccion(int position) {
        String seleccionActual = items.get(position); // Obtiene el ítem seleccionado

        if (primeraSeleccion == null) {
            // Primera selección
            primeraSeleccion = seleccionActual; // Guarda la primera selección
            primeraPosicion = position; // Guarda la posición de la primera selección
        } else {
            // Segunda selección
            if (esEmparejamientoCorrecto(primeraSeleccion, seleccionActual)) {
                tvFeedback.setTextColor(Color.GREEN);
                tvFeedback.setText("¡Correcto! " + primeraSeleccion + " y " + seleccionActual + " son un par.");
                puntajeEmp += 100; // Incrementa el puntaje
                textViewPuntajeEmp.setText("Puntaje: " + puntajeEmp); // Actualiza la vista de puntaje
            } else {
                tvFeedback.setTextColor(Color.RED);
                tvFeedback.setText("Incorrecto. Inténtalo de nuevo.");
            }
            // Resetea las selecciones
            primeraSeleccion = null;
            primeraPosicion = -1;
        }
    }

    private boolean esEmparejamientoCorrecto(String seleccion1, String seleccion2) {
        // Verifica si las dos selecciones son un par
        return (emparejamientos.containsKey(seleccion1) && emparejamientos.get(seleccion1).equals(seleccion2)) ||
                (emparejamientos.containsKey(seleccion2) && emparejamientos.get(seleccion2).equals(seleccion1));
    }
}
