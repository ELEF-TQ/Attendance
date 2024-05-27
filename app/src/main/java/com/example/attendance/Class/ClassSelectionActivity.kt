package com.example.attendance

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ClassSelectionActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_selection)

        databaseHelper = DatabaseHelper(this)
        val classListView = findViewById<ListView>(R.id.class_list_view)

        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM classes", null)
        val classes = mutableListOf<String>()

        if (cursor.moveToFirst()) {
            do {
                classes.add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
            } while (cursor.moveToNext())
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, classes)
        classListView.adapter = adapter

        classListView.setOnItemClickListener { _, _, position, _ ->
            val selectedClass = classes[position]
            val intent = Intent(this, ClassDetailActivity::class.java).apply {
                putExtra("class_name", selectedClass)
            }
            startActivity(intent)
        }
    }
}
