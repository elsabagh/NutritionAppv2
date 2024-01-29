package com.example.nutritionapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.nutritionapp.databinding.FragmentResultBinding
import com.example.nutritionapp.ui.auth.AuthViewModel
import com.example.nutritionapp.util.UiState
import dagger.hilt.android.AndroidEntryPoint


private const val ARG_PARAM1 = "param1"

@AndroidEntryPoint
class ResultFragment : Fragment() {
    private var param1: String? = null
    private lateinit var binding: FragmentResultBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentResultBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
// Observe goal data in your Fragment or Activity
        // Observe goal data in your Fragment or Activity
        viewModel.goalData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    val goalData = uiState.data
                    if (goalData != null) {
                        // Update your UI with the retrieved goal data
                        binding.tvTargetCal.text = goalData.tCalories ?: "N/A"
                        binding.tvCarbsNum.text = goalData.tCarbs ?: "N/A"
                        binding.tvProteinNum.text = goalData.tProtein ?: "N/A"
                        binding.tvFatsNum.text = goalData.tFat ?: "N/A"
                        binding.tvKcal.text = goalData.tFat ?: "N/A"


                        updateCircularProgressBar(
                            goalData.tCalories?.toFloat() ?: 0f,
                            goalData.tFat?.toFloat() ?: 0f
                        )

                    } else {
                        // No goal data found for the user
                        // Handle this case, e.g., display a message or set default values
                        binding.tvTargetCal.text = "N/A"
                        binding.tvCarbsNum.text = "N/A"
                        binding.tvProteinNum.text = "N/A"
                        binding.tvFatsNum.text = "N/A"
                        binding.tvKcal.text = "N/A"
                        updateCircularProgressBar(0f, 0f)
                    }
                }

                is UiState.Loading -> {
                    // Show loading indicator
                    // You can add loading logic if needed
                }

                is UiState.Failure -> {
                    // Handle the failure, show an error message, etc.
                    // You can display an error message or set default values
                    binding.tvTargetCal.text = "Error"
                    binding.tvCarbsNum.text = "Error"
                    binding.tvProteinNum.text = "Error"
                    binding.tvFatsNum.text = "Error"
                }
            }
        }

        viewModel.getSession { user ->
            if (user != null) {
                val userId = user.id
                // Now, you have the userId, you can use it to retrieve the goal data
                viewModel.getGoalData(userId)
            } else {
                // Handle the case where the user is not authenticated or no user session exists
                // This could mean the user needs to log in or authenticate first
            }
        }
    }

    private fun updateCircularProgressBar(targetCalories: Float, consumedCalories: Float) {
        val progressPercentage = if (targetCalories > 0) {
            // Calculate the percentage based on consumedCalories and targetCalories
            (consumedCalories / targetCalories) * 100
        } else {
            0f
        }

        // Set the progress to CircularProgressBar
        binding.calCircularProgressBar.progress = progressPercentage
        binding.tvPercentage.text = String.format("%.0f%%", progressPercentage)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}