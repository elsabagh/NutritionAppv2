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
    val args: InformationBodyFragmentArgs by navArgs()


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

        val selectedGoal = args.selectedGoal
        val selectedActivityLevel = args.selectedActivityLevel

        binding.toolbarTitle.text = "Personal Information"

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

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

        binding.btnNext.setOnClickListener {
            if (selectedGander != null && selectedWeaklyGoal != null) {

                val action =
                    InformationBodyFragmentDirections.actionInformationBodyFragmentToRegisterFragment(
                        selectedGoal = selectedGoal,
                        selectedActivityLevel = selectedActivityLevel,
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
}