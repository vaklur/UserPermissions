package com.example.userpermissions.calendar_permission

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.CalendarContract
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class CalendarFunction {
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

        for (i in calendarEventCountHelp downTo 1 step 1){
            cursor.moveToNext()
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
        }
        cursor.close()
        return calendarEventList

    }
}