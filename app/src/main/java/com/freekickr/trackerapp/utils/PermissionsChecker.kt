package com.freekickr.trackerapp.utils

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

object PermissionsChecker {

    private val oldPermissions = arrayListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    //SDK VER >= Q
    @RequiresApi(Build.VERSION_CODES.Q)
    private val newPermissions = arrayListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    fun check(context: Context, onResult: (PermissionsResult) -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            checkPermissions(context, oldPermissions, onResult)
        } else {
            checkPermissions(context, newPermissions, onResult)
        }
    }

    private fun checkPermissions(context: Context, permissions: List<String>, onResult: (PermissionsResult) -> Unit) {
        Dexter.withContext(context)
            .withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport?) {
                    if (multiplePermissionsReport != null) {
                        when {
                            multiplePermissionsReport.areAllPermissionsGranted() -> {
                                onResult(PermissionsResult.Ok())
                            }
                            multiplePermissionsReport.isAnyPermissionPermanentlyDenied -> {
                                onResult(PermissionsResult.Error(ErrorType.PERM_DENIED))
                            }
                            else -> {
                                onResult(PermissionsResult.Error(ErrorType.NOT_ENABLED))
                            }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest>,
                    permissionToken: PermissionToken
                ) {
                    if (permissionToken != null) {
                        permissionToken.continuePermissionRequest()
                    }
                }
            }).check()
    }
}

sealed class PermissionsResult {
    class Ok: PermissionsResult()
    class Error(val error: ErrorType): PermissionsResult()
}

enum class ErrorType(msg: String) {
    PERM_DENIED("Some permissions was permanently denied"),
    NOT_ENABLED("You need to enable all the permissions")
}