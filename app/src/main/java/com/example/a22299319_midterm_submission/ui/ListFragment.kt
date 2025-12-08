package com.example.a22299319_midterm_submission.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        // Setup RecyclerView
        adapter = LandmarkAdapter(emptyList()) { landmark ->
            Toast.makeText(requireContext(), "Clicked: ${landmark.title}", Toast.LENGTH_SHORT).show()
        }
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
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    adapter.updateList(response.body()!!)
                } else {
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = "Error: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<List<Landmark>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = "Failed: ${t.message}"
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}