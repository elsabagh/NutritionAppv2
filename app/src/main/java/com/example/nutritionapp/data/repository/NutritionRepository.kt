package com.example.nutritionapp.data.repository

import com.example.nutritionapp.data.model.retrofit.NutrientResponse
import com.example.nutritionapp.util.UiState
import com.google.rpc.context.AttributeContext

interface NutritionRepository {
    suspend fun getNutrientInfo(appId: String, appKey: String, ingredient: String): UiState<NutrientResponse>
}