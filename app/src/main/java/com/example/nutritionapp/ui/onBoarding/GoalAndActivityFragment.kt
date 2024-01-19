package com.example.nutritionapp.ui.onBoarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nutritionapp.databinding.FragmentGoalAndActivityBinding
import com.example.nutritionapp.util.applyClickBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoalAndActivityFragment : Fragment() {

    private lateinit var binding: FragmentGoalAndActivityBinding
    var selectedGoal: String? = null // Variable to store the selected goal
    var selectedActivityLevel: String? = null // Variable to store the selected activity level

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGoalAndActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarTitle.text = "Goal & Activity"

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        val selectGoal: List<Pair<View, TextView>> = listOf(
            Pair(binding.viewLoseWeight, binding.tvLoseWeight),
            Pair(binding.viewMaintainWeight, binding.tvMaintainWeight),
            Pair(binding.viewGainWeight, binding.GainWeight),
            // Add similar pairs for other views
        )

        val selectActivityLevel: List<Pair<View, TextView>> = listOf(
            Pair(binding.viewNotVeryActive, binding.tvNotVeryActive),
            Pair(binding.viewLightlyActive, binding.tvLightlyActive),
            Pair(binding.viewActive, binding.tvActive),
            Pair(binding.viewVeryActive, binding.tvVeryActive),
            // Add similar pairs for other views
        )


        selectGoal.applyClickBehavior { selectedGoal ->
            if (selectedGoal != null) {
                this.selectedGoal = selectedGoal
            }
        }

        selectActivityLevel.applyClickBehavior { selectedActivityLevel ->
            if (selectedActivityLevel != null) {
                this.selectedActivityLevel = selectedActivityLevel
            }
        }

        binding.btnNext.setOnClickListener {
            if (selectedGoal != null && selectedActivityLevel != null) {
                // Both goal and activity level are selected, proceed with navigation and data submission
                val action =
                    GoalAndActivityFragmentDirections.actionGoalAndActivityFragmentToInformationBodyFragment(
                        selectedGoal = selectedGoal!!,
                        selectedActivityLevel = selectedActivityLevel!!
                    )
                findNavController().navigate(action)
            } else {
                // Show an error message or handle the case where not both values are selected
            }
        }
    }
}