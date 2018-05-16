package bel.ink.bel.belhunt.activities

import android.Manifest
import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import bel.ink.bel.belhunt.R
import bel.ink.bel.belhunt.viewmodels.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.activity_login.*
import msq.inok.bel.belhunt.util.GOOGLE_INTENT_ID
import msq.inok.bel.belhunt.util.REQUEST_CAMERA_PERMISSION
import msq.inok.bel.belhunt.util.REQUEST_FINE_LOC
import msq.inok.bel.belhunt.util.REQUEST_STORAGE_PERMISSION


class LoginActivity : AppCompatActivity() {

    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        signInGooelView.setOnClickListener {
            val intent = loginViewModel.googleSingInClient.signInIntent
            startActivityForResult(intent, GOOGLE_INTENT_ID)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        progressRegisterAcoount.visibility = View.VISIBLE
        if (requestCode == GOOGLE_INTENT_ID) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            loginViewModel.login(task)
        }
    }


    override fun onResume() {
        super.onResume()
        requestPermissions()

    }


    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
        }

        if ((ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)) {
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOC)
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPermissions()
                } else {
                    Toast.makeText(this@LoginActivity, "CAMERA PERMISSION REQUIRED!", Toast.LENGTH_LONG).show()
                    alert()
                    finish()
                }
            }

            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPermissions()
                } else {
                    Toast.makeText(this@LoginActivity, "STORAGE PERMISSION REQUIRED", Toast.LENGTH_LONG).show()
                    alert()
                    finish()
                }
            }

            REQUEST_FINE_LOC -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPermissions()
                } else {
                    Toast.makeText(this@LoginActivity, "GPS PERMISSION REQUIRED!", Toast.LENGTH_LONG).show()
                    alert()
                    finish()
                }
            }
        }
    }

    fun alert() {
        val listener = DialogInterface.OnClickListener { dialog, id -> finish() }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show()
    }

}



