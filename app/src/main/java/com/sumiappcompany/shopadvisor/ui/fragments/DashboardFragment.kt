package com.sumiappcompany.shopadvisor.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.adapters.MyProductDashBoardAdapter
import com.sumiappcompany.shopadvisor.firestore.FireStoreClass
import com.sumiappcompany.shopadvisor.models.Product
import com.sumiappcompany.shopadvisor.ui.activities.CartListActivity
import com.sumiappcompany.shopadvisor.ui.activities.SettingActivity


class DashboardFragment : BaseFragment() {

    private lateinit var rclview: RecyclerView
    private lateinit var txt: TextView




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        rclview = root.findViewById(R.id.productsrclviewdash)
        txt = root.findViewById(R.id.noProductTextdash)

     //adding menu to the fragment
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.dashboard_menu, menu)
            }
      //action done after selecting the menu
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val id = menuItem.itemId
                when (id) {
                    R.id.action_setting -> {
                         startActivity(Intent(activity, SettingActivity::class.java))

                    }
                    R.id.action_cart->{
                        startActivity(Intent(activity, CartListActivity::class.java))
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return root
    }

    fun successDashboardItemlist(dashboardItemList:ArrayList<Product>){
        hideProgressDialog()
        if (dashboardItemList.size > 0) {
            rclview.visibility = View.VISIBLE
            txt.visibility = View.GONE

            rclview.layoutManager = GridLayoutManager(activity,2)
            rclview.setHasFixedSize(true)
            val productadapter = MyProductDashBoardAdapter(requireActivity(),dashboardItemList)
            rclview.adapter = productadapter
        } else {
            rclview.visibility = View.GONE
            txt.visibility = View.VISIBLE
        }



    }

    private  fun getDashboardItemList(){
        showProgressDialog(requireContext())
        FireStoreClass().allProducts(this@DashboardFragment)

    }

    override fun onResume() {
        super.onResume()
        getDashboardItemList()
    }





}