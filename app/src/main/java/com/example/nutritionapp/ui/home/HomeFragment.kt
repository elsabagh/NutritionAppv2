package com.example.nutritionapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.FragmentHomeBinding
import com.example.nutritionapp.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    replaceFragment(DashFragment.newInstance("Dash"))
                    true
                }
                R.id.action_search -> {
                    replaceFragment(SearchFragment.newInstance("Search"))
                    true
                }
                R.id.action_result -> {
                    replaceFragment(ResultFragment.newInstance("Result"))
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            // Display the initial fragment (e.g., NoteListingFragment)
            replaceFragment(DashFragment.newInstance("Dash"))
            // Set the corresponding menu item as checked in the BottomNavigationView
            binding.bottomNavigationView.selectedItemId = R.id.action_home
        }

        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}