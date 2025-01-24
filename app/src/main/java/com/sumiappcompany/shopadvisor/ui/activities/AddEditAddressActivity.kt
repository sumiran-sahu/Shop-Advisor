package com.sumiappcompany.shopadvisor.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputLayout
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.firestore.FireStoreClass
import com.sumiappcompany.shopadvisor.models.Address
import com.sumiappcompany.shopadvisor.utils.Constants

class AddEditAddressActivity : BaseActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var fullname:EditText
    private lateinit var phonenumber:EditText
    private lateinit var toolbartext:TextView
    private lateinit var address:EditText
    private lateinit var zipcode:EditText
    private lateinit var aditionalNote:EditText
    private lateinit var otherdetails:EditText
    private lateinit var submitaddress:Button
    private lateinit var otherbtn:RadioButton
    private lateinit var home:RadioButton
    private lateinit var office:RadioButton
    private lateinit var gropbtn:RadioGroup
    private lateinit var otherlayout:TextInputLayout

    private var mAddressDetails:Address?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)

        initializeall()
        actionBar()

        if(intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)){
            mAddressDetails=intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        }

        if (mAddressDetails!=null){
            if(mAddressDetails!!.id.isNotEmpty()){
                submitaddress.text="Update"
                toolbartext.text="Edit Address"
                fullname.setText(mAddressDetails?.name)
                phonenumber.setText(mAddressDetails?.mobileNumber)
                address.setText(mAddressDetails?.address)
                aditionalNote.setText(mAddressDetails?.additionalNote)
                zipcode.setText(mAddressDetails?.zipCode)

                when (mAddressDetails?.type) {
                    Constants.HOME -> {
                        home.isChecked = true
                    }
                    Constants.OFFICE -> {
                        office.isChecked = true
                    }
                    else -> {
                        otherbtn.isChecked = true
                        otherlayout.visibility = View.VISIBLE
                        otherdetails.setText(mAddressDetails?.otherDetails)
                    }
                }


            }
        }

        submitaddress.setOnClickListener {
            saveAddressToFirestore()
        }
        gropbtn.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId==R.id.rb_other){
                otherlayout.visibility=View.VISIBLE
            }else{
                otherlayout.visibility=View.GONE
            }

        }

    }

    //adding toolbar and go back function here
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

    //initialize the views here
    private fun initializeall() {
        //xmls

        toolbar = findViewById(R.id.toolbar_add_edit_address_activity)
        fullname=findViewById(R.id.et_full_name)
        phonenumber=findViewById(R.id.et_phone_number)
        address=findViewById(R.id.et_address)
        zipcode=findViewById(R.id.et_zip_code)
        aditionalNote=findViewById(R.id.et_additional_note)
        otherdetails=findViewById(R.id.et_other_details)
        submitaddress=findViewById(R.id.btn_submit_address)
        otherbtn=findViewById(R.id.rb_other)
        home=findViewById(R.id.rb_home)
        office=findViewById(R.id.rb_office)
        gropbtn=findViewById(R.id.rg_type)
        otherlayout=findViewById(R.id.til_other_details)
        toolbartext=findViewById(R.id.tv_title)


    }


    private fun validateData(): Boolean {
        return when {

            TextUtils.isEmpty(fullname.text.toString().trim { it <= ' ' }) -> {
               showErrorMessage("Please enter your name ",true)
                false
            }

            TextUtils.isEmpty(phonenumber.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage("please enter the phone number ",true)
                false
            }

            TextUtils.isEmpty(address.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage("please enter address", true)
                false
            }

            TextUtils.isEmpty(zipcode.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage("please enter zip Code", true)
                false
            }

            otherbtn.isChecked && TextUtils.isEmpty(
                otherbtn.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage("please enter zip Code", true)
                false
            }
            else -> {
                true
            }
        }
    }


    private fun saveAddressToFirestore() {

        // Here we get the text from editText and trim the space
        val fullName: String = fullname.text.toString().trim { it <= ' ' }
        val phoneNumber: String = phonenumber.text.toString().trim { it <= ' ' }
        val address: String = address.text.toString().trim { it <= ' ' }
        val zipCode: String = zipcode.text.toString().trim { it <= ' ' }
        val additionalNote: String = aditionalNote.text.toString().trim { it <= ' ' }
        val otherDetails: String = otherdetails.text.toString().trim { it <= ' ' }

        if (validateData()) {

            // Show the progress dialog.
            showProgressDialog(this@AddEditAddressActivity)

            val addressType: String = when {
                home.isChecked -> {
                    Constants.HOME
                }
                office.isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }

            val addressModel = Address(
                FireStoreClass().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )

            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                FireStoreClass().updateAddress(
                    this@AddEditAddressActivity,
                    addressModel,
                    mAddressDetails!!.id
                )
            } else {
                  FireStoreClass().addAddress(this@AddEditAddressActivity, addressModel)
            }


        }
    }

    fun addUpdateAddressSuccess(){

        hideProgressDialog()

        val notifySuccessMessage: String = if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
           "Address updated successfully"
        } else {
            "Address added successfully"
        }
        Toast.makeText(this@AddEditAddressActivity,notifySuccessMessage,Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        val intent=Intent(this,AddressListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK )
        startActivity(intent)
        finish()
    }
}