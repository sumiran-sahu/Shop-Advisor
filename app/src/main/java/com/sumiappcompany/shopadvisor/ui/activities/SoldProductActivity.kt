package com.sumiappcompany.shopadvisor.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.models.SoldProduct
import com.sumiappcompany.shopadvisor.utils.Constants
import com.sumiappcompany.shopadvisor.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*

class SoldProductActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar

    private lateinit var orderid: TextView
    private lateinit var orderdate: TextView
    private lateinit var pimage: ImageView
    private lateinit var pname: TextView
    private lateinit var pprice: TextView
    private lateinit var pquantity: TextView
    private lateinit var atype: TextView
    private lateinit var aname: TextView
    private lateinit var aaddress: TextView
    private lateinit var aaditionalnote: TextView
    private lateinit var aotherdetails: TextView
    private lateinit var amobile: TextView
    private lateinit var subtotal: TextView
    private lateinit var shipping: TextView
    private lateinit var total: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sold_product)

        initializeall()
        actionBar()

        var productDetails = SoldProduct()

        if (intent.hasExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)) {
            productDetails =
                intent.getParcelableExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)!!
        }
        setupUI(productDetails)

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
        //xmls
        toolbar = findViewById(R.id.toolbar_sold_product_details_activity)
        orderid = findViewById(R.id.tv_sold_product_details_id)
        orderdate = findViewById(R.id.tv_sold_product_details_date)
        pimage = findViewById(R.id.iv_product_item_image)
        pname = findViewById(R.id.tv_product_item_name)
        pprice = findViewById(R.id.tv_product_item_price)
        pquantity = findViewById(R.id.tv_sold_product_quantity)
        atype = findViewById(R.id.tv_sold_details_address_type)
        aname = findViewById(R.id.tv_sold_details_full_name)
        aaddress = findViewById(R.id.tv_sold_details_address)
        aaditionalnote = findViewById(R.id.tv_sold_details_additional_note)
        aotherdetails = findViewById(R.id.tv_sold_details_other_details)
        amobile = findViewById(R.id.tv_sold_details_mobile_number)
        subtotal = findViewById(R.id.tv_sold_product_sub_total)
        shipping = findViewById(R.id.tv_sold_product_shipping_charge)
        total = findViewById(R.id.tv_sold_product_total_amount)


    }

    private fun setupUI(productDetails: SoldProduct) {

        orderid.text = productDetails.order_id

        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = productDetails.order_date
        orderdate.text = formatter.format(calendar.time)

        GlideLoader(this@SoldProductActivity).loadProductPicture(
            productDetails.image,
            pimage
        )
        pname.text = productDetails.title
        pprice.text = "$${productDetails.price}"
        pquantity.text = productDetails.sold_quantity

        atype.text = productDetails.address.type
        aname.text = productDetails.address.name
        aaddress.text =
            "${productDetails.address.address}, ${productDetails.address.zipCode}"
        aaditionalnote.text = productDetails.address.additionalNote

        if (productDetails.address.otherDetails.isNotEmpty()) {
            aotherdetails.visibility = View.VISIBLE
            aotherdetails.text = productDetails.address.otherDetails
        } else {
            aotherdetails.visibility = View.GONE
        }
        amobile.text = productDetails.address.mobileNumber

        subtotal.text = productDetails.sub_total_amount
        shipping.text = productDetails.shipping_charge
        total.text = productDetails.total_amount
    }

}