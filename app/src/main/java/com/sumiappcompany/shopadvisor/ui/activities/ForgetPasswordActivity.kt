package com.sumiappcompany.shopadvisor.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.sumiappcompany.shopadvisor.R
import kotlinx.coroutines.channels.Channel

class ForgetPasswordActivity : BaseActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var email:EditText
    private lateinit var  submit:Button


    val channel = Channel<String> {  }

    //Firebase
    private lateinit var auth:FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

       initialize()

        //adding toolbar and go back function here
        actionBar()

        submit.setOnClickListener {
             val myEmail=email.text.toString().trim{it<=' '}
            if (myEmail.isEmpty()){
                showErrorMessage( getString(R.string.enterEmail), true)
            }else {
                showProgressDialog(this)
                auth.sendPasswordResetEmail(myEmail).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this@ForgetPasswordActivity,getString(R.string.check),Toast.LENGTH_LONG).show()
                        finish()
                    }else{
                        showErrorMessage(it.exception?.message.toString(),true)
                    }
                }

            }
        }
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

    private fun initialize(){
        toolbar=findViewById(R.id.toolbar_forget_activity)
        submit=findViewById(R.id.submit_btn)
        email=findViewById(R.id.et_EmailForget)

        auth=FirebaseAuth.getInstance()
    }
}