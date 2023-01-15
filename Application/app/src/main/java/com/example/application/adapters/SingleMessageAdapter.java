package com.example.application.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;

import java.util.List;

public class SingleMessageAdapter<T> extends RecyclerView.Adapter<SingleMessageAdapter.SingleMessageHolder> {

    private final String message;

    public SingleMessageAdapter(String message) {
        this.message = message;
    }

    @NonNull
    @Override
    public SingleMessageHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_list_item, viewGroup, false);

        return new SingleMessageHolder(view);
    }

    @Override
    public void onBindViewHolder(SingleMessageHolder viewHolder, final int position) {
        viewHolder.setMessageText(message);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class SingleMessageHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public SingleMessageHolder(View view) {
            super(view);

            textView = (TextView) view.findViewById(R.id.name);
        }

        public void setMessageText(String text) {
            textView.setText(text);
        }
    }
}