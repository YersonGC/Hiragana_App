package com.example.learnhiragana.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhiragana.R;

import java.util.List;

public class EmparejamientoAdapter extends RecyclerView.Adapter<EmparejamientoAdapter.EmparejamientoViewHolder>{
    private List<String> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public EmparejamientoAdapter(List<String> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }
    @NonNull
    @Override
    public EmparejamientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.example.learnhiragana.R.layout.item_emparejamiento, parent, false);
        return new EmparejamientoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmparejamientoViewHolder holder, int position) {
        String item = items.get(position);
        holder.bind(item, listener, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class EmparejamientoViewHolder extends RecyclerView.ViewHolder {

        private TextView tvItem;

        public EmparejamientoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tv_item);
        }

        public void bind(final String item, final OnItemClickListener listener, final int position) {
            tvItem.setText(item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }
    }
}

