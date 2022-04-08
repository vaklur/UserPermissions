package cz.vaklur.user_permissions.permission.permission_types.sms_permission

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.vaklur.user_permissions.R


class MySmsAdapter (
        private val context: Context,
        private val smsList: List<MySms>
) : RecyclerView.Adapter<MySmsAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTV:TextView = view.findViewById(R.id.sms_title_TV)
        val dateTV:TextView = view.findViewById(R.id.sms_date_TV)
        val numberTV:TextView = view.findViewById(R.id.sms_number_TV)
        val textTV:TextView = view.findViewById(R.id.sms_text_TV)
        val typeTV:TextView = view.findViewById(R.id.sms_type_TV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_sms_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = smsList[position]
        val smsTitle = context.getString(R.string.sms_title)+" "+position.toString()
        holder.titleTV.text = smsTitle
        holder.dateTV.text = item.date
        holder.numberTV.text = item.number
        holder.textTV.text = item.text
        holder.typeTV.text = item.type
        }

    override fun getItemCount() = smsList.size
}
