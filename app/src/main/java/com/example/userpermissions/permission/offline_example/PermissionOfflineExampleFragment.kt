package com.example.userpermissions.permission.offline_example

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.userpermissions.R
import com.example.userpermissions.databinding.FragmentPermissionOfflineExampleBinding
import com.example.userpermissions.permission.PermissionViewModel
import com.example.userpermissions.permission.permission_types.calendar_permission.CalendarFunction
import com.example.userpermissions.permission.permission_types.calendar_permission.MyCalendarAdapter
import com.example.userpermissions.permission.permission_types.call_log_permission.CallLogFunction
import com.example.userpermissions.permission.permission_types.call_log_permission.MyCallLogAdapter
import com.example.userpermissions.permission.permission_types.camera_permission.MyCameraAdapter
import com.example.userpermissions.permission.permission_types.contact_permission.ContactFunction
import com.example.userpermissions.permission.permission_types.contact_permission.MyContactAdapter
import com.example.userpermissions.permission.permission_types.location_permission.LocationFunction
import com.example.userpermissions.permission.permission_types.phone_state_permission.MyPhoneStateAdapter
import com.example.userpermissions.permission.permission_types.phone_state_permission.PhoneStateFunction
import com.example.userpermissions.permission.permission_types.sms_permission.MySmsAdapter
import com.example.userpermissions.permission.permission_types.sms_permission.SmsFunction
import com.example.userpermissions.permission.permission_types.storage_permission.MyStorageAdapter
import com.example.userpermissions.permission.permission_types.storage_permission.StorageFunction

/**
 * Fragment for display a offline practical example of selected permission abuse.
 */
class PermissionOfflineExampleFragment : Fragment() {

    private lateinit var permissionVM: PermissionViewModel

    private var _binding: FragmentPermissionOfflineExampleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentPermissionOfflineExampleBinding.inflate(inflater,container,false)
        return binding.root
    }

    /**
     * When view created get data from ViewModel and load a recyclerView with data that would be sent to the server.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        permissionVM = ViewModelProvider(requireActivity()).get(PermissionViewModel::class.java)

        val permissionId = permissionVM.getPermissionID()
        val recyclerView = binding.exampleOffRV
        when (permissionId) {
            1 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.sms)))
                val sms = SmsFunction()
                val smsMessages = sms.readSms(10,requireActivity().contentResolver,requireContext())
                if (smsMessages.size!=0) {
                    val smsAdapter = MySmsAdapter(requireContext(),smsMessages)
                    recyclerView.adapter = smsAdapter
                }
                else {
                    val smsAdapter = NoItemAdapter()
                    recyclerView.adapter = smsAdapter
                }
            }
            2 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.contacts)))
                val contact = ContactFunction()
                val contacts = contact.readContacts(requireActivity().contentResolver, 10)
                if (contacts.size!=0){
                val contactAdapter = MyContactAdapter(requireContext(),contacts)
                recyclerView.adapter = contactAdapter
                }
                else{
                    val contactAdapter = NoItemAdapter()
                    recyclerView.adapter = contactAdapter
                }
            }
            3 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.call_log)))
                val callLog = CallLogFunction()
                val callLogs = callLog.readCallLogs(10,requireActivity().contentResolver,requireContext())
                if (callLogs.size!=0){
                    val callLogAdapter = MyCallLogAdapter(requireContext(),callLogs)
                    recyclerView.adapter = callLogAdapter
                }
                else{
                    val callLogAdapter = NoItemAdapter()
                    recyclerView.adapter = callLogAdapter
                }
            }
            4 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.calendar)))
                val calendar = CalendarFunction()
                val calendarEvents = calendar.readCalendarEvents(requireActivity().contentResolver, 10)
                if (calendarEvents.size!=0){
                    val calendarAdapter = MyCalendarAdapter(requireContext(),calendar.readCalendarEvents(requireActivity().contentResolver, 10))
                    recyclerView.adapter = calendarAdapter
                }
                else{
                    val calendarAdapter = NoItemAdapter()
                    recyclerView.adapter = calendarAdapter
                }


            }
            5 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.location)))
                val location = LocationFunction()
                location.getLastLocation(requireContext(),binding.root)

            }
            6 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.storage)))
                val extStorage = StorageFunction()
                val photos = extStorage.getPhotosFromGallery(requireActivity().contentResolver, 10)
                if (photos.size!=0){
                val extStorageAdapter = MyStorageAdapter(requireContext(),photos)
                recyclerView.adapter = extStorageAdapter
                }
                else{
                    val extStorageAdapter = NoItemAdapter()
                    recyclerView.adapter = extStorageAdapter
                }
            }
            7 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.phone)))
                val simInfo = PhoneStateFunction()
                val simInformation = simInfo.getDataFromSIM(requireContext())
                if (simInformation.phoneNumber!=""){
                    val simInfoAdapter = MyPhoneStateAdapter(simInformation)
                    recyclerView.adapter = simInfoAdapter
                }
                else {
                    val simInfoAdapter = NoItemAdapter()
                    recyclerView.adapter = simInfoAdapter
                }
            }
            8 -> {
                binding.exampleOffTV.text = String.format(resources.getString(R.string.example_off_text,resources.getString(R.string.camera)))
                val photo: Bitmap? = permissionVM.getPhoto()
                if (photo!=null){
                val cameraAdapter = MyCameraAdapter(photo)
                recyclerView.adapter = cameraAdapter}
                else{
                    val cameraAdapter = NoItemAdapter()
                    recyclerView.adapter = cameraAdapter
                }
            }
        }
        recyclerView.setHasFixedSize(true)

        binding.theoryOffBTN.setOnClickListener {
            findNavController().navigate(R.id.action_permissionOfflineExampleFragment_to_PermissionTheoryFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}