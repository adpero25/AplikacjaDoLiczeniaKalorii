package com.example.application.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;

public class SingleSpinnerAdapter<T> extends RecyclerView.Adapter<SingleSpinnerAdapter.SingleSpinnerHolder> {

    public SingleSpinnerAdapter() {
    }

    @NonNull
    @Override
    public SingleSpinnerHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.loading_list_item, viewGroup, false);

        return new SingleSpinnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleSpinnerHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class SingleSpinnerHolder extends RecyclerView.ViewHolder {
        public SingleSpinnerHolder(View view) {
            super(view);
        }
    }
}