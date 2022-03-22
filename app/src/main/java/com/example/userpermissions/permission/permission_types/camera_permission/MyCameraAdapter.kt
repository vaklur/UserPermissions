package com.example.userpermissions.permission.permission_types.camera_permission

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.userpermissions.R

class MyCameraAdapter (
        private val dataset:Bitmap
        ):RecyclerView.Adapter<MyCameraAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //val titleTV: TextView = view.findViewById(R.id.camera_title_TV)
        val photoIV: ImageView= view.findViewById(R.id.camera_image_IV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_camera_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }



    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset
        holder.photoIV.setImageBitmap(item)

    }

    override fun getItemCount() = 1
}