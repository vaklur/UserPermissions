package com.example.userpermissions.permission.permission_types.location_permission

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.userpermissions.R

class MyLocationAdapter (
        private val location:MyLocation
        ):RecyclerView.Adapter<MyLocationAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val latitudeTV: TextView = view.findViewById(R.id.location_latitude_TV)
        val longitudeTV: TextView = view.findViewById(R.id.location_longitude_TV)
        val accuracyTV: TextView = view.findViewById(R.id.location_accuracy_TV)
        val altitudeTV: TextView = view.findViewById(R.id.location_altitude_TV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_location_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = location
        holder.latitudeTV.text = "N " + item.latitude
        holder.longitudeTV.text ="E" + item.longitude
        holder.accuracyTV.text = item.accuracy + " m"
        holder.altitudeTV.text = item.altitude + " m MSL"
    }

    override fun getItemCount() = 1
}