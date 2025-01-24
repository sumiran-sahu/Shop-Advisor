package com.sumiappcompany.shopadvisor.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myshoppal.utils.SwipeToDeleteCallback
import com.myshoppal.utils.SwipeToEditCallback
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.adapters.AddressListAdapter
import com.sumiappcompany.shopadvisor.firestore.FireStoreClass
import com.sumiappcompany.shopadvisor.models.Address
import com.sumiappcompany.shopadvisor.utils.Constants



class AddressListActivity : BaseActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var addAddress: TextView
    private lateinit var noaddressfound: TextView
    private lateinit var titleofToolbar: TextView
    private lateinit var rclview: RecyclerView

    private var mSelectedAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)


        initializeall()
        actionBar()
        addAddress.setOnClickListener {
            openSomeActivityForResult()

        }


        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectedAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }

        if (mSelectedAddress) {
            titleofToolbar.text = "SELECT ADDRESS"
            addAddress.visibility=View.GONE

        }




    }

    private fun openSomeActivityForResult() {

        val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
        resultLauncher.launch(intent)
    }


    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                getAddressList()

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

        toolbar = findViewById(R.id.toolbar_address_list_activity)
        addAddress = findViewById(R.id.tv_add_address)
        rclview = findViewById(R.id.rv_address_list)
        noaddressfound = findViewById(R.id.tv_no_address_found)
        titleofToolbar = findViewById(R.id.tv_title)
    }

    private fun getAddressList() {

        // Show the progress dialog.
        showProgressDialog(this)

        FireStoreClass().getAddressesList(this@AddressListActivity)
    }


    fun successAddressListFromFirestore(addressList: ArrayList<Address>) {

        hideProgressDialog()

        if (addressList.size > 0) {
            rclview.visibility = View.VISIBLE
            noaddressfound.visibility = View.GONE

            rclview.layoutManager = LinearLayoutManager(this@AddressListActivity)
            rclview.setHasFixedSize(true)
            val addressListAdapter =
                AddressListAdapter(this@AddressListActivity, addressList, mSelectedAddress)
            rclview.adapter = addressListAdapter
            if (!mSelectedAddress) {
                val editswipehandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = rclview.adapter as AddressListAdapter
                        adapter.notifyEditItem(this@AddressListActivity, viewHolder.adapterPosition)
                    }
                }
                val deleteswipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressDialog(this@AddressListActivity)
                        FireStoreClass().deleteAddress(
                            this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id
                        )
                    }

                }

                val deleteItemTouchHelper = ItemTouchHelper(deleteswipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rclview)


                val editItemTouchHelper = ItemTouchHelper(editswipehandler)
                editItemTouchHelper.attachToRecyclerView(rclview)
            }


        } else {
            rclview.visibility = View.GONE
            noaddressfound.visibility = View.VISIBLE
        }


    }

    fun deleteAddressSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Address deleted", Toast.LENGTH_SHORT).show()
        getAddressList()
    }

    override fun onResume() {
        super.onResume()
        getAddressList()
    }

}