package com.example.userpermissions.storage_permission

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat

class StorageFunction {
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SimpleDateFormat")
    fun getPhotosFromGallery(contentResolver: ContentResolver, photosCount: Int){

        val idCol = MediaStore.Images.Media._ID
        val nameCol = MediaStore.Images.Media.DISPLAY_NAME
        val dateCol = MediaStore.Images.Media.DATE_TAKEN

        val projection = arrayOf(idCol,nameCol,dateCol)

        val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        )

        val idColIdx = cursor!!.getColumnIndex(idCol)
        val nameColIdx = cursor.getColumnIndex(nameCol)
        val dateColIdx = cursor.getColumnIndex(dateCol)

        val photosCountHelp: Int = if (photosCount < cursor.count){
            photosCount
        }
        else{
            cursor.count
        }
        cursor.moveToFirst()

        for (i in photosCountHelp downTo 1 step 1){
            val id = cursor.getString(idColIdx)
            val name = cursor.getString(nameColIdx)
            val dateMSC = cursor.getLong(dateColIdx)
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            val date = simpleDateFormat.format(dateMSC)
            Log.d("test",id)
            Log.d("test",name)
            Log.d("test",date)
            cursor.moveToNext()
        }
        cursor.close()

    }
}