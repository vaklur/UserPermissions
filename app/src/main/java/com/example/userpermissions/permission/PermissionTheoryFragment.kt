package com.example.userpermissions.permission

import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.userpermissions.R
import com.example.userpermissions.databinding.FragmentPermissionTheoryBinding
import com.example.userpermissions.volley_communication.CommunicationFunction
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.front

/**
 * Fragment for display a theory for selected permission and asking user for this permission.
 */
class PermissionTheoryFragment : Fragment() {
    private var _binding: FragmentPermissionTheoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var permissionVM: PermissionViewModel
    private lateinit var serverIpAddress:String
    private lateinit var permissionText:String

    private var fotoapparat: Fotoapparat? = null

    /**
     * Result of request permission call, if permission is granted, call [permissionsGranted] function.
     */
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            permissionsGranted()
            Toast.makeText(requireActivity().application, permissionText + " " + getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
        }
        else {
            PermissionFunction().showSettingsDialog(requireActivity(), permissionText, requireContext())
            Toast.makeText(requireActivity().application, permissionText + " " + getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionTheoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * When view created initialize widgets and onClickListeners.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        permissionVM = ViewModelProvider(requireActivity()).get(PermissionViewModel::class.java)

        // Load data from Bundle to ViewModel
        if (permissionVM.getPermissionID()==0){
            permissionVM.savePermissionID(requireArguments().getInt("permissionType"))
        }

        // Initialize a data variables in ViewModel
        val init = permissionVM.initialize(requireContext())

        permissionText = init.permissionText
        serverIpAddress = init.serverAddress

        // Load text to WebView
        binding.theoryWV.loadDataWithBaseURL(null, init.theoryText, null, "utf-8", null)

        // On click go to permission abuse example
        binding.exampleBTN.setOnClickListener {
            requestPermission.launch(init.permissionType)
        }
    }

    /**
     * Function which, if permission is granted, checks the availability of the server.
     * If the server is available, it sends data to it and display a fragment with a practical example.
     * If the serve is unavailable, display a server offline dialog.
     */
    private fun permissionsGranted (){
        val progressBar = binding.theoryPB
        val progressBarText = binding.theoryProgressBarTV
        progressBar.visibility = View.VISIBLE
        progressBarText.visibility = View.VISIBLE
        binding.exampleBTN.isEnabled = false

        if (permissionVM.getPermissionID() == 8){
            Log.d("test", "camera start")
            createFotoapparat()
            fotoapparat?.start()
        }

        val comFun = CommunicationFunction()
        comFun.testConnectionToServer(serverIpAddress, object : CommunicationFunction.VolleyStringResponse {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onSuccess() {
                progressBar.visibility = View.GONE
                progressBarText.visibility = View.GONE

                if (arguments?.getBoolean("state") == false) {
                    if (!permissionVM.getDataIsSend()) {
                        permissionVM.sendDataToServer(requireActivity(),requireContext())
                        if (permissionVM.getPermissionID()==8){
                            fotoapparat?.takePicture()
                                ?.toBitmap()
                                ?.whenAvailable { bitmapPhoto ->
                                    if (bitmapPhoto != null) {
                                        Log.d("test", "addPhotoToServer")
                                        comFun.addCameraPhotoToServer(requireActivity(), bitmapPhoto.bitmap)
                                        findNavController().navigate(R.id.action_PermissionTheoryFragment_to_PermissionExampleFragment)
                                    }
                                }
                        }
                    }
                }
                if (permissionVM.getPermissionID() != 8 || permissionVM.getDataIsSend()) {
                    findNavController().navigate(R.id.action_PermissionTheoryFragment_to_PermissionExampleFragment)
                }
            }
            override fun onError() {
                serverOfflineDialog(requireActivity(), binding.root)
            }
        })
    }

    /**
     * Dialog that appears when the server is not available.
     *
     * @param activity Fragment activity.
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
        builder.setNeutralButton(R.string.server_dialog_neutral){ _, _->
            if (permissionVM.getPermissionID()==8){
                 fotoapparat?.takePicture()
                        ?.toBitmap()
                        ?.whenAvailable { bitmapPhoto ->
                            if (bitmapPhoto != null) {
                                    permissionVM.savePhoto(bitmapPhoto.bitmap)
                                findNavController().navigate(R.id.action_PermissionTheoryFragment_to_permissionOfflineExampleFragment)
                            }
                        }
            }
            else{
            findNavController().navigate(R.id.action_PermissionTheoryFragment_to_permissionOfflineExampleFragment)
            }
        }
        builder.setNegativeButton(
                R.string.server_dialog_no) { _, _ ->
            binding.exampleBTN.isEnabled = true
            binding.theoryPB.visibility = View.GONE
            binding.theoryProgressBarTV.visibility = View.GONE
        }

        builder.show()
    }

    /**
     * Create camera class for using it.
     */
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
                    Log.d("test", "Recorder errors: $error")
                }
        )
    }

    /**
     * When destroy fragment view stop the camera.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        fotoapparat?.stop()
        _binding = null
    }


}