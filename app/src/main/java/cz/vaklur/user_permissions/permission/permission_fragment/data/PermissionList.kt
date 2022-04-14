package cz.vaklur.user_permissions.permission.permission_fragment.data


import cz.vaklur.user_permissions.R
import cz.vaklur.user_permissions.permission.permission_fragment.model.PermissionModel

/**
 * Load a string list of all dangerous permission with their IDs which will be demonstrated in the application.
 */
class PermissionList {
    fun loadPermissions(): List<PermissionModel> {
        return listOf(
            PermissionModel(R.string.sms_permission, 1),
            PermissionModel(R.string.contacts_permission, 2),
            PermissionModel(R.string.call_log_permission, 3),
            PermissionModel(R.string.calendar_permission, 4),
            PermissionModel(R.string.location_permission, 5),
            PermissionModel(R.string.storage_permission, 6),
            PermissionModel(R.string.phone_permission, 7),
            PermissionModel(R.string.camera_permission, 8)
        )
    }
}