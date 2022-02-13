package com.example.userpermissions.permission.data

import com.example.userpermissions.permission.model.PermissionModel
import com.example.userpermissions.R

/**
 * Load a string list of all dangerous permission which will be demonstrated in the application.
 */
class PermissionDatasource {
    fun loadPermissions(): List<PermissionModel> {
        return listOf(
            PermissionModel(R.string.sms_permision),
            PermissionModel(R.string.calendar_permision),
            PermissionModel(R.string.contacts_permision),
            PermissionModel(R.string.location_permision),
            PermissionModel(R.string.calllog_permision),
            PermissionModel(R.string.camera_permision),
            PermissionModel(R.string.phone_permission),
            PermissionModel(R.string.storage_permission)

            )
    }
}