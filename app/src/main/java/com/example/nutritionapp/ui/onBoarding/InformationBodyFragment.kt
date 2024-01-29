package com.example.nutritionapp.ui.onBoarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.nutritionapp.R
import com.example.nutritionapp.data.model.PersonalInformation
import com.example.nutritionapp.databinding.FragmentInformationBodyBinding
import com.example.nutritionapp.ui.auth.AuthViewModel
import com.example.nutritionapp.util.applyClickBehavior
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InformationBodyFragment : Fragment() {

    private lateinit var binding: FragmentInformationBodyBinding
    private var selectedGander: String? = null // Variable to store the selected goal
    private var selectedWeaklyGoal: String? = null // Variable to store the selected activity level
    private var selectedGoal: String? = null
    private var selectedActivityLevel: String? = null

    private val args: InformationBodyFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInformationBodyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedGoal = args.selectedGoal
        selectedActivityLevel = args.selectedActivityLevel
        binding.toolbarTitle.text = "Personal Information"

        setWeaklyGoals()
        backButton()
        changeStyle()
        nextButton()
    }

    private fun backButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun nextButton() {
        binding.btnNext.setOnClickListener {
            if (selectedGander != null && selectedWeaklyGoal != null) {
                val action =
                    InformationBodyFragmentDirections.actionInformationBodyFragmentToRegisterFragment(
                        selectedGoal = selectedGoal!!,
                        selectedActivityLevel = selectedActivityLevel!!,
                        selectedGander = selectedGander!!,
                        selectedWeaklyGoal = selectedWeaklyGoal!!,
                        age = binding.etAge.text.toString(),
                        height = binding.etHeight.text.toString(),
                        weight = binding.etWeight.text.toString(),
                        goalWight = binding.etGoalWeight.text.toString()
                    )
                findNavController().navigate(action)
            } else {
                // Show an error message or handle the case where not both values are selected
            }
        }
    }

    private fun changeStyle() {
        val selectGander: List<Pair<View, TextView>> = listOf(
            Pair(binding.viewMale, binding.tvMale),
            Pair(binding.viewFemale, binding.tvFemale),
        )

        val selectWeaklyGoal: List<Pair<View, TextView>> = listOf(
            Pair(binding.viewLose1, binding.tvLose1),
            Pair(binding.viewLose2, binding.tvLose2),
            Pair(binding.viewLose3, binding.tvLose3),
            Pair(binding.viewLose4, binding.tvLose4),
        )

        selectGander.applyClickBehavior { selectedGander ->
            if (selectedGander != null) {
                this.selectedGander = selectedGander
            }
        }

        selectWeaklyGoal.applyClickBehavior { selectedWeaklyGoal ->
            if (selectedWeaklyGoal != null) {
                this.selectedWeaklyGoal = selectedWeaklyGoal
            }
        }

    }

    private fun setWeaklyGoals() {
        // Set weakly goals based on selected goal
        when (selectedGoal) {
            "Lose weight" -> {
                binding.tvLose1.text = "Lose 0.25 kg per week"
                binding.tvLose2.text = "Lose 0.5 kg per week"
                binding.tvLose3.text = "Lose 0.57 kg per week"
                binding.tvLose4.text = "Lose 1 kg per week"
            }
            // Add similar cases for other goals
            "Maintain weight" -> {
                binding.tvLose1.text = "Maintain current weight"
                binding.tvLose2.text = "Maintain current weight"
                binding.tvLose3.text = "Maintain current weight"
                binding.tvLose4.text = "Maintain current weight"
            }

            "Gain weight" -> {
                binding.tvLose1.text = "Gain 0.25 kg per week"
                binding.tvLose2.text = "Gain 0.5 kg per week"
                binding.tvLose3.text = "Gain 0.57 kg per week"
                binding.tvLose4.text = "Gain 1 kg per week"
            }
        }
    }
}