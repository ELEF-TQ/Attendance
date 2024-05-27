package com.example.attendance

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

object ClassRepository {
    var classes: List<String> = emptyList()
}

class ClassSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_selection)

        val classListView = findViewById<ListView>(R.id.class_list_view)

        // Initialize the list of classes from the repository if it's not already initialized
        if (ClassRepository.classes.isEmpty()) {
            ClassRepository.classes = listOf("Informatique", "Indus", "Electrique")
        }

        // Retrieve the list of classes from the repository
        val classes = ClassRepository.classes

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, classes)
        classListView.adapter = adapter

        classListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedClass = classes[position]
            val intent = Intent(this, ClassDetailActivity::class.java)
            intent.putExtra("class_name", selectedClass)
            startActivity(intent)
        }
    }
}