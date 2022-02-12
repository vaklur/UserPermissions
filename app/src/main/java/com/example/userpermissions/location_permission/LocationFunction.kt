package com.example.userpermissions.location_permission

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationFunction {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    fun getLastLocation (context: Context){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    val latitude = location.latitude // šířka
                    val longitude = location.longitude // délka
                    val accuraccy = location.accuracy // přesnost
                    val altitude =  location.altitude // n.m.v
                    Log.d("test",latitude.toString())
                    Log.d("test",longitude.toString())
                }

            }
    }
}