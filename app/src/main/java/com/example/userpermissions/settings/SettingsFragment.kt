package com.example.userpermissions.settings


import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsSP = SettingsSharPref(requireContext())
        binding.actualIPTV.text= settingsSP.getIPsettings()
        val comFun = CommunicationFunction()
        comFun.testConnectionToServer(settingsSP.getIPsettings(), object: CommunicationFunction.VolleyStringResponse {
            override fun onSuccess() {
                binding.stateActualTV.text = requireContext().resources.getString(R.string.state_reachable_setting)
                binding.stateActualTV.setBackgroundColor(Color.GREEN)
            }

            override fun onError() {
                binding.stateActualTV.text = resources.getString(R.string.state_unreachable_setting)
                binding.stateActualTV.setBackgroundColor(Color.RED)
            }
        })

        binding.changeIPBTN.setOnClickListener {
            showSetAddressDialog()
        }
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
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (Patterns.WEB_URL.matcher(addressEditText.text.toString()).matches()){
                }
                else{
                    addressEditText.error = "Invalid Url"
                }
            }
        })


        val addressSpinner = dialog.findViewById<Spinner>(R.id.setAddress_S)
        val list = settingsSP.getIpsettingsSet()?.toMutableList()
        list?.add("Vlastní adresa")
        val addressAdapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, list as MutableList<String>)
        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        addressSpinner!!.adapter = addressAdapter
        addressSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectItem = list[position]
                if (selectItem == "Vlastní adresa"){
                    addressEditText.visibility = View.VISIBLE
                    addressEditText.hint = ""
                    newAddress = true
                }
                else{
                    addressEditText.visibility = View.GONE
                    newAddress=false
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }




        dialog.findViewById<Button>(R.id.saveSetAddress_BTN).setOnClickListener {

            val addressToSave:String = if (newAddress){
                addressEditText.text.toString()
            } else{
                addressSpinner.selectedItem.toString()
            }
            Log.d("test",addressToSave)
            val comFun = CommunicationFunction()
            comFun.testConnectionToServer(addressToSave, object: CommunicationFunction.VolleyStringResponse {
                override fun onSuccess() {
                    settingsSP.saveIPsettings(addressToSave)
                    settingsSP.addAddressToIpsettingsSet(addressToSave)
                    binding.actualIPTV.text= settingsSP.getIPsettings()
                    binding.stateActualTV.text = resources.getString(R.string.state_reachable_setting)
                    binding.stateActualTV.setBackgroundColor(Color.GREEN)
                    Toast.makeText(requireContext(),"Connection OK", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }

                override fun onError() {
                    Toast.makeText(requireContext(),"No Connection", Toast.LENGTH_LONG).show()
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