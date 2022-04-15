package cz.vaklur.user_permissions.permission.permission_types.location_permission

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.vaklur.user_permissions.R
import cz.vaklur.user_permissions.constants.Constants

class MyLocationAdapter(
    private val location: MyLocation
) : RecyclerView.Adapter<MyLocationAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val latitudeTV: TextView = view.findViewById(R.id.location_latitude_TV)
        val longitudeTV: TextView = view.findViewById(R.id.location_longitude_TV)
        val accuracyTV: TextView = view.findViewById(R.id.location_accuracy_TV)
        val altitudeTV: TextView = view.findViewById(R.id.location_altitude_TV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.list_location_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = location
        holder.latitudeTV.text = Constants.LOCATION_NORTH + item.latitude
        holder.longitudeTV.text = Constants.LOCATION_EAST + item.longitude
        holder.accuracyTV.text = item.accuracy + Constants.LOCATION_METER
        holder.altitudeTV.text = item.altitude + Constants.LOCATION_METER_ABOVE_SEA_LEVEL
    }

    override fun getItemCount() = 1
}