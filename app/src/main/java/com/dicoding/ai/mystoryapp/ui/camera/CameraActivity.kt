package com.dicoding.ai.mystoryapp.ui.camera

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.dicoding.ai.mystoryapp.R
import com.dicoding.ai.mystoryapp.data.createFile
import com.dicoding.ai.mystoryapp.databinding.ActivityCameraBinding
import com.dicoding.ai.mystoryapp.ui.addstory.AddStoryActivity.Companion.CAMERA_X_RESULT
import com.dicoding.ai.mystoryapp.ui.addstory.AddStoryActivity.Companion.KEY_BACK_CAMERA
import com.dicoding.ai.mystoryapp.ui.addstory.AddStoryActivity.Companion.KEY_PICTURE

class CameraActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var viewbinding : ActivityCameraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewbinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)
        startCamera()
        setupView()
    }

    @Suppress("DEPRECATION")
    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
        viewbinding.switchCamera.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA

            startCamera()
        }
        viewbinding.takePicture.setOnClickListener {
            takePhoto()
        }
    }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewbinding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    getString(R.string.txt_camera_err),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = createFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        getString(R.string.txt_file_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val intent = Intent()
                    intent.putExtra(KEY_PICTURE, photoFile)
                    intent.putExtra(
                        KEY_BACK_CAMERA,
                        cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    setResult(CAMERA_X_RESULT, intent)
                    finish()
                }
            }
        )
    }
}

