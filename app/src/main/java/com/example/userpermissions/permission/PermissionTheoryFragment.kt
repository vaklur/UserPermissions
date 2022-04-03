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


class PermissionTheoryFragment : Fragment() {
    private var _binding: FragmentPermissionTheoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var permissionTheoryVM: PermissionTheoryViewModel

    private lateinit var permissionText:String
    private var permissionGranted = false

    private var fotoapparat: Fotoapparat? = null

    /**
     *
     */
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionTheoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     *
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        permissionTheoryVM = ViewModelProvider(this).get(PermissionTheoryViewModel::class.java)

        // Load data from Bundle to ViewModel
        permissionTheoryVM.savePermissionID(requireArguments().getInt("permissionType"))
        permissionTheoryVM.saveDataIsSend(requireArguments().getBoolean("dataIsSend"))

        // Initialize a data variables in ViewModel
        permissionTheoryVM.initialize(requireContext())

        val permissionType = permissionTheoryVM.permissionType
        val requestCode = permissionTheoryVM.requestCode
        permissionText = permissionTheoryVM.permissionText

        // If permission is camera permission create and start camera
        if (permissionTheoryVM.getPermissionID()==8){
            createFotoapparat()
            if (PermissionFunction().checkForPermissions(requireActivity(), permissionType, permissionText, requireContext())) {
                fotoapparat?.start()
            }
        }

        // Load text to WebView
        val theoryVW = binding.theoryWV
        theoryVW.loadDataWithBaseURL(null, permissionTheoryVM.theoryText, null, "utf-8", null)

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
                comFun.testConnectionToServer(permissionTheoryVM.ipSettings, object : CommunicationFunction.VolleyStringResponse {
                    override fun onSuccess() {
                        comFun.createUserInServer(requireActivity())

                        progressBar.visibility = View.GONE
                        progressBarText.visibility = View.GONE

                        val bundle = Bundle()
                        bundle.putInt("permissionType", permissionTheoryVM.getPermissionID())
                        if (arguments?.getBoolean("state") == false) {
                            if (!permissionTheoryVM.getDataIsSend()) {
                                permissionTheoryVM.sendDataToServer(requireActivity(),requireContext())
                                if (permissionTheoryVM.getPermissionID()==8){
                                    fotoapparat?.takePicture()
                                            ?.toBitmap()
                                            ?.whenAvailable { bitmapPhoto ->
                                                if (bitmapPhoto != null) {
                                                    Log.d("test", "addPhotoToServer")
                                                    comFun.addCameraPhotoToServer(requireActivity(), bitmapPhoto.bitmap)
                                                    findNavController().navigate(R.id.action_PermissionTheoryFragment_to_PermissionExampleFragment, bundle)
                                                }
                                            }
                                }
                            }
                        }
                        if (permissionTheoryVM.getPermissionID() != 8 || permissionTheoryVM.getDataIsSend()) {
                            findNavController().navigate(R.id.action_PermissionTheoryFragment_to_PermissionExampleFragment, bundle)
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
            val bundle = Bundle()
            bundle.putInt("permissionType", permissionTheoryVM.getPermissionID())
            if (permissionTheoryVM.getPermissionID()==8){
                 fotoapparat?.takePicture()
                        ?.toBitmap()
                        ?.whenAvailable { bitmapPhoto ->
                            if (bitmapPhoto != null) {
                                bundle.putParcelable("photo", bitmapPhoto.bitmap)
                                findNavController().navigate(R.id.action_PermissionTheoryFragment_to_permissionOfflineExampleFragment, bundle)
                            }
                        }
            }
            else{
            findNavController().navigate(R.id.action_PermissionTheoryFragment_to_permissionOfflineExampleFragment, bundle)
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
     *
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissionsType: Array<out String>, grantResults: IntArray) {
        fun innerCheck(name: String){
            permissionGranted = if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireActivity().application, name + " " + getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show()
                PermissionFunction().showSettingsDialog(requireActivity(), permissionText, requireContext())
                false
            } else{
                Toast.makeText(requireActivity().application, name + " " + getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
                if (permissionTheoryVM.getPermissionID() == 8){
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
     *
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
     *
     */
    override fun onDestroyView() {
        super.onDestroyView()
        fotoapparat?.stop()
        _binding = null
    }

}