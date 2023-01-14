package com.example.application.database.migrations;

import androidx.room.DeleteColumn;
import androidx.room.migration.AutoMigrationSpec;

@DeleteColumn(
        tableName = "meal",
        columnName = "picture_path"
)

public class MigrationFrom6To7 implements AutoMigrationSpec {

}
