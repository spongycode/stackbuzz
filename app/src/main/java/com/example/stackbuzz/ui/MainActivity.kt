package com.example.stackbuzz.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stackbuzz.R
import com.example.stackbuzz.databinding.ActivityMainBinding
import com.example.stackbuzz.ui.fragment.HomeFragment
import com.example.stackbuzz.ui.fragment.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_home -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, HomeFragment())
                        addToBackStack(null) // Optional: Add this line if you want to add the transaction to the back stack.
                        commit()
                    }
                    Toast.makeText(this, "Home", Toast.LENGTH_LONG).show()
                    true
                }

                else -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, SearchFragment())
                        addToBackStack(null) // Optional: Add this line if you want to add the transaction to the back stack.
                        commit()
                    }
                    Toast.makeText(this, "Search", Toast.LENGTH_LONG).show()
                    true
                }
            }
        }
    }
}