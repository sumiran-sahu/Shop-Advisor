package com.sumiappcompany.shopadvisor.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.adapters.SoldProductsListAdapter
import com.sumiappcompany.shopadvisor.firestore.FireStoreClass
import com.sumiappcompany.shopadvisor.models.SoldProduct


class SoldProductFragment : BaseFragment() {
     lateinit var rclview:RecyclerView
     lateinit var noitem:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

         val root= inflater.inflate(R.layout.fragment_sold_product, container, false)
          rclview=root.findViewById(R.id.rv_sold_product_items)
        noitem=root.findViewById(R.id.tv_no_sold_products_found)
        return root;
    }


    override fun onResume() {
        super.onResume()

        getSoldProductsList()
    }

    private fun getSoldProductsList() {

        showProgressDialog(requireActivity())
        FireStoreClass().getSoldProductsList(this@SoldProductFragment)
    }


    fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {

        // Hide Progress dialog.
        hideProgressDialog()

        if (soldProductsList.size > 0) {
            rclview.visibility = View.VISIBLE
            noitem.visibility = View.GONE

            rclview.layoutManager = LinearLayoutManager(activity)
            rclview.setHasFixedSize(true)

            val soldProductsListAdapter =
                SoldProductsListAdapter(requireActivity(), soldProductsList)
            rclview.adapter = soldProductsListAdapter
        } else {
            rclview.visibility = View.GONE
            noitem.visibility = View.VISIBLE
        }
    }


}