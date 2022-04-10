package cz.vaklur.user_permissions.settings

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.Window
import android.widget.*
import cz.vaklur.user_permissions.Constants
import cz.vaklur.user_permissions.R
import cz.vaklur.user_permissions.databinding.FragmentSettingsBinding
import cz.vaklur.user_permissions.volley_communication.CommunicationService

/**
 *
 */
class SettingsDialog(application: Application) {

    val communicationService = CommunicationService(application)

    /**
     * Show dialog for change language settings
     */
    fun showSetLanguageDialog(activity: Activity, context: Context) {
        val languageCode = SettingsSharedPreferences(context).getLanguageSettings()
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_app_language_settings)

        if (languageCode == Constants.ENGLISH_CODE) {
            dialog.findViewById<RadioButton>(R.id.english_RB).isChecked = true
        } else {
            dialog.findViewById<RadioButton>(R.id.czech_RB).isChecked = true
        }

        dialog.findViewById<Button>(R.id.saveSetLanguage_BTN).setOnClickListener {
            if (dialog.findViewById<RadioButton>(R.id.english_RB).isChecked) {
                if (languageCode != Constants.ENGLISH_CODE) {
                    updateAppLocale(context, Constants.ENGLISH_CODE)
                    dialog.dismiss()
                    activity.recreate()
                } else dialog.dismiss()
            } else {
                if (languageCode != Constants.CZECH_CODE) {
                    updateAppLocale(context, Constants.CZECH_CODE)
                    dialog.dismiss()
                    activity.recreate()
                } else dialog.dismiss()
            }

        }
        dialog.findViewById<Button>(R.id.exitSetLanguage_BTN).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    /**
     * Function for updating a application actual language
     *
     * @param locale Code for new set language
     */
    private fun updateAppLocale(context: Context, locale: String) {
        SettingsSharedPreferences(context).saveLanguageSettings(locale)
        LocaleUtil.applyLocalizedContext(context, locale)
    }

    /**
     * Display a dialog for a set new server address.
     */
    fun showSetAddressDialog(binding: FragmentSettingsBinding, context: Context) {
        val settingsSP = SettingsSharedPreferences(context)
        val dialog = Dialog(context)
        var newAddress = false

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_app_ip_settings)
        val addressEditText = dialog.findViewById<EditText>(R.id.setAddress_ET)
        addressEditText.visibility = View.GONE
        addressEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!Patterns.WEB_URL.matcher(addressEditText.text.toString()).matches()) {
                    addressEditText.error = context.getString(R.string.invalid_address)
                }
            }
        })
        val addressSpinner = dialog.findViewById<Spinner>(R.id.setAddress_S)
        val list = settingsSP.getIpSettingsSet()?.toMutableList()
        list?.add(context.getString(R.string.own_address))
        val addressAdapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            list as MutableList<String>
        )
        addressSpinner!!.adapter = addressAdapter
        addressSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectItem = list[position]
                if (selectItem == context.getString(R.string.own_address)) {
                    addressEditText.visibility = View.VISIBLE
                    addressEditText.hint = ""
                    newAddress = true
                } else {
                    addressEditText.visibility = View.GONE
                    newAddress = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        dialog.findViewById<Button>(R.id.saveSetAddress_BTN).setOnClickListener {
            val addressToSave: String = if (newAddress) {
                addressEditText.text.toString()
            } else {
                addressSpinner.selectedItem.toString()
            }
            communicationService.testConnectionToServer(
                addressToSave,
                object : CommunicationService.VolleyStringResponse {
                    override fun onSuccess() {
                        if (binding.actualIPTV.text != addressToSave) {
                            communicationService.deleteUserInServer()
                        }
                        settingsSP.saveIpSettings(addressToSave)
                        settingsSP.addAddressToIpSettingsSet(addressToSave)
                        binding.actualIPTV.text = settingsSP.getIpSettings()
                        binding.stateActualTV.text =
                            context.getString(R.string.state_reachable_setting)
                        binding.stateActualTV.setBackgroundColor(Color.GREEN)
                        Toast.makeText(
                            context,
                            context.getString(R.string.connection_ok),
                            Toast.LENGTH_LONG
                        ).show()
                        dialog.dismiss()
                    }

                    override fun onError() {
                        Toast.makeText(
                            context,
                            context.getString(R.string.connection_bad),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
        dialog.findViewById<Button>(R.id.exitSetAddress_BTN).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}
