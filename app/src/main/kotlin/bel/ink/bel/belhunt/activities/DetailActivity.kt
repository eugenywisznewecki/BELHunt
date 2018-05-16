package bel.ink.bel.belhunt.activities

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import bel.ink.bel.belhunt.R
import bel.ink.bel.belhunt.utilits.AppRouter
import bel.ink.bel.belhunt.ui.adapters.ViewPagerAdapter
import bel.ink.bel.belhunt.viewmodels.DetailViewModel
import kotlinx.android.synthetic.main.activity_detail.*
import msq.inok.bel.belhunt.util.FILE_PATH
import msq.inok.bel.belhunt.util.POSITION
import java.io.File

class DetailActivity : AppCompatActivity() {

    val photoFolder by lazy { AppRouter.PATH_PHOTO_DIRECTORY }
    val listPhotos by lazy { photoFolder.listFiles().toList() }

    private lateinit var viewModel: DetailViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setUpToolbar()
        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)


        val file = intent.getSerializableExtra(FILE_PATH) as File
        val position = intent.getIntExtra(POSITION, 0)
        val adapterViewPager = ViewPagerAdapter(this, listPhotos.toTypedArray())
        viewPager.adapter = adapterViewPager
        viewPager.currentItem = position

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                viewModel.share(listPhotos[viewPager.currentItem].toString())
            }

            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setUpToolbar() {
        this.apply {
            setSupportActionBar(toolbarGallery as Toolbar)
            title = getString(R.string.gallery)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }
        }
    }
}
