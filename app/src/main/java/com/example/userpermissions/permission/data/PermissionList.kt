package com.example.userpermissions.permission.data

import com.example.userpermissions.permission.model.PermissionModel
import com.example.userpermissions.R

/**
 * Load a string list of all dangerous permission which will be demonstrated in the application.
 */
class PermissionList {
    fun loadPermissions(): List<PermissionModel> {
        return listOf(
            PermissionModel(R.string.sms_permission),
            PermissionModel(R.string.calendar_permission),
            PermissionModel(R.string.contacts_permission),
            PermissionModel(R.string.location_permission),
            PermissionModel(R.string.call_log_permission),
            PermissionModel(R.string.camera_permission),
            PermissionModel(R.string.phone_permission),
            PermissionModel(R.string.storage_permission)

            )
    }
}