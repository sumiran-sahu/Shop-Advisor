package com.sumiappcompany.shopadvisor.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.firestore.FireStoreClass
import com.sumiappcompany.shopadvisor.models.User
import com.sumiappcompany.shopadvisor.utils.Constants
import com.sumiappcompany.shopadvisor.utils.GlideLoader



class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserdetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var muserProfileImageUrl: String = ""
    private lateinit var toolbar: Toolbar
    private lateinit var title: TextView
    private lateinit var image:ImageView
    private lateinit var firstname: TextView
    private lateinit var lastname: TextView
    private lateinit var emailid: TextView
    private lateinit var mobilenumber: TextView
    private lateinit var male:RadioButton
    private lateinit var female:RadioButton
    private lateinit var save:Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        initializeall()



        if (intent.hasExtra(Constants.ExtraUserDetails)) {

            mUserdetails = intent.getParcelableExtra(Constants.ExtraUserDetails)!!

        }

        firstname.text = mUserdetails.firstName
        lastname.text = mUserdetails.lastName
        emailid.isEnabled = false
        emailid.text = mUserdetails.email

        if (mUserdetails.profileCompleted == 0) {
            title.text = "Complete Profile"

            firstname.isEnabled = false
            lastname.isEnabled = false

        } else {
            actionBar()
            title.text = "Edit Profile"
            GlideLoader(this).loadUserPicture(mUserdetails.image, image)
            if (mUserdetails.mobile != 0L) {
                mobilenumber.text = mUserdetails.mobile.toString()
            }
            if (mUserdetails.gender == Constants.MALE) {
                male.isChecked = true
            } else {
                female.isChecked = true
            }

        }



        image.setOnClickListener(this)
        save.setOnClickListener(this)


    }

    private fun initializeall() {
        toolbar = findViewById(R.id.toolbar_user_profile)
        title = findViewById(R.id.title_text)
        image=findViewById(R.id.user_photo)
        firstname=findViewById(R.id.etfirstNameUser)
        lastname=findViewById(R.id.etlastNameUser)
        emailid=findViewById(R.id.etEmail)
        mobilenumber=findViewById(R.id.etmobile)
        male=findViewById(R.id.male)
        female=findViewById(R.id.female)
        save=findViewById(R.id.btn_save)
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

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.user_photo -> {
                    //check if the permission are granted or not  else request for the accepting
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        showImageChooser()

                    } else {
                        ActivityCompat.requestPermissions(
                            this@UserProfileActivity,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }


                }

                R.id.btn_save -> {

                    if (validatinguserProfileDetails()) {
                        showProgressDialog(this)
                        updateuserprofiledetails()
                    }

                }


            }

        }
    }

    private fun updateuserprofiledetails() {
        val userhashmap = HashMap<String, Any>()

        val mobile = mobilenumber.text.toString().trim { it <= ' ' }
        val first_name = firstname.text.toString().trim { it <= ' ' }
        if (first_name != mUserdetails.firstName) {
            userhashmap[Constants.FIRESTNAME] = first_name
        }
        val last_name = lastname.text.toString().trim { it <= ' ' }
        if (last_name != mUserdetails.lastName) {
            userhashmap[Constants.LASTNAME] = last_name
        }


        val gender = if (male.isChecked) Constants.MALE else Constants.FEMALE
        if (mobile.isNotEmpty() && mobile != mUserdetails.mobile.toString()) {
            userhashmap[Constants.MOBILE] = mobile.toLong()
        }
        if (gender.isNotEmpty() && gender != mUserdetails.gender) {
            userhashmap[Constants.GENDER] = gender
        }

        if(muserProfileImageUrl.isNotEmpty()&&muserProfileImageUrl!=mUserdetails.image.toString()){
            userhashmap[Constants.IMAGE]=mUserdetails.image
        }

        userhashmap[Constants.GENDER]=gender

        userhashmap[Constants.IMAGE] = muserProfileImageUrl
        userhashmap[Constants.COMPLETE_PROFILE] = 1

        FireStoreClass().updateUserProfileData(this, userhashmap)
    }

    fun userProfileSuccessfullyUpdated() {
        hideProgressDialog()
        Toast.makeText(this@UserProfileActivity, "Successfully updated", Toast.LENGTH_SHORT).show()

        val intent = Intent(this@UserProfileActivity, DashBoardActivity::class.java)
        startActivity(intent)
        finish()
    }


    //check if the permissions are accepted or not
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
              //if permission is granted or else
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()

            } else {
                Toast.makeText(
                    this@UserProfileActivity,
                    getString(R.string.storage),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //starting an intent to show the images from the gallery
    private fun showImageChooser() {
        getAction.launch("image/*")
    }

    //after picking the image from gallery
    private val getAction = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback { uri ->

            mSelectedImageFileUri = uri
            showProgressDialog(this)
            GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, image)

            FireStoreClass().uploadImagetoCloudStorage(
                this@UserProfileActivity,
                mSelectedImageFileUri,Constants.USER_PROFILE_IMAGE
            )


            hideProgressDialog()
            Toast.makeText(this, "uploaded", Toast.LENGTH_LONG).show()
        })


    private fun validatinguserProfileDetails(): Boolean {

        return when {

            TextUtils.isEmpty(mobilenumber.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage( "Please enter mobile number", true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun successfullyuploadedImage(imageUrl: String) {
        hideProgressDialog()
        muserProfileImageUrl = imageUrl

    }


}
