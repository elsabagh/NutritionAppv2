package com.example.nutritionapp.ui.home

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nutritionapp.R
import com.example.nutritionapp.data.model.NutritionDataF
import com.example.nutritionapp.databinding.FragmentSearchBinding
import com.example.nutritionapp.util.UiState
import com.example.nutritionapp.util.hide
import com.example.nutritionapp.util.show
import com.example.nutritionapp.util.toast
import com.example.nutritionapp.viewModel.AuthViewModel
import com.example.nutritionapp.viewModel.NutritionViewModel
import com.example.nutritionapp.viewModel.SearchViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

private const val ARG_PARAM1 = "param1"

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var param1: String? = null
    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel: SearchViewModel by viewModels()
    private val nutritionViewModel: NutritionViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var selectedMealType: String = ""
    private var selectedButtonId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isInternetAvailable(activity?.applicationContext!!)
        observer()
        search()


        binding.btnBreakfast.setOnClickListener {
            updateButtonColor(R.id.btn_breakfast)
            selectedMealType = "Breakfast"
        }

        binding.btnLaunch.setOnClickListener {
            updateButtonColor(R.id.btn_launch)
            selectedMealType = "Lunch"
        }

        binding.btnDinner.setOnClickListener {
            updateButtonColor(R.id.btn_dinner)
            selectedMealType = "Dinner"
        }

        binding.btnSnack.setOnClickListener {
            updateButtonColor(R.id.btn_snack)
            selectedMealType = "Snacks"
        }

        binding.addMeal.setOnClickListener {
            if (validation()) {
                if (selectedMealType.isNotEmpty()) {
                    val foodName = binding.itemname.text.toString()
                    val calories = binding.calories.text.toString().toDoubleOrNull() ?: 0.0
                    val carbs = binding.carb.text.toString().toDoubleOrNull() ?: 0.0
                    val fat = binding.fats.text.toString().toDoubleOrNull() ?: 0.0
                    val protein = binding.Protein.text.toString().toDoubleOrNull() ?: 0.0

                    // Check if any of the nutrient values are non-zero
                    if (calories > 0 || carbs > 0 || fat > 0 || protein > 0) {
                        nutritionViewModel.addNutrition(
                            NutritionDataF(
                                id = "",
                                foodName = foodName,
                                calories = calories.toString(),
                                carbs = carbs.toString(),
                                fat = fat.toString(),
                                protein = protein.toString(),
                                meal = selectedMealType,
                                date = Date()
                            ).apply {
                                authViewModel.getSession { this.user_id = it?.id ?: "" }
                            }
                        )

                        navigateToMealsFragment()

                    } else {
                        // Display a message indicating that meal data is empty
                        toast("Please enter valid nutrient data")
                    }
                } else {
                    // Handle the case where no meal type is selected
                    toast("Please select a meal type")
                }
            }
        }
    }

    private fun updateButtonColor(clickedButtonId: Int) {
        selectedButtonId?.let { previousButtonId ->
            val previousButton = view?.findViewById<CardView>(previousButtonId)
            previousButton?.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.button_color_selector)
        }

        val clickedButton = view?.findViewById<CardView>(clickedButtonId)
        clickedButton?.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.blue)

        selectedButtonId = clickedButtonId
    }

    private fun search() {
        binding.searchbut.setOnClickListener {
            if (isInternetAvailable(activity?.applicationContext!!)) {
                val ingredient = binding.searchbar.text.toString()
                if (ingredient.isBlank()) {
                    // Display a message indicating that the search input is empty
                    toast("Please enter a valid search term")
                } else {
                    searchViewModel.searchIngredient(ingredient)
                    binding.mealLineChart.visibility = View.VISIBLE

                    // Hide the keyboard after search
                    hideKeyboard()
                }
            } else {
                toast("No internet connection")
                binding.calories.text = ""
                binding.Protein.text = ""
                binding.carb.text = ""
                binding.fats.text = ""
                binding.itemname.text = ""
                binding.mealLineChart.visibility = View.GONE
            }
        }
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchbar.windowToken, 0)
    }


    private fun observer() {

        nutritionViewModel.addNutrition.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.searchProgress.show()

                }

                is UiState.Failure -> {
                    binding.searchProgress.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.searchProgress.hide()
                    toast(state.data)
                }
            }
        }

        searchViewModel.nutrientResponse.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    binding.searchProgress.show()
                }

                is UiState.Success -> {
                    binding.searchProgress.hide()
                    val nutrientResponse = uiState.data
                    val ingredient = binding.searchbar.text.toString()

                    if (nutrientResponse != null &&
                        nutrientResponse.totalNutrients.protein != null &&
                        nutrientResponse.totalNutrients.carbohydrates != null &&
                        nutrientResponse.totalNutrients.fat != null
                    ) {

                        binding.calories.text = nutrientResponse.calories.toString()
                        val proteinQuantity = nutrientResponse.totalNutrients.protein.quantity
                        val carbQuantity = nutrientResponse.totalNutrients.carbohydrates.quantity
                        val fatQuantity = nutrientResponse.totalNutrients.fat.quantity

                        binding.Protein.text = proteinQuantity.toString()
                        binding.carb.text = carbQuantity.toString()
                        binding.fats.text = fatQuantity.toString()
                        binding.itemname.text = ingredient

                        // Update chart and any other UI components

                        val totalCalories = nutrientResponse.calories.toDouble()
                        val totalProtein = proteinQuantity
                        val totalCarbs = carbQuantity
                        val totalFats = fatQuantity

                        val chart = binding.mealLineChart
                        chart.setUsePercentValues(true)
                        chart.description.isEnabled = false
                        chart.legend.isEnabled = false

                        val entries = mutableListOf<PieEntry>()
                        entries.add(PieEntry((totalProtein / totalCalories).toFloat() * 100, ""))
                        entries.add(PieEntry((totalCarbs / totalCalories).toFloat() * 100, ""))
                        entries.add(PieEntry((totalFats / totalCalories).toFloat() * 100, ""))
                        entries.add(
                            PieEntry(
                                ((totalCalories - totalProtein - totalCarbs - totalFats) / totalCalories).toFloat() * 100,
                                ""
                            )
                        )

                        val dataSet = PieDataSet(entries, "")
                        dataSet.setDrawValues(false)

                        dataSet.colors = listOf(
                            Color.parseColor("#FF9738"), // Custom color for Protein
                            Color.parseColor("#41B9E0"), // Custom color for Carbs
                            Color.parseColor("#DD1ECA"), // Custom color for Fats
                            Color.parseColor("#6D5BDD")
                        )


                        chart.setNoDataText("")
                        chart.holeRadius = 85f
                        dataSet.valueTextSize = 12f
                        dataSet.sliceSpace = 4f
                        val pieData = PieData(dataSet)
                        chart.data = pieData
                        chart.invalidate()

                    } else {
                        // Display error message indicating data retrieval problem
                        binding.mealLineChart.visibility = View.GONE
                        toast("Failed to retrieve nutrient information")
                    }

                }


                is UiState.Failure -> {
                    // Show error message
                    binding.searchProgress.hide()
                    toast("Unknown error")
                }
            }
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun navigateToMealsFragment() {
        // You may need to pass any necessary data to MealsFragment using a Bundle
        val bundle = Bundle()
        findNavController().navigate(R.id.action_resultFragment_to_MealsListingFragment, bundle)
    }

    private fun validation(): Boolean {
        if (!isInternetAvailable(requireContext())) {
            // Display a message indicating no internet connection
            toast("No internet connection")
        }

        var isValid = true
        if (binding.Protein.text.toString().isEmpty() ||
            binding.calories.text.toString().isEmpty() ||
            binding.carb.text.toString().isEmpty() ||
            binding.fats.text.toString().isEmpty()
        ) {
            isValid = false
            // Display a message indicating missing data
            toast("Please enter all nutrient values")
        }
        return isValid
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}