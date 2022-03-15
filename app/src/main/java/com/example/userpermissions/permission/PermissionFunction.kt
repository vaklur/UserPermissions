package com.example.userpermissions.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.userpermissions.R

/**
 * Functions for a check that a dangerous permission is granted.
 */
class PermissionFunction {

    /**
     * Check if permission is granted.
     *
     * @param activity Activity class for check state of permissions.
     * @param permissionType Permission type, for example: SMS_READ, CAMERA, etc...
     * @param permissionName Permission name, for example: sms, camera, phone state, etc...
     * @return True if permission is granted and false if not.
     */

    fun checkForPermissions(activity:Activity, permissionType: String, permissionName: String,context: Context):Boolean{
        var smsPermissionGranted = false
        if(ContextCompat.checkSelfPermission(activity.application, permissionType)== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(activity.application, permissionName+" "+context.getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
            smsPermissionGranted = true
        }
        return smsPermissionGranted
    }

    /**
     * Display a settings dialog when an permission is absolute denied.
     *
     * @param activity Activity for get application context.
     * @param permissionName Permission name, for example: sms, camera, phone state, etc...
     */
     fun showSettingsDialog(activity:Activity,permissionName:String,context: Context){
        val builder = AlertDialog.Builder(activity)

        builder.apply {
            setMessage(String.format(context.getString(R.string.permission_function_dialog_message),permissionName ))
            setTitle(context.getString(R.string.permission_function_dialog_title))
            setPositiveButton(context.getString(R.string.permission_function_dialog_settings)){ _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri: Uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
            }
            setNegativeButton(context.getString(R.string.permission_function_dialog_cancel)){ _, _->
                Toast.makeText(activity.application, context.getString(R.string.permission_function_dialog_toast), Toast.LENGTH_SHORT).show()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

}