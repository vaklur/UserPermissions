package cz.vaklur.user_permissions.permission.permission_types.phone_state_permission

/**
 * Class represent phone state with its parameters.
 *
 * @param phoneNumber Mobile phone number.
 * @param dataNetworkState Actual mobile network state.
 * @param operator Actual mobile operator.
 */
data class MyPhoneState(val phoneNumber: String, val dataNetworkState: String, val operator: String)
