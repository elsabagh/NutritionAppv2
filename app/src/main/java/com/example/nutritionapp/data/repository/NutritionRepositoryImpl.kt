package com.example.nutritionapp.data.repository

import com.example.nutritionapp.data.model.retrofit.EdamamApiClient
import com.example.nutritionapp.data.model.retrofit.EdamamApiClient.apiService
import com.example.nutritionapp.data.model.retrofit.EdamamApiService
import com.example.nutritionapp.data.model.retrofit.NutrientResponse
import com.example.nutritionapp.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class NutritionRepositoryImpl @Inject constructor() : NutritionRepository {

    override suspend fun getNutrientInfo(appId: String, appKey: String, ingredient: String): UiState<NutrientResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val call = EdamamApiClient.apiService.getNutrientInfo(appId, appKey, ingredient)
                if (call.isSuccessful) {
                    val nutrientResponse = call.body()
                    nutrientResponse?.let { UiState.Success(it) } ?: UiState.Failure("Failed to fetch nutrient info")
                } else {
                    UiState.Failure("Failed to fetch nutrient info")
                }
            } catch (e: Exception) {
                UiState.Failure(e.message)
            }
        }
    }
}