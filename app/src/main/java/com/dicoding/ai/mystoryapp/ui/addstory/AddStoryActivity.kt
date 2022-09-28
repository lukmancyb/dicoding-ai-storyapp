package com.dicoding.ai.mystoryapp.ui.addstory

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.ai.mystoryapp.R
import com.dicoding.ai.mystoryapp.data.*
import com.dicoding.ai.mystoryapp.data.network.ApiResponse
import com.dicoding.ai.mystoryapp.data.network.ApiServices
import com.dicoding.ai.mystoryapp.data.repository.MainRepository
import com.dicoding.ai.mystoryapp.databinding.ActivityAddStoryBinding
import com.dicoding.ai.mystoryapp.ui.ViewModelFactory
import com.dicoding.ai.mystoryapp.ui.camera.CameraActivity
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = Constants.USER_PREFERENCES)

class AddStoryActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAddStoryBinding
    private lateinit var repository: MainRepository
    private lateinit var viewModel: AddStoryViewModel
    private var getFile: File? = null
    private lateinit var mLoadingDialog: Dialog

    companion object {
        const val CAMERA_X_RESULT = 1991
        const val KEY_PICTURE = "picture"
        const val KEY_BACK_CAMERA = "isBackCamera"
        const val REQUEST_CAMERA_CODE_PERMISSIONS = 10
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            viewBinding.previewImage.setImageURI(selectedImg)
        }
    }


    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra(KEY_PICTURE) as File
            val isBackCamera = it.data?.getBooleanExtra(KEY_BACK_CAMERA, true) as Boolean
            getFile = myFile

            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )
            viewBinding.previewImage.setImageBitmap(result)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        mLoadingDialog = loadingDialog(layoutInflater)
        setUpViewModel()
        setUpView()
        viewModel.storyPostResponse.observe(this) { res ->
            when (res) {
                is ApiResponse.Success -> {
                    Toast.makeText(this, getString(R.string.txt_success_upload), Toast.LENGTH_LONG).show()
                    finish()
                }
                is ApiResponse.Error -> {
                    Snackbar.make(
                        viewBinding.root,
                        if (res.isNetworkError) res.message else res.errorBody.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()

                }
                is ApiResponse.Empty -> {

                }
            }
            mLoadingDialog.dismiss()
        }

    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = getFile as File
            val description =
                viewBinding.edtStoryDesc.text.toString().toRequestBody("text/plain".toMediaType())
            val reqImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                reqImageFile
            )
            mLoadingDialog.show()
            viewModel.postStory(imageMultipart, description)
        } else {
            Toast.makeText(this@AddStoryActivity, getString(R.string.txt_pick_image_upload), Toast.LENGTH_LONG)
                .show()
        }
    }

    @Suppress("DEPRECATION")
    private fun setUpView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
        viewBinding.apply {
            btnGallery.setOnClickListener {
                startGallery()
            }
            btnCameraX.setOnClickListener {
                if (hasCameraPermission()) {
                    startCameraX()
                } else {
                    requestCameraPermission()
                }
            }
            btnSubmit.setOnClickListener {
                if (edtStoryDesc.text.toString().isEmpty()) {
                    edtStoryDesc.error =
                        getString(R.string.err_txt_required)
                    return@setOnClickListener
                }
                uploadImage()


            }
        }
    }


    private fun setUpViewModel() {
        repository =
            MainRepository(ApiServices.getApiServices(), UserPreference.getInstance(datastore))
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(repository)
        )[AddStoryViewModel::class.java]

    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }


}