package msq.inok.bel.belhunt.ui.activities

import android.Manifest
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
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
import org.jetbrains.anko.toast


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





		presenter.observableIN = RxTextView.textChanges(cityTitleView)

	}


	override fun updateDataUI() {

		listStartActivity.adapter.notifyDataSetChanged()

		when {
			(listStartActivity.adapter.itemCount == 0) -> {
				listStartActivity.visibility = View.GONE
				imageGUNS.visibility = View.VISIBLE
			}
			(listStartActivity.adapter.itemCount > 0) -> {
				listStartActivity.visibility = View.VISIBLE
				imageGUNS.visibility = View.GONE
			}
		}
	}

	override fun onForecastsLoaded(forecastList: ForecastList) {
		toast("updated")
		listStartActivity.adapter = WeatherListAdapter(forecastList)
		updateDataUI()
	}


	override fun onShowErrorMessage(message: String) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}


	override fun onShowForecastList() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun sendObservable() {
		presenter.setObservableInPresenter(RxTextView.textChanges(cityTitleView))
	}

	override fun getData() {
		presenter.setEditTextListener()
	}
}

