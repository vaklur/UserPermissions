package com.example.userpermissions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.userpermissions.databinding.FragmentMainMenuBinding
import com.example.userpermissions.volley_communication.CommunicationFunction

/**
 * A fragment that displays the main menu for navigating in the application.
 */
class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    /**
     * Create a view of fragment with view bindings.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater,container,false)
        return binding.root
    }

    /**
     * This method create the view of fragment.
     * <p>
     * Initializes the button onClickListeners to access other fragments.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Go to PermissionFragment and delete user in server
        binding.permissionsBTN.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainMenuFragment_to_permissionFragment)
        }
        // Go to SettingsFragment
        binding.settingsBTN.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainMenuFragment_to_settingsFragment)
        }
        // Go to AboutAppFragment
        binding.aboutAppBTN.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainMenuFragment_to_aboutAppFragment)
        }
    }

    /**
     * Destroy a view of fragment with view bindings.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}