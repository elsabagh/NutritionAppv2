package com.example.nutritionapp.ui.home

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.nutritionapp.R
import com.example.nutritionapp.data.model.NutritionDataF
import com.example.nutritionapp.databinding.FragmentSearchBinding
import com.example.nutritionapp.ui.auth.AuthViewModel
import com.example.nutritionapp.util.UiState
import com.example.nutritionapp.util.hide
import com.example.nutritionapp.util.show
import com.example.nutritionapp.util.toast
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
    private val searchNutritionViewModel: SearchNutritionViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var selectedMealType: String? = null
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
            selectedMealType = "Snack"
        }

        binding.addMeal.setOnClickListener {
            if (validation()) {
                val selectedMeal = selectedMealType ?: ""
                searchNutritionViewModel.addNutrition(
                    NutritionDataF(
                        id = "",
                        foodName = binding.itemname.text.toString(),
                        calories = binding.calories.text.toString(),
                        carbs = binding.carb.text.toString(),
                        fat = binding.fats.text.toString(),
                        protein = binding.Protien.text.toString(),
                        meal = selectedMeal,
                        date = Date()
                    ).apply {
                        authViewModel.getSession { this.user_id = it?.id ?: "" }
                    }
                )
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
                searchViewModel.searchIngredient(ingredient)
                binding.mealLineChart.visibility = View.VISIBLE

            } else {
                binding.calories.text = "-"
                binding.Protien.text = "0"
                binding.carb.text = "0"
                binding.fats.text = "0"
                binding.itemname.text = "0"
                binding.mealLineChart.visibility = View.GONE

            }
        }
    }


    private fun observer() {

        searchNutritionViewModel.addNutrition.observe(viewLifecycleOwner) { state ->
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

                }

                is UiState.Success -> {

                    val nutrientResponse = uiState.data
                    val ingredient = binding.searchbar.text.toString()
                    binding.calories.text = nutrientResponse.calories.toString()
                    binding.Protien.text =
                        nutrientResponse.totalNutrients.protein.quantity.toString()
                    binding.carb.text =
                        nutrientResponse.totalNutrients.carbohydrates.quantity.toString()
                    binding.fats.text = nutrientResponse.totalNutrients.fat.quantity.toString()
                    binding.itemname.text = ingredient

                    val totalCalories = nutrientResponse.calories.toDouble()
                    val totalProtein = nutrientResponse.totalNutrients.protein.quantity
                    val totalCarbs = nutrientResponse.totalNutrients.carbohydrates.quantity
                    val totalFats = nutrientResponse.totalNutrients.fat.quantity

                    val chart = binding.mealLineChart
                    chart.setUsePercentValues(true)
                    chart.description.isEnabled = false
                    chart.legend.isEnabled = false
                    chart.setUsePercentValues(true)

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
                    dataSet.valueTextSize = 12f
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
                    val pieData = PieData(dataSet)
                    chart.data = pieData
                    chart.invalidate()

                }

                is UiState.Failure -> {
                    // Show error message
                    Toast.makeText(
                        requireContext(),
                        uiState.error ?: "Unknown error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    private fun validation(): Boolean {
        var isValid = true
        if (binding.Protien.text.toString().isNullOrEmpty()) {
            isValid = false
            toast("check internet")
        }
        if (binding.calories.text.toString().isNullOrEmpty()) {
            isValid = false
            toast("check internet")
        }
        if (binding.carb.text.toString().isNullOrEmpty()) {
            isValid = false
            toast("check internet")
        }
        if (binding.fats.text.toString().isNullOrEmpty()) {
            isValid = false
            toast("check internet")
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