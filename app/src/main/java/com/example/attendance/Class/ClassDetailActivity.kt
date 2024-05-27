package com.example.attendance

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class ClassDetailActivity : AppCompatActivity() {
    private lateinit var databaseHelper: StudentDatabaseHelper
    private lateinit var students: MutableList<Student>
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_detail)

        databaseHelper = StudentDatabaseHelper(this)
        val classNameTextView = findViewById<TextView>(R.id.class_name_text_view)
        val dateTextView = findViewById<TextView>(R.id.date_text_view)
        val studentListView = findViewById<ListView>(R.id.student_list_view)
        val saveButton = findViewById<Button>(R.id.save_button)
        val addButton = findViewById<Button>(R.id.add_student_button) // Add this line

        val className = intent.getStringExtra("class_name")
        classNameTextView.text = className

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        dateTextView.text = "Date: $currentDate"

        students = fetchStudents(className)

        adapter = StudentAdapter(this, students)
        studentListView.adapter = adapter

        // Add a click listener to the add student button
        addButton.setOnClickListener {
            val intent = Intent(this, StudentListActivity::class.java)
            intent.putExtra("class_name", className) // Pass the class name to AddStudentActivity
            startActivity(intent)
        }

        saveButton.setOnClickListener {
            saveAbsences()
            Toast.makeText(this, "Attendance saved", Toast.LENGTH_SHORT).show()
        }
    }


    private fun fetchStudents(className: String?): MutableList<Student> {
        val studentList = mutableListOf<Student>()
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${StudentDatabaseHelper.TABLE_STUDENTS} WHERE ${StudentDatabaseHelper.COLUMN_CLASS_NAME} = ?",
            arrayOf(className)
        )

        if (cursor.moveToFirst()) {
            do {
                val student = Student(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_NAME)),
                    className = cursor.getString(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_CLASS_NAME)),
                    absences = cursor.getInt(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_ABSENCES))
                )
                studentList.add(student)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return studentList
    }

    private fun saveAbsences() {
        val db = databaseHelper.writableDatabase
        for (student in students) {
            if (student.isAbsent) {
                student.absences++
                val values = ContentValues().apply {
                    put(StudentDatabaseHelper.COLUMN_ABSENCES, student.absences)
                }
                db.update(
                    StudentDatabaseHelper.TABLE_STUDENTS,
                    values,
                    "${StudentDatabaseHelper.COLUMN_ID} = ?",
                    arrayOf(student.id.toString())
                )
            }
        }
        db.close()
    }
}
