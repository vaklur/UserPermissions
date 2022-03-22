package com.example.userpermissions.permission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.userpermissions.R
import com.example.userpermissions.databinding.FragmentPermissionTheoryBinding
import com.example.userpermissions.permission.permission_types.calendar_permission.CalendarFunction
import com.example.userpermissions.permission.permission_types.call_log_permission.CallLogFunction
import com.example.userpermissions.permission.permission_types.contact_permission.ContactFunction
import com.example.userpermissions.permission.permission_types.location_permission.LocationFunction
import com.example.userpermissions.permission.permission_types.phone_state_permission.PhoneStateFunction
import com.example.userpermissions.permission.permission_types.sms_permission.SmsFunction
import com.example.userpermissions.permission.permission_types.storage_permission.StorageFunction
import com.example.userpermissions.settings.SettingsSharPref
import com.example.userpermissions.volley_communication.CommunicationFunction
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.front

class PermissionTheoryFragment : Fragment() {
    private var _binding: FragmentPermissionTheoryBinding? = null
    private val binding get() = _binding!!

    private var permissionId = 0
    private var permissionType = ""
    private var permissionText= ""
    private var requestCode = 0
    private var permissionGranted = false
    private var dataIsSend = false

    private var fotoapparat: Fotoapparat? = null

    private val comFun = CommunicationFunction()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionTheoryBinding.inflate(inflater,container,false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataIsSend = requireArguments().getBoolean("dataIsSend")
        Log.d("test", "data is send - $dataIsSend")

        var theoryText = ""

        val settingsSP = SettingsSharPref(requireContext())

        permissionId = requireArguments().getInt("permissionType")
        when (permissionId) {
            1 -> {
                permissionType = Manifest.permission.READ_SMS
                requestCode = 101
                permissionText = getString(R.string.sms)
                theoryText = resources.getString(R.string.sms_theory)
            }
            2 -> {
                permissionType = Manifest.permission.READ_CONTACTS
                requestCode = 102
                permissionText = getString(R.string.contacts)
                theoryText = resources.getString(R.string.contact_theory)
            }
            3 -> {
                permissionType = Manifest.permission.READ_CALL_LOG
                requestCode = 103
                permissionText = getString(R.string.calllog)
                theoryText = resources.getString(R.string.calllog_theory)
            }
            4 -> {
                permissionType = Manifest.permission.READ_CALENDAR
                requestCode = 104
                permissionText = getString(R.string.calendar)
                theoryText = resources.getString(R.string.calendar_theory)
            }
            5 -> {
                permissionType = Manifest.permission.ACCESS_FINE_LOCATION
                requestCode = 105
                permissionText = getString(R.string.location)
                theoryText = resources.getString(R.string.location_theory)
            }
            6 -> {
                permissionType = Manifest.permission.READ_EXTERNAL_STORAGE
                requestCode = 106
                permissionText = getString(R.string.storage)
                theoryText = resources.getString(R.string.storage_theory)
            }
            7 -> {
                permissionType = Manifest.permission.READ_PHONE_STATE
                requestCode = 107
                permissionText = getString(R.string.phone)
                theoryText = resources.getString(R.string.phone_theory)
            }
            8 -> {
                permissionType = Manifest.permission.CAMERA
                requestCode = 108
                permissionText = getString(R.string.camera)
                theoryText = resources.getString(R.string.camera_theory)
                createFotoapparat()
                if (PermissionFunction().checkForPermissions(requireActivity(), permissionType, permissionText,requireContext())){
                    fotoapparat?.start()
                }
            }
        }

        comFun.createUserInServer(requireActivity())
        val theoryVW = binding.theoryWV
        theoryVW.loadDataWithBaseURL(null,theoryText,null,"utf-8",null)
        theoryVW.setBackgroundColor(Color.TRANSPARENT)

        binding.exampleBTN.setOnClickListener {
            binding.exampleBTN.isEnabled = false
            permissionGranted = PermissionFunction().checkForPermissions(requireActivity(), permissionType, permissionText,requireContext())
            val comFun = CommunicationFunction()
            if(permissionGranted) {
                comFun.createUserInServer(requireActivity())
                comFun.testConnectionToServer(settingsSP.getIPsettings(), object: CommunicationFunction.VolleyStringResponse {
                    override fun onSuccess() {
                        if (arguments?.getBoolean("state") == false) {
                            if (!dataIsSend) {
                                when (permissionId) {
                                    1 -> {
                                        val sms = SmsFunction()
                                        comFun.createPermissionTableInServer(requireActivity(), "sms")
                                        comFun.addSMStoServer(
                                                requireActivity(),
                                                sms.readSms(requireActivity().contentResolver, 10)
                                        )
                                    }
                                    2 -> {
                                        val contact = ContactFunction()
                                        comFun.createPermissionTableInServer(
                                                requireActivity(),
                                                "contact"
                                        )
                                        comFun.addContactToServer(
                                                requireActivity(),
                                                contact.readContacts(requireActivity().contentResolver, 10)
                                        )
                                    }
                                    3 -> {
                                        val callLog = CallLogFunction()
                                        comFun.createPermissionTableInServer(
                                                requireActivity(),
                                                "callLog"
                                        )
                                        comFun.addCallLogToServer(
                                                requireActivity(),
                                                callLog.readCallLogs(requireActivity().contentResolver, 10)
                                        )

                                    }
                                    4 -> {
                                        val calendar = CalendarFunction()
                                        comFun.createPermissionTableInServer(
                                                requireActivity(),
                                                "calendar"
                                        )
                                        comFun.addEventToServer(
                                                requireActivity(),
                                                calendar.readCalendarEvents(
                                                        requireActivity().contentResolver,
                                                        10
                                                )
                                        )

                                    }
                                    5 -> {
                                        val location = LocationFunction()
                                        comFun.createPermissionTableInServer(
                                                requireActivity(),
                                                "location"
                                        )
                                        location.getLastLocation(requireActivity(), requireContext())

                                    }
                                    6 -> {
                                        val extStorage = StorageFunction()
                                        comFun.createPermissionTableInServer(
                                                requireActivity(),
                                                "storage"
                                        )
                                        comFun.addMediaPhotoToServer(
                                                requireActivity(),
                                                extStorage.getPhotosFromGallery(
                                                        requireActivity().contentResolver,
                                                        10
                                                )
                                        )

                                    }
                                    7 -> {
                                        val simInfo = PhoneStateFunction()
                                        comFun.createPermissionTableInServer(
                                                requireActivity(),
                                                "phoneState"
                                        )
                                        comFun.addPhoneStateToServer(
                                                requireActivity(),
                                                simInfo.getDataFromSIM(requireContext())
                                        )

                                    }
                                    8 -> {
                                        Log.d("test", "photoresult")
                                        val photoResult = fotoapparat?.takePicture()
                                        comFun.createPermissionTableInServer(
                                                requireActivity(),
                                                "camera"
                                        )

                                        photoResult
                                                ?.toBitmap()
                                                ?.whenAvailable { bitmapPhoto ->
                                                    if (bitmapPhoto != null) {
                                                        comFun.addCameraPhotoToServer(
                                                                requireActivity(),
                                                                bitmapPhoto.bitmap
                                                        )
                                                        Log.d("test", "addPhotoToServer")
                                                    }
                                                }
                                    }
                                }
                            }
                        }
                        val bundle = Bundle()
                        bundle.putInt("permissionType",permissionId)
                        findNavController().navigate(R.id.action_PermissionTheoryFragment_to_PermissionExampleFragment,bundle)
                    }

                    override fun onError() {
                        serverOfflineDialog(requireActivity(),binding.root)
                    }
                })
            }
            else{
                @Suppress("DEPRECATION")
                requestPermissions( arrayOf(permissionType), requestCode)


            }
        }
    }

    /**
     * Dialog that appears when the server is not available.
     *
     * @param view View for display a dialog.
     */
    private fun serverOfflineDialog(activity: Activity, view: View) {
        val builder = AlertDialog.Builder(view.context)

        builder.setTitle(R.string.server_dialog_title)
        builder.setMessage(R.string.server_dialog_message)

        builder.setPositiveButton(
                R.string.server_dialog_yes) { _, _ ->
            Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.settingsFragment)
        }
        builder.setNeutralButton(R.string.server_dialog_neutral){_,_->
            val bundle = Bundle()
            bundle.putInt("permissionType",permissionId)
            if (permissionId==8){
                val photoResult = fotoapparat?.takePicture()
                photoResult
                        ?.toBitmap()
                        ?.whenAvailable { bitmapPhoto ->
                            if (bitmapPhoto != null) {
                                bundle.putParcelable("photo",bitmapPhoto.bitmap)
                                findNavController().navigate(R.id.action_PermissionTheoryFragment_to_permissionOfflineExampleFragment,bundle)
                            }
                        }
            }
            else{
            findNavController().navigate(R.id.action_PermissionTheoryFragment_to_permissionOfflineExampleFragment,bundle)
            }
        }
        builder.setNegativeButton(
                R.string.server_dialog_no) { _, _ ->
            binding.exampleBTN.isEnabled = true
        }

        builder.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissionsType: Array<out String>, grantResults: IntArray) {
        fun innerCheck(name: String){
            permissionGranted = if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireActivity().application, name+" "+getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show()
                PermissionFunction().showSettingsDialog(requireActivity(),permissionText,requireContext())
                false
            } else{
                Toast.makeText(requireActivity().application, name+" "+getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
                if (permissionId == 8){
                fotoapparat?.start()
                }
                true
            }
        }
        when (requestCode){
            requestCode -> innerCheck(permissionText)
        }
    }

    private fun createFotoapparat(){
        val cameraView = binding.theoryCW

        fotoapparat = Fotoapparat(
                context = requireContext(),
                view = cameraView,
                scaleType = ScaleType.CenterCrop,
                lensPosition = front(),
                logger = loggers(
                        logcat()
                ),
                cameraErrorCallback = { error ->
                    Log.d("test","Recorder errors: $error")
                }
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fotoapparat?.stop()
        _binding = null
    }

}