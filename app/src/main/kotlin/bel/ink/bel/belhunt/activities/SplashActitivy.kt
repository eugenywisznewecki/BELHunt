package bel.ink.bel.belhunt.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.animation.AnimationUtils
import bel.ink.bel.belhunt.R
import bel.ink.bel.belhunt.ui.interpolators.HeartInterpolator
import kotlinx.android.synthetic.main.activity_splash.*

/// SIMPLY SPLASH
// lately to get from firebase user's data
class SplashActitivy : AppCompatActivity() {


    private val splashHandler by lazy { Handler() }
    private lateinit var splashRunnuble: Runnable
    private val SPLASH_TIME = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val animation = AnimationUtils.loadAnimation(this, R.anim.alpha)
        animation.interpolator = HeartInterpolator()
        logoSplashImage.startAnimation(animation)

        splashRunnuble = Runnable {
            try {
                splashHandler.removeCallbacks(splashRunnuble)
                splashHandler.postDelayed(splashRunnuble, SPLASH_TIME)
            } finally {

                startActivity(Intent(this, LoginActivity::class.java))
                splashHandler.removeCallbacks(splashRunnuble)

                finish()
            }
        }

        splashHandler.postDelayed(splashRunnuble, SPLASH_TIME)
    }
}
