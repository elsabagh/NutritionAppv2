package com.example.nutritionapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutritionapp.data.model.GoalData
import com.example.nutritionapp.databinding.FragmentMealListingBinding
import com.example.nutritionapp.ui.auth.AuthViewModel
import com.example.nutritionapp.util.UiState
import com.example.nutritionapp.util.hide
import com.example.nutritionapp.util.show
import com.example.nutritionapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


private const val ARG_PARAM1 = "param1"

@AndroidEntryPoint
class MealsListingFragment : Fragment() {
    private var param1: String? = null
    private lateinit var binding: FragmentMealListingBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val viewModel: SearchNutritionViewModel by viewModels()
    private var selectedDate: Date? = null
    private val dateFormat = SimpleDateFormat("MMM d")
    private var backClickCount = 0

    private val adapter by lazy {
        MealsAdapter(
            onItemClicked = { pos, item ->

            }
        )
    }

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
        // Inflate the layout for this fragment
        return if (this::binding.isInitialized) {
            binding.root
        } else {
            binding = FragmentMealListingBinding.inflate(layoutInflater)
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mealsListing.layoutManager = LinearLayoutManager(requireContext())
        binding.mealsListing.adapter = adapter

        binding.calendarView.visibility = View.GONE
        binding.imgCalendar.setOnClickListener {
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
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Create a Date object from the selected date
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val selectedDate = calendar.time

            // Update the selectedDate and the current date text
            this.selectedDate = selectedDate
            updateCurrentDateText(selectedDate)

            authViewModel.getSession {

                // Now, you have the userId, you can use it to retrieve the goal data
                viewModel.getNutritionData(it, selectedDate)
            }
        }

        if (selectedDate == null) {
            val currentDate = Calendar.getInstance().time
            selectedDate = currentDate
            updateCurrentDateText(currentDate)
            authViewModel.getSession {
                viewModel.getNutritionData(it, currentDate)
            }
        }

        backButton()
        observer()
        prevDay()
        nextDay()
    }

    private fun observer() {
        viewModel.nutritionData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    // Handle UI updates on the main thread
                    binding.progressBar.hide()
                    adapter.updateList(state.data.toMutableList())

                    val sumCalories = state.data.sumOf { it.calories?.toDoubleOrNull() ?: 0.0 }
                    val sumCarbs = state.data.sumOf { it.carbs?.toDoubleOrNull() ?: 0.0 }
                    val sumFat = state.data.sumOf { it.fat?.toDoubleOrNull() ?: 0.0 }
                    val sumProtein = state.data.sumOf { it.protein?.toDoubleOrNull() ?: 0.0 }
                    binding.totalCalories.text = sumCalories.toString()

                    // Use lifecycleScope to launch a coroutine for background tasks
                    viewLifecycleOwner.lifecycleScope.launch {
                        authViewModel.updateGoalData(
                            GoalData(
                                sCalories = sumCalories,
                                sCarbs = sumCarbs,
                                sFat = sumFat,
                                sProtein = sumProtein
                            ).apply {
                                authViewModel.getSession { this.user_id = it?.id ?: "" }
                            }
                        )
                    }
                }
                is UiState.Loading -> {
                    binding.progressBar.show()
                }
                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }
            }
        }
    }
    private fun nextDay() {
        binding.btnNextDay.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate ?: Date()
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            selectedDate = calendar.time
            updateCurrentDateText(selectedDate)
            binding.calendarView.date = selectedDate?.time ?: System.currentTimeMillis()
            authViewModel.getSession {
                viewModel.getNutritionData(it, selectedDate)
            }
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
            authViewModel.getSession {
                viewModel.getNutritionData(it, selectedDate)
            }
        }
    }

    private fun backButton() {
        binding.back.setOnClickListener {
            if (backClickCount == 0) {
                // Move to current day
                val currentDate = Calendar.getInstance().time
                selectedDate = currentDate
                updateCurrentDateText(currentDate)
                authViewModel.getSession {
                    viewModel.getNutritionData(it, currentDate)
                }
            } else if (backClickCount == 1) {
                // Check if the selected date is today's date
                val today = Calendar.getInstance().time
                if (!dateFormat.format(selectedDate).equals(dateFormat.format(today), ignoreCase = true)) {
                    // If not, move to today's date
                    selectedDate = today
                    updateCurrentDateText(today)
                    authViewModel.getSession {
                        viewModel.getNutritionData(it, today)
                    }
                    backClickCount = 0 // Reset backClickCount for future use
                    return@setOnClickListener
                }
                // If the selected date is today's date, navigate up
                findNavController().navigateUp()
            }
            backClickCount++
        }
    }


    private fun updateCurrentDateText(date: Date?) {
        binding.tvCurrentDate.text = dateFormat.format(date ?: Date())
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            MealsListingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}