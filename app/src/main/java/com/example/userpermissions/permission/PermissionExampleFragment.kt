package com.example.userpermissions.permission

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.userpermissions.EndPoints
import com.example.userpermissions.R
import com.example.userpermissions.databinding.FragmentPermissionExampleBinding
import com.example.userpermissions.volley_communication.CommunicationFunction

class PermissionExampleFragment : Fragment() {

    private var _binding: FragmentPermissionExampleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionExampleBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val permissionId = requireArguments().getInt("permissionType")

        val comFun = CommunicationFunction()

        // ***

        binding.loginTV.text = String.format(resources.getString(R.string.id),comFun.getAndroidId(requireActivity().contentResolver))+"  "+ String.format(resources.getString(R.string.password),comFun.getPassword(requireActivity().contentResolver))

        // ***
        val webView = binding.WebWV
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView .webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    view?.loadUrl(url)
                }
                return true
            }
        }
        webView.loadUrl(comFun.getServerAddress(EndPoints.URL_LOGIN_USER,requireActivity()))

        binding.theoryBTN.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("permissionType",permissionId)
            bundle.putBoolean("dataIsSend",true)
            findNavController().navigate(R.id.action_PermissionExampleFragment_to_PermissionTheoryFragment,bundle)
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