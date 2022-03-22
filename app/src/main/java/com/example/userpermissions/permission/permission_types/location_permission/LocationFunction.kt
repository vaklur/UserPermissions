package com.example.userpermissions.permission.permission_types.location_permission

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
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
        var latitude: String // šířka
        var longitude: String // délka
        var accuracy: String // přesnost
        var altitude: String // n.m.v
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    latitude = location.latitude.toString() // šířka
                    longitude = location.longitude.toString() // délka
                    accuracy = location.accuracy.toString() // přesnost
                    altitude =  location.altitude.toString() // n.m.v
                    val lastKnownLocation = MyLocation(latitude,longitude,accuracy,altitude)
                    Log.d("test","location "+lastKnownLocation.latitude)
                    Log.d("test","location "+lastKnownLocation.longitude)
                    Log.d("test","location "+lastKnownLocation.accuracy)
                    Log.d("test","location "+lastKnownLocation.altitude)
                    val comFun = CommunicationFunction()
                    comFun.addLocationToServer(activity,lastKnownLocation)
                }
            }

    }

    @SuppressLint("MissingPermission")
    fun getLastLocation (context: Context,view: View){
        var latitude: String // šířka
        var longitude: String // délka
        var accuracy: String // přesnost
        var altitude: String // n.m.v
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location->
                    val recyclerView =  view.findViewById<RecyclerView>(R.id.example_off_RV)
                    if (location != null) {
                        latitude = location.latitude.toString() // šířka
                        longitude = location.longitude.toString() // délka
                        accuracy = location.accuracy.toString() // přesnost
                        altitude =  location.altitude.toString() // n.m.v
                        val lastKnownLocation = MyLocation(latitude,longitude,accuracy,altitude)
                        Log.d("test","location "+lastKnownLocation.latitude)
                        Log.d("test","location "+lastKnownLocation.longitude)
                        Log.d("test","location "+lastKnownLocation.accuracy)
                        Log.d("test","location "+lastKnownLocation.altitude)
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