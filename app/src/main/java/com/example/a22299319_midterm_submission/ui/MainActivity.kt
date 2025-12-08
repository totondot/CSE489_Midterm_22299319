package com.example.a22299319_midterm_submission

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.a22299319_midterm_submission.databinding.ActivityMainBinding
import com.example.a22299319_midterm_submission.ui.FormFragment
import com.example.a22299319_midterm_submission.ui.ListFragment
import com.example.a22299319_midterm_submission.ui.MapFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the Map by default
        loadFragment(MapFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_map -> loadFragment(MapFragment())
                R.id.nav_list -> loadFragment(ListFragment())
                R.id.nav_create -> loadFragment(FormFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}