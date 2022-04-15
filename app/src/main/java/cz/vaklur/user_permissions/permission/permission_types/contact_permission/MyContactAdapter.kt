package cz.vaklur.user_permissions.permission.permission_types.contact_permission

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.vaklur.user_permissions.R

class MyContactAdapter(
    private val context: Context,
    private val contactList: List<MyContact>
) : RecyclerView.Adapter<MyContactAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTV: TextView = view.findViewById(R.id.contact_title_TV)
        val nameTV: TextView = view.findViewById(R.id.contact_name_TV)
        val numberTV: TextView = view.findViewById(R.id.contact_number_TV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.list_contact_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = contactList[position]
        val contactTitle = context.getString(R.string.contact_title) + " " + position.toString()
        holder.titleTV.text = contactTitle
        holder.nameTV.text = item.name
        holder.numberTV.text = item.phoneNumber
    }

    override fun getItemCount() = contactList.size
}