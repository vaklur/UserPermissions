package cz.vaklur.user_permissions.permission

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cz.vaklur.user_permissions.R
import cz.vaklur.user_permissions.constants.EndPoints
import cz.vaklur.user_permissions.databinding.FragmentPermissionExampleBinding
import cz.vaklur.user_permissions.volley_communication.CommunicationService

/**
 * Fragment for display a practical example of selected permission abuse.
 */
class PermissionExampleFragment : Fragment() {

    private var _binding: FragmentPermissionExampleBinding? = null
    private val binding get() = _binding!!

    private lateinit var communicationService: CommunicationService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionExampleBinding.inflate(inflater, container, false)
        communicationService = CommunicationService(requireActivity().application)
        return binding.root
    }

    /**
     * When view created get data from ViewModel and load a server application.
     */
    @SuppressLint("SetJavaScriptEnabled", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginTV.text = String.format(
            resources.getString(R.string.id),
            communicationService.userId + "  " + String.format(
                resources.getString(R.string.password),
                communicationService.password
            )
        )

        val webView = binding.WebWV
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (request?.url != null) {
                    view?.loadUrl(request.url.toString())
                }
                return true
            }
        }
        webView.loadUrl(communicationService.getServerAddress(EndPoints.URL_LOGIN_USER))

        binding.theoryBTN.setOnClickListener {
            findNavController().navigate(R.id.action_permissionExampleFragment_to_permissionTheoryFragment)
        }

        binding.refreshBTN.setOnClickListener {
            webView.reload()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}