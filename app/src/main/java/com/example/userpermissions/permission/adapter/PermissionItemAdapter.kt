package com.example.userpermissions.permission.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.userpermissions.R
import com.example.userpermissions.permission.model.PermissionModel

/**
 * Adapter for the [RecyclerView] in PermissionActivity. Displays PermissionDatasource data object.
 */
class PermissionItemAdapter (
    private val context: Context,
    private val dataset: List<PermissionModel>
    ) : RecyclerView.Adapter<PermissionItemAdapter.ItemViewHolder>() {

    /**
     * Provide a reference to the views for each data item.
     */
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val permissionTypeBTN: Button = view.findViewById(R.id.permissionType_BTN)
    }

    /**
     * Create a new views.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_permission_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    /**
     * Replace the contents of a view.
     * When user click in permission type button, the permission type activity starts.
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val item = dataset[position]
            holder.permissionTypeBTN.text = context.resources.getString(item.stringResourceId)

            holder.permissionTypeBTN.setOnClickListener {
                if (context.resources.getString(item.stringResourceId) == context.getString(R.string.sms_permision)) {
                    holder.permissionTypeBTN.findNavController().navigate(R.id.action_permissionFragment_to_smsPermissionTheoryFragment)
                }
            }
    }

    /**
     * Return a size of the dataset.
     */
    override fun getItemCount() = dataset.size
}
