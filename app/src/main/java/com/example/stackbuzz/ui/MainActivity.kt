package com.example.stackbuzz.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.stackbuzz.R
import com.example.stackbuzz.databinding.ActivityMainBinding
import com.example.stackbuzz.ui.fragment.HomeFragment
import com.example.stackbuzz.ui.fragment.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private var currentFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_home -> {
                    if (currentFragment != homeFragment) {
                        loadFragment(homeFragment)
                        currentFragment = homeFragment
                    } else {
                        homeFragment.updateQuestions()
                    }
                    true
                }

                else -> {
                    if (currentFragment != searchFragment) {
                        loadFragment(searchFragment)
                        currentFragment = searchFragment
                    }
                    true
                }
            }
        }

        loadFragment(homeFragment)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }
}