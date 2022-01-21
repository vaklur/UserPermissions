package com.example.userpermissions.permission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.userpermissions.databinding.FragmentPermissionBinding
import com.example.userpermissions.permission.adapter.PermissionItemAdapter
import com.example.userpermissions.permission.data.PermissionDatasource

class PermissionFragment : Fragment() {


    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val permissionDataset = PermissionDatasource().loadPermissions()

        val recyclerView = binding.PermissionRV
        recyclerView.adapter = PermissionItemAdapter(requireContext(), permissionDataset)

        recyclerView.setHasFixedSize(true)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}