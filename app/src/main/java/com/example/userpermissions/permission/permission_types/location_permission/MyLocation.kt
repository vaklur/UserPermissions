package com.example.userpermissions.permission.permission_types.location_permission

/**
 * Class represent last known location with its parameters.
 *
 * @param latitude Last known latitude.
 * @param longitude Last known longitude.
 * @param accuracy Last known accuracy.
 * @param altitude Last known altitude.
 */
data class MyLocation(val latitude:String, val longitude:String, val accuracy:String,val altitude:String)
