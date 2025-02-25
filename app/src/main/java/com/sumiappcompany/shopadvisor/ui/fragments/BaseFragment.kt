package com.sumiappcompany.shopadvisor.ui.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sumiappcompany.shopadvisor.R



open class BaseFragment : Fragment() {


    lateinit var mProgressDialog : Dialog



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false)


    }


    //progressbar
    fun showProgressDialog(context: Context){

        mProgressDialog = Dialog(context)
        val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        mProgressDialog.setContentView(inflate)
        mProgressDialog.setCancelable(false)
        mProgressDialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        mProgressDialog.show()
    }
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }


}