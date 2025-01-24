package com.sumiappcompany.shopadvisor.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.firestore.FireStoreClass
import com.sumiappcompany.shopadvisor.models.User


class RegisterActivity : BaseActivity() {
    //xml s
    private lateinit var gotoLogin: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var firstname: EditText
    private lateinit var lastname: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var cpassword: EditText
    private lateinit var cbox: CheckBox
    private lateinit var register: Button


    //firebase variables
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initializeall()
        actionBar()


        //  if clicked go to login page
        gotoLogin.setOnClickListener {
            onBackPressed()
        }
        //register user if clicked after validating
        register.setOnClickListener {
            registerUser()
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
        gotoLogin = findViewById(R.id.tv_loginpage)
        toolbar = findViewById(R.id.toolbar_register_activity)
        firstname = findViewById(R.id.etfirstName)
        lastname = findViewById(R.id.etlastName)
        email = findViewById(R.id.etEmail)
        password = findViewById(R.id.etpassword)
        cpassword = findViewById(R.id.etconfirmpassword)
        cbox = findViewById(R.id.checkboxRegister)
        register = findViewById(R.id.Register_btn)

        //firebase initialize
        auth = FirebaseAuth.getInstance()


    }

    //a function to validate the user credentials
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(firstname.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage("please fill the firstname", true)
                false
            }
            TextUtils.isEmpty(lastname.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage("please fill the lastname", true)
                false
            }
            TextUtils.isEmpty(email.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage("please fill the email", true)
                false
            }
            TextUtils.isEmpty(password.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage("please fill the password", true)
                false
            }
            TextUtils.isEmpty(cpassword.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage("please fill the confirm password", true)
                false
            }
            password.text.toString().trim { it <= ' ' } != cpassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorMessage("please confirm password correctly", true)
                false
            }
            !cbox.isChecked -> {
                showErrorMessage("please agree Terms&Conditions", true)
                false
            }

            else -> {
//                showErrorMessage(this, "Your credentials are valid", true)
                true
            }

        }
    }

    //register function
    private fun registerUser() {

        if (validateRegisterDetails()) {
            showProgressDialog(this)

            val myemail = email.text.toString().trim { it <= ' ' }
            val mypassword = password.text.toString().trim { it <= ' ' }


            //creating the user with email and password
            auth.createUserWithEmailAndPassword(myemail, mypassword).addOnCompleteListener(
                OnCompleteListener {

                    //if registation is successful or not
                    if (it.isSuccessful) {
                        val firebaseUser = it.result!!.user!!
                        val user = User(
                            firebaseUser.uid,
                            firstname.text.toString().trim { it <= ' ' },
                            lastname.text.toString().trim { it <= ' ' },
                            email.text.toString().trim { it <= ' ' },
                        )

                        FireStoreClass().registerUser(this@RegisterActivity, user)


                    } else {
                        hideProgressDialog()
                        showErrorMessage(it.exception?.message.toString(), true)

                    }

                })

        }


    }

    fun userRegistationSuccess() {
        hideProgressDialog()
        Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()
        val intent = Intent(this@RegisterActivity, LogInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
    }


}