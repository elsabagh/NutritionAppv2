package com.example.nutritionapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.nutritionapp.R
import com.example.nutritionapp.data.model.PersonalInformation
import com.example.nutritionapp.data.model.User
import com.example.nutritionapp.databinding.FragmentRegisterBinding
import com.example.nutritionapp.util.UiState
import com.example.nutritionapp.util.hide
import com.example.nutritionapp.util.isValidEmail
import com.example.nutritionapp.util.show
import com.example.nutritionapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels()
    val args: RegisterFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()

        binding.toolbarTitle.text = "Create Account"

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnReg.setOnClickListener {
            if (validation()) {
                viewModel.register(
                    email = binding.etEmail.text.toString(),
                    password = binding.etPass.text.toString(),
                    user = getUserObj()
                )
            }
        }


    }

    private fun observer() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.registerProgress.show()
                }

                is UiState.Failure -> {
                    binding.registerProgress.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.registerProgress.hide()
                    toast(state.data)

                    viewModel.dataInfo(
                        PersonalInformation(
                            id = "",
                            goal = args.selectedGoal,
                            levelActivity = args.selectedActivityLevel,
                            gander = args.selectedGander,
                            weaklyGoal =  args.selectedWeaklyGoal,
                            age = args.age,
                            height = args.height,
                            weight =args.weight,
                            goalWight =args.goalWight
                        ).apply {
                            viewModel.getSession { this.user_id = it?.id ?: "" }
                        }
                    )

                    findNavController().navigate(R.id.action_registerFragment_to_home_nav)
                }
            }
        }
    }

    private fun getUserObj(): User {
        return User(
            id = "",
            email = binding.etEmail.text.toString(),
        )
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
        if (binding.etPass.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_password))
        } else {
            if (binding.etPass.text.toString().length < 8) {
                isValid = false
                toast(getString(R.string.invalid_password))
            }
        }

        val password = binding.etPass.text.toString()
        val confirmPassword = binding.etConfPass.text.toString()
        if (confirmPassword != password) {
            isValid = false
            toast(getString(R.string.passwords_do_not_match))
        }
        return isValid
    }

    companion object {
        const val TAG: String = "RegisterFragment"
    }

}