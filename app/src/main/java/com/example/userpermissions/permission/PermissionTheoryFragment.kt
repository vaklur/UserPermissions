package com.example.userpermissions.permission

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    private lateinit var permissionText:String

    private var permissionGranted = false
    private var fotoapparat: Fotoapparat? = null


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
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        permissionVM = ViewModelProvider(requireActivity()).get(PermissionViewModel::class.java)

        // Load data from Bundle to ViewModel
        if (permissionVM.getPermissionID()==0){
        permissionVM.savePermissionID(requireArguments().getInt("permissionType"))}

        // Initialize a data variables in ViewModel
        permissionVM.initialize(requireContext())

        val permissionType = permissionVM.permissionType
        val requestCode = permissionVM.requestCode
        permissionText = permissionVM.permissionText

        // If permission is camera permission create and start camera
        if (permissionVM.getPermissionID()==8){
            createFotoapparat()
            if (PermissionFunction().checkForPermissions(requireActivity(), permissionType, permissionText, requireContext())) {
                fotoapparat?.start()
            }
        }

        // Load text to WebView
        val theoryVW = binding.theoryWV
        theoryVW.loadDataWithBaseURL(null, permissionVM.theoryText, null, "utf-8", null)

        // On click go to permission abuse example
        binding.exampleBTN.setOnClickListener {
            val progressBar = binding.theoryPB
            val progressBarText = binding.theoryProgressBarTV
            // Check if permission granted
            permissionGranted = PermissionFunction().checkForPermissions(requireActivity(), permissionType, permissionText, requireContext())
            if(permissionGranted) {
                progressBar.visibility = View.VISIBLE
                progressBarText.visibility = View.VISIBLE
                binding.exampleBTN.isEnabled = false
                val comFun = CommunicationFunction()
                comFun.createUserInServer(requireActivity())
                comFun.testConnectionToServer(permissionVM.ipSettings, object : CommunicationFunction.VolleyStringResponse {
                    override fun onSuccess() {
                        comFun.createUserInServer(requireActivity())

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
            else{
                @Suppress("DEPRECATION")
                requestPermissions(arrayOf(permissionType), requestCode)
            }
        }
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
     * Callback for the result from requesting permissions. Based on the result show dialog for permission request or do nothing.
     *
     * @param requestCode Code for request permission.
     * @param permissionsType Type of requested permission.
     * @param grantResults The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissionsType: Array<out String>, grantResults: IntArray) {
        fun innerCheck(name: String){
            permissionGranted = if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireActivity().application, name + " " + getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show()
                PermissionFunction().showSettingsDialog(requireActivity(), permissionText, requireContext())
                false
            } else{
                Toast.makeText(requireActivity().application, name + " " + getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
                if (permissionVM.getPermissionID() == 8){
                fotoapparat?.start()
                }
                true
            }
        }
        when (requestCode){
            requestCode -> innerCheck(permissionText)
        }
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