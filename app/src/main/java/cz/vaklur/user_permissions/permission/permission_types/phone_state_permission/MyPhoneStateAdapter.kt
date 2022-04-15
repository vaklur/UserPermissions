package cz.vaklur.user_permissions.permission.permission_types.phone_state_permission

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.vaklur.user_permissions.R

class MyPhoneStateAdapter(
    private val phoneState: MyPhoneState
) : RecyclerView.Adapter<MyPhoneStateAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val numberTV: TextView = view.findViewById(R.id.phone_state_number_TV)
        val networkTypeTV: TextView = view.findViewById(R.id.phone_state_network_type_TV)
        val operatorTV: TextView = view.findViewById(R.id.phone_state_operator_TV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_phone_state_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = phoneState
        holder.numberTV.text = item.phoneNumber
        holder.networkTypeTV.text = item.dataNetworkState
        holder.operatorTV.text = item.operator
    }

    override fun getItemCount() = 1
}