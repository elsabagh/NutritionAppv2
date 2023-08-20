package com.example.nutritionapp.data.repository

import com.example.nutritionapp.data.model.NutritionDataF
import com.example.nutritionapp.util.UiState

interface NutritionFoodRepository {

    fun addNutrition(nutrition: NutritionDataF, result: (UiState<String>) -> Unit)

}