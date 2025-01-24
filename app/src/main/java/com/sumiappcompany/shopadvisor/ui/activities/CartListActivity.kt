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
import com.sumiappcompany.shopadvisor.models.CartItem
import com.sumiappcompany.shopadvisor.models.Product
import com.sumiappcompany.shopadvisor.utils.Constants

class CartListActivity : BaseActivity() {

    private lateinit var mProduCtList: ArrayList<Product>
    private lateinit var mCartListItems: ArrayList<CartItem>

    //xmls
    private lateinit var toolbar: Toolbar
    private lateinit var rclView: RecyclerView
    private lateinit var nocartproduct: TextView
    private lateinit var checkoutdata: LinearLayout
    private lateinit var subtotal: TextView
    private lateinit var shippingcharge: TextView
    private lateinit var total: TextView
    private lateinit var checkOut:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

        initializeall()
        actionBar()

        checkOut.setOnClickListener {
         val intent=Intent(this,AddressListActivity::class.java)
         intent.putExtra(Constants.EXTRA_SELECT_ADDRESS,true)
         startActivity(intent)
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

    private fun initializeall() {
        toolbar = findViewById(R.id.toolbar_cart_list_activity)
        rclView = findViewById(R.id.rv_cart_items_list)
        nocartproduct = findViewById(R.id.tv_no_cart_item_found)
        checkoutdata = findViewById(R.id.ll_checkout)
        subtotal = findViewById(R.id.tv_sub_total)
        shippingcharge = findViewById(R.id.tv_shipping_charge)
        total = findViewById(R.id.tv_total_amount)
        checkoutdata=findViewById(R.id.ll_checkout)
        checkOut=findViewById(R.id.btn_checkout)
    }

    fun successproductListFromFireStore(productList: ArrayList<Product>) {
        mProduCtList = productList
        hideProgressDialog()
        getCartItemList()
    }

    private fun getProductsList() {
        showProgressDialog(this)
        FireStoreClass().getAllProductListforcart(this@CartListActivity)

    }

    fun successCartItemList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()

        for (product in mProduCtList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {

                    cart.stock_quantity = product.stock_quantity

                    if (product.stock_quantity.toInt() == 0) {
                        cart.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        if (mCartListItems.size > 0) {
            rclView.visibility = View.VISIBLE
            nocartproduct.visibility = View.GONE
            checkoutdata.visibility = View.VISIBLE

            rclView.layoutManager = LinearLayoutManager(this@CartListActivity)
            rclView.setHasFixedSize(true)
            rclView.adapter = CartAdapter(this@CartListActivity,mCartListItems,true)

            var subTotal = 0.00
            for (item in mCartListItems) {

                val availableQuantity = item.stock_quantity.toInt()
                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()

                    subTotal += (price * quantity)

                }

            }
            subtotal.text = "INR$subTotal"
            shippingcharge.text = "INR 70"

            if (subTotal > 0) {
                checkoutdata.visibility = View.VISIBLE
                val totalamount = subTotal + 70
                total.text = "INR $totalamount"
            } else {
                checkoutdata.visibility = View.GONE
            }


        } else {
            rclView.visibility = View.GONE
            nocartproduct.visibility = View.VISIBLE
            checkoutdata.visibility = View.GONE

        }


    }

    private fun getCartItemList() {

        FireStoreClass().getCartLists(this@CartListActivity)
    }
     fun itemRemovedSuccess(){
        hideProgressDialog()
        Toast.makeText(this@CartListActivity,"Item Removed Successfully",Toast.LENGTH_SHORT).show()
        getCartItemList()
    }

    override fun onResume() {
        super.onResume()

        getProductsList()
    }

    fun itemUpdateSuccess(){
        hideProgressDialog()
        getCartItemList()
    }

}