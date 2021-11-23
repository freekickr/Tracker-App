package com.freekickr.trackerapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.freekickr.trackerapp.databinding.FragmentSplashBinding
import com.freekickr.trackerapp.ui.viewmodels.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class SplashFragment: Fragment() {

    private val delayTime: Long = 800L

    private lateinit var binding: FragmentSplashBinding

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            delay(delayTime)
            if (viewModel.checkColdStart()) {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            } else {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHistoryFragment())
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        (activity as AppCompatActivity).supportActionBar?.hide()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        (activity as AppCompatActivity).supportActionBar?.show()
//    }
}