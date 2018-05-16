package bel.ink.bel.belhunt.utilits

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log

class NetChecker(val context: Context) {


	fun checInternet(): Boolean {
		val wifiCheck: NetworkInfo?
		val connectionManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		wifiCheck = connectionManager.getActiveNetworkInfo()

		if (wifiCheck != null) {
			when (wifiCheck.getType()) {
				(ConnectivityManager.TYPE_WIFI) -> {
					return true
				}
				(ConnectivityManager.TYPE_MOBILE) -> {
					return true
				}
			}
		} else Log.d("TAG", "wifiCheck null!")
		return false
	}


	//for intent services, will be used later
	fun isNetworkForBackgroundAwailable(): Boolean {
		val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val isNetAwailable = connectivityManager.activeNetworkInfo != null
		val isNetConnected = isNetAwailable && connectivityManager.activeNetworkInfo.isConnected
		return isNetConnected
	}
}