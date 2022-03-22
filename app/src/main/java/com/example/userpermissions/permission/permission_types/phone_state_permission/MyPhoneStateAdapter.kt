package com.example.userpermissions.permission.permission_types.phone_state_permission

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.userpermissions.R

class MyPhoneStateAdapter(
        private val dataset:MyPhoneState
):RecyclerView.Adapter<MyPhoneStateAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTV: TextView = view.findViewById(R.id.phone_state_title_TV)
        val numberTV: TextView = view.findViewById(R.id.phone_state_number_TV)
        val networkTypeTV: TextView = view.findViewById(R.id.phone_state_network_type_TV)
        val operatorTV: TextView = view.findViewById(R.id.phone_state_operator_TV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_phone_state_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }



    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset
        val phoneStateTitle = "Information from SIM card"
        holder.titleTV.text = phoneStateTitle
        holder.numberTV.text = item.phoneNumber
        holder.networkTypeTV.text = item.dataNetworkState
        holder.operatorTV.text = item.operator
    }

    override fun getItemCount() = 1
}