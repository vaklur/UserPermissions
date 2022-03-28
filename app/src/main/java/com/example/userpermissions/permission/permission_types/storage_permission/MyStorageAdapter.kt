package com.example.userpermissions.permission.permission_types.storage_permission

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.userpermissions.R

class MyStorageAdapter(
        private val context: Context,
        private val imageList: List<MyStorage>
):RecyclerView.Adapter<MyStorageAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTV: TextView = view.findViewById(R.id.storage_title_TV)
        val idTV: TextView = view.findViewById(R.id.storage_id_TV)
        val nameTV: TextView = view.findViewById(R.id.storage_name_TV)
        val dateTV: TextView = view.findViewById(R.id.storage_date_TV)
        val imageIV:ImageView = view.findViewById(R.id.storage_image_IV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_storage_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = imageList[position]
        val storageTitle = context.getString(R.string.storage_title)+" "+position.toString()
        holder.titleTV.text = storageTitle
        holder.idTV.text = item.id
        holder.nameTV.text = item.name
        holder.dateTV.text = item.date
        val bitmapImage = loadImageFromExternalStorage(context,item.uri)
        holder.imageIV.setImageBitmap(bitmapImage)
    }

    override fun getItemCount() = imageList.size
}

@RequiresApi(Build.VERSION_CODES.P)
fun loadImageFromExternalStorage(context: Context, imageUri: Uri): Bitmap {
    val source = ImageDecoder.createSource(context.contentResolver, imageUri)
    return ImageDecoder.decodeBitmap(source)
}