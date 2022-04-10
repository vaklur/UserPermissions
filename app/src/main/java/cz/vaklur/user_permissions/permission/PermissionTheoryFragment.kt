package cz.vaklur.user_permissions.permission

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
import androidx.navigation.fragment.findNavController
import cz.vaklur.user_permissions.R
import cz.vaklur.user_permissions.databinding.FragmentPermissionTheoryBinding
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.front

/**
 * Fragment for display a theory for selected permission and asking user for this permission.
 */
class PermissionTheoryFragment : Fragment() {
    private var _binding: FragmentPermissionTheoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var permissionVM: PermissionViewModel
    private lateinit var permissionText: String

    private var fotoapparat: Fotoapparat? = null

    /**
     * Result of request permission call, if permission is granted, call [permissionsGranted] function.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                permissionsGranted()
                Toast.makeText(
                    requireActivity().application,
                    permissionText + " " + getString(R.string.permission_granted),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                PermissionFunction().showSettingsDialog(
                    requireActivity(),
                    permissionText,
                    requireContext()
                )
                Toast.makeText(
                    requireActivity().application,
                    permissionText + " " + getString(R.string.permission_not_granted),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionTheoryBinding.inflate(inflater, container, false)
        permissionVM = ViewModelProvider(requireActivity()).get(PermissionViewModel::class.java)
        return binding.root
    }

    /**
     * When view created initialize widgets and onClickListeners.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize permission theory variables in ViewModel
        val init = permissionVM.initPermissionTexts(requireContext())
        permissionText = init.permissionText
        // Load text to WebView
        binding.theoryWV.loadDataWithBaseURL(null, init.theoryText, null, "utf-8", null)
        // On click control if permission is granted and go to permission abuse example
        binding.exampleBTN.setOnClickListener {
            requestPermission.launch(init.permissionType)
        }
    }

    /**
     * Function which, if permission is granted, checks the availability of the server.
     * If the server is available, it sends data to it and display a fragment with a practical example.
     * If the serve is unavailable, display a server offline dialog.
     */

    private fun permissionsGranted() {
        progressBarOn(true)

        // If is the camera permission, create Fotoapparat class and start camera
        if (permissionVM.getPermissionID() == 8) {
            createFotoapparat()
            fotoapparat?.start()
        }

        permissionVM.sendDataToServer(requireActivity(), requireContext())

        permissionVM.successServerCommunication.observe(viewLifecycleOwner) {
            Log.d("test", "OBSERVE")
            Log.d("test", "successServerCommunication$it")
            when (it) {
                "ok" -> {
                    progressBarOn(false)
                    // Control current destination
                    if (findNavController().currentDestination?.id != R.id.permissionTheoryFragment) findNavController().navigate(
                        R.id.permissionTheoryFragment
                    )
                    if (permissionVM.getPermissionID() == 8) {
                        takePhoto(true)
                    } else findNavController().navigate(R.id.action_permissionTheoryFragment_to_permissionExampleFragment)
                }
                "error" -> {
                    serverOfflineDialog(binding.root)
                }
            }
        }
    }

    /**
     * Dialog that appears when the server is not available.
     *
     * @param view View for display a dialog.
     */
    private fun serverOfflineDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)

        builder.setTitle(R.string.server_dialog_title)
        builder.setMessage(R.string.server_dialog_message)

        builder.setPositiveButton(R.string.server_dialog_yes) { dialog, _ ->
            dialog.dismiss()
            findNavController().navigate(R.id.settingsFragment)

        }
        builder.setNeutralButton(R.string.server_dialog_neutral) { dialog, _ ->
            dialog.dismiss()
            if (permissionVM.getPermissionID() == 8) {
                takePhoto(false)
            } else {
                findNavController().navigate(R.id.action_PermissionTheoryFragment_to_permissionOfflineExampleFragment)
            }
        }
        builder.setNegativeButton(R.string.server_dialog_no) { dialog, _ ->
            progressBarOn(false)
            dialog.dismiss()
        }

        builder.show()
    }

    /**
     * Function set the visibility of "connecting to server" progress bar
     */
    private fun progressBarOn(visible: Boolean) {
        var visibility = View.GONE
        if (visible) visibility = View.VISIBLE
        binding.theoryPB.visibility = visibility
        binding.theoryProgressBarTV.visibility = visibility
        binding.exampleBTN.isEnabled = !visible
    }

    /**
     * Function that takes the photo from camera and if the server is available, send it to the server a go to online example.
     * If the server is unavailable, save the photo to View Model.
     */
    private fun takePhoto(serverAvailability: Boolean) {
        Log.d("test", "TAKE PHOTO")
        fotoapparat?.takePicture()
            ?.toBitmap()
            ?.whenAvailable { bitmapPhoto ->
                if (bitmapPhoto != null) {
                    if (serverAvailability) {
                        if (!permissionVM.getDataIsSend()) {
                            permissionVM.sendPermissionDataToServer(
                                bitmapPhoto.bitmap
                            )
                            Log.d("test", "SEND PHOTO TO SERVER")
                        }
                        findNavController().navigate(R.id.action_permissionTheoryFragment_to_permissionExampleFragment)
                    } else {
                        permissionVM.savePhoto(bitmapPhoto.bitmap)
                        findNavController().navigate(R.id.action_PermissionTheoryFragment_to_permissionOfflineExampleFragment)
                    }
                }
                Log.d("test", "bitmap is null")
            }
    }

    /**
     * Create camera class for using it.
     */
    private fun createFotoapparat() {
        fotoapparat = Fotoapparat(
            context = requireContext(),
            view = binding.theoryCW,
            scaleType = ScaleType.CenterCrop,
            lensPosition = front(),
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