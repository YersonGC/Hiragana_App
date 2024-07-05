package com.example.learnhiragana.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhiragana.R;

import java.util.ArrayList;

public class PalabrasAdapter extends RecyclerView.Adapter<PalabrasAdapter.PalabraViewHolder> {
    ArrayList<String> palabras;
    Context context;
    public PalabrasAdapter(ArrayList<String> palabras, Context context){
        this.palabras = palabras;
        this.context = context;
    }
    @NonNull
    @Override
    public PalabraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new PalabrasAdapter.PalabraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PalabraViewHolder holder, int position) {
        holder.h_amor.setText(palabras.get(position));
    }

    @Override
    public int getItemCount() {
        return palabras.size();
    }

    public static class PalabraViewHolder extends RecyclerView.ViewHolder {
        TextView h_amor;
        public PalabraViewHolder(@NonNull View itemView) {
            super(itemView);
            this.h_amor = itemView.findViewById(R.id.h_amor);
        }
    }
}
