package com.example.nutritionapp.data.model

import java.util.Date

data class NutritionDataF(
    var id: String = "",
    var user_id: String = "",
    val foodName: String? = null,
    val calories: String? = null,
    val carbs: String? = null,
    val fat: String? = null,
    val protein: String? = null,
    val meal :String? = null,
    val date: Date = Date(),
)

