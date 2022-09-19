package com.dicoding.ai.mystoryapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.protobuf.Internal
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dicoding.ai.mystoryapp.R
import com.dicoding.ai.mystoryapp.data.Constants
import com.dicoding.ai.mystoryapp.data.UserPreference
import com.dicoding.ai.mystoryapp.data.network.ApiResponse
import com.dicoding.ai.mystoryapp.data.network.ApiServices
import com.dicoding.ai.mystoryapp.data.repository.MainRepository
import com.dicoding.ai.mystoryapp.databinding.ActivityMainBinding
import com.dicoding.ai.mystoryapp.ui.StoryAdapter
import com.dicoding.ai.mystoryapp.ui.ViewModelFactory
import com.dicoding.ai.mystoryapp.ui.auth.login.LoginActivity

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = Constants.USER_PREFERENCES)

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var repository: MainRepository
    private lateinit var viewbinding: ActivityMainBinding
    private lateinit var mAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)
        mAdapter = StoryAdapter {}
        viewbinding.rvItemStory.adapter = mAdapter

        setupViewModel()

    }


    private fun setupViewModel() {
        repository =
            MainRepository(ApiServices.getApiServices(), UserPreference.getInstance(datastore))
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(repository)
        )[MainViewModel::class.java]

        mainViewModel.listStory.observe(this) { res ->
            when (res) {
                is ApiResponse.Empty -> {
                    Log.e("Data :", "Data kosong")

                }
                is ApiResponse.Success -> {
                    mAdapter.submitList(res.data)

                }
                is ApiResponse.Error -> {
                    Log.e("Error :", res.message)
                }
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_change_language -> startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            R.id.menu_logout -> {
                mainViewModel.removeUser()
                Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(this)
                    finish()
                }
            }
        }
        return true
    }
}