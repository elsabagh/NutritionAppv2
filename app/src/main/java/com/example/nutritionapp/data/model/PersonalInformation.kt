package com.example.nutritionapp.data.model

data class PersonalInformation(
    var id: String = "",
    var user_id: String = "",
    val goal: String? = null,
    val levelActivity: String? = null,
    val gander: String? = null,
    val age: String? = null,
    val height: String? = null,
    val weight: String? = null,
    val goalWight: String? = null,
    val weaklyGoal: String? = null,
)