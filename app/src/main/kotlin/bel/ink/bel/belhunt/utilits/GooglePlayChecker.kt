package bel.ink.bel.belhunt.utilits

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class GooglePlayChecker(private val contextIn: Context) {

    public fun checkGooglePlay(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(contextIn)
        return (resultCode == ConnectionResult.SUCCESS)

    }
}

/*
val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
        getApplicationContext())
if (code != ConnectionResult.SUCCESS)
{
    val dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS)
    dlg.show()
}*/
