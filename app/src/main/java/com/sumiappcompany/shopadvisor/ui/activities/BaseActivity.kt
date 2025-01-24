package com.sumiappcompany.shopadvisor.ui.activities


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.sumiappcompany.shopadvisor.R

open class BaseActivity : AppCompatActivity() {

    private var doubleBackPressed=false

    private lateinit var mProgressDialog: Dialog

    fun showErrorMessage( message :String, errormessage:Boolean){
//        val snackbar=Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message,
            Snackbar.LENGTH_LONG).setAction("Action", null).setTextColor(Color.WHITE)


        if (errormessage){
            snackbar.setBackgroundTint(Color.RED)
        }else{
            snackbar.setBackgroundTint(Color.GREEN)
        }


        snackbar.show()
    }
//    //progressbar
//    fun showProgressDialog(context: Context){
//
//        mProgressDialog = Dialog(context)
//        var inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
//        mProgressDialog.setContentView(inflate)
//        mProgressDialog.setCancelable(false)
//        mProgressDialog.window!!.setBackgroundDrawable(
//            ColorDrawable(Color.TRANSPARENT)
//        )
        fun showProgressDialog(context: Context) {
            mProgressDialog = Dialog(this)
            mProgressDialog.setContentView(R.layout.progress_dialog)
            mProgressDialog.setCancelable(false)
            mProgressDialog.setCanceledOnTouchOutside(false)
//            mProgressDialog.window!!.setBackgroundDrawable(
//                ColorDrawable(Color.TRANSPARENT)
//            )
            //Start the dialog and display it on screen.
            mProgressDialog.show()
        }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }


    fun doublebacktoexit(){
        if(doubleBackPressed){
            super.onBackPressed()
            return
        }

        this.doubleBackPressed=true

        Toast.makeText(this,"Please click back again to exit ",Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({doubleBackPressed=false},2000)

    }


}


