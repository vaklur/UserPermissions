package cz.vaklur.user_permissions.permission.permission_offline_example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.vaklur.user_permissions.R

/**
 * Adapter for displaying data if there is no data on the device for the selected permission.
 */
class NoItemAdapter : RecyclerView.Adapter<NoItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.list_none_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {}

    override fun getItemCount() = 1
}