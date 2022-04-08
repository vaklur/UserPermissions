package cz.vaklur.user_permissions.permission.permission_types.calendar_permission

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.CalendarContract
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * Functions to read calendar events from phone.
 */
class CalendarFunction {
    /**
     * Return a list of calendar events, the number of events is specified by the variable calendarEventCount.
     *
     * @param contentResolver ContentResolver for read SMS from device.
     * @param calendarEventCount The int number of events to be read.
     * @return List of calendar events.
     */
    @SuppressLint("SimpleDateFormat")
    fun readCalendarEvents (contentResolver: ContentResolver, calendarEventCount: Int):MutableList<MyCalendarEvent>{
        val calendarEventList: MutableList<MyCalendarEvent> = ArrayList()

        val titleCol = CalendarContract.Events.TITLE
        val startDateCol = CalendarContract.Events.DTSTART
        val endDateCol = CalendarContract.Events.DTEND
        val descriptionCol = CalendarContract.Events.DESCRIPTION

        val projection = arrayOf(titleCol,startDateCol,endDateCol,descriptionCol)
        val selection = CalendarContract.Events.DELETED + " != 1"

        val cursor = contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection, selection, null, null
        )

        val titleColIdx = cursor!!.getColumnIndex(titleCol)
        val startDateColIdx = cursor.getColumnIndex(startDateCol)
        val endDateColIdx = cursor.getColumnIndex(endDateCol)
        val descriptionColIdx = cursor.getColumnIndex(descriptionCol)

        val calendarEventCountHelp: Int = if (calendarEventCount < cursor.count){
            calendarEventCount
        }
        else{
            cursor.count
        }
        cursor.moveToLast()
        for (i in calendarEventCountHelp downTo 1 step 1){
            val title = cursor.getString(titleColIdx)
            val startDateMSC = cursor.getLong(startDateColIdx)
            val endDateMSC = cursor.getLong(endDateColIdx)
            val description = cursor.getString(descriptionColIdx)
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            val startDate = simpleDateFormat.format(startDateMSC)
            val endDate = simpleDateFormat.format(endDateMSC)
            Log.d("test",title)
            Log.d("test",startDate)
            Log.d("test",endDate)
            Log.d("test",description)
            calendarEventList.add(MyCalendarEvent(title,startDate,endDate,description))
            cursor.moveToPrevious()
        }
        cursor.close()
        return calendarEventList

    }
}