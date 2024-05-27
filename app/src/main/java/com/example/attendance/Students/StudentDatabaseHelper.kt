package com.example.attendance

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StudentDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "studentDatabase"
        private const val TABLE_STUDENTS = "students"

        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_CLASS_NAME = "class_name"
        private const val COLUMN_ABSENCES = "absences"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createStudentsTable = ("CREATE TABLE $TABLE_STUDENTS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT, "
                + "$COLUMN_CLASS_NAME TEXT, "
                + "$COLUMN_ABSENCES INTEGER)")
        db.execSQL(createStudentsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        onCreate(db)
    }

    fun addStudent(student: Student) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, student.name)
        values.put(COLUMN_CLASS_NAME, student.className)
        values.put(COLUMN_ABSENCES, student.absences)
        db.insert(TABLE_STUDENTS, null, values)
        db.close()
    }

    fun getAllStudents(): List<Student> {
        val studentList = ArrayList<Student>()
        val selectQuery = "SELECT * FROM $TABLE_STUDENTS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val student = Student(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    className = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_NAME)),
                    absences = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ABSENCES))
                )
                studentList.add(student)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return studentList
    }

    fun updateAttendance(id: Int) {
        val db = this.writableDatabase
        val selectQuery = "SELECT $COLUMN_ABSENCES FROM $TABLE_STUDENTS WHERE $COLUMN_ID = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(id.toString()))

        var currentAbsences = 0
        if (cursor.moveToFirst()) {
            currentAbsences = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ABSENCES))
        }
        cursor.close()

        val values = ContentValues()
        values.put(COLUMN_ABSENCES, currentAbsences + 1)
        db.update(TABLE_STUDENTS, values, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteStudent(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_STUDENTS, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
    }
}
