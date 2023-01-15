package com.example.application.fragments;

import static com.example.application.activities.ServingsActivity.SERVING_DATE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;
import com.example.application.activities.AddingServingActivity;
import com.example.application.adapters.OneButtonListItemAdapter;
import com.example.application.database.models.enums.MealType;
import com.example.application.database.models.junctions.DayWithServings;
import com.example.application.database.models.junctions.ServingWithMeal;
import com.example.application.database.repositories.DaysRepository;
import com.example.application.database.repositories.ServingsRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ServingsFragment extends Fragment {

    public static final String MEAL_TYPE = "MEAL_TYPE";

    View view;

    TextView titleView;
    RecyclerView listRoot;

    LocalDate day;
    MealType type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_servings_list, container, false);

        listRoot = (RecyclerView) view.findViewById(R.id.list_root);
        titleView = (TextView) view.findViewById(R.id.title);


        tryToReload();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void setDayAndMealType(LocalDate day, MealType type) {
        this.day = day;
        this.type = type;


        tryToReload();
    }

    public void tryToReload() {
        if (day == null || type == null || titleView == null || listRoot == null) {
            return;
        }

        DaysRepository repo = new DaysRepository(requireActivity().getApplication());
        repo.getOrCreateByDate(day).thenAccept(this::loadServingsForDay);

        switch (type) {
            case Breakfast:
                titleView.setText(getString(R.string.breakfast));
                break;
            case Dinner:
                titleView.setText(getString(R.string.dinner));
                break;
            case Supper:
                titleView.setText(getString(R.string.supper));
                break;
            default:
                titleView.setText("");
                break;
        }

        ((Button) view.findViewById(R.id.add)).setOnClickListener((v) -> {
            Intent intent = new Intent(getActivity(), AddingServingActivity.class);
            intent.putExtra(MEAL_TYPE, type);
            intent.putExtra(SERVING_DATE, day);
            startActivity(intent);
        });
    }

    public void loadServingsForDay(DayWithServings day) {

        List<ServingWithMeal> servings = day.servings.stream().filter(s -> s.serving.mealType == type).collect(Collectors.toList());

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        listRoot.setLayoutManager(layoutManager);
        listRoot.setItemAnimator(null);

        ServingsRepository servingsRepo = new ServingsRepository(requireActivity().getApplication());

        OneButtonListItemAdapter<ServingWithMeal> adapter = new OneButtonListItemAdapter<ServingWithMeal>(servings,
                (serving) -> serving.meals.meal.name,
                () -> getString(R.string.delete),
                (context) ->
                        (View.OnClickListener) v -> {
                            servingsRepo.delete(context.object.serving);
                            context.thisAdapter.removeAt(context.position);
                        }
        );
        listRoot.setAdapter(adapter);
    }
}
