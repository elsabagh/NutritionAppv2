package com.example.nutritionapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nutritionapp.data.model.GoalData
import com.example.nutritionapp.data.model.PersonalInformation
import com.example.nutritionapp.data.model.User
import com.example.nutritionapp.data.repository.AuthRepository
import com.example.nutritionapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: AuthRepository
) : ViewModel() {

    private val _register = MutableLiveData<UiState<String>>()
    val register: LiveData<UiState<String>>
        get() = _register

    private val _login = MutableLiveData<UiState<String>>()
    val login: LiveData<UiState<String>>
        get() = _login

    private val _forgotPassword = MutableLiveData<UiState<String>>()
    val forgotPassword: LiveData<UiState<String>>
        get() = _forgotPassword

    private val _dataInfo = MutableLiveData<UiState<String>>()
    val dataInfo: LiveData<UiState<String>>
        get() = _dataInfo

    private val _dataDayGoal = MutableLiveData<UiState<String>>()
    val dataDayGoal: LiveData<UiState<String>>
        get() = _dataDayGoal

    private val _goalData = MutableLiveData<UiState<GoalData?>>()
    val goalData: LiveData<UiState<GoalData?>>
        get() = _goalData


    fun register(
        email: String,
        password: String,
        user: User
    ) {
        _register.value = UiState.Loading
        repository.registerUser(
            email = email,
            password = password,
            user = user
        ) {
            _register.value = it
        }
    }

    fun login(
        email: String,
        password: String
    ) {
        _login.value = UiState.Loading
        repository.loginUser(
            email = email,
            password = password
        ) {
            _login.value = it
        }
    }

    fun forgotPassword(
        email: String
    ) {
        _forgotPassword.value = UiState.Loading
        repository.forgotPassword(
            email = email
        ) {
            _forgotPassword.value = it
        }
    }

    fun logout(result: () -> Unit) {
        repository.logout(result)
    }

    fun getSession(result: (User?) -> Unit) {
        repository.getSession(result)
    }

    fun dataInfo(userInfo: PersonalInformation) {
        _dataInfo.value = UiState.Loading
        repository.dataPersonalInformation(userInfo) {
            _dataInfo.value = it
        }
    }

    fun dataDayGoal(dayGoal: GoalData) {
        _dataDayGoal.value = UiState.Loading
        repository.dayGoal(dayGoal) {
            _dataDayGoal.value = it
        }
    }

    fun updateGoalData(dayGoal: GoalData) {
        _dataDayGoal.value = UiState.Loading
        repository.updateGoalData(dayGoal) {
            _dataDayGoal.value = it
        }
    }
    fun getGoalData(userId: String) {
        _goalData.value = UiState.Loading
        repository.getGoalData(userId) {
            _goalData.value = it
        }
    }
}