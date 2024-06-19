package com.example.nutritionapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import com.example.nutritionapp.viewModel.AuthViewModel
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

        binding.toolbarTitle.text = getString(R.string.create_account)

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
                            tFat = fat,
                        ).apply {
                            viewModel.getSession { this.user_id = it?.id ?: "" }
                        }
                    )
                    restartApp()
                    findNavController().navigate(R.id.action_registerFragment_to_home_nav)
                }
            }
        }
    }

    private fun restartApp() {
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)
    }

    private fun getUserObj(): User {
        return User(
            id = "",
            email = binding.etEmail.text.toString(),
            first_name = binding.etFirstName.text.toString(),
            last_name = binding.etLastName.text.toString(),
        )
    }

    private fun getAdjustedWeight(currentWeight: Double, goalWeight: Double): Double {
        val weightDifference = goalWeight - currentWeight
        val adjustmentFactor = when {
            weightDifference > 0 -> 0.1 // For gaining weight, adjust by 10%
            weightDifference < 0 -> 0.05 // For losing weight, adjust by 5%
            else -> 0.0 // No adjustment if the goal is to maintain weight
        }
        return currentWeight + (weightDifference * adjustmentFactor)
    }


    private fun calculationForMale() {
        val currentWeight = args.weight.toDouble()
        val goalWeight = args.goalWight.toDouble()
        val adjustedWeight = getAdjustedWeight(currentWeight, goalWeight)

        val h = args.height.toDouble()
        val a = args.age.toDouble()
        val BMR = 88.362 + (13.397 * adjustedWeight) + (4.799 * h) - (5.677 * a)
        val ac = when (args.selectedActivityLevel) {
            "Not Very Active" -> BMR * 1.2
            "Lightly Active" -> BMR * 1.3
            "Active" -> BMR * 1.55
            "Very Active" -> BMR * 1.7
            else -> BMR
        }

        val ca = when (args.selectedWeaklyGoal) {
            "Lose 0.25 kg per week" -> ac - 200
            "Lose 0.5 kg per week" -> ac - 300
            "Lose 0.75 kg per week" -> ac - 400
            "Lose 1 kg per week" -> ac - 500
            "Maintain current weight" -> ac
            "Gain 0.25 kg per week" -> ac + 200
            "Gain 0.5 kg per week" -> ac + 300
            "Gain 0.75 kg per week" -> ac + 400
            "Gain 1 kg per week" -> ac + 500
            else -> ac
        }

        val protein = adjustedWeight * if (args.selectedWeaklyGoal.startsWith("Lose")) 2.5 else 2.0
        val fatFactor = if (args.selectedWeaklyGoal.contains("Gain")) 0.25 else 0.2
        val fats = (ca * fatFactor) / 9
        val k = (protein * 4) + (fats * 9)
        val carbs = (ca - k) / 4

        calories = ca.toString()
        this.protein = protein.toString()
        this.carbs = carbs.toString()
        this.fat = fats.toString()
    }

    private fun calculationForFemale() {
        val currentWeight = args.weight.toDouble()
        val goalWeight = args.goalWight.toDouble()
        val adjustedWeight = getAdjustedWeight(currentWeight, goalWeight)

        val h = args.height.toDouble()
        val a = args.age.toDouble()
        val BMR = 447.593 + (9.247 * adjustedWeight) + (3.098 * h) - (4.330 * a)
        val ac = when (args.selectedActivityLevel) {
            "Not Very Active" -> BMR * 1.2
            "Lightly Active" -> BMR * 1.3
            "Active" -> BMR * 1.55
            "Very Active" -> BMR * 1.7
            else -> BMR
        }

        val ca = when (args.selectedWeaklyGoal) {
            "Lose 0.25 kg per week" -> ac - 200
            "Lose 0.5 kg per week" -> ac - 300
            "Lose 0.75 kg per week" -> ac - 400
            "Lose 1 kg per week" -> ac - 500
            "Maintain current weight" -> ac
            "Gain 0.25 kg per week" -> ac + 200
            "Gain 0.5 kg per week" -> ac + 300
            "Gain 0.75 kg per week" -> ac + 400
            "Gain 1 kg per week" -> ac + 500
            else -> ac
        }

        val protein = adjustedWeight * if (args.selectedWeaklyGoal.startsWith("Lose")) 2.0 else 1.5
        val fatFactor = when (args.selectedWeaklyGoal) {
            "Gain 0.75 kg per week", "Gain 1 kg per week" -> 0.35
            "Maintain current weight", "Gain 0.25 kg per week", "Gain 0.5 kg per week" -> 0.3
            else -> 0.25
        }
        val fats = (ca * fatFactor) / 9
        val k = (protein * 4) + (fats * 9)
        val carbs = (ca - k) / 4

        calories = ca.toString()
        this.protein = protein.toString()
        this.carbs = carbs.toString()
        this.fat = fats.toString()
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