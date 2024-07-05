package com.example.learnhiragana.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.learnhiragana.R;

public class CustomAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] values;

    public CustomAdapter(@NonNull Context context, @NonNull String[] values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, true);

        TextView textView = rowView.findViewById(R.id.h_amor);
        textView.setText(values[position]);

        return rowView;
    }
}
