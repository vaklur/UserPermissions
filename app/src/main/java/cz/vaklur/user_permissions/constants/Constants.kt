package cz.vaklur.user_permissions.constants

/**
 * Application constants.
 */
object Constants {
    // Shared Preferences
    const val SHARED_PREFERENCES_FILE = "settings"
    const val DEFAULT_SERVER_ADDRESS = "https://vaklur.cz/permissions"
    const val DEFAULT_APP_LANGUAGE = "en"
    const val DEFAULT_USER_CREATED = false
    const val SHARED_PREFERENCES_IP_ADDRESS = "ipAddress"
    const val SHARED_PREFERENCES_LANGUAGE = "language"
    const val SHARED_PREFERENCES_IP_ADDRESS_LIST = "ipAddressSet"
    const val SHARED_PREFERENCES_USER_CREATED = "userCreated"

    // Languages codes
    const val ENGLISH_CODE = "en"
    const val CZECH_CODE = "cs"

    // Location
    const val LOCATION_NORTH = "N "
    const val LOCATION_EAST = "E "
    const val LOCATION_METER = " m"
    const val LOCATION_METER_ABOVE_SEA_LEVEL = " m MSL"

    // Date format
    const val MY_DATE_FORMAT = "dd/MM/yyyy hh:mm:ss"

    // Server communication states
    const val STATE_OK = "ok"
    const val STATE_ERROR = "error"
    const val STATE_WAITING = "waiting"
}