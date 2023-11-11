package ru.alt.tasksdistribution.helpers

import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

object PermissionsManager {
    fun checkPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PermissionChecker.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
    fun requestPermissions(activity: Activity, permissions: Array<String>) {
        ActivityCompat.requestPermissions(activity, permissions,200)
    }
}