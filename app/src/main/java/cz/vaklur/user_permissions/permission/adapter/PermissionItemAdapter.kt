package cz.vaklur.user_permissions.permission.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import cz.vaklur.user_permissions.R
import cz.vaklur.user_permissions.permission.PermissionViewModel
import cz.vaklur.user_permissions.permission.model.PermissionModel

/**
 * Adapter for the [RecyclerView] in PermissionFragment. Displays PermissionList data object.
 */
class PermissionItemAdapter (
    private val context: Context,
    private val permissionVM:PermissionViewModel,
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
     * When user click in permission type button, the permission type fragment starts.
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = permissionList[position]
        holder.permissionTypeBTN.text = context.resources.getString(item.stringResourceId)

        holder.permissionTypeBTN.setOnClickListener {
            permissionVM.setPermissionID(item.permissionId)
            holder.permissionTypeBTN.findNavController().navigate(R.id.action_permissionFragment_to_PermissionTheoryFragment)
        }
    }

    /**
     * Return a size of the permissionList.
     */
    override fun getItemCount() = permissionList.size
}
