package com.dicoding.ai.mystoryapp.ui.auth.login

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.ai.mystoryapp.R
import com.dicoding.ai.mystoryapp.data.Constants.USER_PREFERENCES
import com.dicoding.ai.mystoryapp.data.UserPreference
import com.dicoding.ai.mystoryapp.data.network.ApiResponse
import com.dicoding.ai.mystoryapp.data.network.ApiServices
import com.dicoding.ai.mystoryapp.data.repository.MainRepository
import com.dicoding.ai.mystoryapp.data.loadingDialog
import com.dicoding.ai.mystoryapp.databinding.ActivityLoginBinding
import com.dicoding.ai.mystoryapp.ui.ViewModelFactory
import com.dicoding.ai.mystoryapp.ui.auth.signup.SignUpActivity
import com.dicoding.ai.mystoryapp.ui.home.MainActivity
import com.google.android.material.snackbar.Snackbar


private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES)

class LoginActivity : AppCompatActivity() {


    private lateinit var viewBinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var repository: MainRepository
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        dialog = loadingDialog(layoutInflater)
        setContentView(viewBinding.root)
        setUpView()
        setUpViewModel()
        setUpLogin()
    }

    private fun setUpLogin() {
        viewBinding.apply {
            btnLogin.setOnClickListener {
                if (edtLoginEmail.text.toString().isEmpty()) {
                    edtLoginEmail.error = getString(R.string.err_txt_required)
                    return@setOnClickListener
                }
                if (edtLoginPassword.text.toString().isEmpty()) {
                    edtLoginPassword.error = getString(R.string.err_txt_required)
                    return@setOnClickListener
                }
                dialog.show()
                loginViewModel.login(
                    edtLoginEmail.text.toString(),
                    edtLoginPassword.text.toString()
                )
            }
            loginViewModel.loginResponse.observe(this@LoginActivity) {
                when (it) {
                    is ApiResponse.Success -> {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    is ApiResponse.Error -> {

                        Snackbar.make(
                            viewBinding.root,
                            it.errorBody.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()

                    }
                    is ApiResponse.Empty -> {

                    }
                }
                dialog.dismiss()
            }


        }
    }

    private fun setUpViewModel() {
        repository =
            MainRepository(ApiServices.getApiServices(), UserPreference.getInstance(datastore))
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(repository)
        )[LoginViewModel::class.java]
        loginViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
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
        viewBinding.textViewRegisterAction.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

}