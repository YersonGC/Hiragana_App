// src/main/java/com/example/learnhiragana/Memory3.java
package com.example.learnhiragana;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.learnhiragana.adapters.LeaderboardAdapter;
import com.example.learnhiragana.models.PlayerTotalScore;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Memory3 extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerViewLeaderboardScore;
    private RecyclerView recyclerViewLeaderboardScore2;
    private RecyclerView recyclerViewLeaderboardTotal;
    private LeaderboardAdapter leaderboardAdapterScore;
    private LeaderboardAdapter leaderboardAdapterScore2;
    private LeaderboardAdapter leaderboardAdapterTotal;
    private List<PlayerTotalScore> leaderboardListScore;
    private List<PlayerTotalScore> leaderboardListScore2;
    private List<PlayerTotalScore> leaderboardListTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory3);

        recyclerViewLeaderboardScore = findViewById(R.id.recycler_view_leaderboard_score);
        recyclerViewLeaderboardScore2 = findViewById(R.id.recycler_view_leaderboard_score2);
        recyclerViewLeaderboardTotal = findViewById(R.id.recycler_view_leaderboard_total);

        recyclerViewLeaderboardScore.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLeaderboardScore2.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLeaderboardTotal.setLayoutManager(new LinearLayoutManager(this));

        leaderboardListScore = new ArrayList<>();
        leaderboardListScore2 = new ArrayList<>();
        leaderboardListTotal = new ArrayList<>();

        leaderboardAdapterScore = new LeaderboardAdapter(leaderboardListScore);
        leaderboardAdapterScore2 = new LeaderboardAdapter(leaderboardListScore2);
        leaderboardAdapterTotal = new LeaderboardAdapter(leaderboardListTotal);

        recyclerViewLeaderboardScore.setAdapter(leaderboardAdapterScore);
        recyclerViewLeaderboardScore2.setAdapter(leaderboardAdapterScore2);
        recyclerViewLeaderboardTotal.setAdapter(leaderboardAdapterTotal);

        db = FirebaseFirestore.getInstance();

        displayTopScores("score", 5, leaderboardListScore, leaderboardAdapterScore);
        displayTopScores("score2", 5, leaderboardListScore2, leaderboardAdapterScore2);
        displayTopTotalScores(5, leaderboardListTotal, leaderboardAdapterTotal);
    }

    private void displayTopScores(String field, int limit, List<PlayerTotalScore> leaderboardList, LeaderboardAdapter leaderboardAdapter) {
        db.collection("score")
                .orderBy(field, Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    leaderboardList.clear();
                    for (int i = 0; i < documents.size(); i++) {
                        String email = documents.get(i).getId();
                        int score = documents.get(i).getLong(field).intValue();
                        leaderboardList.add(new PlayerTotalScore(email, score));
                    }
                    leaderboardAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Manejar el error
                });
    }

    private void displayTopTotalScores(int limit, List<PlayerTotalScore> leaderboardList, LeaderboardAdapter leaderboardAdapter) {
        db.collection("score")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    List<PlayerTotalScore> playerTotalScores = new ArrayList<>();

                    for (DocumentSnapshot doc : documents) {
                        String email = doc.getId();
                        int score = doc.getLong("score") != null ? doc.getLong("score").intValue() : 0;
                        int score2 = doc.getLong("score2") != null ? doc.getLong("score2").intValue() : 0;
                        int totalScore = score + score2;
                        playerTotalScores.add(new PlayerTotalScore(email, totalScore));
                    }

                    Collections.sort(playerTotalScores, (p1, p2) -> Integer.compare(p2.getTotalScore(), p1.getTotalScore()));

                    leaderboardList.clear();
                    leaderboardList.addAll(playerTotalScores.subList(0, Math.min(limit, playerTotalScores.size())));
                    leaderboardAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Manejar el error
                });
    }
}
