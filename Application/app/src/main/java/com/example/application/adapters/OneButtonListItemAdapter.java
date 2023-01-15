package com.example.application.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class OneButtonListItemAdapter<T> extends RecyclerView.Adapter<OneButtonListItemAdapter.OneButtonListItemHolder> {

    private final List<T> data;
    private final Function<T,String> nameSelector;
    private final Supplier<String> buttonTextSelector;
    private final Function<OnClickListenerSelectorContext<T>,View.OnClickListener> onClickListenerSelector;

    public OneButtonListItemAdapter(List<T> data, Function<T,String> nameSelector, Supplier<String> buttonTextSelector, Function<OnClickListenerSelectorContext<T>,View.OnClickListener> onClickListenerSelector) {
        this.data = data;
        this.nameSelector = nameSelector;
        this.buttonTextSelector = buttonTextSelector;
        this.onClickListenerSelector = onClickListenerSelector;
    }

    @NonNull
    @Override
    public OneButtonListItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.one_button_list_item, viewGroup, false);

        return new OneButtonListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(OneButtonListItemHolder viewHolder, final int position) {
        T currentItem = data.get(position);
        viewHolder.setNameTextViewText(nameSelector.apply(currentItem));
        viewHolder.setButtonText(buttonTextSelector.get());
        viewHolder.setOnClickListener(onClickListenerSelector.apply(new OnClickListenerSelectorContext<T>(currentItem,this,position)));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<T> getData(){
        return data;
    }


    public static class OneButtonListItemHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final Button button;

        public OneButtonListItemHolder(View view) {
            super(view);

            nameTextView = (TextView) view.findViewById(R.id.name);
            button = (Button) view.findViewById(R.id.button);
        }

        public void setNameTextViewText(String text) {
            nameTextView.setText(text);
        }

        public void setButtonText(String text) {
            button.setText(text);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            button.setOnClickListener(listener);
        }
    }

    public static class OnClickListenerSelectorContext<T> {
        public T object;
        public OneButtonListItemAdapter<T> thisAdapter;
        public int position;

        public OnClickListenerSelectorContext(T object, OneButtonListItemAdapter<T> thisAdapter, int position) {
            this.object = object;
            this.thisAdapter = thisAdapter;
            this.position = position;
        }
    }
}