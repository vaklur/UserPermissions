package cz.vaklur.user_permissions.volley_communication

import android.app.Application
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import cz.vaklur.user_permissions.settings.LocaleUtil
import cz.vaklur.user_permissions.settings.SettingsSharPref

/**
 * This class keeps track of the requests.
 */
class VolleySingleton : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { LocaleUtil.getLocalizedContext(it,
            SettingsSharPref(base).getLanguageSettings()) })
    }

    private val requestQueue: RequestQueue? = null
        get() {
            if (field == null) {
                return Volley.newRequestQueue(applicationContext)
            }
            return field
        }

    fun <T> addToRequestQueue(request: Request<T>) {
        request.tag = TAG
        requestQueue?.add(request)
    }


    companion object {
        private val TAG = VolleySingleton::class.java.simpleName
        @get:Synchronized var instance: VolleySingleton? = null
            private set
    }
}