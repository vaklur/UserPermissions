package com.example.userpermissions.permission.permission_types.phone_state_permission

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager

/**
 * Functions to read data from phone SIM card.
 */
class PhoneStateFunction {
    /**
     * Return a data from SIM card.
     *
     * @param context Application context.
     * @return MyPhoneState variable with information from SIm card.
     */
    @SuppressLint("MissingPermission")
    fun getDataFromSIM(context: Context):MyPhoneState{
        val subManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val telManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var phoneNumber = ""
       subManager.activeSubscriptionInfoList?.forEach {
            val number: String? = it.number
            if (!number.isNullOrBlank()) phoneNumber += "$number,"
        }
        val dataNetworkType = telManager.dataNetworkType
        val operator = telManager.networkOperator

        return MyPhoneState(phoneNumber,networkTypeToString(dataNetworkType),getOperatorName(operator))
    }

    /**
     * Get operator name from operator code for the Czech Republic and neighboring countries.
     *
     * @param operator String operator code.
     *
     * @return String operator name.
     */
    private fun getOperatorName (operator:String):String{
        return when (operator){
            // Czech
            "23003" -> "$operator(Vodafone)"
            "23002" -> "$operator(O2)"
            "23001" -> "$operator(T-Mobile)"
            // Slovakia
            "23106" -> "$operator(O2)"
            "23104" -> "$operator(Telekom)"
            "23103" -> "$operator(4ka)"
            "23102" -> "$operator(Telekom)"
            "23101" -> "$operator(Orange)"
            // Poland
            "26001" -> "$operator(Plus)"
            "26002" -> "$operator(T-Mobile)"
            "26003" -> "$operator(Orange)"
            "26006" -> "$operator(Play)"
            // Germany
            "26201" -> "$operator(Telekom)"
            "26202" -> "$operator(Vodafone)"
            "26203" -> "$operator(O2)"
            // Austria
            "23201" -> "$operator(A1)"
            "23203" -> "$operator(Magenta Telekom)"
            "23205" -> "$operator(Drei)"
            else -> "$operator(unknown)"
        }
    }

    /**
     * Get the text type of mobile network from int value.
     *
     * @param intNetworkType network type in int format.
     *
     * @return Network type in text.
     */
    private fun networkTypeToString (intNetworkType:Int):String{
        return when(intNetworkType){
            0 -> "unknown"
            1 -> "GPRS"
            2 -> "EDGE"
            3 -> "UMTS"
            4 -> "CDMA"
            5 -> "EVDO 0"
            6 -> "EVDO A"
            7 -> "1xRTT"
            8 -> "HSDPA"
            9 -> "HSUPA"
            10 -> "HSPA"
            11 -> "IDEN"
            12 -> "EVDO B"
            13 -> "LTE"
            14 -> "eHRPD"
            15 -> "HSPA+"
            16 -> "GSM"
            17 -> "TD_SCDMA"
            18 -> "IWLAN"
            19 -> "unknown"
            20 -> "NR 5G"
            else -> "unknown"
        }
    }
}