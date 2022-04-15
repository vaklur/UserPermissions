package cz.vaklur.user_permissions.permission.permission_types.call_log_permission

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.provider.CallLog
import cz.vaklur.user_permissions.R
import cz.vaklur.user_permissions.constants.Constants
import java.text.SimpleDateFormat

/**
 * Functions to read call logs from phone.
 */
class CallLogFunction {
    /**
     * Return a list of call logs, the number of call logs is specified by the variable callLogCount.
     *
     * @param contentResolver ContentResolver for read call logs from device.
     * @param callLogCount The int number of call logs to be read.
     * @param context Application context.
     * @return List of call logs.
     */
    @SuppressLint("SimpleDateFormat")
    fun readCallLogs(
        callLogCount: Int,
        contentResolver: ContentResolver,
        context: Context
    ): MutableList<MyCallLog> {
        val callLogList: MutableList<MyCallLog> = ArrayList()

        val numberCol = CallLog.Calls.NUMBER
        val dateCol = CallLog.Calls.DATE
        val durationCol = CallLog.Calls.DURATION
        val typeCol = CallLog.Calls.TYPE // 1 - Incoming, 2 - Outgoing, 3 - Missed

        val projection = arrayOf(numberCol, dateCol, durationCol, typeCol)

        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection, null, null, null
        )

        val numberColIdx = cursor!!.getColumnIndex(numberCol)
        val dateColIdx = cursor.getColumnIndex(dateCol)
        val durationColIdx = cursor.getColumnIndex(durationCol)
        val typeColIdx = cursor.getColumnIndex(typeCol)

        val callLogCountHelp: Int = if (callLogCount < cursor.count) {
            callLogCount
        } else {
            cursor.count
        }
        cursor.moveToLast()

        for (i in callLogCountHelp downTo 1 step 1) {
            val number = cursor.getString(numberColIdx)
            val duration = cursor.getString(durationColIdx)
            val type = callLogTypeNumberToString(cursor.getString(typeColIdx), context)
            val dateMSC = cursor.getLong(dateColIdx)
            val simpleDateFormat = SimpleDateFormat(Constants.MY_DATE_FORMAT)
            val date = simpleDateFormat.format(dateMSC)
            callLogList.add(MyCallLog(number, date, duration, type))
            cursor.moveToPrevious()
        }
        cursor.close()

        return callLogList

    }

    /**
     * The method that replaces the numeric parameter type with related text.
     *
     * @param type Type of call log  1 -> Incoming call, 2 -> Outgoing call, 3 -> Missed call, other -> Unknown.
     * @param context Application context.
     * @return text call log type.
     */
    private fun callLogTypeNumberToString(type: String, context: Context): String {
        var outCallLogType = context.getString(R.string.unknown)
        when (type) {
            "1" -> {
                outCallLogType = context.getString(R.string.call_log_incoming)
            }
            "2" -> {
                outCallLogType = context.getString(R.string.call_log_outgoing)
            }
            "3" -> {
                outCallLogType = context.getString(R.string.call_log_missed)
            }
            else -> {
                //Do Nothing
            }
        }
        return outCallLogType
    }

}