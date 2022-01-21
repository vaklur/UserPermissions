package com.example.userpermissions.permission

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

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

    fun checkForPermissions(activity:Activity, permissionType: String, permissionName: String):Boolean{
        var smsPermissionGranted = false
        if(ContextCompat.checkSelfPermission(activity.application, permissionType)== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(activity.application, "$permissionName povolení uděleno", Toast.LENGTH_SHORT).show()
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
     fun showSettingsDialog(activity:Activity,permissionName:String){
        val builder = AlertDialog.Builder(activity)

        builder.apply {
            setMessage("Povolení k přístupu k $permissionName je vyžadováno pro praktický příklad.")
            setTitle("Žádost o povolení")
            setPositiveButton("Nastavení"){ dialog, which ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri: Uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
            }
            setNegativeButton("Zavřít"){ dialog, which ->
                Toast.makeText(activity.application, "Praktická ukázka nemůže být spuštěna", Toast.LENGTH_SHORT).show()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

}