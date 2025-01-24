package com.sumiappcompany.shopadvisor.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.firestore.FireStoreClass
import com.sumiappcompany.shopadvisor.models.User
import com.sumiappcompany.shopadvisor.utils.Constants

class LogInActivity : BaseActivity() {
    //xml s
    private lateinit var gotoRegister: TextView
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var login: Button
    private lateinit var forgetPassword: TextView

    //firebase
    private lateinit var mAuth: FirebaseAuth







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)


        initialize()
        gotoRegister.setOnClickListener {
            val intent = Intent(this@LogInActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {
            logInUser()
        }

        forgetPassword.setOnClickListener {
            val intent = Intent(this@LogInActivity, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }


    }


    private fun initialize() {
        gotoRegister = findViewById(R.id.tv_registerpage)
        email = findViewById(R.id.et_EmailLogIn)
        password = findViewById(R.id.et_passwordLogIn)

        login = findViewById(R.id.login_btn)
        forgetPassword = findViewById(R.id.ForgetPassword)

        mAuth = FirebaseAuth.getInstance()

    }

    private fun logInUser() {


        //get the user email and password
        val myEmail = email.text.toString().trim { it <= ' ' }
        val myPassword = password.text.toString().trim { it <= ' ' }
        if (validateLoginDetails()) {
            showProgressDialog(this)

            //logIn using firebase
            mAuth.signInWithEmailAndPassword(myEmail, myPassword).addOnCompleteListener {

                if (it.isSuccessful) {
                    FireStoreClass().getCurrentDetails(this@LogInActivity)
                } else {
                    hideProgressDialog()
                    showErrorMessage( it.exception?.message.toString(), true)
                }
            }

        }
    }

    //validating user inputs
    private fun validateLoginDetails(): Boolean {
        return when {


            TextUtils.isEmpty(email.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage( "please fill the email", true)
                false
            }
            TextUtils.isEmpty(password.text.toString().trim { it <= ' ' }) -> {
                showErrorMessage( "please fill the password", true)
                false
            }
            else -> {
//                showErrorMessage(this, "Your credentials are valid", true)
                true
            }

        }
    }

    //user login successful
    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()



        if (user.profileCompleted == 0) {
            val intent = Intent(this@LogInActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.ExtraUserDetails, user)
            startActivity(intent)
        } else {
            val intent = Intent(this@LogInActivity, DashBoardActivity::class.java)
            startActivity(intent)
        }
        finish()


    }





}