package com.example.attendance

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.widget.*
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
        val addButton = findViewById<Button>(R.id.add_student_button)

        val className = intent.getStringExtra("class_name")
        classNameTextView.text = className

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        dateTextView.text = "Date: $currentDate"

        students = fetchStudents(className)
        adapter = StudentAdapter(this, students)
        studentListView.adapter = adapter

        addButton.setOnClickListener {
            showAddStudentDialog(className)
        }

        saveButton.setOnClickListener {
            saveAbsences()
            Toast.makeText(this, "Attendance saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddStudentDialog(className: String?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_student, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.name_edit_text)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Student")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameEditText.text.toString()
                if (name.isNotBlank()) {
                    addStudentToDatabase(name, className)
                    students.add(Student(0, name, className ?: "", 0))
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Please enter the student's name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun addStudentToDatabase(name: String, className: String?) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(StudentDatabaseHelper.COLUMN_NAME, name)
            put(StudentDatabaseHelper.COLUMN_CLASS_NAME, className)
            put(StudentDatabaseHelper.COLUMN_ABSENCES, 0)
        }
        db.insert(StudentDatabaseHelper.TABLE_STUDENTS, null, values)
        db.close()
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
