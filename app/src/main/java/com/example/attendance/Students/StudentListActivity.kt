package com.example.attendance

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
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
        val classEditText = dialogLayout.findViewById<EditText>(R.id.class_edit_text)

        with(builder) {
            setTitle("Ajouter un étudiant")
            setView(dialogLayout)
            setPositiveButton("Ajouter") { dialog, _ ->
                val name = nameEditText.text.toString()
                val className = classEditText.text.toString()
                val newStudent = Student(0, name, className, 0)
                studentDatabaseHelper.addStudent(newStudent)
                displayStudents()
                dialog.dismiss()
            }
            setNegativeButton("Annuler") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
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
