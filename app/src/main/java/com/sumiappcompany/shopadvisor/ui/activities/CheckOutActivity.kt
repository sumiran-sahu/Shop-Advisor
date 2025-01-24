package com.sumiappcompany.shopadvisor.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.adapters.CartAdapter
import com.sumiappcompany.shopadvisor.firestore.FireStoreClass
import com.sumiappcompany.shopadvisor.models.Address
import com.sumiappcompany.shopadvisor.models.CartItem
import com.sumiappcompany.shopadvisor.models.Order
import com.sumiappcompany.shopadvisor.models.Product
import com.sumiappcompany.shopadvisor.utils.Constants

class CheckOutActivity : BaseActivity() {

    //xml s
    private lateinit var toolbar: Toolbar
    private lateinit var rclview: RecyclerView
    private lateinit var type: TextView
    private lateinit var name: TextView
    private lateinit var address: TextView
    private lateinit var additionalnote: TextView
    private lateinit var otherdetails: TextView
    private lateinit var mobilenumber: TextView
    private lateinit var subtotal: TextView
    private lateinit var shippingcharge: TextView
    private lateinit var totalcharge: TextView
    private lateinit var placeorder: Button
    private lateinit var checkoutlayout:LinearLayout

    //data
    private var mAddressDetails:Address?=null
    private lateinit var mProductsList:ArrayList<Product>
    private lateinit var mCartItemsList:ArrayList<CartItem>
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0

       //after order placed
    private lateinit var mOrderDetails: Order



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)
        initializeall()
        actionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {

          mAddressDetails=intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS)
        }
        if (mAddressDetails!=null){
            type.text=mAddressDetails?.type
            name.text=mAddressDetails?.name
            address.text="${mAddressDetails!!.address},${mAddressDetails!!.zipCode}"
            additionalnote.text=mAddressDetails?.additionalNote
            mobilenumber.text=mAddressDetails?.mobileNumber
            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                otherdetails.text=mAddressDetails?.otherDetails
            }
        }
        getProductList()
        placeorder.setOnClickListener {
            placeAnOrder()

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
        toolbar = findViewById(R.id.toolbar_checkout_activity)
        rclview = findViewById(R.id.rv_cart_list_items)
        type = findViewById(R.id.tv_checkout_address_type)
        name = findViewById(R.id.tv_checkout_full_name)
        address = findViewById(R.id.tv_checkout_address)
        additionalnote = findViewById(R.id.tv_checkout_additional_note)
        otherdetails = findViewById(R.id.tv_checkout_other_details)
        mobilenumber = findViewById(R.id.tv_checkout_mobile_number)
        subtotal = findViewById(R.id.tv_checkout_sub_total)
        shippingcharge = findViewById(R.id.tv_checkout_shipping_charge)
        totalcharge = findViewById(R.id.tv_checkout_total_amount)
        placeorder = findViewById(R.id.btn_place_order)
        checkoutlayout=findViewById(R.id.ll_checkout_place_order)
    }


    private fun getProductList() {

        // Show the progress dialog.
        showProgressDialog(this@CheckOutActivity)

        FireStoreClass().getAllProductListforcart(this@CheckOutActivity)
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {

        mProductsList = productsList

        getCartItemsList()
    }

    private fun getCartItemsList() {

        FireStoreClass().getCartLists(this@CheckOutActivity)
    }
    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()
        for (product in mProductsList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity
                }
            }
        }
        mCartItemsList = cartList

       rclview.layoutManager = LinearLayoutManager(this@CheckOutActivity)
        rclview.setHasFixedSize(true)

        val cartListAdapter = CartAdapter(this@CheckOutActivity, mCartItemsList, false)
        rclview.adapter = cartListAdapter

        for (item in mCartItemsList) {

            val availableQuantity = item.stock_quantity.toInt()

            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()

                mSubTotal += (price * quantity)
            }
        }
        subtotal.text = "INR$mSubTotal"
        shippingcharge.text = "INR 70.0"
        if (mSubTotal > 0) {
            checkoutlayout.visibility = View.VISIBLE
            mTotalAmount = mSubTotal + 70.0
            totalcharge.text = "INR$mTotalAmount"
        } else {
            checkoutlayout.visibility = View.GONE
        }
    }



    private fun placeAnOrder() {

        // Show the progress dialog.
        showProgressDialog(this@CheckOutActivity)

        mOrderDetails = Order(
            FireStoreClass().getCurrentUserID(),
            mCartItemsList,
            mAddressDetails!!,
            "My order ${System.currentTimeMillis()}",
            mCartItemsList[0].image,
            mSubTotal.toString(),
            "70.0", // The Shipping Charge is fixed as $10 for now in our case.
            mTotalAmount.toString(),
            System.currentTimeMillis()
        )

       FireStoreClass().placeOrder(this@CheckOutActivity, mOrderDetails)
    }


    fun orderPlacedSuccess() {

        FireStoreClass().updateAllDetails(this@CheckOutActivity, mCartItemsList, mOrderDetails)

    }


    fun allDetailsUpdatedSuccessfully() {

        // Hide the progress dialog.
        hideProgressDialog()

        Toast.makeText(this@CheckOutActivity, "Your order placed successfully.", Toast.LENGTH_SHORT)
            .show()

        val intent = Intent(this@CheckOutActivity, DashBoardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


}