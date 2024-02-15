package com.example.nutritionapp.data.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class NutritionDataF(
    var id: String = "",
    var user_id: String = "",
    val foodName: String? = null,
    val calories: String? = null,
    val carbs: String? = null,
    val fat: String? = null,
    val protein: String? = null,
    val meal :String? = null,
    @ServerTimestamp
    val date: Date = Date(),
) : Parcelable

