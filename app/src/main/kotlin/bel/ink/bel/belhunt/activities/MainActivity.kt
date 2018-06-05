package bel.ink.bel.belhunt.activities


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import bel.ink.bel.belhunt.R
import bel.ink.bel.belhunt.fragments.AnimalFragment
import bel.ink.bel.belhunt.fragments.GalleryFragment
import bel.ink.bel.belhunt.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //because not in activity.xml
    private lateinit var actionSettings: Toolbar

    private lateinit var viewModel: MainViewModel
    private var countRows: Int = 3
    private lateinit var listPhotosFiles: List<File>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        actionSettings = findViewById(R.id.toolbar) as Toolbar


        actionSettings.setOnClickListener(this)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        if (!viewModel.isPlayMarker()) finish()


        val liveCountRows = viewModel.getCoutRows()



        liveCountRows.observe(this, Observer { count ->
            count?.let { this.countRows = count }
            onUpdateView()
        })

        val liveListPhotos = viewModel.getLiveLisststPhotos()
        liveListPhotos.observe(this, Observer { list ->
            list?.let {
                listPhotosFiles = list
                onUpdateView()
            }
        })


        /*  imageGalleryView.setOnScrollListener(object : RecyclerView.OnScrollListener() {
              override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                  if (dy > 0 || dy < 0 && fab.isShown()) {
                      fab.hide()
                  }
              }

              override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                  if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                      fab.show()
                  }
                  super.onScrollStateChanged(recyclerView, newState)
              }
          })*/



        fab.setOnClickListener {
            viewModel.startCameraActivity()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

    }


    override fun onResume() {
        super.onResume()
        if (!this::listPhotosFiles.isInitialized || listPhotosFiles.size == 0) {
            notifyNoPhotoView.visibility = View.VISIBLE
        } else notifyNoPhotoView.visibility = View.INVISIBLE

        viewModel.getListPath()

    }

    fun onUpdateView() {

/*        imageGalleryView.setHasFixedSize(false)
        imageGalleryView.layoutManager = GridLayoutManager(applicationContext, countRows) as RecyclerView.LayoutManager
        val galleryAdapter = GalleryAdapter(this, listPhotosFiles)
        imageGalleryView.adapter = galleryAdapter
        imageGalleryView.adapter.notifyDataSetChanged()*/


    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {


        val fragmentManager = supportFragmentManager

        //TO companion ROUNTER
        when (item.itemId) {
            R.id.nav_camera -> {
                startActivity(Intent(applicationContext, FaceCameraActivity::class.java))
            }

            R.id.nav_qrscanner -> {
                startActivity(Intent(applicationContext, BarcodeActivity::class.java))
            }
            R.id.nav_gallery -> {
                drawer_layout.closeDrawers()

                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, GalleryFragment()).commit()


            }

            R.id.nav_forecast -> {
                startActivity(Intent(applicationContext, WeatherActivity::class.java))
            }

            R.id.logout -> {
                viewModel.logout()
                finish()
            }


            R.id.nav_animals_cataloque -> {
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, AnimalFragment()).commit()

            }


        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onClick(v: View) {
        when (v) {
            (actionSettings) -> {
                when (countRows) {
                    0 -> {
                        viewModel.counRows = 1
                    }

                    1 -> {
                        viewModel.counRows = 2
                    }
                    2 -> {
                        viewModel.counRows = 3
                    }
                    3 -> {
                        viewModel.counRows = 1
                    }
                }
            }
        }

    }


}


