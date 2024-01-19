package com.example.nutritionapp.data.repository

import android.content.SharedPreferences
import com.example.nutritionapp.data.model.GoalData
import com.example.nutritionapp.data.model.PersonalInformation
import com.example.nutritionapp.data.model.User
import com.example.nutritionapp.util.FireStoreTAbles
import com.example.nutritionapp.util.SharedPrefConstants
import com.example.nutritionapp.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class AuthRepositoryImp(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson: Gson
) : AuthRepository {

    override fun registerUser(
        email: String,
        password: String,
        user: User,
        result: (UiState<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    user.id = it.result.user?.uid ?: ""
                    updateUserInfo(user) { state ->
                        when (state) {
                            is UiState.Success -> {
                                storeSession(id = it.result.user?.uid ?: "") {
                                    if (it == null) {
                                        result.invoke(UiState.Failure("User register successfully but session failed to store"))
                                    } else {
                                        result.invoke(
                                            UiState.Success("User register successfully!")
                                        )
                                    }
                                }
                            }

                            is UiState.Failure -> {
                                result.invoke(UiState.Failure(state.error))
                            }

                            else -> {}
                        }
                    }

                } else {
                    try {
                        throw it.exception ?: java.lang.Exception("Invalid authentication")

                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result.invoke(UiState.Failure("Authentication failed, Password should be at least 6 characters"))

                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result.invoke(UiState.Failure("Authentication failed, Invalid email entered"))

                    } catch (e: FirebaseAuthUserCollisionException) {
                        result.invoke(UiState.Failure("Authentication failed, Email already registered."))

                    } catch (e: Exception) {
                        result.invoke(
                            UiState.Failure(e.message)
                        )
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun loginUser(
        email: String,
        password: String,
        result: (UiState<String>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storeSession(id = it.result.user?.uid ?: "") {
                        if (it == null) {
                            result.invoke(UiState.Failure("Failed to store local session"))

                        } else {
                            result.invoke(UiState.Success("Login successfully!"))
                        }
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure("Authentication failed, Check email and password")
                )
            }
    }

    override fun updateUserInfo(user: User, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreTAbles.User).document(user.id)
        document.set(user)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("User has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun forgotPassword(
        email: String,
        result: (UiState<String>) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Success("Email has been sent"))
                } else {
                    result.invoke(UiState.Failure(it.exception?.message))
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, null).apply()
        result.invoke()
    }

    override fun storeSession(id: String, result: (User?) -> Unit) {
        database.collection(FireStoreTAbles.User).document(id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = it.result.toObject(User::class.java)
                    appPreferences.edit()
                        .putString(SharedPrefConstants.USER_SESSION, gson.toJson(user)).apply()
                    result.invoke(user)
                } else {
                    result.invoke(null)
                }
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }

    override fun getSession(result: (User?) -> Unit) {
        val userS = appPreferences.getString(SharedPrefConstants.USER_SESSION, null)
        if (userS == null) {
            result.invoke(null)
        } else {
            val user = gson.fromJson(userS, User::class.java)
            result.invoke(user)
        }
    }

    override fun dataPersonalInformation(
        userInfo: PersonalInformation,
        result: (UiState<String>) -> Unit
    ) {
        val document = database.collection("UserInfo").document()
        userInfo.id = document.id
        document.set(userInfo)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("User has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun dayGoal(
        goalData: GoalData,
        result: (UiState<String>) -> Unit
    ) {
        val document = database.collection("Goal").document()
        goalData.id = document.id
        document.set(goalData)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("goal has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    // Inside AuthRepositoryImp class
    override fun getGoalData(userId: String, result: (UiState<GoalData?>) -> Unit) {
        database.collection("Goal").whereEqualTo("user_id", userId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result?.documents
                    if (!documents.isNullOrEmpty()) {
                        val goalData = documents[0].toObject(GoalData::class.java)
                        result.invoke(UiState.Success(goalData))
                    } else {
                        result.invoke(UiState.Success(null)) // No goal data found
                    }
                } else {
                    result.invoke(UiState.Failure(task.exception?.localizedMessage ?: "Failed to fetch goal data"))
                }
            }
    }




}