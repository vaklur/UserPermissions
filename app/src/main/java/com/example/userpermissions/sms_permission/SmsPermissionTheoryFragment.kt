package com.example.userpermissions.sms_permission

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.userpermissions.R
import com.example.userpermissions.databinding.FragmentSmsPermissionTheoryBinding
import com.example.userpermissions.permission.PermissionFunction
import com.example.userpermissions.volley_communication.CommunicationFunction

class SmsPermissionTheoryFragment : Fragment() {
    private var _binding: FragmentSmsPermissionTheoryBinding? = null
    private val binding get() = _binding!!

    private val smsPermission: String = Manifest.permission.READ_SMS
    private val smsRequestCode: Int = 101
    private var smsPermissionGranted: Boolean = false

    private val comFun = CommunicationFunction()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSmsPermissionTheoryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        comFun.createUserInServer(requireActivity())
        val theoryVW = binding.smsTheoryWV
        theoryVW.loadDataWithBaseURL(null,resources.getString(R.string.sms_theory),null,"utf-8",null)
        theoryVW.setBackgroundColor(Color.TRANSPARENT)

        binding.smsExampleBTN.setOnClickListener {
            smsPermissionGranted = PermissionFunction().checkForPermissions(requireActivity(),smsPermission, "SMS")
            val sms = SmsFunction()
            val comFun = CommunicationFunction()
            if(smsPermissionGranted) {
                if (arguments?.getBoolean("state") == null) {
                    comFun.addSMStoServer(requireActivity(), sms.readSms(requireActivity().contentResolver, 10))

                }
                findNavController().navigate(R.id.action_smsPermissionTheoryFragment_to_smsPermissionExampleFragment)
            }
            else{
                @Suppress("DEPRECATION")
                requestPermissions( arrayOf(smsPermission), smsRequestCode)

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissionsType: Array<out String>, grantResults: IntArray) {
        fun innerCheck(name: String){
            smsPermissionGranted = if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireActivity().application, "$name povolení zamítnuto", Toast.LENGTH_SHORT).show()
                PermissionFunction().showSettingsDialog(requireActivity(),"SMS")
                false
            } else{
                Toast.makeText(requireActivity().application, "$name povolení uděleno", Toast.LENGTH_SHORT).show()
                true
            }
        }
        when (requestCode){
            smsRequestCode -> innerCheck("SMS")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        comFun.deleteUserInServer(requireActivity())
    }
}