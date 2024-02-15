package com.example.nutritionapp.data.repository

import com.example.nutritionapp.data.model.NutritionDataF
import com.example.nutritionapp.data.model.User
import com.example.nutritionapp.util.FireStoreDocumentField
import com.example.nutritionapp.util.FireStoreTAbles
import com.example.nutritionapp.util.UiState
import com.example.nutritionapp.util.endOfDay
import com.example.nutritionapp.util.startOfDay
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date


class NutritionFoodRepositoryImpl(
    private val database: FirebaseFirestore
) : NutritionFoodRepository {
    override fun addNutrition(nutrition: NutritionDataF, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreTAbles.NutritionData).document()
        nutrition.id = document.id

        document.set(nutrition)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("meal has been created successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }

    }

    override fun getNutritionData(
        userId: User?,
        date: Date?,
        result: (UiState<List<NutritionDataF>>) -> Unit
    ) {
        var query = database.collection(FireStoreTAbles.NutritionData)
            .whereEqualTo(FireStoreDocumentField.USER_ID, userId?.id)
        if (date != null) {
            val startOfDay = date.startOfDay()
            val endOfDay = date.endOfDay()
            query = query.whereGreaterThanOrEqualTo(FireStoreDocumentField.DATE, startOfDay)
                .whereLessThanOrEqualTo(FireStoreDocumentField.DATE, endOfDay)
        }

        query.orderBy(FireStoreDocumentField.DATE, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val nutrition = arrayListOf<NutritionDataF>()
                for (document in it) {
                    val note = document.toObject(NutritionDataF::class.java)
                    nutrition.add(note)
                }
                result.invoke(
                    UiState.Success(nutrition)
                )

            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

}
