package com.example.userpermissions.permission.permission_types.contact_permission

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.userpermissions.R

class MyContactAdapter (
        private val dataset: List<MyContact>
) : RecyclerView.Adapter<MyContactAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTV: TextView = view.findViewById(R.id.contact_title_TV)
        val nameTV: TextView = view.findViewById(R.id.contact_name_TV)
        val numberTV: TextView = view.findViewById(R.id.contact_number_TV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_contact_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }



    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        val contactTitle = "Contact $position"
        holder.titleTV.text = contactTitle
        holder.nameTV.text = item.name
        holder.numberTV.text = item.phoneNumber


    }

    override fun getItemCount() = dataset.size
}