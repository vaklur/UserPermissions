package com.example.userpermissions.permission.permission_types.sms_permission

/**
 * Class represent SMS message with its parameters.
 *
 * @param date Date and time when the SMS was received / sent.
 * @param number Date and time when the SMS was received / sent.
 * @param text Text SMS message.
 * @param type Type of sms 1-> received SMS 2-> sent.
 */
data class MySms (val date:String, val number:String, val text:String, val type:String)