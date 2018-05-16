package bel.ink.bel.belhunt.utilits

import android.content.Context

class UserSessionWriter(context: Context) {

    private val PREF_NAME = "SessionWriter"
    private val KEY_IS_FIRSTTIME_LAUNCH = "IsFirstTime"
    private val KEY_IS_REGISTERED_USER = "IsRegistered"

    val sharedPreferences by lazy { context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) }
    val editor by lazy { sharedPreferences.edit() }

    fun isFirstLaunch() = when (sharedPreferences.getBoolean(KEY_IS_FIRSTTIME_LAUNCH, true)) {
        (true) -> {
            editor.putBoolean(KEY_IS_FIRSTTIME_LAUNCH, false)
            editor.commit()
            true
        }
        (false) -> {
            editor.commit()
            false
        }
    }

    fun isRegistered() = sharedPreferences.getBoolean(KEY_IS_REGISTERED_USER, false)

    fun setRegistered(isResistered: Boolean): Boolean {
        editor.putBoolean(KEY_IS_REGISTERED_USER, isResistered)
        editor.commit()
        return true
    }




}