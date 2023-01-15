package com.example.application.database.models.enums;

import com.example.application.CaloriesCalculatorContext;
import com.example.application.R;

public enum ActivityIndicator {
    LittleOrNoActivity(1, 1.2, R.string.LittleOrNoActivity),
    LightlyActivity(2, 1.375, R.string.LightlyActivity),
    ModeratelyActivity(3, 1.55, R.string.ModeratelyActivity),
    Active(4, 1.725, R.string.Active),
    VeryActive(5, 1.9, R.string.VeryActive);

    public final int id;
    private final double value;
    private final int resourceId;

    ActivityIndicator(int id, double val, int resourceId) {
        this.id = id;
        this.value = val;
        this.resourceId = resourceId;
    }

    public static ActivityIndicator getActivity(int id) {
        switch (id) {
            case 1:
                return ActivityIndicator.LittleOrNoActivity;
            case 2:
                return ActivityIndicator.LightlyActivity;
            case 3:
                return ActivityIndicator.ModeratelyActivity;
            case 4:
                return ActivityIndicator.Active;
            case 5:
                return ActivityIndicator.VeryActive;
            default:
                return ActivityIndicator.LightlyActivity;
        }
    }

    public int getId() {
        return this.id;
    }

    public double getIndicator() {
        return value;
    }

    public String toString() {
        return CaloriesCalculatorContext.getAppContext().getString(resourceId);
    }
}
