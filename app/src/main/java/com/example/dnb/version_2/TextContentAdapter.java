package com.example.dnb.version_2;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dnb.R;

import java.util.ArrayList;

public class TextContentAdapter extends RecyclerView.Adapter<TextContentAdapter.TextContentViewHolder> {

    private ArrayList<String> textContentList;

    public TextContentAdapter(ArrayList<String> textContentList) {
        this.textContentList = textContentList;
    }

    @NonNull
    @Override
    public TextContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_content, parent, false);
        return new TextContentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TextContentViewHolder holder, int position) {
        String textContent = textContentList.get(position);
        holder.textViewContent.setText("-> "+textContent);
    }

    @Override
    public int getItemCount() {
        return textContentList.size();
    }

    public static class TextContentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewContent;

        public TextContentViewHolder(View itemView) {
            super(itemView);
            textViewContent = itemView.findViewById(R.id.textViewContent);
        }
    }
}

