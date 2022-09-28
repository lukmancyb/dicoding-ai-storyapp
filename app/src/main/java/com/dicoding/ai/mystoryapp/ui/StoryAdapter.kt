package com.dicoding.ai.mystoryapp.ui

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dicoding.ai.mystoryapp.R
import com.dicoding.ai.mystoryapp.data.model.StoryModel
import com.dicoding.ai.mystoryapp.data.withDateFormat
import com.dicoding.ai.mystoryapp.databinding.ItemStoryBinding
import com.dicoding.ai.mystoryapp.ui.detail.DetailStoryActivity
import com.dicoding.ai.mystoryapp.ui.home.MainActivity

class StoryAdapter :
    ListAdapter<StoryModel, StoryAdapter.StoryViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<StoryModel>() {
        override fun areItemsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
            return oldItem == newItem
        }
    }


    inner class StoryViewHolder(private var binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(storyModel: StoryModel) {
            binding.apply {
                nameItemStory.text = storyModel.name
                imageStoryItem.load(storyModel.photoUrl) {
                    placeholder(R.drawable.ic_loading_image)
                    error(R.drawable.ic_broken_image)

                }
                descItemStory.text = storyModel.description
                dateItemStory.text = storyModel.createdAt?.withDateFormat()
                root.setOnClickListener {
                    Intent(root.context, DetailStoryActivity::class.java).apply {
                        putExtra(MainActivity.PARCELIZE_STORY, storyModel)
                        val optionsCompat: ActivityOptionsCompat =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                root.context as Activity,
                                Pair(imageStoryItem, "imageDetail"),
                                Pair(nameItemStory, "textNameDetail"),
                                Pair(dateItemStory, "textNameDate"),
                                Pair(descItemStory, "textNameDesc")
                            )
                        root.context.startActivity(this, optionsCompat.toBundle())
                    }


                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder(
            ItemStoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val stories = getItem(position)
        holder.bind(stories)
//        holder.itemView.setOnClickListener {
//            onItemClicked(stories)
//
//        }
    }
}