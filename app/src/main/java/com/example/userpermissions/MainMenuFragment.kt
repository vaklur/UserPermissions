package com.example.userpermissions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.userpermissions.databinding.FragmentMainMenuBinding
import com.example.userpermissions.volley_communication.CommunicationFunction


class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    private val comFun = CommunicationFunction()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.permissionsBTN.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainMenuFragment_to_permissionFragment)
            comFun.deleteUserInServer(requireActivity())
        }

        binding.settingsBTN.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainMenuFragment_to_settingsFragment)
        }

        binding.aboutAppBTN.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainMenuFragment_to_aboutAppFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}