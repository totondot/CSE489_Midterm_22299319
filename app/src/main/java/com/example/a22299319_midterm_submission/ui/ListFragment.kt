package com.example.a22299319_midterm_submission.ui

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
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

class ListFragment : Fragment() { // <--- CLASS STARTS HERE

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

        // Setup RecyclerView
        adapter = LandmarkAdapter(
            emptyList(),
            onClick = { landmark ->
                // Short Click -> Open Update Dialog
                showUpdateDialog(landmark)
            },
            onLongClick = { landmark ->
                // Long Click -> Delete
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

    // --- Delete Dialog ---
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

    // --- Delete API Call ---
    private fun deleteLandmark(landmark: Landmark) {
        val idInt = landmark.id.toString().toDoubleOrNull()?.toInt()
        if (idInt == null) {
            Toast.makeText(context, "Invalid ID", Toast.LENGTH_SHORT).show()
            return
        }

        val api = RetrofitClient.instance.create(ApiService::class.java)
        api.deleteLandmark(idInt).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                    loadLandmarks()
                } else {
                    Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- Update Dialog ---
    private fun showUpdateDialog(landmark: Landmark) {
        val context = requireContext()
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val inputTitle = EditText(context)
        inputTitle.hint = "Title"
        inputTitle.setText(landmark.title)
        layout.addView(inputTitle)

        val inputLat = EditText(context)
        inputLat.hint = "Latitude"
        inputLat.setText(landmark.lat.toString())
        inputLat.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(inputLat)

        val inputLon = EditText(context)
        inputLon.hint = "Longitude"
        inputLon.setText(landmark.lon.toString())
        inputLon.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(inputLon)

        AlertDialog.Builder(context)
            .setTitle("Update Landmark")
            .setView(layout)
            .setPositiveButton("Update") { _, _ ->
                val newTitle = inputTitle.text.toString()
                val newLat = inputLat.text.toString().toDoubleOrNull() ?: landmark.lat
                val newLon = inputLon.text.toString().toDoubleOrNull() ?: landmark.lon

                // Safe ID conversion
                val idInt = landmark.id.toString().toDoubleOrNull()?.toInt()
                if (idInt != null) {
                    updateLandmark(idInt, newTitle, newLat, newLon)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // --- Update API Call ---
    private fun updateLandmark(id: Int, title: String, lat: Double, lon: Double) {
        val api = RetrofitClient.instance.create(ApiService::class.java)
        api.updateLandmark(id, title, lat, lon).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show()
                    loadLandmarks()
                } else {
                    Toast.makeText(context, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}