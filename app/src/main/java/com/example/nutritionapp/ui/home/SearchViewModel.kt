package com.example.nutritionapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritionapp.data.model.NutritionDataF
import com.example.nutritionapp.data.model.retrofit.EdamamApiClient
import com.example.nutritionapp.data.model.retrofit.NutrientResponse
import com.example.nutritionapp.data.repository.NutritionRepository
import com.example.nutritionapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val nutritionRepository: NutritionRepository
) : ViewModel() {

    private val appId = "a2d6a9e9"
    private val appKey = "41d46f9ea3b1ca4cc38209c28205eb0b"

    private val _nutrientResponse = MutableLiveData<UiState<NutrientResponse>>()
    val nutrientResponse: LiveData<UiState<NutrientResponse>>
        get() = _nutrientResponse

    fun searchIngredient(ingredient: String) {
        viewModelScope.launch {
            _nutrientResponse.value = UiState.Loading
            val result = nutritionRepository.getNutrientInfo(appId, appKey, ingredient)
            _nutrientResponse.value = result
        }
    }

}