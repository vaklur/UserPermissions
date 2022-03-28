package com.example.userpermissions.permission.permission_types.location_permission

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.userpermissions.R
import com.example.userpermissions.permission.offline_example.NoItemAdapter
import com.example.userpermissions.volley_communication.CommunicationFunction
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationFunction {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    fun getLastLocation (activity: Activity, context: Context){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    val latitude = location.latitude.toString()
                    val longitude = location.longitude.toString()
                    val accuracy = location.accuracy.toString()
                    val altitude =  location.altitude.toString()
                    val lastKnownLocation = MyLocation(latitude,longitude,accuracy,altitude)
                    val comFun = CommunicationFunction()
                    comFun.addLocationToServer(activity,lastKnownLocation)
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation (context: Context,view: View){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location->
                    val recyclerView =  view.findViewById<RecyclerView>(R.id.example_off_RV)
                    if (location != null) {
                        val latitude = location.latitude.toString()
                        val longitude = location.longitude.toString()
                        val accuracy = location.accuracy.toString()
                        val altitude =  location.altitude.toString()
                        val lastKnownLocation = MyLocation(latitude,longitude,accuracy,altitude)
                        val locationAdapter = MyLocationAdapter(lastKnownLocation)
                        recyclerView.adapter = locationAdapter
                    }
                    else{
                        val locationAdapter = NoItemAdapter()
                        recyclerView.adapter = locationAdapter
                    }
                }
    }
}