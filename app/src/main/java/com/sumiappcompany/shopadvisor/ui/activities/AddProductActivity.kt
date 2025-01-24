package com.sumiappcompany.shopadvisor.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import com.sumiappcompany.shopadvisor.models.Product
import com.sumiappcompany.shopadvisor.utils.Constants
import com.sumiappcompany.shopadvisor.utils.GlideLoader


class AddProductActivity : BaseActivity(), View.OnClickListener {


    private lateinit var pname: EditText
    private lateinit var pprice: EditText
    private lateinit var pdescription: EditText
    private lateinit var pquantity: EditText
    private lateinit var submit: Button

    private lateinit var toolbar: Toolbar
    private lateinit var addimageproduct: ImageButton
    private lateinit var productimage: ImageView

    private var mSelectedImageFileUri: Uri? = null
    private var mProductImageUrl: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        initializeall()
        actionBar()
        addimageproduct.setOnClickListener(this@AddProductActivity)
        submit.setOnClickListener(this@AddProductActivity)
    }


    private fun initializeall() {


        toolbar = findViewById(R.id.toolbar_addproduct_profile)
        addimageproduct = findViewById(R.id.add_image)
        productimage = findViewById(R.id.product_pic)
        pname = findViewById(R.id.ProductTitle)
        pprice = findViewById(R.id.productPrice)
        pdescription = findViewById(R.id.productDescription)
        pquantity = findViewById(R.id.productQuantity)
        submit = findViewById(R.id.btn_submit)
    }

    //seting us the action  bar
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

    //starting an intent to show the images from the gallery
    private fun showImageChooser() {
        getAction.launch("image/*")
    }

    //after picking the image from gallery
    private val getAction = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback { uri ->

            mSelectedImageFileUri = uri

            if (mSelectedImageFileUri != null) {
                addimageproduct.setImageResource(R.drawable.ic_baseline_edit_24)
            }
            GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, productimage)


        })

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
                    this@AddProductActivity,
                    getString(R.string.storage),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.add_image -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        showImageChooser()
                    } else {
                        ActivityCompat.requestPermissions(
                            this@AddProductActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }


                }
                R.id.btn_submit -> {
                    if (validatinguserProfileDetails()) {
                        uploadProductImage()
                    }
                }

            }
        }
    }

    private fun uploadProductImage() {
        showProgressDialog(this)
        FireStoreClass().uploadImagetoCloudStorage(
            this,
            mSelectedImageFileUri,
            Constants.PRODUCT_IMAGE
        )

    }

    fun successfullyuploadedImage(imageUrl: String) {
        mProductImageUrl = imageUrl

        //upload product details
        uploadProductDetails()

    }

    fun productUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(this@AddProductActivity,"Product Upload Success",Toast.LENGTH_SHORT).show()
       finish()
    }

    private fun uploadProductDetails() {
        val username = this.getSharedPreferences(
            Constants.ShopAdvisor_Preferences, Context.MODE_PRIVATE
        ).getString(Constants.LOGGED_IN_USERNAME, "")!!

        val product=Product(
            FireStoreClass().getCurrentUserID(),
            username,
            pname.text.toString().trim{it<=' '},
            pprice.text.toString().trim{it<=' '},
            pdescription.text.toString().trim{it<=' '},
            pquantity.text.toString().trim{it<=' '},
            mProductImageUrl
        )


        FireStoreClass().UploadProductsDetails(this,product)

    }

    private fun validatinguserProfileDetails(): Boolean {

        return when {
            mSelectedImageFileUri == null -> {
                showErrorMessage( "Please select a image ", true)
                false
            }
            TextUtils.isEmpty(pname.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage( "Please enter product name", true)
                false
            }
            TextUtils.isEmpty(pprice.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage( "Please enter product price", true)
                false
            }
            TextUtils.isEmpty(pdescription.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage( "Please enter product description", true)
                false
            }
            TextUtils.isEmpty(pquantity.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage( "Please enter product quantity", true)
                false
            }
            else -> {
                true
            }


        }
    }
}