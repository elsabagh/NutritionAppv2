package com.example.nutritionapp.data.repository

import com.example.nutritionapp.data.model.NutritionDataF
import com.example.nutritionapp.util.FireStoreTAbles
import com.example.nutritionapp.util.UiState
import com.google.firebase.firestore.FirebaseFirestore

class NutritionFoodRepositoryImpl(
    private val database: FirebaseFirestore
) : NutritionFoodRepository {
    override fun addNutrition(nutrition: NutritionDataF, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreTAbles.NutritionData).document()
        nutrition.id = document.id

        document.set(nutrition)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Note has been created successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }

    }

}