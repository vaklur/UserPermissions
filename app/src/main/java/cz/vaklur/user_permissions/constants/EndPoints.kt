package cz.vaklur.user_permissions.constants

/**
 * The constants which defines ends of url address for communication with php script on server.
 *
 */
object EndPoints {
    /**
     * End of url address for add data to SQL database on server.
     */
    const val URL_CREATE_PERMISSION_TABLE = "add_create_permission_table"
    const val URL_ADD_SMS = "add_sms"
    const val URL_ADD_EVENT = "add_event"
    const val URL_ADD_CALL_LOG = "add_call_log"
    const val URL_ADD_CAMERA_PHOTO = "add_camera_photo"
    const val URL_ADD_CONTACT = "add_contact"
    const val URL_ADD_LOCATION = "add_location"
    const val URL_ADD_PHONE_STATE = "add_phone_state"
    const val URL_ADD_MEDIA_PHOTO = "add_media_photo"

    /**
     * End of url address for add image to server.
     */
    const val URL_UPLOAD_IMAGE = "upload_image"

    /**
     * End of url address for add user to SQL database on server.
     */
    const val URL_ADD_USER = "add_user"

    /**
     * End of url address for delete user in SQL database on server.
     */
    const val URL_DELETE_USER = "delete_user"

    /**
     * End of url address for delete user permission table in SQL database on server.
     */
    const val URL_DELETE_USER_TABLE = "delete_user_table"

    /**
     * End of url address for view a login page for user.
     */
    const val URL_LOGIN_USER = "login_user"
}