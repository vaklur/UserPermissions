package cz.vaklur.user_permissions.permission.permission_types.call_log_permission

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.vaklur.user_permissions.R

class MyCallLogAdapter(
        private val context:Context,
        private val callLogList:List<MyCallLog>
): RecyclerView.Adapter<MyCallLogAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTV: TextView = view.findViewById(R.id.call_log_title_TV)
        val dateTV: TextView = view.findViewById(R.id.call_log_date_TV)
        val numberTV: TextView = view.findViewById(R.id.call_log_number_TV)
        val durationTV: TextView = view.findViewById(R.id.call_log_duration_TV)
        val typeTV: TextView = view.findViewById(R.id.call_log_type_TV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_call_log_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = callLogList[position]
        val callLogTitle = context.getString(R.string.call_log_title)+" "+position.toString()
        holder.titleTV.text = callLogTitle
        holder.dateTV.text = item.date
        holder.numberTV.text = item.phoneNumber
        holder.durationTV.text = item.duration
        holder.typeTV.text = item.type
    }

    override fun getItemCount() = callLogList.size
}