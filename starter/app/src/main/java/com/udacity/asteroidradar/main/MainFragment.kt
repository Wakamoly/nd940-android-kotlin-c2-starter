package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.*
import com.udacity.asteroidradar.base.ViewModelFactory
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.db.AsteroidDao
import com.udacity.asteroidradar.db.PhotoOfTheDayDao
import com.udacity.asteroidradar.db.RadarDatabase
import com.udacity.asteroidradar.main.adapters.AsteroidAdapter
import com.udacity.asteroidradar.main.adapters.AsteroidListener

class MainFragment : Fragment() {

    private lateinit var asteroidDao: AsteroidDao
    private lateinit var photoOfTheDayDao: PhotoOfTheDayDao
    private lateinit var asteroidAdapter: AsteroidAdapter
    private lateinit var binding: FragmentMainBinding
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
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        asteroidAdapter = AsteroidAdapter(AsteroidListener {
            viewModel.onAsteroidClicked(it)
        })
        binding.asteroidRecycler.adapter = asteroidAdapter

        subscribeObservers()

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun subscribeObservers(){
        viewModel.asteroids.observe(viewLifecycleOwner, Observer{ asteroidList ->
            asteroidList?.also {
                asteroidAdapter.submitList(it)
            }
        })

        viewModel.navigateToAsteroidDetail.observe(viewLifecycleOwner, Observer<Asteroid>{ asteroid ->
            asteroid?.also {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayAsteroidDetailsComplete()
            }
        })

        viewModel.photoOTD.observe(viewLifecycleOwner, Observer { photoOTD ->
            photoOTD?.also {
                addPhotoOTDLayout(it)
                /**
                 * Test YouTube image loading by commenting the above and uncommenting the following function call
                 */
                //this was an official response for 12/15/2020
                //addPhotoOTDLayout(PictureOfDay("video", "Sonified: The Matter of the Bullet Cluster", "https://www.youtube.com/embed/sau5-39wK1c?rel=0"))
            }
        })

        viewModel.dateState.observe(viewLifecycleOwner, Observer {
            when (it){
                is Resource.Success -> {
                    binding.statusLoadingWheel.visible(false)
                }
                is Resource.Failure -> {
                    binding.statusLoadingWheel.visible(false)
                    handleApiError(it) { viewModel.refreshAll() }
                    viewModel.displayNetworkErrorCompleted()
                }
                is Resource.Loading -> {
                    binding.statusLoadingWheel.visible(true)
                }
            }

        })

    }

    private fun addPhotoOTDLayout(photoOTD: PictureOfDay){
        binding.statusLoadingWheel.visible(false)
        binding.photoOtdTitle.text = photoOTD.title

        if (photoOTD.mediaType == "image"){
            // Util.kt function
            binding.activityMainImageOfTheDay.setImageFromNetwork(photoOTD.url)
        }else if (photoOTD.mediaType == "video"){
            binding.activityMainImageOfTheDay.setImageFromNetworkVideo(photoOTD.url)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_week -> {
                viewModel.updateAsteroidFilter(AsteroidFilter.WEEK)
            }
            R.id.show_today -> {
                viewModel.updateAsteroidFilter(AsteroidFilter.TODAY)
            }
            R.id.show_saved -> {
                viewModel.updateAsteroidFilter(AsteroidFilter.SAVED)
            }
        }
        return true
    }
}
