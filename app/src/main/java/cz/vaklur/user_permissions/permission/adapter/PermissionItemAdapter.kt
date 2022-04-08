package cz.vaklur.user_permissions.permission.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import cz.vaklur.user_permissions.R
import cz.vaklur.user_permissions.permission.model.PermissionModel

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
            bundle.putInt("permissionType",position+1)
            holder.permissionTypeBTN.findNavController().navigate(R.id.action_permissionFragment_to_PermissionTheoryFragment, bundle)
        }
    }

    /**
     * Return a size of the permissionList.
     */
    override fun getItemCount() = permissionList.size
}
