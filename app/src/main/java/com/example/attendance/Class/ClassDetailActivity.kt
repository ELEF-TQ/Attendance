package com.example.attendance

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class ClassDetailActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var students: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_detail)

        databaseHelper = DatabaseHelper(this)
        val classNameTextView = findViewById<TextView>(R.id.class_name_text_view)
        val dateTextView = findViewById<TextView>(R.id.date_text_view)
        val studentListView = findViewById<ListView>(R.id.student_list_view)
        val saveButton = findViewById<Button>(R.id.save_button)

        val className = intent.getStringExtra("class_name")
        classNameTextView.text = className

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        dateTextView.text = "Date: $currentDate"

        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM students WHERE class_id = (SELECT id FROM classes WHERE name = ?)", arrayOf(className))
        students = mutableListOf()

        if (cursor.moveToFirst()) {
            do {
                val studentName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val absences = cursor.getInt(cursor.getColumnIndexOrThrow("absences"))
                students.add("$studentName - Absences: $absences")
            } while (cursor.moveToNext())
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, students)
        studentListView.adapter = adapter

        studentListView.setOnItemClickListener { _, _, position, _ ->
            val selectedStudent = students[position]
            val parts = selectedStudent.split(" - ")
            val studentName = parts[0]
            val absenceCount = parts[1].split(": ")[1].toInt() + 1
            students[position] = "$studentName - Absences: $absenceCount"
            adapter.notifyDataSetChanged()
        }

        saveButton.setOnClickListener {
            val writableDb = databaseHelper.writableDatabase
            students.forEach { student ->
                val parts = student.split(" - ")
                val studentName = parts[0]
                val absenceCount = parts[1].split(": ")[1].toInt()
                writableDb.execSQL("UPDATE students SET absences = ? WHERE name = ?", arrayOf(absenceCount, studentName))
            }
            Toast.makeText(this, "Attendance saved", Toast.LENGTH_SHORT).show()
        }
    }
}
