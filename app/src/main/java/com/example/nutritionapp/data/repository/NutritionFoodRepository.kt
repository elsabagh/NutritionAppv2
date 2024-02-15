package com.example.nutritionapp.data.repository

import com.example.nutritionapp.data.model.NutritionDataF
import com.example.nutritionapp.data.model.User
import com.example.nutritionapp.util.UiState
import java.util.Date

interface NutritionFoodRepository {

    fun addNutrition(nutrition: NutritionDataF, result: (UiState<String>) -> Unit)

    fun getNutritionData(userId: User?, date: Date? = null, result: (UiState<List<NutritionDataF>>) -> Unit)

}