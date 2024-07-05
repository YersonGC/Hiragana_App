// src/main/java/com/example/learnhiragana/adapters/LeaderboardAdapter.java
package com.example.learnhiragana.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.learnhiragana.R;
import com.example.learnhiragana.models.PlayerTotalScore;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private List<PlayerTotalScore> leaderboard;

    public LeaderboardAdapter(List<PlayerTotalScore> leaderboard) {
        this.leaderboard = leaderboard;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        PlayerTotalScore player = leaderboard.get(position);
        holder.rank.setText(String.valueOf(position + 1));
        holder.email.setText(player.getEmail());
        holder.score.setText(String.valueOf(player.getTotalScore()));
    }

    @Override
    public int getItemCount() {
        return leaderboard.size();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView rank, email, score;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.tv_rank);
            email = itemView.findViewById(R.id.tv_email);
            score = itemView.findViewById(R.id.tv_score);
        }
    }
}
