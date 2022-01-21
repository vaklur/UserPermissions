package com.example.userpermissions.sms_permission

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.os.Build
import android.provider.Telephony
import java.text.SimpleDateFormat

/**
 * Functions to read SMS message from phone.
 */
class SmsFunction {

    /**
     * Return a list of received and sent SMS, the number of SMS is specified by the variable smsCount.
     *
     * @param contentResolver ContentResolver for read SMS from device.
     * @param smsCount The int number of sms to be read.
     * @return List of received and sent SMS.
     */
    @SuppressLint("SimpleDateFormat")
    fun readSms(contentResolver: ContentResolver, smsCount: Int):MutableList<MySms> {
        val smsList: MutableList<MySms> = ArrayList()
        val numberCol = Telephony.TextBasedSmsColumns.ADDRESS
        val textCol = Telephony.TextBasedSmsColumns.BODY
        val typeCol = Telephony.TextBasedSmsColumns.TYPE // 1 - Inbox, 2 - Sent
        val dateCol = Telephony.TextBasedSmsColumns.DATE


        val projection = arrayOf(numberCol, textCol, typeCol, dateCol)

        val cursor =
            contentResolver.query(
                    Telephony.Sms.CONTENT_URI,
                    projection, null, null, null
            )

        val numberColIdx = cursor!!.getColumnIndex(numberCol)
        val textColIdx = cursor.getColumnIndex(textCol)
        val typeColIdx = cursor.getColumnIndex(typeCol)
        val dateColIdx = cursor.getColumnIndex(dateCol)

        val smsCountHelp: Int = if (smsCount < cursor.count){
           smsCount
       }
       else{
           cursor.count
       }

        for (i in smsCountHelp downTo 1 step 1){
            cursor.moveToNext()
            val number = cursor.getString(numberColIdx)
            val text = cursor.getString(textColIdx)
            val type = smsTypeNumberToString(cursor.getString(typeColIdx))
            val dateMSC = cursor.getLong(dateColIdx)
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            val date = simpleDateFormat.format(dateMSC)

            smsList.add(MySms(date,number,text,type))
        }
        cursor.close()
        return smsList
    }

    /**
     * The method that replaces the numeric parameter type with related text.
     *
     * @param type Type of sms 1-> received SMS 2-> sent.
     * @return text SMS type.
     */
    private fun smsTypeNumberToString (type:String):String{
        var outSmsType = "Nezařazeno"
        when (type) {
            "1" -> {
                outSmsType="Přijatá"
            }
            "2" -> {
                outSmsType="Odeslaná"
            }
            else -> {
                //Do Nothing
            }
        }
        return outSmsType
    }
}