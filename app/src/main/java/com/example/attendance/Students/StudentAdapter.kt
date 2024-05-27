package com.example.attendance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView

class StudentAdapter(private val context: Context, private val students: List<Student>) : BaseAdapter() {

    override fun getCount(): Int = students.size

    override fun getItem(position: Int): Any = students[position]

    override fun getItemId(position: Int): Long = students[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }

        val student = students[position]
        viewHolder.nameTextView.text = student.name
        viewHolder.absencesTextView.text = "Absences: ${student.absences}"
        viewHolder.absentCheckBox.isChecked = student.isAbsent

        viewHolder.absentCheckBox.setOnCheckedChangeListener { _, isChecked ->
            student.isAbsent = isChecked
        }

        return view
    }

    private class ViewHolder(view: View) {
        val nameTextView: TextView = view.findViewById(R.id.student_name_text_view)
        val absencesTextView: TextView = view.findViewById(R.id.student_absences_text_view)
        val absentCheckBox: CheckBox = view.findViewById(R.id.student_absent_checkbox)
    }
}
