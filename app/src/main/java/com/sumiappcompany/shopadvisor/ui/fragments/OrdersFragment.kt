package com.sumiappcompany.shopadvisor.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.adapters.MyOrdersListAdapter
import com.sumiappcompany.shopadvisor.firestore.FireStoreClass
import com.sumiappcompany.shopadvisor.models.Order


class OrdersFragment : BaseFragment() {
   private  lateinit var rclView:RecyclerView
   private lateinit var textview:TextView



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val root=inflater.inflate(R.layout.fragment_orders,container,false )
        initializeall(root)

        return root
    }

    private fun initializeall(root: View?) {
        if (root!=null){
            textview= root.findViewById(R.id.tv_no_orders_found)
            rclView=root.findViewById(R.id.rv_my_order_items)
        }

    }

    override fun onResume() {
        super.onResume()

        getMyOrdersList()
    }


    private fun getMyOrdersList() {

        showProgressDialog(requireActivity())

       FireStoreClass().getMyOrdersList(this@OrdersFragment)
    }


    fun populateOrdersListInUI(ordersList: ArrayList<Order>) {
        hideProgressDialog()
        if (ordersList.size > 0) {
            rclView.visibility = View.VISIBLE
            textview.visibility = View.GONE

            rclView.layoutManager = LinearLayoutManager(activity)
            rclView.setHasFixedSize(true)

            val myOrdersAdapter = MyOrdersListAdapter(requireActivity(), ordersList)
            rclView.adapter = myOrdersAdapter
        } else {
            rclView.visibility = View.GONE
            textview.visibility = View.VISIBLE
        }
    }


}