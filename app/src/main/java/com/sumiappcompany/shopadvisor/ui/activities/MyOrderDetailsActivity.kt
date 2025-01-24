package com.sumiappcompany.shopadvisor.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.adapters.CartAdapter
import com.sumiappcompany.shopadvisor.models.Order
import com.sumiappcompany.shopadvisor.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyOrderDetailsActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var orderid:TextView
    private lateinit var orderdate:TextView
    private lateinit var orderStatus:TextView
    private lateinit var rclView:RecyclerView

    private lateinit var Atype:TextView
    private lateinit var Aname:TextView
    private lateinit var Aaddress:TextView
    private lateinit var AadditionalNote:TextView
    private lateinit var AotherDetails:TextView
    private lateinit var Amobile:TextView


    private lateinit var subtotal:TextView
    private lateinit var shipping:TextView
    private lateinit var total:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_order_details)

        initializeall()
        actionBar()
        var myOrderDetails= Order()
        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)) {
            myOrderDetails =
                intent.getParcelableExtra<Order>(Constants.EXTRA_MY_ORDER_DETAILS)!!
        }
        setupUI(myOrderDetails)
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
        toolbar = findViewById(R.id.toolbar_my_order_details_activity)
        orderid=findViewById(R.id.tv_order_details_id)
        orderdate=findViewById(R.id.tv_order_details_date)
        orderStatus=findViewById(R.id.tv_order_status)
        rclView=findViewById(R.id.rv_my_order_items_list)
        Atype=findViewById(R.id.tv_my_order_details_address_type)
        Aname=findViewById(R.id.tv_my_order_details_full_name)
        Aaddress=findViewById(R.id.tv_my_order_details_address)
        AadditionalNote=findViewById(R.id.tv_my_order_details_additional_note)
        AotherDetails=findViewById(R.id.tv_my_order_details_other_details)
        Amobile=findViewById(R.id.tv_my_order_details_mobile_number)
        subtotal=findViewById(R.id.tv_order_details_sub_total)
        shipping=findViewById(R.id.tv_order_details_shipping_charge)
        total=findViewById(R.id.tv_order_details_total_amount)
    }


    private fun setupUI(orderDetails: Order) {

       orderid.text = orderDetails.title

        val dateFormat = "dd MMM yyyy HH:mm"

        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.order_datetime

        val orderDateTime = formatter.format(calendar.time)
        orderdate.text = orderDateTime

        val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)


        when {
            diffInHours < 1 -> {
                orderStatus.text = "Pending"
                orderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorAccent
                    )
                )
            }

            diffInHours < 2 -> {
                orderStatus.text = "In Process"
                orderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusInProcess
                    )
                )
            }
            else -> {
                orderStatus.text ="Delivered"
                orderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusDelivered
                    )
                )
            }
        }

        rclView.layoutManager = LinearLayoutManager(this@MyOrderDetailsActivity)
        rclView.setHasFixedSize(true)

        val cartListAdapter =
            CartAdapter(this@MyOrderDetailsActivity, orderDetails.items, false)
        rclView.adapter = cartListAdapter

        Atype.text = orderDetails.address.type
        Aname.text = orderDetails.address.name
        Aaddress.text =
            "${orderDetails.address.address}, ${orderDetails.address.zipCode}"
        AadditionalNote.text = orderDetails.address.additionalNote

        if (orderDetails.address.otherDetails.isNotEmpty()) {
            AotherDetails.visibility = View.VISIBLE
            AotherDetails.text = orderDetails.address.otherDetails
        } else {
            AotherDetails.visibility = View.GONE
        }
        Amobile.text = orderDetails.address.mobileNumber

        subtotal.text = orderDetails.sub_total_amount
        shipping.text = orderDetails.shipping_charge
        total.text = orderDetails.total_amount
    }

}