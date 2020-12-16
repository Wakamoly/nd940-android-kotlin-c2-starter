package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.base.ViewModelFactory
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.db.AsteroidDao
import com.udacity.asteroidradar.db.PhotoOfTheDayDao
import com.udacity.asteroidradar.db.RadarDatabase

class MainFragment : Fragment() {

    private lateinit var asteroidDao: AsteroidDao
    private lateinit var photoOfTheDayDao: PhotoOfTheDayDao
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(MainRepository(asteroidDao, photoOfTheDayDao))).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        asteroidDao = RadarDatabase(requireContext()).getAsteroidDao()
        photoOfTheDayDao = RadarDatabase(requireContext()).getPhotoOfTheDayDao()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        subscribeObservers()

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun subscribeObservers(){

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // TODO: 12/14/20 Finish menu
            R.id.show_week -> {

            }
            R.id.shoe_today -> {

            }
            R.id.show_saved -> {

            }
        }
        return true
    }
}
