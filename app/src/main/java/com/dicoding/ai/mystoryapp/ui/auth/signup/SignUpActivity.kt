package com.dicoding.ai.mystoryapp.ui.auth.signup

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log.e
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.ai.mystoryapp.R
import com.dicoding.ai.mystoryapp.data.Constants
import com.dicoding.ai.mystoryapp.data.UserPreference
import com.dicoding.ai.mystoryapp.data.loadingDialog
import com.dicoding.ai.mystoryapp.data.network.ApiResponse
import com.dicoding.ai.mystoryapp.data.network.ApiServices
import com.dicoding.ai.mystoryapp.data.repository.MainRepository
import com.dicoding.ai.mystoryapp.databinding.ActivitySignUpBinding
import com.dicoding.ai.mystoryapp.ui.ViewModelFactory


private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = Constants.USER_PREFERENCES)

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivitySignUpBinding
    private lateinit var signupViewModel: SignupViewModel
    private lateinit var repository: MainRepository
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        dialog = loadingDialog(layoutInflater)
        setUpView()
        setUpViewModel()
        setUpRegister()


    }

    fun setUpRegister() {
        viewBinding.apply {
            btnSignup.setOnClickListener {
                if (edtSignupFullName.text.toString().isEmpty()) {
                    edtSignupFullName.error = "Tidak boleh kosong"
                    return@setOnClickListener
                }
                if (edtSignupEmail.text.toString().isEmpty()) {
                    edtSignupEmail.error = "Tidak boleh kosong"
                    return@setOnClickListener
                }
                if (edtSignupPassword.text.toString().isEmpty()) {
                    edtSignupPassword.error = "Tidak boleh kosong"
                    return@setOnClickListener
                }
                dialog.show()
                signupViewModel.register(
                    edtSignupFullName.text.toString(),
                    edtSignupEmail.text.toString().trim(),
                    edtSignupPassword.text.toString().trim()
                )

            }
            signupViewModel.registerResponse.observe(this@SignUpActivity) {
                when (it) {
                    is ApiResponse.Success -> {
                        Toast.makeText(
                            this@SignUpActivity,
                            it.data.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                    is ApiResponse.Error -> {
                        e("ERROR", it.isNetworkError.toString())
                        if (it.isNetworkError) {
                            Toast.makeText(this@SignUpActivity, it.message, Toast.LENGTH_LONG)
                                .show()
                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                it.errorBody,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                    }
                    is ApiResponse.Empty -> {}
                }
                dialog.dismiss()
            }


        }
    }

    private fun setUpViewModel() {
        repository =
            MainRepository(ApiServices.getApiServices(), UserPreference.getInstance(datastore))
        signupViewModel = ViewModelProvider(
            this,
            ViewModelFactory(repository)
        )[SignupViewModel::class.java]
    }

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
    }


}