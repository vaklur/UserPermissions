package cz.vaklur.user_permissions.settings

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cz.vaklur.user_permissions.MainActivity
import cz.vaklur.user_permissions.R
import cz.vaklur.user_permissions.constants.Constants
import cz.vaklur.user_permissions.server_communication.CommunicationService

/**
 * View model for settings fragment
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    // Live data for widgets data binding
    private var _serverState = MutableLiveData<String>()
    val serverState: LiveData<String> = _serverState

    private var _serverStateColor = MutableLiveData<Drawable>()
    val serverStateColor: LiveData<Drawable> = _serverStateColor

    private var _changeServerAddressButtonEnable = MutableLiveData<Boolean>()
    val changeServerAddressButtonEnable: LiveData<Boolean> = _changeServerAddressButtonEnable

    private var _refreshServerStateButtonEnable = MutableLiveData<Boolean>()
    val refreshServerStateButtonEnable: LiveData<Boolean> = _refreshServerStateButtonEnable

    private var _changeLanguageButtonEnable = MutableLiveData<Boolean>()
    val changeLanguageButtonEnable: LiveData<Boolean> = _changeLanguageButtonEnable

    private var _progressBarVisibility = MutableLiveData<Int>()
    var progressBarVisibility: LiveData<Int> = _progressBarVisibility

    private var _progressBarTextViewVisibility = MutableLiveData<Int>()
    var progressBarTextViewVisibility: LiveData<Int> = _progressBarTextViewVisibility

    // Variables for functions
    private var settingsSharedPreferences =
        SettingsSharedPreferences(application.applicationContext)
    private var communicationService = CommunicationService(application)

    // Variables for actual settings
    lateinit var actualLanguage: String
    var actualIpAddress: String

    init {
        _serverState.value =
            application.applicationContext.getString(R.string.state_connecting_setting)
        _serverStateColor.value =
            AppCompatResources.getDrawable(application.applicationContext, R.color.gray)
        _changeServerAddressButtonEnable.value = true
        _changeLanguageButtonEnable.value = true
        _refreshServerStateButtonEnable.value = true
        _progressBarVisibility.value = View.GONE
        _progressBarTextViewVisibility.value = View.GONE
        actualIpAddress = settingsSharedPreferences.getIpSettings()
    }

    /**
     * Get server state
     */
    fun getServerState(activity: Activity, context: Context) {
        (activity as MainActivity).allowBackPressed = false
        _serverState.value = context.getString(R.string.state_connecting_setting)
        _serverStateColor.value = AppCompatResources.getDrawable(context, R.color.gray)
        _changeServerAddressButtonEnable.value = false
        _changeLanguageButtonEnable.value = false
        _refreshServerStateButtonEnable.value = false
        _progressBarVisibility.value = View.VISIBLE
        _progressBarTextViewVisibility.value = View.VISIBLE
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        communicationService.testConnectionToServer(settingsSharedPreferences.getIpSettings(),
            object : CommunicationService.VolleyStringResponse {
                override fun onSuccess() {
                    _serverState.value = context.getString(R.string.state_reachable_setting)
                    activity.allowBackPressed = true
                    _serverStateColor.value = AppCompatResources.getDrawable(context, R.color.green)
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
                    _changeServerAddressButtonEnable.value = true
                    _changeLanguageButtonEnable.value = true
                    _refreshServerStateButtonEnable.value = true
                    _progressBarVisibility.value = View.GONE
                    _progressBarTextViewVisibility.value = View.GONE
                }

                override fun onError() {
                    _serverState.value = context.getString(R.string.state_unreachable_setting)
                    activity.allowBackPressed = true
                    _serverStateColor.value = AppCompatResources.getDrawable(context, R.color.red)
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
                    _changeServerAddressButtonEnable.value = true
                    _changeLanguageButtonEnable.value = true
                    _refreshServerStateButtonEnable.value = true
                    _progressBarVisibility.value = View.GONE
                    _progressBarTextViewVisibility.value = View.GONE
                }
            })
    }

    /**
     * Function that returns the full name of the current language, depending on the local language code.
     *
     * @return Full name of actual language.
     */
    fun getActualLanguage(context: Context): String {
        val languageCode = settingsSharedPreferences.getLanguageSettings()
        if (languageCode == Constants.ENGLISH_CODE) {
            actualLanguage = context.getString(R.string.language_english)
        } else if (languageCode == Constants.CZECH_CODE) {
            actualLanguage = context.getString(R.string.language_czech)
        }
        return actualLanguage
    }
}