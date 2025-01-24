package com.sumiappcompany.shopadvisor.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.adapters.MyProductListAdapter
import com.sumiappcompany.shopadvisor.firestore.FireStoreClass
import com.sumiappcompany.shopadvisor.models.Product
import com.sumiappcompany.shopadvisor.ui.activities.AddProductActivity


class ProductsFragment : BaseFragment() {

    private lateinit var rclview: RecyclerView
    private lateinit var txt: TextView


    fun successProductListFromFireStore(productList: ArrayList<Product>) {

        hideProgressDialog()
        if (productList.size > 0) {
            rclview.visibility = View.VISIBLE
            txt.visibility = View.GONE

            rclview.layoutManager = LinearLayoutManager(activity)
            rclview.setHasFixedSize(true)
            val productadapter = MyProductListAdapter(requireActivity(), productList,this)
            rclview.adapter = productadapter
        } else {
            rclview.visibility = View.GONE
            txt.visibility = View.VISIBLE
        }


    }

    fun productdeleteSuccess(){
        hideProgressDialog()
        Toast.makeText(requireActivity(),"Product deleted",Toast.LENGTH_SHORT).show()
        getProductsFromFireStore()
    }

   private fun getProductsFromFireStore() {
        showProgressDialog(requireContext())
        FireStoreClass().getProductsList(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val root = inflater.inflate(R.layout.fragment_products, container, false)
        rclview = root.findViewById(R.id.productsrclview)
        txt = root.findViewById(R.id.noProductText)


        //adding menu to the fragment
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.productmenu, menu)
            }

            //action done after selecting the menu
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_add -> {
                        startActivity(Intent(activity, AddProductActivity::class.java))

                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return root
    }


    override fun onResume() {
        super.onResume()
        getProductsFromFireStore()
    }


    fun deleteProduct(product:String){
       showAlertDialogTodeleteProduct(product)
    }
  //alert dialog to delete a product
    private fun showAlertDialogTodeleteProduct(productid: String){
        val builder=AlertDialog.Builder(requireActivity())

        builder.setTitle("Delete")
        builder.setMessage("Are you sure you want to delete the product")
        builder.setIcon(R.drawable.alert)

        builder.setPositiveButton("Yes"){dialogInterface, _ ->
            showProgressDialog(requireActivity())
            FireStoreClass().deleteAProduct(this,productid)
              dialogInterface.dismiss()
        }
        builder.setNegativeButton("No"){dialogInterface, _ ->
            dialogInterface.dismiss()
        }

      val alertDialog:AlertDialog=builder.create()
      alertDialog.setCancelable(false)
      alertDialog.show()
    }


}