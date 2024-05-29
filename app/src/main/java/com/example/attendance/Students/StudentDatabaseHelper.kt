package com.example.attendance

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StudentDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createStudentsTable = """
            CREATE TABLE $TABLE_STUDENTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_CLASS_NAME TEXT NOT NULL,
                $COLUMN_ABSENCES INTEGER DEFAULT 0
            );
        """.trimIndent()

        db?.execSQL(createStudentsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "attendance.db"
        const val DATABASE_VERSION = 1
        const val TABLE_STUDENTS = "students"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_CLASS_NAME = "class_name"
        const val COLUMN_ABSENCES = "absences"
    }
}
