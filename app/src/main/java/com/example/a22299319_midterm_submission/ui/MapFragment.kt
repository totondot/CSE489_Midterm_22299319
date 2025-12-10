package com.example.a22299319_midterm_submission.ui

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.a22299319_midterm_submission.api.ApiService
import com.example.a22299319_midterm_submission.api.RetrofitClient
import com.example.a22299319_midterm_submission.databinding.FragmentMapBinding
import com.example.a22299319_midterm_submission.models.Landmark
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 1. Initialize OSMDroid Configuration (Required!)
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 2. Setup Map Settings
        binding.map.setTileSource(TileSourceFactory.MAPNIK) // Standard OSM look
        binding.map.setMultiTouchControls(true)             // Enable zoom gestures

        // 3. Set Default Position (Bangladesh)
        val startPoint = GeoPoint(23.6850, 90.3563)
        binding.map.controller.setZoom(7.0)
        binding.map.controller.setCenter(startPoint)

        loadLandmarksOnMap()
    }

    private fun loadLandmarksOnMap() {
        binding.progressBarMap.visibility = View.VISIBLE
        val api = RetrofitClient.instance.create(ApiService::class.java)

        api.getLandmarks().enqueue(object : Callback<List<Landmark>> {
            override fun onResponse(call: Call<List<Landmark>>, response: Response<List<Landmark>>) {
                // Check if fragment is valid
                if (_binding == null) return

                binding.progressBarMap.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val landmarks = response.body()!!

                    // Clear old markers
                    binding.map.overlays.clear()

                    for (item in landmarks) {
                        // Create Marker
                        val marker = Marker(binding.map)
                        marker.position = GeoPoint(item.lat, item.lon)
                        marker.title = item.title
                        marker.snippet = "Lat: ${item.lat}, Lon: ${item.lon}"

                        // Set Icon (Optional, uses default if skipped)
                        marker.icon = resources.getDrawable(org.osmdroid.library.R.drawable.marker_default)

                        // Add to map
                        binding.map.overlays.add(marker)
                    }
                    // Refresh map to show markers
                    binding.map.invalidate()
                }
            }

            override fun onFailure(call: Call<List<Landmark>>, t: Throwable) {
                if (_binding != null) {
                    binding.progressBarMap.visibility = View.GONE
                    Toast.makeText(context, "Map Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // OSM requires handling lifecycle
    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}