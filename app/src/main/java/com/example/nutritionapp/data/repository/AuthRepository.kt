package com.example.nutritionapp.data.repository

import com.example.nutritionapp.data.model.GoalData
import com.example.nutritionapp.data.model.PersonalInformation
import com.example.nutritionapp.data.model.User
import com.example.nutritionapp.util.UiState

interface AuthRepository {
    fun registerUser(
        email: String,
        password: String,
        user: User,
        result: (UiState<String>) -> Unit
    )

    fun loginUser(
        email: String,
        password: String,
        result: (UiState<String>) -> Unit
    )

    fun updateUserInfo(
        user: User,
        result: (UiState<String>) -> Unit
    )

    fun forgotPassword(
        email: String,
        result: (UiState<String>) -> Unit
    )

    fun logout(result: () -> Unit)

    fun storeSession(id: String, result: (User?) -> Unit)

    fun getSession(result: (User?) -> Unit)

    fun dataPersonalInformation(
        personalInformation: PersonalInformation,
        result: (UiState<String>) -> Unit
    )

    fun dayGoal(
        goalData: GoalData,
        result: (UiState<String>) -> Unit
    )

    fun getGoalData(userId: String, result: (UiState<GoalData?>) -> Unit)

    fun updateGoalData(goalData: GoalData, result: (UiState<String>) -> Unit)
}