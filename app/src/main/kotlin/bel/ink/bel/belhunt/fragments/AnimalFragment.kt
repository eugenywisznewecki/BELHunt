package bel.ink.bel.belhunt.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bel.ink.bel.belhunt.R
import bel.ink.bel.belhunt.entities.Animal
import bel.ink.bel.belhunt.ui.adapters.AnimalAdapter
import bel.ink.bel.belhunt.viewmodels.AnimalsViewModel
import kotlinx.android.synthetic.main.amimals_fragment.*
import kotlinx.android.synthetic.main.gallery_fragment.*

class AnimalFragment: Fragment() {

    lateinit var myView: View

    lateinit var mainViewModel: AnimalsViewModel

    private lateinit var listAminals: List<Animal>

    private lateinit var galleryAdapter: AnimalAdapter


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        mainViewModel = ViewModelProviders.of(this).get(AnimalsViewModel::class.java)

        val liveListPhotos = mainViewModel.getLiveLisststAnimals()
        liveListPhotos.observe(this, Observer { list ->
            list?.let {
                listAminals = list
                onUpdateView()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        myView = inflater.inflate(R.layout.amimals_fragment, container, false)
        return myView
    }

    override fun onResume() {
        super.onResume()

        mainViewModel.getListPath()
    }

    fun onUpdateView() {
        galleryAdapter = AnimalAdapter(activity!!.applicationContext, listAminals)
        recycle_animals.adapter = galleryAdapter
        recycle_animals.layoutManager = GridLayoutManager(activity, 3) as RecyclerView.LayoutManager
        recycle_animals.setHasFixedSize(false)
        recycle_animals.adapter.notifyDataSetChanged()

    }


}