package com.example.userpermissions.permission.offline_example

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.example.userpermissions.R
import com.example.userpermissions.databinding.FragmentPermissionOfflineExampleBinding
import com.example.userpermissions.permission.data.PermissionDatasource
import com.example.userpermissions.permission.permission_types.calendar_permission.CalendarFunction
import com.example.userpermissions.permission.permission_types.call_log_permission.CallLogFunction
import com.example.userpermissions.permission.permission_types.contact_permission.ContactFunction
import com.example.userpermissions.permission.permission_types.location_permission.LocationFunction
import com.example.userpermissions.permission.permission_types.phone_state_permission.PhoneStateFunction
import com.example.userpermissions.permission.permission_types.sms_permission.MySmsAdapter
import com.example.userpermissions.permission.permission_types.sms_permission.SmsFunction
import com.example.userpermissions.permission.permission_types.storage_permission.StorageFunction
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.front


class PermissionOfflineExampleFragment : Fragment() {

    private var _binding: FragmentPermissionOfflineExampleBinding? = null
    private val binding get() = _binding!!

    private var fotoapparat: Fotoapparat? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentPermissionOfflineExampleBinding.inflate(inflater,container,false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val permissionId = requireArguments().getInt("permissionType")

        when (permissionId) {
            1 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.sms)))
                val sms = SmsFunction()
                val smsRecyclerView = binding.exampleOffRV
                val smsAdapter = MySmsAdapter(sms.readSms(requireActivity().contentResolver, 10))
                smsRecyclerView.adapter = smsAdapter
                smsRecyclerView.setHasFixedSize(true)
            }
            2 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.contacts)))
                val contact = ContactFunction()
                contact.readContacts(requireActivity().contentResolver, 10)

            }
            3 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.calllog)))
                val callLog = CallLogFunction()
                callLog.readCallLogs(requireActivity().contentResolver, 10)
            }
            4 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.calendar)))
                val calendar = CalendarFunction()
                calendar.readCalendarEvents(requireActivity().contentResolver, 10)

            }
            5 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.location)))
                val location = LocationFunction()
                //location.getLastLocation(requireActivity(), requireContext())

            }
            6 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.storage)))
                val extStorage = StorageFunction()
                extStorage.getPhotosFromGallery(requireActivity().contentResolver, 10)
            }
            7 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.phone)))
                val simInfo = PhoneStateFunction()
                simInfo.getDataFromSIM(requireContext())
            }
            8 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.camera)))
                createFotoapparat()
                val photoResult = fotoapparat?.takePicture()

                photoResult
                    ?.toBitmap()
                    ?.whenAvailable { bitmapPhoto ->
                        if (bitmapPhoto != null) {
                            bitmapPhoto.bitmap
                            Log.d("test", "addPhotoToServer")
                        }
                    }
            }
        }

        binding.theoryOffBTN.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("permissionType",permissionId)
            bundle.putBoolean("dataIsSend",false)
            findNavController().navigate(R.id.action_permissionOfflineExampleFragment_to_PermissionTheoryFragment,bundle)
        }
    }

    private fun createFotoapparat(){
        val cameraView = binding.exampleCW

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
        _binding=null
    }
}