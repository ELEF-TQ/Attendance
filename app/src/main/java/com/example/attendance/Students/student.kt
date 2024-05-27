package com.example.attendance

data class Student(
    val id: Int,
    val name: String,
    val className: String,
    var absences: Int,
    var isAbsent: Boolean = false
)
