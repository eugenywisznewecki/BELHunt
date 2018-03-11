package msq.inok.bel.belhunt.ui.activities

import android.Manifest
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.jakewharton.rxbinding2.widget.RxTextView
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_start.*
import msq.inok.bel.belhunt.App
import msq.inok.bel.belhunt.R
import msq.inok.bel.belhunt.entities.ForecastList
import msq.inok.bel.belhunt.mvp.presenters.Presenter
import msq.inok.bel.belhunt.mvp.view.ImvpMainView
import msq.inok.bel.belhunt.ui.listAdapters.WeatherListAdapter


class StartActivity : MvpAppCompatActivity(), ImvpMainView {

	@InjectPresenter(type = PresenterType.GLOBAL)
	lateinit var presenter: Presenter


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_start)
		Log.d("TAG", "oncreate")

		App.component.inject(this)

		Dexter.withActivity(this)
				.withPermissions(
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.ACCESS_NETWORK_STATE,
						Manifest.permission.INTERNET,
						Manifest.permission.BIND_REMOTEVIEWS // possibly doesnt not work
				)
				.withListener(BaseMultiplePermissionsListener())
				.check()


		listStartActivity.layoutManager = LinearLayoutManager(this)

		//sends observable to presenter
		presenter.observableIN = RxTextView.textChanges(cityTitleView)


		//crutch!!! TODO do this in a right way!
		cityTitleView.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(p0: Editable?) {
				presenter.startSubscription()
				presenter.isNewActivity = false
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

			}

			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

			}
		})
	}


	override fun onForecastsLoaded(forecastList: ForecastList?) {
		if (forecastList != null) {
			listStartActivity.adapter = WeatherListAdapter(forecastList)
			listStartActivity.adapter.notifyDataSetChanged()

			listStartActivity.visibility = View.VISIBLE
			imageGUNS.visibility = View.GONE
		} else {
			listStartActivity.visibility = View.GONE
			imageGUNS.visibility = View.VISIBLE
		}
	}


	override fun onDestroy() {
		super.onDestroy()
	}
}

