package cz.vaklur.user_permissions.settings

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cz.vaklur.user_permissions.constants.Constants
import cz.vaklur.user_permissions.MainActivity
import cz.vaklur.user_permissions.R
import cz.vaklur.user_permissions.databinding.FragmentSettingsBinding
import cz.vaklur.user_permissions.volley_communication.CommunicationService

/**
 * Fragment for application settings - server address and language
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var communicationService: CommunicationService
    private lateinit var settingsSP: SettingsSharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        communicationService = CommunicationService(requireActivity().application)
        settingsSP = SettingsSharedPreferences(requireContext())
        return binding.root
    }

    /**
     * * Create view, load saved settings and define onClickListeners
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dialogs = SettingsDialog(requireActivity().application)
        // Load and display last known settings
        binding.languageTV.text = getActualLanguage(settingsSP.getLanguageSettings())
        binding.actualIPTV.text = settingsSP.getIpSettings()
        // Test connection to server
        getServerState()
        // Initialize onClickListeners for buttons
        binding.changeIPBTN.setOnClickListener {
            dialogs.showSetAddressDialog(binding, requireContext())
        }
        binding.changeLanguageBTN.setOnClickListener {
            dialogs.showSetLanguageDialog(requireActivity(), requireContext())
        }
        binding.refreshIPBTN.setOnClickListener {
            getServerState()
        }
    }

    /**
     * Function set the widget parameters if the test of server connection
     */
    private fun setServerStateWidgets(getResponse: Boolean, serverState: Boolean) {
        binding.changeIPBTN.isEnabled = getResponse
        binding.refreshIPBTN.isEnabled = getResponse
        binding.changeLanguageBTN.isEnabled = getResponse
        (activity as MainActivity).allowBackPressed = getResponse

        val stateActualTV = binding.stateActualTV
        stateActualTV.text = requireContext().resources.getString(R.string.state_connecting_setting)
        stateActualTV.setBackgroundColor(Color.TRANSPARENT)
        var visibility = View.VISIBLE
        if (getResponse) {
            visibility = View.GONE
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
            if (serverState) {
                stateActualTV.text =
                    requireContext().resources.getString(R.string.state_reachable_setting)
                stateActualTV.setBackgroundColor(Color.GREEN)
            } else {
                stateActualTV.text = resources.getString(R.string.state_unreachable_setting)
                stateActualTV.setBackgroundColor(Color.RED)
            }
        } else {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        }
        binding.settingsPB.visibility = visibility
        binding.settingsProgressBarTV.visibility = visibility
    }

    /**
     * Function that returns the full name of the current language, depending on the local language code.
     *
     * @param languageCode Code of actual language.
     *
     * @return Full name of actual language.
     */
    private fun getActualLanguage(languageCode: String): String {
        var actualLanguage: String = getString(R.string.language_english)
        if (languageCode == Constants.ENGLISH_CODE) {
            actualLanguage = getString(R.string.language_english)
        } else if (languageCode == Constants.CZECH_CODE) {
            actualLanguage = getString(R.string.language_czech)
        }
        return actualLanguage
    }

    /**
     * Function for get actual server state and set the widgets based on server state
     */
    private fun getServerState() {
        setServerStateWidgets(getResponse = false, serverState = false)
        communicationService.testConnectionToServer(
            settingsSP.getIpSettings(),
            object : CommunicationService.VolleyStringResponse {
                override fun onSuccess() {
                    setServerStateWidgets(getResponse = true, serverState = true)
                }

                override fun onError() {
                    setServerStateWidgets(getResponse = true, serverState = false)
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}