package com.example.application.database.models.enums;

public enum MassTarget {
    Reduce(1),
    Gain(2),
    Maintenance(3);

    private int id;

    MassTarget(int id){
        this.id = id;
    }

    public static MassTarget getTarget(int id){
        switch(id){
            case 1:
                return MassTarget.Reduce;
            case 2:
                return MassTarget.Gain;
            case 3:
                return MassTarget.Maintenance;
            default:
                return MassTarget.Maintenance;
        }
    }

    public int getId(){
        return this.id;
    }
}
