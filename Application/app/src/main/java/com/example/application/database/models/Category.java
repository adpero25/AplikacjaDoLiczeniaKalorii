package com.example.application.database.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "category",
        indices = {
                @Index(
                        value = "name",
                        unique = true
                )
        }
)
public class Category {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    public Long categoryId;

    @NonNull
    public String name = "";
}
