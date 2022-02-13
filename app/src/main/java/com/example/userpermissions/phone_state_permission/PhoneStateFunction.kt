package com.example.userpermissions.phone_state_permission

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log

class PhoneStateFunction {
    @SuppressLint("MissingPermission")
    fun getDataFromSIM(context: Context){
        val subManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val telManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var phoneNumber = ""
       subManager.activeSubscriptionInfoList?.forEach {
            val number: String? = it.number
            if (!number.isNullOrBlank()) phoneNumber += number
        }
        val dataNetworkType = telManager.dataNetworkType
        val operator = telManager.networkOperator
        Log.d("test",phoneNumber)
        Log.d("test",dataNetworkType.toString())
        Log.d("test",operator)
    }
}