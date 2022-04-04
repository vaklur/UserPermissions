package com.example.userpermissions.permission.permission_types.calendar_permission

/**
 * Class represent calendar event with its parameters.
 *
 * @param title Title of calendar event.
 * @param startDate Start date of calendar event.
 * @param endDate End date of calendar event.
 * @param description Description of calendar event.
 */
data class MyCalendarEvent(val title:String,val startDate:String,val endDate:String,val description:String)
