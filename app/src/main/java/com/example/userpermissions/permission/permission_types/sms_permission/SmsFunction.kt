package com.example.userpermissions.permission.permission_types.sms_permission

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.provider.Telephony
import com.example.userpermissions.R
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
    fun readSms( smsCount: Int,contentResolver: ContentResolver,context: Context):MutableList<MySms> {
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
            val type = smsTypeNumberToString(cursor.getString(typeColIdx),context)
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
    private fun smsTypeNumberToString (type:String,context: Context):String{
        var outSmsType = context.getString(R.string.unknown)
        when (type) {
            "1" -> {
                outSmsType=context.getString(R.string.sms_received)
            }
            "2" -> {
                outSmsType=context.getString(R.string.sms_sent)
            }
            else -> {
                //Do Nothing
            }
        }
        return outSmsType
    }
}