package com.example.userpermissions.permission.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.userpermissions.R
import com.example.userpermissions.permission.model.PermissionModel

/**
 * Adapter for the [RecyclerView] in PermissionActivity. Displays PermissionList data object.
 */
class PermissionItemAdapter (
    private val context: Context,
    private val permissionList: List<PermissionModel>
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
        val item = permissionList[position]
        holder.permissionTypeBTN.text = context.resources.getString(item.stringResourceId)
        /**
         * Permissions IDs
         * sms  - 1
         * contacts - 2
         * call logs - 3
         * calendar - 4
         * location - 5
         * extStorage - 6
         * sim - 7
         * camera - 8
         */
        holder.permissionTypeBTN.setOnClickListener {
            val bundle = Bundle()
            when {
                context.resources.getString(item.stringResourceId) == context.getString(R.string.sms_permision) -> {
                    bundle.putInt("permissionType",1)
                }
                context.resources.getString(item.stringResourceId) == context.getString(R.string.contacts_permision) -> {
                    bundle.putInt("permissionType",2)
                }
                context.resources.getString(item.stringResourceId) == context.getString(R.string.calllog_permision) -> {
                    bundle.putInt("permissionType",3)
                }
                context.resources.getString(item.stringResourceId) == context.getString(R.string.calendar_permision) -> {
                    bundle.putInt("permissionType",4)
                }
                context.resources.getString(item.stringResourceId) == context.getString(R.string.location_permision) -> {
                    bundle.putInt("permissionType",5)
                }
                context.resources.getString(item.stringResourceId) == context.getString(R.string.storage_permission) -> {
                    bundle.putInt("permissionType",6)
                }
                context.resources.getString(item.stringResourceId) == context.getString(R.string.phone_permission) -> {
                    bundle.putInt("permissionType",7)
                }
                context.resources.getString(item.stringResourceId) == context.getString(R.string.camera_permision) -> {
                    bundle.putInt("permissionType",8)
                }
            }
            if (context.resources.getString(item.stringResourceId) == context.getString(R.string.camera_permision)){
                holder.permissionTypeBTN.findNavController()
                        .navigate(R.id.action_permissionFragment_to_PermissionTheoryFragment, bundle)
            }
            else {
                holder.permissionTypeBTN.findNavController()
                    .navigate(R.id.action_permissionFragment_to_PermissionTheoryFragment, bundle)
            }
        }
    }

    /**
     * Return a size of the permissionList.
     */
    override fun getItemCount() = permissionList.size
}
