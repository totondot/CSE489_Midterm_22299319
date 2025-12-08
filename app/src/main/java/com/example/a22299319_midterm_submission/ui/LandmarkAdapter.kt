package com.example.a22299319_midterm_submission.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.a22299319_midterm_submission.databinding.ItemLandmarkBinding
import com.example.a22299319_midterm_submission.models.Landmark
import com.example.a22299319_midterm_submission.R

class LandmarkAdapter(
    private var landmarks: List<Landmark>,
    private val onClick: (Landmark) -> Unit
) : RecyclerView.Adapter<LandmarkAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemLandmarkBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLandmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = landmarks[position]

        holder.binding.tvTitle.text = item.title
        holder.binding.tvCoordinates.text = "Lat: ${item.lat}, Lon: ${item.lon}"

        // Construct the full image URL
        val imageUrl = "https://labs.anontech.info/cse489/t3/" + item.image

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background) // Using default icon as placeholder
            .error(R.drawable.ic_launcher_foreground)     // Using default icon as error
            .into(holder.binding.ivLandmark)

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = landmarks.size

    fun updateList(newLandmarks: List<Landmark>) {
        landmarks = newLandmarks
        notifyDataSetChanged()
    }
}