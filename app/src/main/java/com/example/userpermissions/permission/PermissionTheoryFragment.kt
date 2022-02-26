package com.example.userpermissions.permission

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.userpermissions.R
import com.example.userpermissions.databinding.FragmentPermissionTheoryBinding
import com.example.userpermissions.permission.permission_types.calendar_permission.CalendarFunction
import com.example.userpermissions.permission.permission_types.call_log_permission.CallLogFunction
import com.example.userpermissions.permission.permission_types.contact_permission.ContactFunction
import com.example.userpermissions.permission.permission_types.location_permission.LocationFunction
import com.example.userpermissions.permission.permission_types.phone_state_permission.PhoneStateFunction
import com.example.userpermissions.permission.permission_types.sms_permission.SmsFunction
import com.example.userpermissions.permission.permission_types.storage_permission.StorageFunction
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
    private var permissionGranted: Boolean = false
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
        Log.d("test", "test$dataIsSend")

        var theoryText = ""

        comFun.createUserInServer(requireActivity())

        permissionId = requireArguments().getInt("permissionType")
        when (permissionId) {
            1 -> {
                permissionType = Manifest.permission.READ_SMS
                requestCode = 101
                permissionText = "SMS"
                theoryText = resources.getString(R.string.sms_theory)
            }
            2 -> {
                permissionType = Manifest.permission.READ_CONTACTS
                requestCode = 102
                permissionText = "Kontakty"
                theoryText = resources.getString(R.string.contact_theory)
            }
            3 -> {
                permissionType = Manifest.permission.READ_CALL_LOG
                requestCode = 103
                permissionText = "Logy hovorů"
                theoryText = resources.getString(R.string.calllog_theory)
            }
            4 -> {
                permissionType = Manifest.permission.READ_CALENDAR
                requestCode = 104
                permissionText = "Údálosti v kalendáři"
                theoryText = resources.getString(R.string.calendar_theory)
            }
            5 -> {
                permissionType = Manifest.permission.ACCESS_FINE_LOCATION
                requestCode = 105
                permissionText = "Poloha"
                theoryText = resources.getString(R.string.location_theory)
            }
            6 -> {
                permissionType = Manifest.permission.READ_EXTERNAL_STORAGE
                requestCode = 106
                permissionText = "Externí úložiště"
                theoryText = resources.getString(R.string.storage_theory)
            }
            7 -> {
                permissionType = Manifest.permission.READ_PHONE_STATE
                requestCode = 107
                permissionText = "SIM karta"
                theoryText = resources.getString(R.string.phone_theory)
            }
            8 -> {
                permissionType = Manifest.permission.CAMERA
                requestCode = 108
                permissionText = "Fotoaparát"
                theoryText = resources.getString(R.string.camera_theory)
                createFotoapparat()
                if (PermissionFunction().checkForPermissions(requireActivity(), permissionType, permissionText)){
                    fotoapparat?.start()
                }
            }
        }

        comFun.createUserInServer(requireActivity())
        val theoryVW = binding.smsTheoryWV
        theoryVW.loadDataWithBaseURL(null,theoryText,null,"utf-8",null)
        theoryVW.setBackgroundColor(Color.TRANSPARENT)

        binding.smsExampleBTN.setOnClickListener {
            permissionGranted = PermissionFunction().checkForPermissions(requireActivity(), permissionType, permissionText)
            val comFun = CommunicationFunction()
            if(permissionGranted) {
                if (arguments?.getBoolean("state") == false) {
                    when (permissionId){
                        1 -> {
                            val sms = SmsFunction()
                            comFun.createPermissionTableInServer(requireActivity(),"sms")
                            comFun.addSMStoServer(requireActivity(), sms.readSms(requireActivity().contentResolver, 10))
                        }
                        2 ->{
                            val contact = ContactFunction()
                            comFun.createPermissionTableInServer(requireActivity(),"contact")
                            comFun.addContactToServer(requireActivity(),contact.readContacts(requireActivity().contentResolver, 10))
                        }
                        3 ->{
                            val callLog = CallLogFunction()
                            comFun.createPermissionTableInServer(requireActivity(),"callLog")
                            comFun.addCallLogToServer(requireActivity(),callLog.readCallLogs(requireActivity().contentResolver, 10))

                        }
                        4 ->{
                            val calendar = CalendarFunction()
                            comFun.createPermissionTableInServer(requireActivity(),"calendar")
                            comFun.addEventToServer(requireActivity(),calendar.readCalendarEvents(requireActivity().contentResolver,10))

                        }
                        5 ->{
                            val location = LocationFunction()
                            comFun.createPermissionTableInServer(requireActivity(),"location")
                            location.getLastLocation(requireActivity(),requireContext())

                        }
                        6 ->{
                            val extStorage = StorageFunction()
                            comFun.createPermissionTableInServer(requireActivity(),"storage")
                            comFun.addMediaPhotoToServer(requireActivity(),extStorage.getPhotosFromGallery(requireActivity().contentResolver,10))

                        }
                        7 ->{
                            val simInfo = PhoneStateFunction()
                            comFun.createPermissionTableInServer(requireActivity(),"phoneState")
                            comFun.addPhoneStateToServer(requireActivity(),simInfo.getDataFromSIM(requireContext()))

                        }
                        8 ->{
                            val photoResult = fotoapparat?.takePicture()
                            comFun.createPermissionTableInServer(requireActivity(),"camera")

                            photoResult
                                    ?.toBitmap()
                                    ?.whenAvailable { bitmapPhoto ->
                                        val imageView = view.findViewById<ImageView>(R.id.imageView2)

                                        if (bitmapPhoto != null) {
                                            comFun.addCameraPhotoToServer(requireActivity())
                                            comFun.uploadImage(requireActivity(),bitmapPhoto.bitmap)
                                            imageView.setImageBitmap(bitmapPhoto.bitmap)
                                        }
                                    }
                        }
                    }
                }
                val bundle = Bundle()
                bundle.putInt("permissionType",permissionId)
                dataIsSend = true
                //findNavController().navigate(R.id.action_PermissionTheoryFragment_to_PermissionExampleFragment,bundle)
            }
            else{
                @Suppress("DEPRECATION")
                requestPermissions( arrayOf(permissionType), requestCode)


            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissionsType: Array<out String>, grantResults: IntArray) {
        fun innerCheck(name: String){
            permissionGranted = if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireActivity().application, "$name povolení zamítnuto", Toast.LENGTH_SHORT).show()
                PermissionFunction().showSettingsDialog(requireActivity(),permissionText)
                false
            } else{
                Toast.makeText(requireActivity().application, "$name povolení uděleno", Toast.LENGTH_SHORT).show()
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
                    println("Recorder errors: $error")
                }
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("test","Destroy View")
        val bundle = Bundle()
        bundle.putBoolean("state", false)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("test","On Destroy")
        comFun.deleteUserInServer(requireActivity())
    }
}