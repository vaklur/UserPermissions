package com.example.userpermissions.permission.permission_types.contact_permission

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log

/**
 * Functions to read contacts from phone.
 */
class ContactFunction {
    /**
     * Return a list of contacts, the number of contacts is specified by the variable contactsCount.
     *
     * @param contentResolver ContentResolver for read contacts from device.
     * @param contactsCount The int number of contacts to be read.
     * @return List of contacts.
     */
    @SuppressLint("SimpleDateFormat")
    fun readContacts(contentResolver: ContentResolver, contactsCount: Int):MutableList<MyContact> {
        val contactsList: MutableList<MyContact> = ArrayList()
        val nameCol = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        val numberCol = ContactsContract.CommonDataKinds.Phone.NUMBER

        val projection = arrayOf(nameCol, numberCol)
        Log.d("test","Read contacts")
        val cursor =
                contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        projection, null, null, null
                )

        val nameColIdx = cursor!!.getColumnIndex(nameCol)
        val numberColIdx = cursor.getColumnIndex(numberCol)

        val contactsCountHelp: Int = if (contactsCount < cursor.count){
            contactsCount
        }
        else{
            cursor.count
        }

        for (i in contactsCountHelp downTo 1 step 1){
            cursor.moveToNext()
            val number = cursor.getString(numberColIdx)
            val name = cursor.getString(nameColIdx)
            contactsList.add(MyContact(name,number))
        }
        cursor.close()
        return contactsList
    }
}