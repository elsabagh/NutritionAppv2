package com.example.nutritionapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.FragmentResultBinding
import com.example.nutritionapp.util.UiState
import com.example.nutritionapp.viewModel.AuthViewModel
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


private const val ARG_PARAM1 = "param1"

@AndroidEntryPoint
class ResultFragment : Fragment() {
    private var param1: String? = null
    private lateinit var binding: FragmentResultBinding
    private val viewModel: AuthViewModel by viewModels()
    private var selectedDate: Date? = null
    private val dateFormat = SimpleDateFormat("MMM d")
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

        binding.btnAddMeals.setOnClickListener {
            navigateToMealsFragment()
        }
        selectedDate = Calendar.getInstance().time
        updateCurrentDateText(selectedDate)

        binding.calendarView.visibility = View.GONE
        binding.imgCalendarProg.setOnClickListener {
            if (binding.calendarView.visibility == View.VISIBLE) {
                binding.calendarView.visibility = View.GONE
            } else {
                // Set the calendar to the selectedDate (current date)
                val calendar = Calendar.getInstance()
                calendar.time = selectedDate ?: Date()
                binding.calendarView.date = calendar.timeInMillis
                binding.calendarView.visibility = View.VISIBLE
            }
        }

        binding.CardBreakfast.setOnClickListener {
            navigateToMealsListingFragment("Breakfast")
        }
        binding.CardLaunch.setOnClickListener {
            navigateToMealsListingFragment("Lunch")
        }
        binding.CardDinner.setOnClickListener {
            navigateToMealsListingFragment("Dinner")
        }
        binding.CardSnacks.setOnClickListener {
            navigateToMealsListingFragment("Snacks")
        }
        nextDay()
        prevDay()
        observer()
    }

    private fun navigateToMealsListingFragment(mealType: String) {
        val bundle = Bundle().apply {
            putString("mealType", mealType)
        }
        findNavController().navigate(R.id.action_resultFragment_to_MealsListingFragment, bundle)
    }

    private fun observer() {
        // Observe goal data in your Fragment or Activity
        viewModel.goalData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    val goalData = uiState.data
                    if (goalData != null) {
                        // Update your UI with the retrieved goal data
                        binding.tvTargetCal.text = goalData.tCalories ?: "0.0"
                        binding.tvCarbsNum.text = goalData.tCarbs ?: "0.0"
                        binding.tvProteinNum.text = goalData.tProtein ?: "0.0"
                        binding.tvFatsNum.text = goalData.tFat ?: "0.0"


                        // Check if the date in the goal data is equal to the current date
                        val currentDate = Calendar.getInstance().time
                        if (!dateFormat.format(goalData.date).equals(dateFormat.format(currentDate), ignoreCase = true)) {
                            // If not equal, set tvKcal to "0.0"
                            binding.tvKcal.text = "0.0"
                            updateCircularProgressBar(
                                binding.calCircularProgressBar,
                                goalData.tCalories?.toFloat() ?: 0f,
                                0f,
                                binding.tvPercentage
                            )
                            updateCircularProgressBar(
                                binding.CarbsCircularProgressBar,
                                goalData.tCarbs?.toFloat() ?: 0f,
                                0f,
                                binding.tvPercentageCarbs
                            )
                            updateCircularProgressBar(
                                binding.FatsCircularProgressBar,
                                goalData.tFat?.toFloat() ?: 0f,
                                0f,
                                binding.tvPercentageFats
                            )
                            updateCircularProgressBar(
                                binding.ProteinCircularProgressBar,
                                goalData.tProtein?.toFloat() ?: 0f,
                                0f,
                                binding.tvPercentageProtein
                            )
                        } else {
                            // If equal, set tvKcal to the consumed calories
                            binding.tvKcal.text = goalData.sCalories.toString() ?: "0.0"
                            updateCircularProgressBar(
                                binding.calCircularProgressBar,
                                goalData.tCalories?.toFloat() ?: 0f,
                                goalData.sCalories?.toFloat() ?: 0f,
                                binding.tvPercentage
                            )
                            updateCircularProgressBar(
                                binding.CarbsCircularProgressBar,
                                goalData.tCarbs?.toFloat() ?: 0f,
                                goalData.sCarbs?.toFloat() ?: 0f,
                                binding.tvPercentageCarbs
                            )
                            updateCircularProgressBar(
                                binding.FatsCircularProgressBar,
                                goalData.tFat?.toFloat() ?: 0f,
                                goalData.sFat?.toFloat() ?: 0f,
                                binding.tvPercentageFats
                            )
                            updateCircularProgressBar(
                                binding.ProteinCircularProgressBar,
                                goalData.tProtein?.toFloat() ?: 0f,
                                goalData.sProtein?.toFloat() ?: 0f,
                                binding.tvPercentageProtein
                            )
                        }
                    } else {
                        // No goal data found for the user
                        // Handle this case, e.g., display a message or set default values
                        binding.tvTargetCal.text = "0.0"
                        binding.tvCarbsNum.text = "0.0"
                        binding.tvProteinNum.text = "0.0"
                        binding.tvFatsNum.text = "0.0"
                        binding.tvKcal.text = "0.0"
                        updateCircularProgressBar(binding.calCircularProgressBar, 0f, 0f, binding.tvPercentage)
                        updateCircularProgressBar(binding.CarbsCircularProgressBar, 0f, 0f, binding.tvPercentageCarbs)
                        updateCircularProgressBar(binding.FatsCircularProgressBar, 0f, 0f, binding.tvPercentageFats)
                        updateCircularProgressBar(binding.ProteinCircularProgressBar, 0f, 0f, binding.tvPercentageProtein)
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


    private fun updateCircularProgressBar(
        progressBar: CircularProgressBar,
        targetCalories: Float,
        consumedCalories: Float,
        percentageTextView: TextView
    ) {
        val progressPercentage = if (targetCalories > 0) {
            // Calculate the percentage based on consumedCalories and targetCalories
            (consumedCalories / targetCalories) * 100
        } else {
            0f
        }

        progressBar.progress = progressPercentage
        percentageTextView.text = String.format("%.0f%%", progressPercentage)
    }

    private fun navigateToMealsFragment() {
        // You may need to pass any necessary data to MealsFragment using a Bundle
        val bundle = Bundle()
        findNavController().navigate(R.id.action_resultFragment_to_MealsListingFragment, bundle)
    }

    private fun nextDay() {
        binding.btnNextDay.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate ?: Date()
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            selectedDate = calendar.time
            updateCurrentDateText(selectedDate)
            binding.calendarView.date = selectedDate?.time ?: System.currentTimeMillis()

        }
    }

    private fun prevDay() {
        binding.btnPrevDay.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate ?: Date()
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            selectedDate = calendar.time
            updateCurrentDateText(selectedDate)
            binding.calendarView.date = selectedDate?.time ?: System.currentTimeMillis()

        }
    }

    private fun updateCurrentDateText(date: Date?) {
        binding.tvCurrentDate.text = dateFormat.format(date ?: Date())
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