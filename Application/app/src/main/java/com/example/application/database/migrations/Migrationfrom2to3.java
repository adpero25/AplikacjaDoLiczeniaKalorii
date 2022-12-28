package com.example.application.database.migrations;

import androidx.room.RenameColumn;
import androidx.room.migration.AutoMigrationSpec;

@RenameColumn(
        tableName = "day",
        fromColumnName = "hasPracticed",
        toColumnName = "has_practiced"
)

public class Migrationfrom2to3 implements AutoMigrationSpec {

}
