package com.example.nutritionapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nutritionapp.MainActivity
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.FragmentLoginBinding
import com.example.nutritionapp.util.UiState
import com.example.nutritionapp.util.hide
import com.example.nutritionapp.util.isValidEmail
import com.example.nutritionapp.util.show
import com.example.nutritionapp.util.toast
import com.example.nutritionapp.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val binding: FragmentLoginBinding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.bbtnLog.setOnClickListener {
            if (validation()) {
                viewModel.login(
                    email = binding.etEmail.text.toString(),
                    password = binding.etPass.text.toString(),
                )
            }
        }

        binding.textReg.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_goalAndActivityFragment)
        }

        binding.labelForgetPass.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    private fun observer() {
        viewModel.login.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.loginProgress.show()
                }

                is UiState.Failure -> {
                    binding.loginProgress.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.loginProgress.hide()
                    toast(state.data)
                    restartApp()
                    findNavController().navigate(R.id.action_loginFragment_to_home_nav)
                }
            }
        }
    }

    private fun restartApp() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        requireActivity().finish()
        startActivity(intent)
    }

    private fun validation(): Boolean {
        when {
            binding.etEmail.text.isNullOrEmpty() -> {
                toast(getString(R.string.enter_email))
                return false
            }

            !binding.etEmail.text.toString().isValidEmail() -> {
                toast(getString(R.string.invalid_email))
                return false
            }

            binding.etPass.text.isNullOrEmpty() -> {
                toast(getString(R.string.enter_password))
                return false
            }

            binding.etPass.text.toString().length < 8 -> {
                toast(getString(R.string.invalid_password))
                return false
            }

            else -> return true
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSession { user ->
            if (user != null) {
                findNavController().navigate(R.id.action_loginFragment_to_home_nav)
            }
        }
    }

    companion object {
        const val TAG: String = "RegisterFragment"
    }

}