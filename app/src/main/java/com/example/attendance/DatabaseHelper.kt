package com.example.attendance

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createClassesTable = "CREATE TABLE classes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL" +
                ");"

        val createStudentsTable = "CREATE TABLE students (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "class_id INTEGER, " +
                "absences INTEGER DEFAULT 0, " +
                "FOREIGN KEY(class_id) REFERENCES classes(id)" +
                ");"

        db?.execSQL(createClassesTable)
        db?.execSQL(createStudentsTable)

        // Insert sample data
        db?.execSQL("INSERT INTO classes (name) VALUES ('Class A'), ('Class B'), ('Class C');")
        db?.execSQL("INSERT INTO students (name, class_id) VALUES ('Student 1', 1), ('Student 2', 1), ('Student 3', 2);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS students")
        db?.execSQL("DROP TABLE IF EXISTS classes")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "attendance.db"
        const val DATABASE_VERSION = 1
    }
}
