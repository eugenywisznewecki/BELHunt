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
import bel.ink.bel.belhunt.ui.adapters.GalleryAdapter
import bel.ink.bel.belhunt.viewmodels.GalleryViewModel
import kotlinx.android.synthetic.main.gallery_fragment.*
import java.io.File

class GalleryFragment : Fragment() {


    lateinit var myView: View

    lateinit var mainViewModel: GalleryViewModel

    private lateinit var listPhotosFiles: List<File>

    private lateinit var galleryAdapter: GalleryAdapter


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        mainViewModel = ViewModelProviders.of(this).get(GalleryViewModel::class.java)

        val liveListPhotos = mainViewModel.getLiveLisststPhotos()
        liveListPhotos.observe(this, Observer { list ->
            list?.let {
                listPhotosFiles = list
                onUpdateView()

            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        myView = inflater.inflate(R.layout.gallery_fragment, container, false)
        return myView
    }

    override fun onResume() {
        super.onResume()

        mainViewModel.getListPath()
    }

    fun onUpdateView() {
        galleryAdapter = GalleryAdapter(activity!!.applicationContext, listPhotosFiles)
        imageGalleryView.adapter = galleryAdapter
        imageGalleryView.layoutManager = GridLayoutManager(activity, 3) as RecyclerView.LayoutManager
        imageGalleryView.setHasFixedSize(false)
        imageGalleryView.adapter.notifyDataSetChanged()

    }


}