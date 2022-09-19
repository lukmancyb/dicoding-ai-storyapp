package com.dicoding.ai.mystoryapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.bumptech.glide.Glide
import com.dicoding.ai.mystoryapp.data.model.StoryModel
import com.dicoding.ai.mystoryapp.databinding.ItemStoryBinding

class StoryAdapter(private val onItemClicked : (StoryModel) -> Unit) : ListAdapter<StoryModel, StoryAdapter.StoryViewHolder>(DiffCallback){

    companion object DiffCallback : DiffUtil.ItemCallback<StoryModel>() {
        override fun areItemsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
                return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
            return oldItem == newItem
        }
    }


    inner class StoryViewHolder(private var binding : ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(storyModel: StoryModel){
            binding.nameItemStory.text = storyModel.name
            Glide.with(binding.imageView2.context)
                .load(storyModel.photoUrl)
                .into(binding.imageView2)

            binding.textView2.text = storyModel.description

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
       return StoryViewHolder(ItemStoryBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val stories = getItem(position)
        holder.bind(stories)
        holder.itemView.setOnClickListener {
            onItemClicked(stories)
        }
    }
}