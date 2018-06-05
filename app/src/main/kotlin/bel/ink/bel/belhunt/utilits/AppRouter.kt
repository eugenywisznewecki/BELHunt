package bel.ink.bel.belhunt.utilits

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import bel.ink.bel.belhunt.R
import bel.ink.bel.belhunt.activities.*
import msq.inok.bel.belhunt.util.DIRECTORY_PHOTO
import msq.inok.bel.belhunt.util.FILE_PATH
import msq.inok.bel.belhunt.util.POSITION
import java.io.File

class AppRouter(val contextIn: Context) {

    companion object {
        val PATH_PHOTO_DIRECTORY = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), DIRECTORY_PHOTO)
    }


    fun openDelailActivity(file: File, position: Int) {

        val intent = Intent(contextIn, DetailActivity::class.java).putExtra(FILE_PATH, file).addFlags(FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(POSITION, position)
        contextIn.startActivity(intent)
    }

    fun openMainGalery(activity: AppCompatActivity) {
        activity.startActivity(Intent(contextIn, MainActivity::class.java))
    }

    fun openMainGaleryFromViewModel() {
        contextIn.startActivity(Intent(contextIn, MainActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK))
    }

    fun openCameraFromViewModel() {
        contextIn.startActivity(Intent(contextIn, FaceCameraActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK))
    }

    fun openLoginActivityFromViewModel(){
        contextIn.startActivity(Intent(contextIn, LoginActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK))
    }

    fun openFaceCamera(activity: AppCompatActivity) {

    }

    fun openQRScannerActivity(activity: AppCompatActivity) {
        contextIn.startActivity(Intent(contextIn, BarcodeActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK))
    }

    fun sharePhoto(image: String) {

        val imageFile = File(image)
        val uriToImage = Uri.fromFile(imageFile)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            setType("image/jpeg")
            putExtra(Intent.EXTRA_STREAM, uriToImage)

        }
        contextIn.startActivity(Intent.createChooser(shareIntent, contextIn.resources.getString(R.string.shareWith)).addFlags(FLAG_ACTIVITY_NEW_TASK))
    }
}