package bel.ink.bel.belhunt.ui.adapters

import android.app.Activity
import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import bel.ink.bel.belhunt.R
import com.bumptech.glide.Glide
import java.io.File

class ViewPagerAdapter(val activity: Activity, val images: Array<File>) : PagerAdapter() {


    private lateinit var inflater: LayoutInflater
    override fun getCount() = images.size

    override fun isViewFromObject(view: View, obj: Any): Boolean {

        return view === obj
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = activity.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val item = inflater.inflate(R.layout.item_viewpager, container, false)


        val image = item.findViewById(R.id.imageViewPager) as ImageView

        val metrics: DisplayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)

        image.minimumHeight = metrics.heightPixels
        image.minimumWidth = metrics.widthPixels


        Glide.with(activity)
                .load(images[position])
                .into(image)


        container.addView(item)
        return item


    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        (container as ViewPager).removeView(obj as View)
    }

}