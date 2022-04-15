package cz.vaklur.user_permissions.permission.permission_types.call_log_permission

/**
 * Class represent phone call log with its parameters.
 *
 * @param phoneNumber Phone number.
 * @param date Date and time of call.
 * @param duration Duration of call.
 * @param type Type of call.
 */
data class MyCallLog(
    val phoneNumber: String,
    val date: String,
    val duration: String,
    val type: String
)
