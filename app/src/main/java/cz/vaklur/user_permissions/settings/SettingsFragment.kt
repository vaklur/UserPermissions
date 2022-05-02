package cz.vaklur.user_permissions.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cz.vaklur.user_permissions.databinding.FragmentSettingsBinding

/**
 * Fragment for application settings - server address and language.
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        binding.settingsViewModel = settingsViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        settingsViewModel.getActualLanguage(requireContext())
        return binding.root
    }

    /**
     * * Create view, load saved settings and define onClickListeners.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialogs = SettingsDialog(requireActivity().application)
        // Test connection to server
        settingsViewModel.getServerState(requireActivity(), requireContext())
        // Initialize onClickListeners for buttons
        binding.changeIPBTN.setOnClickListener {
            dialogs.showSetAddressDialog(binding, requireContext())
        }
        binding.changeLanguageBTN.setOnClickListener {
            dialogs.showSetLanguageDialog(requireActivity(), requireContext())
        }
        binding.refreshIPBTN.setOnClickListener {
            settingsViewModel.getServerState(requireActivity(), requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}