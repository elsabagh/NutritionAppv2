package com.example.nutritionapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nutritionapp.data.model.NutritionDataF
import com.example.nutritionapp.data.model.User
import com.example.nutritionapp.data.repository.NutritionFoodRepository
import com.example.nutritionapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SearchNutritionViewModel @Inject constructor(
    val repository: NutritionFoodRepository
) : ViewModel() {

    private val _addNutrition = MutableLiveData<UiState<String>>()
    val addNutrition: LiveData<UiState<String>>
        get() = _addNutrition


    private val _nutritionData = MutableLiveData<UiState<List<NutritionDataF>>>()
    val nutritionData: LiveData<UiState<List<NutritionDataF>>>
        get() = _nutritionData

    fun addNutrition(nutrition: NutritionDataF) {
        _addNutrition.value = UiState.Loading
        repository.addNutrition(nutrition) {
            _addNutrition.value = it
        }
    }


    fun getNutritionData(user: User?, date: Date? = null) {
        _nutritionData.value = UiState.Loading
        repository.getNutritionData(user, date) {
            _nutritionData.value = it
        }
    }
}

