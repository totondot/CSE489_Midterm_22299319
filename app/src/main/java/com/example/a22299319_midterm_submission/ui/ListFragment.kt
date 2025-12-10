package com.example.a22299319_midterm_submission.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a22299319_midterm_submission.api.ApiService
import com.example.a22299319_midterm_submission.api.RetrofitClient
import com.example.a22299319_midterm_submission.databinding.FragmentListBinding
import com.example.a22299319_midterm_submission.models.Landmark
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: LandmarkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView with BOTH Click (Toast) and LongClick (Delete)
        adapter = LandmarkAdapter(
            emptyList(),
            onClick = { landmark ->
                // Simple click shows the title
                Toast.makeText(requireContext(), "Clicked: ${landmark.title}", Toast.LENGTH_SHORT).show()
            },
            onLongClick = { landmark ->
                // Long click opens the delete dialog
                showDeleteDialog(landmark)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        loadLandmarks()
    }

    private fun loadLandmarks() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE

        val api = RetrofitClient.instance.create(ApiService::class.java)

        api.getLandmarks().enqueue(object : Callback<List<Landmark>> {
            override fun onResponse(call: Call<List<Landmark>>, response: Response<List<Landmark>>) {
                // Check if fragment is still attached to avoid crashes
                if (!isAdded) return

                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    adapter.updateList(response.body()!!)
                } else {
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = "Error: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<List<Landmark>>, t: Throwable) {
                if (!isAdded) return
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = "Failed: ${t.message}"
            }
        })
    }

    // --- NEW: Delete Dialog ---
    private fun showDeleteDialog(landmark: Landmark) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Landmark")
            .setMessage("Are you sure you want to delete '${landmark.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                deleteLandmark(landmark)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // --- NEW: Delete API Call ---
    private fun deleteLandmark(landmark: Landmark) {
        // Safely convert ID to Integer (handles cases where ID might be string or double in JSON)
        val idInt = landmark.id.toString().toDoubleOrNull()?.toInt()

        if (idInt == null) {
            Toast.makeText(context, "Invalid ID, cannot delete", Toast.LENGTH_SHORT).show()
            return
        }

        val api = RetrofitClient.instance.create(ApiService::class.java)
        api.deleteLandmark(idInt).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                    loadLandmarks() // Refresh the list to remove the item
                } else {
                    Toast.makeText(context, "Delete Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}