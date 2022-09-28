package com.dicoding.ai.mystoryapp.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.ai.mystoryapp.R
import com.dicoding.ai.mystoryapp.data.Constants
import com.dicoding.ai.mystoryapp.data.UserPreference
import com.dicoding.ai.mystoryapp.data.network.ApiResponse
import com.dicoding.ai.mystoryapp.data.network.ApiServices
import com.dicoding.ai.mystoryapp.data.repository.MainRepository
import com.dicoding.ai.mystoryapp.databinding.ActivityMainBinding
import com.dicoding.ai.mystoryapp.ui.StoryAdapter
import com.dicoding.ai.mystoryapp.ui.ViewModelFactory
import com.dicoding.ai.mystoryapp.ui.addstory.AddStoryActivity
import com.dicoding.ai.mystoryapp.ui.auth.login.LoginActivity
import com.google.android.material.snackbar.Snackbar

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = Constants.USER_PREFERENCES)

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var repository: MainRepository
    private lateinit var viewbinding: ActivityMainBinding
    private lateinit var mAdapter: StoryAdapter


    companion object {
        const val PARCELIZE_STORY = "stories"
    }

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)
        mAdapter = StoryAdapter()

        viewbinding.rvItemStory.adapter = mAdapter
        setupViewModel()
        setupView()


    }

    private fun setupView() {

        viewbinding.apply {
            fab.setOnClickListener {
                startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
            }
            swipeRefresh.setOnRefreshListener {
                mainViewModel.refresh()
            }
        }
    }


    private fun setupViewModel() {
        repository =
            MainRepository(ApiServices.getApiServices(), UserPreference.getInstance(datastore))
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(repository)
        )[MainViewModel::class.java]
        viewbinding.swipeRefresh.isRefreshing = true

        mainViewModel.listStory.observe(this) { res ->

            when (res) {
                is ApiResponse.Empty -> {
                    viewbinding.swipeRefresh.isRefreshing = false
                    viewbinding.txtNetworkError.text = getString(R.string.txt_empty_data)
                    showNetworkError(true)
                }
                is ApiResponse.Success -> {
                    viewbinding.swipeRefresh.isRefreshing = false
                    showNetworkError(false)
                    mAdapter.submitList(res.data) {
                        viewbinding.rvItemStory.scrollToPosition(0)
                    }

                }
                is ApiResponse.Error -> {
                    viewbinding.swipeRefresh.isRefreshing = false
                    if (res.isNetworkError) {
                        showNetworkError(true)
                    } else {
                        Snackbar.make(
                            viewbinding.root,
                            res.errorBody.toString(),
                            Snackbar.LENGTH_LONG
                        )
                            .setAction(getString(R.string.text_reload)) {
                                mainViewModel.refresh()
                            }
                            .show()
                    }

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

    private fun showNetworkError(isError: Boolean) {
        viewbinding.apply {
            imgNetworkError.visibility = if (isError) View.VISIBLE else View.GONE
            txtNetworkError.visibility = if (isError) View.VISIBLE else View.GONE
            rvItemStory.visibility = if (!isError) View.VISIBLE else View.GONE

        }
    }
}