package com.freekickr.trackerapp.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.freekickr.trackerapp.databinding.FragmentHistoryBinding
import com.freekickr.trackerapp.domain.TrackSortingOrder
import com.freekickr.trackerapp.ui.adapters.TrackAdapter
import com.freekickr.trackerapp.ui.viewmodels.HistoryViewModel
import com.freekickr.trackerapp.utils.ErrorType
import com.freekickr.trackerapp.utils.PermissionsChecker
import com.freekickr.trackerapp.utils.PermissionsResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding

    private val viewModel: HistoryViewModel by viewModels()

    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater)

        initAdapter()

        binding.spFilter.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, TrackSortingOrder.values())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabNewTrack.setOnClickListener {
            findNavController().navigate(HistoryFragmentDirections.actionHistoryFragmentToTrackingFragment())
        }

        binding.spFilter.setSelection(viewModel.sortType.ordinal)

        binding.spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.changeSortType(TrackSortingOrder.values()[p2])
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        viewModel.tracks.observe(viewLifecycleOwner, {
            adapter.updateList(it)
        })

        requestPermissions()
    }

    private fun initAdapter() {
        adapter = TrackAdapter()
        binding.rvTrackHistory.adapter = adapter
    }

    private fun requestPermissions() {
        PermissionsChecker.check(requireContext(), ::permissionChecked)
    }

    private fun permissionChecked(result: PermissionsResult) {
        when (result::class) {
            PermissionsResult.Ok::class -> {

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