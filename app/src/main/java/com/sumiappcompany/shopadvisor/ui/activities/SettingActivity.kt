package com.sumiappcompany.shopadvisor.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.firestore.FireStoreClass
import com.sumiappcompany.shopadvisor.models.User
import com.sumiappcompany.shopadvisor.utils.Constants
import com.sumiappcompany.shopadvisor.utils.GlideLoader


class SettingActivity : BaseActivity(), View.OnClickListener {

    private lateinit var toolbar: Toolbar
    private lateinit var imageView: ImageView
    private lateinit var name: TextView
    private lateinit var gender: TextView
    private lateinit var email: TextView
    private lateinit var phnumber: TextView
    private lateinit var logout: Button
    private lateinit var edit: TextView
    private lateinit var Addresslist:LinearLayout


    private lateinit var mUserDeatils: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        initializeall()
        actionBar()
        getUserdetails()


        edit.setOnClickListener(this)
        logout.setOnClickListener(this)
        Addresslist.setOnClickListener(this)
    }

    @SuppressLint("RestrictedApi")
    private fun actionBar() {

        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        if (actionbar != null) {

            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setDisplayShowHomeEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.back_arrow)
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

    }

    private fun getUserdetails() {

        FireStoreClass().getCurrentDetails(this@SettingActivity)
    }

    fun userDetailsSuccess(user: User) {
        hideProgressDialog()
        mUserDeatils=user


            GlideLoader(this@SettingActivity).loadUserPicture(user.image, imageView)

        name.text = "${user.firstName} ${user.lastName}"
        email.text = user.email
        phnumber.text = user.mobile.toString()
        gender.text = user.gender

    }

    private fun initializeall() {


        toolbar = findViewById(R.id.setting_toolbar)
        imageView = findViewById(R.id.profile_image)
        name = findViewById(R.id.username)
        email = findViewById(R.id.userEmail)
        gender = findViewById(R.id.userGender)
        phnumber = findViewById(R.id.userPhnumber)
        logout = findViewById(R.id.Logoutbtn)
        edit = findViewById(R.id.editbtn)
        Addresslist=findViewById(R.id.ll_address)
    }

    override fun onResume() {
        super.onResume()
        showProgressDialog(this)
        getUserdetails()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.Logoutbtn -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingActivity, LogInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.editbtn -> {
                     val intent=Intent(this@SettingActivity,UserProfileActivity::class.java)
                    intent.putExtra(Constants.ExtraUserDetails,mUserDeatils)
                    startActivity(intent)
                }
                R.id.ll_address->{
                    val intent=Intent(this@SettingActivity,AddressListActivity::class.java)
                    startActivity(intent)
                }
            }
        }

    }
}


