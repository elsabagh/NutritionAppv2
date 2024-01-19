package com.example.nutritionapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.nutritionapp.R
import com.example.nutritionapp.data.model.GoalData
import com.example.nutritionapp.data.model.PersonalInformation
import com.example.nutritionapp.data.model.User
import com.example.nutritionapp.databinding.FragmentRegisterBinding
import com.example.nutritionapp.util.UiState
import com.example.nutritionapp.util.hide
import com.example.nutritionapp.util.isValidEmail
import com.example.nutritionapp.util.show
import com.example.nutritionapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels()
    private val args: RegisterFragmentArgs by navArgs()
    private var calories = ""
    private var carbs = ""
    private var protein = ""
    private var fat = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()

        binding.toolbarTitle.text = "Create Account"

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnReg.setOnClickListener {
            if (validation()) {
                viewModel.register(
                    email = binding.etEmail.text.toString(),
                    password = binding.etPass.text.toString(),
                    user = getUserObj()
                )
            }

            when (args.selectedGander) {
                "Male" -> {
                    calculationForMale()
                }

                "Female" -> {
                    calculationForFemale()
                }
            }
        }


    }

    private fun observer() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.registerProgress.show()
                }

                is UiState.Failure -> {
                    binding.registerProgress.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.registerProgress.hide()
                    toast(state.data)

                    viewModel.dataInfo(
                        PersonalInformation(
                            id = "",
                            goal = args.selectedGoal,
                            levelActivity = args.selectedActivityLevel,
                            gander = args.selectedGander,
                            weaklyGoal = args.selectedWeaklyGoal,
                            age = args.age,
                            height = args.height,
                            weight = args.weight,
                            goalWight = args.goalWight
                        ).apply {
                            viewModel.getSession { this.user_id = it?.id ?: "" }
                        }
                    )

                    viewModel.dataDayGoal(
                        GoalData(
                            id = "",
                            tCalories = calories,
                            tCarbs = carbs,
                            tProtein = protein,
                            tFat = fat
                        ).apply {
                            viewModel.getSession { this.user_id = it?.id ?: "" }
                        }
                    )

                    findNavController().navigate(R.id.action_registerFragment_to_home_nav)
                }
            }
        }
    }

    private fun getUserObj(): User {
        return User(
            id = "",
            email = binding.etEmail.text.toString(),
        )
    }

    fun calculationForMale() {
        val w = args.weight.toDouble()
        val h = args.height.toDouble()
        val a = args.age.toDouble()
        val BMR = 88.362 + (13.397 * w) + (4.799 * h) - (5.877 * a)
        var ac = 0.0
        var ca = 0.0
        var protien = 0.0
        var carb = 0.0
        var fats = 0.0
        when (args.selectedActivityLevel) {
            "Not Very Active" -> {
                val c = BMR * 1.2
                ac = c
            }

            "Lightly Active" -> {
                val c = BMR * 1.3
                ac = c
            }

            "Active" -> {
                val c = BMR * 1.55
                ac = c
            }

            "Very Active" -> {
                val c = BMR * 1.7
                ac = c
            }
        }

        when (args.selectedWeaklyGoal) {
            "Lose 0.25 kg per week" -> {
                val Calories = ac - 200
                ca = Calories
                protien = (w * 2.5)
                fats = (ca * 0.2) / 9
                val k = (protien * 4) + (fats * 9)
                carb = (ca - (k)) / 4
            }

            "Lose 0.5 kg per week" -> {
                val Calories = ac - 300
                ca = Calories
                protien = (w * 2.5)
                fats = (ca * 0.2) / 9
                val k = (protien * 4) + (fats * 9)
                carb = (ca - (k)) / 4
            }

            "Lose 0.75 kg per week" -> {
                val Calories = ac - 400
                ca = Calories
                protien = (w * 2.5)
                fats = (ca * 0.2) / 9
                val k = (protien * 4) + (fats * 9)
                carb = (ca - (k)) / 4

            }

            "Lose 1 kg per week" -> {
                val Calories = ac - 500
                ca = Calories
                protien = (w * 2.5)
                fats = (ca * 0.2) / 9
                val k = (protien * 4) + (fats * 9)
                carb = (ca - (k)) / 4

            }
        }

//        if (args.selectedWeaklyGoal == "Maintain weight") {
//            val Calories = ac
//            ca = Calories
//            protien = (w * 2)
//            fats = (ca * 0.25) / 9
//            val k = (protien * 4) + (fats * 9)
//            carb = (ca - (k)) / 4
//        }

//        if (binding.checkgoal.text == "Gain 0.25 kg/week") {
//            val Calories = ac + 200
//            ca = Calories
//            protien = (w * 2)
//            fats = (ca * 0.25) / 9
//            val k = (protien * 4) + (fats * 9)
//            carb = (ca - (k)) / 4
//        }
//
//        if (binding.checkgoal.text == "Gain 0.5 kg/week") {
//            val Calories = ac + 300
//            ca = Calories
//            protien = (w * 2)
//            fats = (ca * 0.25) / 9
//            val k = (protien * 4) + (fats * 9)
//            carb = (ca - (k)) / 4
//        }
//
//
//        if (binding.checkgoal.text == "Gain 1 kg/week") {
//            val Calories = ac + 500
//            ca = Calories
//            protien = (w * 2)
//            fats = (ca * 0.3) / 9
//            val k = (protien * 4) + (fats * 9)
//            carb = (ca - (k)) / 4
//        }
        calories = ca.toString()
        protein = protien.toString()
        carbs = carb.toString()
        fat = fats.toString()
    }


    fun calculationForFemale() {
        val w = args.weight.toDouble()
        val h = args.height.toDouble()
        val a = args.age.toDouble()
        val BMR = 447.593 + (9.247 * w) + (3.098 * h) - (4.330 * a)
        var ac = 0.0
        var ca = 0.0
        var protien = 0.0
        var carb = 0.0
        var fats = 0.0
        when (args.selectedActivityLevel) {
            "Not Very Active" -> {
                val c = BMR * 1.2
                ac = c
            }

            "Lightly Active" -> {
                val c = BMR * 1.3
                ac = c
            }

            "Active" -> {
                val c = BMR * 1.55
                ac = c
            }

            "Very Active" -> {
                val c = BMR * 1.7
                ac = c
            }
        }

        when (args.selectedWeaklyGoal) {
            "Lose 0.25 kg per week" -> {
                val Calories = ac - 200
                ca = Calories
                protien = (w * 2)
                fats = (ca * 0.25) / 9
                val k = (protien * 4) + (fats * 9)
                carb = (ca - (k)) / 4
            }

            "Lose 0.5 kg per week" -> {
                val Calories = ac - 300
                ca = Calories
                protien = (w * 2)
                fats = (ca * 0.25) / 9
                val k = (protien * 4) + (fats * 9)
                carb = (ca - (k)) / 4
            }

            "Lose 0.75 kg per week" -> {
                val Calories = ac - 500
                ca = Calories
                protien = (w * 2)
                fats = (ca * 0.25) / 9
                val k = (protien * 4) + (fats * 9)
                carb = (ca - (k)) / 4

            }

            "Lose 1 kg per week" -> {
                val Calories = ac - 500
                ca = Calories
                protien = (w * 2)
                fats = (ca * 0.25) / 9
                val k = (protien * 4) + (fats * 9)
                carb = (ca - (k)) / 4

            }
        }
//
//        if (args.selectedWeaklyGoal == "Lose 0.25 kg per week") {
//            val Calories = ac
//            ca = Calories
//            protien = (w * 2)
//            fats = (ca * 0.3) / 9
//            val k = (protien * 4) + (fats * 9)
//            carb = (ca - (k)) / 4
//        }
//
//        if (binding.checkgoal.text == "Gain 0.25 kg/week") {
//            val Calories = ac + 200
//            ca = Calories
//            protien = (w * 1.5)
//            fats = (ca * 0.3) / 9
//            val k = (protien * 4) + (fats * 9)
//            carb = (ca - (k)) / 4
//        }
//
//        if (binding.checkgoal.text == "Gain 0.5 kg/week") {
//            val Calories = ac + 300
//            ca = Calories
//            protien = (w * 1.5)
//            fats = (ca * 0.3) / 9
//            val k = (protien * 4) + (fats * 9)
//            carb = (ca - (k)) / 4
//        }
//
//
//        if (binding.checkgoal.text == "Gain 1 kg/week") {
//            val Calories = ac + 500
//            ca = Calories
//            protien = (w * 1.5)
//            fats = (ca * 0.35) / 9
//            val k = (protien * 4) + (fats * 9)
//            carb = (ca - (k)) / 4
//        }
        calories = ca.toString()
        protein = protien.toString()
        carbs = carb.toString()
        fat = fats.toString()
    }


    private fun validation(): Boolean {
        var isValid = true

        if (binding.etEmail.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_email))
        } else {
            if (!binding.etEmail.text.toString().isValidEmail()) {
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }
        if (binding.etPass.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_password))
        } else {
            if (binding.etPass.text.toString().length < 8) {
                isValid = false
                toast(getString(R.string.invalid_password))
            }
        }

        val password = binding.etPass.text.toString()
        val confirmPassword = binding.etConfPass.text.toString()
        if (confirmPassword != password) {
            isValid = false
            toast(getString(R.string.passwords_do_not_match))
        }
        return isValid
    }

    companion object {
        const val TAG: String = "RegisterFragment"
    }

}