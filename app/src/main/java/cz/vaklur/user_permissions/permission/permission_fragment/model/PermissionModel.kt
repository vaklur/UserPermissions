package cz.vaklur.user_permissions.permission.permission_fragment.model

import androidx.annotation.StringRes

/**
 * Model of data for Permission List.
 */
data class PermissionModel(
    @StringRes val stringResourceId: Int,
    val permissionId: Int
)