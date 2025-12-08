package com.example.a22299319_midterm_submission.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.a22299319_midterm_submission.R
import com.example.a22299319_midterm_submission.api.ApiService
import com.example.a22299319_midterm_submission.api.RetrofitClient
import com.example.a22299319_midterm_submission.databinding.FragmentMapBinding
import com.example.a22299319_midterm_submission.models.Landmark
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Find the map fragment inside this fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Default center: Bangladesh
        val bangladesh = LatLng(23.6850, 90.3563)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bangladesh, 7f))

        loadLandmarksOnMap()
    }

    private fun loadLandmarksOnMap() {
        binding.progressBarMap.visibility = View.VISIBLE
        val api = RetrofitClient.instance.create(ApiService::class.java)

        api.getLandmarks().enqueue(object : Callback<List<Landmark>> {
            override fun onResponse(call: Call<List<Landmark>>, response: Response<List<Landmark>>) {
                binding.progressBarMap.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val landmarks = response.body()!!
                    for (item in landmarks) {
                        val pos = LatLng(item.lat, item.lon)
                        mMap.addMarker(MarkerOptions().position(pos).title(item.title))
                    }
                }
            }

            override fun onFailure(call: Call<List<Landmark>>, t: Throwable) {
                binding.progressBarMap.visibility = View.GONE
                Toast.makeText(context, "Map Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}