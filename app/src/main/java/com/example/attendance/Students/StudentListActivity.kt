package com.example.attendance

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class StudentListActivity : AppCompatActivity() {
    private lateinit var studentDatabaseHelper: StudentDatabaseHelper
    private lateinit var studentListView: ListView
    private lateinit var addButton: Button
    private lateinit var removeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)

        studentDatabaseHelper = StudentDatabaseHelper(this)
        studentListView = findViewById(R.id.student_list_view)
        addButton = findViewById(R.id.add_student_button)
        removeButton = findViewById(R.id.remove_student_button)

        addButton.setOnClickListener { showAddStudentDialog() }
        removeButton.setOnClickListener { showRemoveStudentDialog() }

        displayStudents()
    }

    private fun displayStudents() {
        val studentList = studentDatabaseHelper.getAllStudents()
        val studentDetails = studentList.map { "${it.name} - ${it.className} - Absences: ${it.absences}" }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, studentDetails)
        studentListView.adapter = adapter
        studentListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        studentListView.setOnItemClickListener { _, _, position, _ ->
            val student = studentList[position]
            studentDatabaseHelper.updateAttendance(student.id)
            displayStudents()
        }
    }

    private fun showAddStudentDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_add_student, null)

        val nameEditText = dialogLayout.findViewById<EditText>(R.id.name_edit_text)
        val classSpinner = dialogLayout.findViewById<Spinner>(R.id.class_spinner)

        // Retrieve the list of classes from the database
        val availableClasses = getAvailableClassesFromDatabase()

        // Set up the Spinner with the list of classes
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, availableClasses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        classSpinner.adapter = adapter

        with(builder) {
            setTitle("Add Student")
            setView(dialogLayout)
            setPositiveButton("Add") { dialog, _ ->
                val name = nameEditText.text.toString()
                val selectedClass = classSpinner.selectedItem.toString()

                // Add the student to the database
                addStudentToDatabase(name, selectedClass)

                // Refresh the student list for the selected class
                displayStudentsForClass(selectedClass)

                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }


    @SuppressLint("Range")
    private fun getAvailableClassesFromDatabase(): List<String> {
        val dbHelper = DatabaseHelper(this)
        val classes = ArrayList<String>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT name FROM classes", null)
        cursor.use {
            while (it.moveToNext()) {
                classes.add(it.getString(it.getColumnIndex("name")))
            }
        }
        db.close()
        return classes
    }
    private fun addStudentToDatabase(name: String, className: String) {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("class_id", getClassIdFromClass(className))
        }
        db.insert("students", null, values)
        db.close()
    }

    @SuppressLint("Range")
    private fun getClassIdFromClass(className: String): Int {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id FROM classes WHERE name=?", arrayOf(className))
        var classId = -1
        cursor.use {
            if (it.moveToNext()) {
                classId = it.getInt(it.getColumnIndex("id"))
            }
        }
        db.close()
        return classId
    }


    @SuppressLint("Range")
    private fun displayStudentsForClass(className: String) {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase
        val students = ArrayList<String>()
        val cursor = db.rawQuery(
            "SELECT name FROM students WHERE class_id=(SELECT id FROM classes WHERE name=?)",
            arrayOf(className)
        )
        cursor.use {
            while (it.moveToNext()) {
                students.add(it.getString(it.getColumnIndex("name")))
            }
        }
        db.close()
        // Display the students in your UI as needed
    }


    private fun showRemoveStudentDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_remove_student, null)
        val idEditText = dialogLayout.findViewById<EditText>(R.id.id_edit_text)

        with(builder) {
            setTitle("Supprimer un étudiant")
            setView(dialogLayout)
            setPositiveButton("Supprimer") { dialog, _ ->
                val id = idEditText.text.toString().toInt()
                studentDatabaseHelper.deleteStudent(id)
                displayStudents()
                dialog.dismiss()
            }
            setNegativeButton("Annuler") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }
}
