package com.dicoding.ai.mystoryapp.ui.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import coil.load
import com.dicoding.ai.mystoryapp.data.model.StoryModel
import com.dicoding.ai.mystoryapp.data.withDateFormat
import com.dicoding.ai.mystoryapp.databinding.ActivityDetailStoryBinding
import com.dicoding.ai.mystoryapp.ui.home.MainActivity.Companion.PARCELIZE_STORY

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        intent.extras?.getParcelable<StoryModel>(PARCELIZE_STORY)?.let { story ->
            binding.apply {
                imgDetailStory.load(story.photoUrl)
                textDetailName.text = story.name
                textDetailStoryDate.text = story.createdAt?.withDateFormat() ?: "-"
                textDetailStoryDescription.text = story.description
            }

        }
    }
}