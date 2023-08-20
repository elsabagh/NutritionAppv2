package com.example.nutritionapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nutritionapp.data.model.NutritionDataF
import com.example.nutritionapp.data.repository.NutritionFoodRepository
import com.example.nutritionapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchNutritionViewModel @Inject constructor(
    val repository: NutritionFoodRepository
) : ViewModel() {

    private val _addNutrition = MutableLiveData<UiState<String>>()
    val addNutrition: LiveData<UiState<String>>
        get() = _addNutrition

    fun addNutrition(nutrition: NutritionDataF) {
        _addNutrition.value = UiState.Loading
        repository.addNutrition(nutrition) {
            _addNutrition.value = it
        }
    }

}
