package com.example.userpermissions.contact_permission

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log

class ContactFunction {

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

        var number: String
        var name:String


        for (i in contactsCountHelp downTo 1 step 1){
            cursor.moveToNext()
            number = cursor.getString(numberColIdx)
            name = cursor.getString(nameColIdx)
            Log.d("test",name)
            Log.d("test",number)
            contactsList.add(MyContact(name,number))
        }
        cursor.close()
        return contactsList
    }
}