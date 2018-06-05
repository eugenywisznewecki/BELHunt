package bel.ink.bel.belhunt.fragments


import android.os.Bundle
import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bel.ink.bel.belhunt.R

class AnimalFragment: Fragment() {


    lateinit var myView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        myView = inflater.inflate(R.layout.secondlayout, container, false)
        return myView
    }
}