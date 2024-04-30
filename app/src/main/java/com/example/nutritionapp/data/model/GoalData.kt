package com.example.nutritionapp.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class GoalData(
    var id: String = "",
    var user_id: String = "",
    var tCalories: String? = null,
    var tCarbs: String? = null,
    var tFat: String? = null,
    var tProtein: String? = null,
    var sCalories: Double? = 0.0,
    var sCarbs: Double? = 0.0,
    var sFat: Double? = 0.0,
    var sProtein: Double? = 0.0,
    @ServerTimestamp
    var date: Date = Date(),
)