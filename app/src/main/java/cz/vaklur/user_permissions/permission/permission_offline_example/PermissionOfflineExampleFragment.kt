package cz.vaklur.user_permissions.permission.permission_offline_example

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
import cz.vaklur.user_permissions.R
import cz.vaklur.user_permissions.databinding.FragmentPermissionOfflineExampleBinding
import cz.vaklur.user_permissions.permission.permission_types.calendar_permission.CalendarFunction
import cz.vaklur.user_permissions.permission.permission_types.calendar_permission.MyCalendarAdapter
import cz.vaklur.user_permissions.permission.permission_types.call_log_permission.CallLogFunction
import cz.vaklur.user_permissions.permission.permission_types.call_log_permission.MyCallLogAdapter
import cz.vaklur.user_permissions.permission.permission_types.camera_permission.MyCameraAdapter
import cz.vaklur.user_permissions.permission.permission_types.contact_permission.ContactFunction
import cz.vaklur.user_permissions.permission.permission_types.contact_permission.MyContactAdapter
import cz.vaklur.user_permissions.permission.permission_types.location_permission.LocationFunction
import cz.vaklur.user_permissions.permission.permission_types.phone_state_permission.MyPhoneStateAdapter
import cz.vaklur.user_permissions.permission.permission_types.phone_state_permission.PhoneStateFunction
import cz.vaklur.user_permissions.permission.permission_types.sms_permission.MySmsAdapter
import cz.vaklur.user_permissions.permission.permission_types.sms_permission.SmsFunction
import cz.vaklur.user_permissions.permission.permission_types.storage_permission.MyStorageAdapter
import cz.vaklur.user_permissions.permission.permission_types.storage_permission.StorageFunction
import cz.vaklur.user_permissions.permission.view_model.PermissionViewModel

/**
 * Fragment for display a offline practical example of selected permission abuse.
 */
class PermissionOfflineExampleFragment : Fragment() {

    private lateinit var permissionVM: PermissionViewModel

    private var _binding: FragmentPermissionOfflineExampleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionOfflineExampleBinding.inflate(inflater, container, false)
        permissionVM = ViewModelProvider(requireActivity()).get(PermissionViewModel::class.java)
        return binding.root
    }

    /**
     * When view created get data from ViewModel and load a recyclerView with data that would be sent to the server.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPermissionDataToRecyclerView(permissionVM.getPermissionID())

        binding.theoryOffBTN.setOnClickListener {
            findNavController().navigate(R.id.action_permissionOfflineExampleFragment_to_PermissionTheoryFragment)
        }
    }

    /**
     * Load permission data and give it to permission adapters.
     */
    private fun loadPermissionDataToRecyclerView(permissionId: Int) {
        val recyclerView = binding.exampleOffRV
        when (permissionId) {
            1 -> {
                setOfflineExampleTitle(resources.getString(R.string.sms))
                val smsMessages =
                    SmsFunction().readSms(10, requireActivity().contentResolver, requireContext())
                if (smsMessages.size != 0) {
                    recyclerView.adapter = MySmsAdapter(requireContext(), smsMessages)
                } else recyclerView.adapter = NoItemAdapter()
            }
            2 -> {
                setOfflineExampleTitle(resources.getString(R.string.contacts))
                val contacts = ContactFunction().readContacts(requireActivity().contentResolver, 10)
                if (contacts.size != 0) {
                    recyclerView.adapter = MyContactAdapter(requireContext(), contacts)
                } else recyclerView.adapter = NoItemAdapter()
            }
            3 -> {
                setOfflineExampleTitle(resources.getString(R.string.call_log))
                val callLogs = CallLogFunction().readCallLogs(
                    10,
                    requireActivity().contentResolver,
                    requireContext()
                )
                if (callLogs.size != 0) {
                    recyclerView.adapter = MyCallLogAdapter(requireContext(), callLogs)
                } else recyclerView.adapter = NoItemAdapter()

            }
            4 -> {
                setOfflineExampleTitle(resources.getString(R.string.calendar))
                val calendarEvents =
                    CalendarFunction().readCalendarEvents(requireActivity().contentResolver, 10)
                if (calendarEvents.size != 0) {
                    recyclerView.adapter = MyCalendarAdapter(requireContext(), calendarEvents)
                } else recyclerView.adapter = NoItemAdapter()

            }
            5 -> {
                setOfflineExampleTitle(resources.getString(R.string.location))
                LocationFunction().getLastLocation(requireContext(), binding.root)
            }
            6 -> {
                setOfflineExampleTitle(resources.getString(R.string.storage))
                val photos =
                    StorageFunction().getPhotosFromGallery(requireActivity().contentResolver, 10)
                if (photos.size != 0) {
                    recyclerView.adapter = MyStorageAdapter(requireContext(), photos)
                } else recyclerView.adapter = NoItemAdapter()

            }
            7 -> {
                setOfflineExampleTitle(resources.getString(R.string.phone))
                val simInformation = PhoneStateFunction().getDataFromSIM(requireContext())
                if (simInformation.phoneNumber != "") {
                    recyclerView.adapter = MyPhoneStateAdapter(simInformation)
                } else recyclerView.adapter = NoItemAdapter()
            }
            8 -> {
                setOfflineExampleTitle(resources.getString(R.string.camera))
                val photo: Bitmap? = permissionVM.getPhoto()
                if (photo != null) {
                    recyclerView.adapter = MyCameraAdapter(photo)
                } else recyclerView.adapter = NoItemAdapter()
            }
        }
        recyclerView.setHasFixedSize(true)
    }

    /**
     * Set the title of offline example fragment.
     */
    private fun setOfflineExampleTitle(title: String) {
        binding.exampleOffTV.text = String.format(
            resources.getString(
                R.string.example_off_text,
                title
            )
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}