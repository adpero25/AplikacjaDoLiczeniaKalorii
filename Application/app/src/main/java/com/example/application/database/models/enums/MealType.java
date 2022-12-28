package com.example.application.database.models.enums;

import com.example.application.database.models.Meal;

public enum MealType {
    Breakfast(1),
    Dinner(2),
    Supper(3);

    private int id;

    MealType(int id){
        this.id = id;
    }

    public static MealType getMeal(int id){
        switch(id){
            case 1:
                return MealType.Breakfast;
            case 2:
                return MealType.Dinner;
            case 3:
                return MealType.Supper;
            default:
                return MealType.Breakfast;
        }
    }

    public int getId(){
        return this.id;
    }
}
