package com.example.userpermissions.permission

import android.Manifest
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.userpermissions.R
import com.example.userpermissions.settings.SettingsSharPref

class PermissionTheoryViewModel:ViewModel() {

    private var permissionId = 0
    var ipSettings = ""
    var theoryText = ""
    var permissionType = ""
    var permissionText= ""
    var requestCode = 0

    fun savePermissionID (newPermissionId:Int){
        permissionId = newPermissionId
    }

    fun initialize(context: Context){
        val settingsSP = SettingsSharPref(context)
        ipSettings = settingsSP.getIPsettings()

        when (permissionId) {
            1 -> {
                permissionType = Manifest.permission.READ_SMS
                requestCode = 101
                permissionText = context.getString(R.string.sms)
                theoryText = context.resources.getString(R.string.sms_theory)
            }
            2 -> {
                permissionType = Manifest.permission.READ_CONTACTS
                requestCode = 102
                permissionText = context.getString(R.string.contacts)
                theoryText = context.resources.getString(R.string.contact_theory)
            }
            3 -> {
                permissionType = Manifest.permission.READ_CALL_LOG
                requestCode = 103
                permissionText = context.getString(R.string.calllog)
                theoryText = context.resources.getString(R.string.calllog_theory)
            }
            4 -> {
                permissionType = Manifest.permission.READ_CALENDAR
                requestCode = 104
                permissionText = context.getString(R.string.calendar)
                theoryText = context.resources.getString(R.string.calendar_theory)
            }
            5 -> {
                permissionType = Manifest.permission.ACCESS_FINE_LOCATION
                requestCode = 105
                permissionText = context.getString(R.string.location)
                theoryText = context.resources.getString(R.string.location_theory)
            }
            6 -> {
                permissionType = Manifest.permission.READ_EXTERNAL_STORAGE
                requestCode = 106
                permissionText = context.getString(R.string.storage)
                theoryText = context.resources.getString(R.string.storage_theory)
            }
            7 -> {
                permissionType = Manifest.permission.READ_PHONE_STATE
                requestCode = 107
                permissionText = context.getString(R.string.phone)
                theoryText = context.resources.getString(R.string.phone_theory)
            }
            8 -> {
                permissionType = Manifest.permission.CAMERA
                requestCode = 108
                permissionText = context.getString(R.string.camera)
                theoryText = context.resources.getString(R.string.camera_theory)
            }
        }
    }


}