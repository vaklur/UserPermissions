package com.example.userpermissions.camera_permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.userpermissions.R
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.front
import io.fotoapparat.view.CameraView


class CameraFragment : Fragment() {

    private var fotoapparatState : FotoapparatState? = null
    private var fotoapparat: Fotoapparat? = null
    enum class FotoapparatState{
        ON
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createFotoapparat()


        view.findViewById<Button>(R.id.button).setOnClickListener {
            val photoResult = fotoapparat?.takePicture()

            photoResult
                ?.toBitmap()
                ?.whenAvailable { bitmapPhoto ->
                    val imageView = view.findViewById<ImageView>(R.id.imageView)

                    if (bitmapPhoto != null) {
                        imageView.setImageBitmap(bitmapPhoto.bitmap)
                    }
                }
        }


    }



    private fun createFotoapparat(){
        val cameraView = view?.findViewById<CameraView>(R.id.camera_view)

        fotoapparat = cameraView?.let {
            Fotoapparat(
                context = requireContext(),
                view = it,
                scaleType = ScaleType.CenterCrop,
                lensPosition = front(),
                logger = loggers(
                    logcat()
                ),
                cameraErrorCallback = { error ->
                    println("Recorder errors: $error")
                }
            )
        }

    }

    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    private fun hasNoPermissions(): Boolean{
        return ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(requireActivity(), permissions,0)
    }

    override fun onStart() {
        super.onStart()
        if (hasNoPermissions()) {
            requestPermission()
        }else{
            fotoapparat?.start()
            fotoapparatState = FotoapparatState.ON
        }
    }
}