package cz.vaklur.user_permissions.permission.permission_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cz.vaklur.user_permissions.databinding.FragmentPermissionBinding
import cz.vaklur.user_permissions.permission.permission_fragment.adapter.PermissionItemAdapter
import cz.vaklur.user_permissions.permission.permission_fragment.data.PermissionList
import cz.vaklur.user_permissions.permission.view_model.PermissionViewModel

/**
 * Fragment with menu for select a abuse permission example.
 */
class PermissionFragment : Fragment() {

    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!

    private lateinit var permissionVM: PermissionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionBinding.inflate(inflater, container, false)
        permissionVM = ViewModelProvider(requireActivity()).get(PermissionViewModel::class.java)
        return binding.root
    }

    /**
     * Create a menu for select a abuse permission example.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val permissionData = PermissionList().loadPermissions()
        val recyclerView = binding.PermissionRV
        recyclerView.adapter = PermissionItemAdapter(requireContext(), permissionVM, permissionData)
        recyclerView.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}