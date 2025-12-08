package com.example.a22299319_midterm_submission.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.a22299319_midterm_submission.api.ApiService
import com.example.a22299319_midterm_submission.api.RetrofitClient
import com.example.a22299319_midterm_submission.databinding.FragmentFormBinding
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class FormFragment : Fragment() {

    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!
    private var selectedImageFile: File? = null

    // Image Picker
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                binding.ivPreview.setImageURI(it)
                selectedImageFile = uriToFile(it)
            }
        }
    }

    // Permission Launcher
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) getCurrentLocation() else Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        binding.btnGetLocation.setOnClickListener {
            checkLocationPermission()
        }

        binding.btnSubmit.setOnClickListener {
            uploadLandmark()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getCurrentLocation() {
        val client = LocationServices.getFusedLocationProviderClient(requireContext())
        client.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                binding.etLat.setText(location.latitude.toString())
                binding.etLon.setText(location.longitude.toString())
            } else {
                Toast.makeText(context, "Could not get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadLandmark() {
        val title = binding.etTitle.text.toString()
        val lat = binding.etLat.text.toString()
        val lon = binding.etLon.text.toString()

        if (title.isEmpty() || lat.isEmpty() || selectedImageFile == null) {
            Toast.makeText(context, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBarForm.visibility = View.VISIBLE
        binding.btnSubmit.isEnabled = false

        // Prepare Data for API
        val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val latPart = lat.toRequestBody("text/plain".toMediaTypeOrNull())
        val lonPart = lon.toRequestBody("text/plain".toMediaTypeOrNull())

        val requestFile = selectedImageFile!!.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", selectedImageFile!!.name, requestFile)

        val api = RetrofitClient.instance.create(ApiService::class.java)
        api.createLandmark(titlePart, latPart, lonPart, imagePart).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                binding.progressBarForm.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
                if (response.isSuccessful) {
                    Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show()
                    // Clear form
                    binding.etTitle.setText("")
                    binding.ivPreview.setImageResource(android.R.drawable.ic_menu_camera)
                } else {
                    Toast.makeText(context, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                binding.progressBarForm.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Helper to convert URI to File
    private fun uriToFile(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "upload.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()
        return file
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}