package com.example.application.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TwoButtonListItemAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int CATEGORY_ITEM = 1;
    private final static int NORMAL_ITEM = 2;

    private final Map<String, List<T>> data;
    private final Function<T, String> nameSelector;

    private final Supplier<String> firstButtonTextSelector;
    private final Function<OnClickListenerSelectorContext<T>, View.OnClickListener> firstOnClickListenerSelector;

    private final Supplier<String> secondButtonTextSelector;
    private final Function<OnClickListenerSelectorContext<T>, View.OnClickListener> secondOnClickListenerSelector;

    public TwoButtonListItemAdapter(Map<String, List<T>> data,
                                    Function<T, String> nameSelector,
                                    Supplier<String> firstButtonTextSelector,
                                    Function<OnClickListenerSelectorContext<T>, View.OnClickListener> firstOnClickListenerSelector,
                                    Supplier<String> secondButtonTextSelector,
                                    Function<OnClickListenerSelectorContext<T>, View.OnClickListener> secondOnClickListenerSelector) {
        Map<String, List<T>> collect = data.entrySet().stream()
                .filter(x -> !x.getValue().isEmpty())
                .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));

        this.data = collect;
        this.nameSelector = nameSelector;

        this.firstButtonTextSelector = firstButtonTextSelector;
        this.firstOnClickListenerSelector = firstOnClickListenerSelector;

        this.secondButtonTextSelector = secondButtonTextSelector;
        this.secondOnClickListenerSelector = secondOnClickListenerSelector;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        switch (viewType) {
            case CATEGORY_ITEM:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.category_list_item, viewGroup, false);
                return new CategoryItemHolder(view);
            case NORMAL_ITEM:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.two_buttons_list_item, viewGroup, false);
                return new TwoButtonListItemHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case CATEGORY_ITEM:
                CategoryItemHolder categoryHolder = (CategoryItemHolder) holder;
                categoryHolder.setTitleTextViewText(getCategory(position));
                break;
            case NORMAL_ITEM:
                TwoButtonListItemHolder itemHolder = (TwoButtonListItemHolder) holder;
                T currentItem = getItem(position);
                itemHolder.setNameTextViewText(nameSelector.apply(currentItem));

                itemHolder.setFirstButtonText(firstButtonTextSelector.get());
                itemHolder.setFirstOnClickListener(firstOnClickListenerSelector.apply(new OnClickListenerSelectorContext<T>(currentItem, this, position)));

                itemHolder.setSecondButtonText(secondButtonTextSelector.get());
                itemHolder.setSecondOnClickListener(secondOnClickListenerSelector.apply(new OnClickListenerSelectorContext<T>(currentItem, this, position)));
                break;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (Map.Entry<String, List<T>> entry : data.entrySet()) {
            count += entry.getValue().size() + 1;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int index = 0;

        for (Map.Entry<String, List<T>> entry : data.entrySet()) {
            List<T> currentList = entry.getValue();

            if (position - index <= currentList.size()) {
                if (position - index == 0) {
                    return CATEGORY_ITEM;
                }
                return NORMAL_ITEM;
            }
            index += currentList.size() + 1;
        }
        return NORMAL_ITEM;
    }

    public void removeAt(int position) {
        int index = 0;

        String keyToBeRemoved = null;

        for (Map.Entry<String, List<T>> entry : data.entrySet()) {
            List<T> currentList = entry.getValue();

            if (position - index <= currentList.size()) {
                currentList.remove((position - index) - 1);
                if (currentList.size() == 0) {
                    keyToBeRemoved = entry.getKey();
                }
                break;
            }
            index += currentList.size() + 1;
        }
        notifyItemRemoved(index);

        if (keyToBeRemoved != null) {
            data.remove(keyToBeRemoved);
            notifyItemRemoved((position - index) - 1);
        }

        notifyItemRangeChanged(index, getItemCount());
    }

    private String getCategory(int position) {
        int index = 0;
        for (Map.Entry<String, List<T>> entry : data.entrySet()) {
            List<T> currentList = entry.getValue();

            if (position - index == 0) {
                return entry.getKey();
            }
            index += currentList.size() + 1;
        }
        return null;
    }

    private T getItem(int position) {
        int index = 0;
        for (Map.Entry<String, List<T>> entry : data.entrySet()) {
            List<T> currentList = entry.getValue();

            if (position - index <= currentList.size()) {
                return currentList.get((position - index) - 1);
            }
            index += currentList.size() + 1;
        }
        return null;
    }

    public static class TwoButtonListItemHolder<T> extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final Button firstButton;
        private final Button secondButton;

        private View.OnClickListener firstOnClickListenerSelectorInHolder;
        private View.OnClickListener secondOnClickListenerSelectorInHolder;

        public TwoButtonListItemHolder(View view) {
            super(view);

            nameTextView = (TextView) view.findViewById(R.id.name);
            firstButton = (Button) view.findViewById(R.id.button);
            secondButton = (Button) view.findViewById(R.id.button2);
        }

        public void setNameTextViewText(String text) {
            nameTextView.setText(text);
        }

        public void setFirstButtonText(String text) {
            firstButton.setText(text);
        }

        public void setFirstOnClickListener(View.OnClickListener listener) {
            firstButton.setOnClickListener(listener);
        }

        public void setSecondButtonText(String text) {
            if (text.equals("")) {
                secondButton.setVisibility(View.GONE);
                secondButton.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }
            secondButton.setText(text);
        }

        public void setSecondOnClickListener(View.OnClickListener listener) {
            secondButton.setOnClickListener(listener);

        }
    }


    public static class CategoryItemHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;

        public CategoryItemHolder(View view) {
            super(view);

            titleTextView = (TextView) view;
        }

        public void setTitleTextViewText(String text) {
            if (text.equals("")) {
                titleTextView.setVisibility(View.GONE);
                titleTextView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }
            titleTextView.setText(text);
        }
    }

    public static class OnClickListenerSelectorContext<T> {
        public T object;
        public TwoButtonListItemAdapter<T> thisAdapter;
        public int position;

        public OnClickListenerSelectorContext(T object, TwoButtonListItemAdapter<T> thisAdapter, int position) {
            this.object = object;
            this.thisAdapter = thisAdapter;
            this.position = position;
        }
    }
}