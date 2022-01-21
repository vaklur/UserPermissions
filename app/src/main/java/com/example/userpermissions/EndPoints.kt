package com.example.userpermissions

/**
 * The constants which defines ends of url address for communication with php script on server.
 *
 */
object EndPoints {
    /**
     * End of url address for add sms message to SQL database on server.
     */
    const val URL_ADD_SMS = "add_sms"
    /**
     * End of url address for add user to SQL database on server.
     */
    const val URL_CREATE_USER = "create_user"
    /**
     * End of url address for delete user in SQL database on server.
     */
    const val URL_DELETE_USER = "delete_user"
    /**
     * End of url address for view a login page for user.
     */
    const val URL_LOGIN_USER = "login_user"
}