package com.example.application.database.migrations;

import androidx.room.DeleteColumn;
import androidx.room.RenameColumn;
import androidx.room.migration.AutoMigrationSpec;

@DeleteColumn(
        tableName = "meal",
        columnName = "picture_path"
)

public class Migrationfrom5to6 implements AutoMigrationSpec {

}
