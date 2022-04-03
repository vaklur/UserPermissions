package com.example.userpermissions.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.userpermissions.R
import com.example.userpermissions.permission.permission_types.calendar_permission.CalendarFunction
import com.example.userpermissions.permission.permission_types.call_log_permission.CallLogFunction
import com.example.userpermissions.permission.permission_types.contact_permission.ContactFunction
import com.example.userpermissions.permission.permission_types.location_permission.LocationFunction
import com.example.userpermissions.permission.permission_types.phone_state_permission.PhoneStateFunction
import com.example.userpermissions.permission.permission_types.sms_permission.SmsFunction
import com.example.userpermissions.permission.permission_types.storage_permission.StorageFunction
import com.example.userpermissions.settings.SettingsSharPref
import com.example.userpermissions.volley_communication.CommunicationFunction

class PermissionViewModel:ViewModel() {

    private val comFun = CommunicationFunction()

    private var permissionId = 0
    private var dataIsSend = false

    private var bitmapPhoto:Bitmap? = null

    var ipSettings = ""
    var theoryText = ""
    var permissionType = ""
    var permissionText= ""
    var requestCode = 0

    fun savePhoto (photo:Bitmap){
        bitmapPhoto=photo
    }

    fun getPhoto(): Bitmap? {
        return bitmapPhoto
    }


    fun savePermissionID (newPermissionId:Int){
        permissionId = newPermissionId
    }

    fun getPermissionID ():Int{
        return permissionId
    }

    fun saveDataIsSend (newDataIsSend:Boolean){
        dataIsSend = newDataIsSend
    }

    fun getDataIsSend ():Boolean{
        return dataIsSend
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

    fun clear(){
        permissionId=0
        dataIsSend=false
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun sendDataToServer (activity: Activity, context: Context){
        when (permissionId) {
            1 -> {
                val sms = SmsFunction()
                comFun.createPermissionTableInServer(activity, "sms")
                comFun.addSMStoServer(
                    activity,
                    sms.readSms( 10,activity.contentResolver,context)
                )
            }
            2 -> {
                val contact = ContactFunction()
                comFun.createPermissionTableInServer(
                    activity,
                    "contact"
                )
                comFun.addContactToServer(
                    activity,
                    contact.readContacts(activity.contentResolver, 10)
                )
            }
            3 -> {
                val callLog = CallLogFunction()
                comFun.createPermissionTableInServer(
                    activity,
                    "callLog"
                )
                comFun.addCallLogToServer(
                    activity,
                    callLog.readCallLogs( 10,activity.contentResolver,context)
                )

            }
            4 -> {
                val calendar = CalendarFunction()
                comFun.createPermissionTableInServer(
                    activity,
                    "calendar"
                )
                comFun.addEventToServer(
                    activity,
                    calendar.readCalendarEvents(
                        activity.contentResolver,
                        10
                    )
                )

            }
            5 -> {
                val location = LocationFunction()
                comFun.createPermissionTableInServer(
                    activity,
                    "location"
                )
                location.getLastLocation(activity, context)

            }
            6 -> {
                val extStorage = StorageFunction()
                comFun.createPermissionTableInServer(
                    activity,
                    "storage"
                )
                comFun.addMediaPhotoToServer(
                    activity,
                    extStorage.getPhotosFromGallery(
                        activity.contentResolver,
                        10
                    )
                )

            }
            7 -> {
                val simInfo = PhoneStateFunction()
                comFun.createPermissionTableInServer(
                    activity,
                    "phoneState"
                )
                comFun.addPhoneStateToServer(
                    activity,
                    simInfo.getDataFromSIM(context)
                )

            }
            8 -> {
                comFun.createPermissionTableInServer(
                    activity,
                    "camera"
                )

            }
        }
    }


}