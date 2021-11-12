package com.freekickr.trackerapp.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.freekickr.trackerapp.databinding.FragmentRunBinding
import com.freekickr.trackerapp.ui.viewmodels.MainViewModel
import com.freekickr.trackerapp.utils.ErrorType
import com.freekickr.trackerapp.utils.PermissionsChecker
import com.freekickr.trackerapp.utils.PermissionsResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment() {

    private lateinit var binding: FragmentRunBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRunBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabNewTrack.setOnClickListener {
            findNavController().navigate(RunFragmentDirections.actionRunFragmentToTrackingFragment())
        }

        requestPermissions()
    }

    private fun requestPermissions() {
        PermissionsChecker.check(requireContext(), ::permissionChecked)
    }

    private fun permissionChecked(result: PermissionsResult) {
        when (result::class) {
            PermissionsResult.Ok::class -> {
                Toast.makeText(
                    requireContext(),
                    "Разрещения выданы",
                    Toast.LENGTH_SHORT
                ).show()
            }
            PermissionsResult.Error::class -> {
                when ((result as PermissionsResult.Error).error) {
                    ErrorType.PERM_DENIED -> {
                        showPermanentPermissionsErrorDialog()
                    }
                    ErrorType.NOT_ENABLED -> {
                        Toast.makeText(
                            requireContext(),
                            "Необходимо выдать все разрешения",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    private fun showPermanentPermissionsErrorDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Внимание!")
            .setCancelable(false)
            .setMessage(
                """
                            Некоторые разрешения были перманентно выключены.
                            Для корректной работы программы необходимо вручную выдать их приложению через настройки
                            """.trimIndent()
            )
            .setPositiveButton(
                "Разрешить"
            ) { dialog, which ->
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
                requireActivity().finish()
            }
        alertDialog.show()
    }
}