package cz.vaklur.user_permissions.permission.permission_types.storage_permission

import android.net.Uri


/**
 * Class represent Image from external storage with its parameters.
 *
 * @param id ID of the image..
 * @param name Name of the image.
 * @param date Date and time when the Image was taken.
 * @param uri URI of the image.
 */
data class MyStorage(val id:String,val name:String, val date:String, val uri: Uri)
