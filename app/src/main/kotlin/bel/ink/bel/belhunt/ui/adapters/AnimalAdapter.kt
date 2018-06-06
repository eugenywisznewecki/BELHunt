package bel.ink.bel.belhunt.ui.adapters

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import bel.ink.bel.belhunt.R
import bel.ink.bel.belhunt.entities.Animal
import bel.ink.bel.belhunt.utilits.AppRouter
import com.bumptech.glide.Glide
import msq.inok.bel.belhunt.util.ANIMAL_PHOTO_PATH
import org.w3c.dom.Text
import timber.log.Timber
import java.io.File

class AnimalAdapter(val context: Context, val animalList: List<Animal>) : RecyclerView.Adapter<AnimalAdapter.ViewHolder>() {

    val appRouter by lazy { AppRouter(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_animal, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = animalList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
                .load(
                        Uri.parse("file:///android_asset/${ANIMAL_PHOTO_PATH}/${animalList[position].image}")

                        )
                .into(holder.img)



        holder.animalNale.text = animalList[position].title
        holder.animalDescr.text = animalList[position].title

        Timber.d("++ ${animalList[position]}")

    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val img: ImageView
        val animalNale: TextView
        val animalDescr: TextView


        init {
            img = view.findViewById<View>(R.id.img_animal) as ImageView
            animalNale = view.findViewById<View>(R.id.animalNameView) as TextView
            animalDescr = view.findViewById<View>(R.id.animalDescriptionName) as TextView

            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            view.setOnClickListener {
               /* appRouter.openDelailActivity(animalList[position], position)*/
            }
        }
    }
}