package bel.ink.bel.belhunt.ui.adapters

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import bel.ink.bel.belhunt.R
import bel.ink.bel.belhunt.utilits.AppRouter
import com.bumptech.glide.Glide
import timber.log.Timber
import java.io.File


class GalleryAdapter(val context: Context, val galleryList: List<File>) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    val appRouter by lazy { AppRouter(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = galleryList.size

    override fun onBindViewHolder(holder: GalleryAdapter.ViewHolder, position: Int) {
        Glide.with(context)
                .load(galleryList[position])
                .into(holder.img)

        Timber.d("++ ${galleryList[position]} ++++ ${Uri.fromFile(galleryList[position])}")

    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val img: ImageView

        init {
            img = view.findViewById<View>(R.id.img) as ImageView
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            view.setOnClickListener {
                appRouter.openDelailActivity(galleryList[position], position)
            }
        }
    }
}