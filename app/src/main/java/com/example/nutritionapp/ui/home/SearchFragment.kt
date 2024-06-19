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
import com.example.nutritionapp.data.model.retrofit.NutrientResponse
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isInternetAvailable(requireContext())) {
            toast("No internet connection")
        }

        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBreakfast.setOnClickListener { updateMealType(R.id.btn_breakfast, "Breakfast") }
        binding.btnLaunch.setOnClickListener { updateMealType(R.id.btn_launch, "Lunch") }
        binding.btnDinner.setOnClickListener { updateMealType(R.id.btn_dinner, "Dinner") }
        binding.btnSnack.setOnClickListener { updateMealType(R.id.btn_snack, "Snacks") }

        binding.addMeal.setOnClickListener { addMeal() }
        binding.searchbut.setOnClickListener { searchIngredient() }
    }

    private fun updateMealType(buttonId: Int, mealType: String) {
        updateButtonColor(buttonId)
        selectedMealType = mealType
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

    private fun addMeal() {
        if (!isInternetAvailable(requireContext())) {
            toast("No internet connection")
            return
        }

        if (!isValidInput()) {
            toast("Please enter all nutrient values")
            return
        }

        if (selectedMealType.isEmpty()) {
            toast("Please select a meal type")
            return
        }

        val foodName = binding.itemname.text.toString()
        val calories = binding.calories.text.toString().toDoubleOrNull() ?: 0.0
        val carbs = binding.carb.text.toString().toDoubleOrNull() ?: 0.0
        val fat = binding.fats.text.toString().toDoubleOrNull() ?: 0.0
        val protein = binding.Protein.text.toString().toDoubleOrNull() ?: 0.0

        if (calories <= 0 && carbs <= 0 && fat <= 0 && protein <= 0) {
            toast("Please enter valid nutrient data")
            return
        }

        authViewModel.getSession { session ->
            val userId = session?.id.orEmpty()
            val nutritionData = NutritionDataF(
                id = "",
                foodName = foodName,
                calories = calories.toString(),
                carbs = carbs.toString(),
                fat = fat.toString(),
                protein = protein.toString(),
                meal = selectedMealType,
                date = Date(),
                user_id = userId
            )
            nutritionViewModel.addNutrition(nutritionData)
        }

        navigateToMealsFragment()
    }

    private fun searchIngredient() {
        if (!isInternetAvailable(requireContext())) {
            toast("No internet connection")
            clearNutritionFields()
            return
        }

        val ingredient = binding.searchbar.text.toString()
        if (ingredient.isBlank()) {
            toast("Please enter a valid search term")
            return
        }

        searchViewModel.searchIngredient(ingredient)
        binding.mealLineChart.visibility = View.VISIBLE
        binding.calories.visibility = View.VISIBLE
        binding.dataLayout.visibility = View.VISIBLE


        hideKeyboard()
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchbar.windowToken, 0)
    }

    private fun setupObservers() {
        nutritionViewModel.addNutrition.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> binding.searchProgress.show()
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
                is UiState.Loading -> binding.searchProgress.show()
                is UiState.Success -> {
                    binding.searchProgress.hide()
                    handleNutrientResponse(uiState.data)
                }
                is UiState.Failure -> {
                    binding.searchProgress.hide()
                    binding.mealLineChart.visibility = View.GONE
                    toast("Failed to retrieve nutrient information")
                }
            }
        }
    }

    private fun handleNutrientResponse(nutrientResponse: NutrientResponse?) {
        if (nutrientResponse == null) {
            toast("Failed to retrieve nutrient information")
            return
        }

        val protein = nutrientResponse.totalNutrients.protein?.quantity ?: 0.0
        val carbs = nutrientResponse.totalNutrients.carbohydrates?.quantity ?: 0.0
        val fat = nutrientResponse.totalNutrients.fat?.quantity ?: 0.0
        val calories = nutrientResponse.calories.toDouble()

        binding.calories.text = calories.toString()
        binding.Protein.text = protein.toString()
        binding.carb.text = carbs.toString()
        binding.fats.text = fat.toString()
        binding.itemname.text = binding.searchbar.text.toString()

        updateChart(protein, carbs, fat, calories)
    }


    private fun updateChart(protein: Double, carbs: Double, fat: Double, calories: Double) {
        val chart = binding.mealLineChart
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false

        val entries = listOf(
            PieEntry((protein / calories * 100).toFloat(), ""),
            PieEntry((carbs / calories * 100).toFloat(), ""),
            PieEntry((fat / calories * 100).toFloat(), ""),
            PieEntry(((calories - protein - carbs - fat) / calories * 100).toFloat(), "")
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawValues(false)
        dataSet.colors = listOf(
            Color.parseColor("#FF902B"), // Protein
            Color.parseColor("#57A2E4"), // Carbs
            Color.parseColor("#6D5BDD"), // Fats
            Color.parseColor("#19E069")  // Calories
        )

        chart.setNoDataText("")
        chart.holeRadius = 85f
        dataSet.valueTextSize = 12f
        dataSet.sliceSpace = 4f

        chart.data = PieData(dataSet)
        chart.invalidate()
    }

    private fun clearNutritionFields() {
        binding.calories.text = ""
        binding.Protein.text = ""
        binding.carb.text = ""
        binding.fats.text = ""
        binding.itemname.text = ""
        binding.mealLineChart.visibility = View.GONE
    }

    private fun isValidInput(): Boolean {
        return binding.Protein.text.toString().isNotEmpty() &&
                binding.calories.text.toString().isNotEmpty() &&
                binding.carb.text.toString().isNotEmpty() &&
                binding.fats.text.toString().isNotEmpty()
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
        findNavController().navigate(R.id.action_resultFragment_to_MealsListingFragment)
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
