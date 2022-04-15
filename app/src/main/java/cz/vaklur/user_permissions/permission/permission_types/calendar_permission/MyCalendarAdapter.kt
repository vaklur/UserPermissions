package cz.vaklur.user_permissions.permission.permission_types.calendar_permission

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.vaklur.user_permissions.R

class MyCalendarAdapter(
    private val context: Context,
    private val eventList: List<MyCalendarEvent>
) : RecyclerView.Adapter<MyCalendarAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTv: TextView = view.findViewById(R.id.calendar_title_TV)
        val title1Tv: TextView = view.findViewById(R.id.calendar_title1_TV)
        val startDateTv: TextView = view.findViewById(R.id.calendar_startDate_TV)
        val endDateTv: TextView = view.findViewById(R.id.calendar_endDate_TV)
        val descriptionTv: TextView = view.findViewById(R.id.calendar_description_TV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.list_calendar_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = eventList[position]
        val calendarTitle = context.getString(R.string.calendar_title) + " " + position.toString()
        holder.titleTv.text = calendarTitle
        holder.title1Tv.text = item.title
        holder.startDateTv.text = item.startDate
        holder.endDateTv.text = item.endDate
        holder.descriptionTv.text = item.description
    }

    override fun getItemCount() = eventList.size
}