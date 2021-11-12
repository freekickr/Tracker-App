package com.freekickr.trackerapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.freekickr.trackerapp.R
import com.freekickr.trackerapp.databinding.ActivityMainBinding
import com.freekickr.trackerapp.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(binding.topAppBar)

        setContentView(binding.root)

        navController = findNavController(R.id.navHostFragment)

        setupNavigation()

        navigateToTrackingFragmentIfNeeded(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun setupNavigation() {
        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.runFragment,
                R.id.settingsFragment,
                R.id.statsFragment -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        intent?.let {
            if (it.action == ACTION_SHOW_TRACKING_FRAGMENT) {
                navController.navigate(R.id.action_run_global_tracking_fragment)
            }
        }
    }
}