package com.sumiappcompany.shopadvisor.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.firestore.FireStoreClass
import com.sumiappcompany.shopadvisor.models.CartItem
import com.sumiappcompany.shopadvisor.models.Product
import com.sumiappcompany.shopadvisor.utils.Constants
import com.sumiappcompany.shopadvisor.utils.GlideLoader


class ProductDetailsActivity : BaseActivity(), View.OnClickListener {

    private var mProductId: String = ""
    private lateinit var mProductDetails: Product
    private lateinit var ProductId: String
    private var mProductOwnerId: String = ""

    //xml s
    private lateinit var toolbar: Toolbar
    private lateinit var image: ImageView
    private lateinit var title: TextView
    private lateinit var price: TextView
    private lateinit var desc: TextView
    private lateinit var quan: TextView
    private lateinit var addcart: Button
    private lateinit var gotocart: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        initializeall()


        actionBar()
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!


        }

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            mProductOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        if (FireStoreClass().getCurrentUserID() == mProductOwnerId) {
            addcart.visibility = View.GONE
            gotocart.visibility = View.GONE
        } else {
            addcart.visibility = View.VISIBLE
            gotocart.visibility = View.VISIBLE
        }

        getproductDetails()
        addcart.setOnClickListener(this)
        gotocart.setOnClickListener(this)
    }

    private fun initializeall() {
        toolbar = findViewById(R.id.toolbar_product_details_activity)
        image = findViewById(R.id.product_detail_image)
        title = findViewById(R.id.tv_product_details_title)
        price = findViewById(R.id.tv_product_details_price)
        desc = findViewById(R.id.tv_product_details_description)
        quan = findViewById(R.id.tv_product_details_available_quantity)
        addcart = findViewById(R.id.btn_add_to_cart)
        gotocart = findViewById(R.id.go_to_cart_btn)
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

    fun getproductDetails() {
        showProgressDialog(this)
        FireStoreClass().gettingaProductDetails(this, mProductId)
    }

    fun productDetailsSuccess(product: Product) {
        mProductDetails = product


        GlideLoader(this).loadProductPicture(product.image, image)
        title.text = product.title
        price.text = product.price
        desc.text = product.description
        quan.text = product.stock_quantity

        if (product.stock_quantity.toInt() == 0) {
            hideProgressDialog()
            addcart.visibility = View.GONE
            quan.text = "OutOfStock"

            quan.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailsActivity,
                    R.color.colorSnackbarError
                )
            )
        } else {
            if (FireStoreClass().getCurrentUserID() == product.user_id) {
                hideProgressDialog()
            } else {
                ProductId = product.product_id
                FireStoreClass().checkIfProductPresentInCart(this@ProductDetailsActivity, ProductId)
            }

        }


    }

    private fun AddtoCart() {
        val addtocartitems = CartItem(
            FireStoreClass().getCurrentUserID(),
            mProductOwnerId,
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY,
            mProductDetails.stock_quantity
        )
        showProgressDialog(this@ProductDetailsActivity)
        FireStoreClass().addCartItems(this@ProductDetailsActivity, addtocartitems)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_add_to_cart -> {
                    AddtoCart()
                }
                R.id.go_to_cart_btn -> {
                    startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
                }
            }
        }
    }

    fun addtoCartSuccess() {
        hideProgressDialog()
        Toast.makeText(this@ProductDetailsActivity, "Added to cart", Toast.LENGTH_SHORT).show()
        addcart.visibility = View.GONE
        gotocart.visibility = View.VISIBLE
    }

    fun ProductExistInCart() {
        hideProgressDialog()
        addcart.visibility = View.GONE
        gotocart.visibility = View.VISIBLE

    }


}