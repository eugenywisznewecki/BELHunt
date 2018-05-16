package bel.ink.bel.belhunt.viewmodels


import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import bel.ink.bel.belhunt.utilits.AppRouter
import com.crashlytics.android.Crashlytics
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import msq.inok.bel.belhunt.util.SERVERKEYGOOGLE
import timber.log.Timber

class LoginViewModel(application: Application
                     /*private var router: AppRouter*/) : AndroidViewModel(application) {

    private lateinit var router: AppRouter
    init {
        router = AppRouter(application.applicationContext)
    }

    private val googleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(SERVERKEYGOOGLE)
                .requestEmail()
                .build()
    }

    internal val googleSingInClient by lazy {
        GoogleSignIn.getClient(application.applicationContext, googleSignInOptions)
    }
    private val authFB: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    internal fun login(task: Task<GoogleSignInAccount>) {

        try {
            val googleAccount = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(googleAccount)
        } catch (e: ApiException) {
            Crashlytics.log(e.message)
        }
    }


    internal fun firebaseAuthWithGoogle(googleAccount: GoogleSignInAccount) {
        Timber.d("firebaseAuthWithGoogle: + googleAccount.getId()")
        val credential = GoogleAuthProvider.getCredential(googleAccount.getIdToken(), null)
        try {
            authFB.signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                     router.openMainGaleryFromViewModel()
                }
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            authFB.signOut()
            Crashlytics.log(e.message)
        } catch (e: Exception) {
            Crashlytics.logException(e)
        }
    }
}