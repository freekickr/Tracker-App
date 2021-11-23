package com.freekickr.trackerapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.freekickr.trackerapp.databinding.FragmentLoginBinding
import com.freekickr.trackerapp.ui.viewmodels.LoginViewModel
import com.freekickr.trackerapp.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fields = listOf(
            binding.tietName,
            binding.tietWeight,
        )

        binding.btnNext.setOnClickListener {
            if (Utils.checkEditTextFields(fields)) {
                writeUserToSharedPrefs()
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHistoryFragment())
            }
        }
    }

    private fun writeUserToSharedPrefs(): Boolean {
        val name = binding.tietName.text.toString()
        val weight = binding.tietWeight.text.toString()
        val isSaveUser = binding.cbRemember.isChecked

        viewModel.saveColdStartValue(isSaveUser)
        viewModel.saveUser(name, weight)
        return true
    }
}