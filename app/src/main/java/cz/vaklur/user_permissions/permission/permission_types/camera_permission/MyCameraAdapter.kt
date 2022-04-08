package cz.vaklur.user_permissions.permission.permission_types.camera_permission

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cz.vaklur.user_permissions.R

class MyCameraAdapter (
        private val photo:Bitmap
        ):RecyclerView.Adapter<MyCameraAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val photoIV: ImageView= view.findViewById(R.id.camera_image_IV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_camera_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = photo
        holder.photoIV.setImageBitmap(item)

    }

    override fun getItemCount() = 1
}