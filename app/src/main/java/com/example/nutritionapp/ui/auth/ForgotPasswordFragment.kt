package com.example.nutritionapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.FragmentForgotPasswordBinding
import com.example.nutritionapp.util.UiState
import com.example.nutritionapp.util.hide
import com.example.nutritionapp.util.isValidEmail
import com.example.nutritionapp.util.show
import com.example.nutritionapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        binding.btnForgetPass.setOnClickListener {
            if (validation()) {
                viewModel.forgotPassword(binding.etEmail.text.toString())
            }
        }

        binding.textCreate.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_registerFragment)
        }
    }

    private fun observer() {
        viewModel.forgotPassword.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnForgetPass.text = ""
                    binding.forgotPassProgress.show()
                }

                is UiState.Failure -> {
                    binding.btnForgetPass.text = "Send"
                    binding.forgotPassProgress.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.btnForgetPass.text = "Send"
                    binding.forgotPassProgress.hide()
                    toast(state.data)
                }
            }
        }
    }

    private fun validation(): Boolean {
        var isValid = true

        if (binding.etEmail.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_email))
        } else {
            if (!binding.etEmail.text.toString().isValidEmail()) {
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }
        return isValid
    }

    companion object {
        const val TAG: String = "RegisterFragment"
    }
}