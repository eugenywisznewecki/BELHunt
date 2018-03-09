package msq.inok.bel.testKKD.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log

/**
 * Created by User on 13.01.2018.
 */
class WifiChecker(val context: Context) {


	fun checkWifi(): Boolean {
		val wifiCheck: NetworkInfo?
		val connectionManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		wifiCheck = connectionManager?.getActiveNetworkInfo()

		if (wifiCheck != null) {
			when (wifiCheck.getType()) {
				(ConnectivityManager.TYPE_WIFI) -> {
					return true
				}
				(ConnectivityManager.TYPE_MOBILE) -> {
					return false
				}
				(ConnectivityManager.TYPE_MOBILE) -> {
					return false
				}
			}
		} else Log.d("TAG", "wifiCheck null!")
		return false
	}


	fun isNetworkForBackgroundAwailable(): Boolean {
		val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val isNetAwailable = connectivityManager.activeNetworkInfo != null
		val isNetConnected = isNetAwailable && connectivityManager.activeNetworkInfo.isConnected
		return isNetConnected
	}
}