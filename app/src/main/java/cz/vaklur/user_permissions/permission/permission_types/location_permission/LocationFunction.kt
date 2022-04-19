package cz.vaklur.user_permissions.permission.permission_types.location_permission

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import cz.vaklur.user_permissions.R
import cz.vaklur.user_permissions.permission.permission_offline_example.NoItemAdapter
import cz.vaklur.user_permissions.server_communication.CommunicationService

/**
 * Functions to get last known phone location.
 */
class LocationFunction {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    /**
     * Get phone last known location and send it to the server.
     *
     * @param activity Application activity.
     * @param context Application context.
     */
    @SuppressLint("MissingPermission")
    fun getLastLocation(
        activity: Activity,
        context: Context,
        volleyStringResponse: CommunicationService.VolleyStringResponse
    ) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude.toString()
                    val longitude = location.longitude.toString()
                    val accuracy = location.accuracy.toString()
                    val altitude = location.altitude.toString()
                    val lastKnownLocation = MyLocation(latitude, longitude, accuracy, altitude)
                    CommunicationService(activity.application).addLocationToServer(
                        lastKnownLocation,
                        volleyStringResponse
                    )
                }
            }
    }

    /**
     * Get phone last known location and display it in the PermissionOfflineExampleFragments.
     *
     * @param context Application context.
     * @param view View for display location in offline example.
     */
    @SuppressLint("MissingPermission")
    fun getLastLocation(context: Context, view: View) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                val recyclerView = view.findViewById<RecyclerView>(R.id.example_off_RV)
                if (location != null) {
                    val latitude = location.latitude.toString()
                    val longitude = location.longitude.toString()
                    val accuracy = location.accuracy.toString()
                    val altitude = location.altitude.toString()
                    val lastKnownLocation = MyLocation(latitude, longitude, accuracy, altitude)
                    val locationAdapter = MyLocationAdapter(lastKnownLocation)
                    recyclerView.adapter = locationAdapter
                } else {
                    val locationAdapter = NoItemAdapter()
                    recyclerView.adapter = locationAdapter
                }
            }
    }
}