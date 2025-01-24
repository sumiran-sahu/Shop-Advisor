package com.sumiappcompany.shopadvisor.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sumiappcompany.shopadvisor.R
import java.io.IOException

class GlideLoader(val context:Context) {

       fun loadUserPicture(image:Any,imageView: ImageView){
           try {
               Glide.with(context).load(image)
                   .centerCrop().placeholder(R.drawable.user)
                   .into(imageView)
           }catch (e:IOException){

           }
       }

    fun loadProductPicture(image:Any,imageView: ImageView){
        try {
            Glide.with(context).load(image)
                .centerCrop()
                .into(imageView)
        }catch (e:IOException){

        }
    }

}