package com.example.userpermissions.settings


import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.userpermissions.R
import com.example.userpermissions.databinding.FragmentSettingsBinding
import com.example.userpermissions.volley_communication.CommunicationFunction


/**
 * *
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    /**
     * *
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsSP = SettingsSharPref(requireContext())
        binding.languageTV.text = getActualLanguage(settingsSP.getLanguageSettings())
        binding.actualIPTV.text= settingsSP.getIPsettings()

        getServerState()

        binding.changeIPBTN.setOnClickListener {
            showSetAddressDialog()
        }
        binding.changeLanguageBTN.setOnClickListener {
            showSetLanguageDialog()
        }
        binding.refreshIPBTN.setOnClickListener {
            getServerState()
        }

    }

    /**
     * *
     */
    private fun getServerState(){
        val settingsSP = SettingsSharPref(requireContext())
        val comFun = CommunicationFunction()
        val stateActualTV = binding.stateActualTV
        val stateChangeBTN = binding.changeIPBTN
        val stateRefreshBTN = binding.refreshIPBTN
        binding.changeLanguageBTN.isEnabled=false
        stateChangeBTN.isEnabled = false
        stateRefreshBTN.isEnabled = false
        stateActualTV .text = requireContext().resources.getString(R.string.state_connecting_setting)
        stateActualTV .setBackgroundColor(Color.TRANSPARENT)
        comFun.testConnectionToServer(settingsSP.getIPsettings(), object: CommunicationFunction.VolleyStringResponse {
            override fun onSuccess() {
                stateActualTV .text = requireContext().resources.getString(R.string.state_reachable_setting)
                stateActualTV .setBackgroundColor(Color.GREEN)
                stateChangeBTN.isEnabled = true
                stateRefreshBTN.isEnabled = true
                binding.changeLanguageBTN.isEnabled=true
            }

            override fun onError() {
                stateActualTV .text = resources.getString(R.string.state_unreachable_setting)
                stateActualTV .setBackgroundColor(Color.RED)
                stateChangeBTN.isEnabled = true
                stateRefreshBTN.isEnabled = true
                binding.changeLanguageBTN.isEnabled=true
            }
        })
    }

    /**
     * *
     */
    private fun getActualLanguage(languageCode:String):String{
        var actualLanguage:String = resources.getString(R.string.language_english)
        if (languageCode =="en"){
            actualLanguage = resources.getString(R.string.language_english)
        }
        else if(languageCode =="cs"){
            actualLanguage = resources.getString(R.string.language_czech)
        }
        return  actualLanguage
    }

    /**
     * *
     */
    private fun showSetLanguageDialog(){
        val settingsSP = SettingsSharPref(requireContext())
        val languageCode = settingsSP.getLanguageSettings()
        val dialog = Dialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_app_language_settings)

        if (languageCode =="en"){
            dialog.findViewById<RadioButton>(R.id.english_RB).isChecked = true
        }
        else {
            dialog.findViewById<RadioButton>(R.id.czech_RB).isChecked = true
        }

        dialog.findViewById<Button>(R.id.saveSetLanguage_BTN).setOnClickListener {

            if (dialog.findViewById<RadioButton>(R.id.english_RB).isChecked){
                if (languageCode!="en"){
                    updateAppLocale("en")
                    dialog.dismiss()
                    requireActivity().recreate()
                }
                else dialog.dismiss()
            }
            else {
                if (languageCode!="cs"){
                    updateAppLocale("cs")
                    dialog.dismiss()
                    requireActivity().recreate()
                }
                else dialog.dismiss()
            }

        }

        dialog.findViewById<Button>(R.id.exitSetLanguage_BTN).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * *
     */
    private fun updateAppLocale(locale: String) {
        SettingsSharPref(requireContext()).saveLanguageSettings(locale)
        LocaleUtil.applyLocalizedContext(requireContext(), locale)
    }

    /**
     * Display a dialog for a set new server address.
     */
    private fun showSetAddressDialog() {
        val settingsSP = SettingsSharPref(requireContext())
        val dialog = Dialog(requireContext())
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
                if (!Patterns.WEB_URL.matcher(addressEditText.text.toString()).matches()){
                    addressEditText.error = getString(R.string.invalid_address)
                }
            }
        })


        val addressSpinner = dialog.findViewById<Spinner>(R.id.setAddress_S)
        val list = settingsSP.getIPsettingsSet()?.toMutableList()
        list?.add(getString(R.string.own_address))
        val addressAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, list as MutableList<String>)
        addressSpinner!!.adapter = addressAdapter
        addressSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectItem = list[position]
                if (selectItem == getString(R.string.own_address)){
                    addressEditText.visibility = View.VISIBLE
                    addressEditText.hint = ""
                    newAddress = true
                }
                else{
                    addressEditText.visibility = View.GONE
                    newAddress=false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }




        dialog.findViewById<Button>(R.id.saveSetAddress_BTN).setOnClickListener {

            val addressToSave:String = if (newAddress){
                addressEditText.text.toString()
            } else{
                addressSpinner.selectedItem.toString()
            }
            val comFun = CommunicationFunction()
            comFun.testConnectionToServer(addressToSave, object: CommunicationFunction.VolleyStringResponse {
                override fun onSuccess() {
                    settingsSP.saveIPsettings(addressToSave)
                    settingsSP.addAddressToIPsettingsSet(addressToSave)
                    binding.actualIPTV.text= settingsSP.getIPsettings()
                    binding.stateActualTV.text = resources.getString(R.string.state_reachable_setting)
                    binding.stateActualTV.setBackgroundColor(Color.GREEN)
                    Toast.makeText(requireContext(),getString(R.string.connection_ok), Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
                override fun onError() {
                    Toast.makeText(requireContext(),getString(R.string.connection_bad), Toast.LENGTH_LONG).show()
                }
            })
        }
        dialog.findViewById<Button>(R.id.exitSetAdress_BTN).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}