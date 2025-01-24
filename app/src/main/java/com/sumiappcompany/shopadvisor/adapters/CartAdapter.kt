package com.sumiappcompany.shopadvisor.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.firestore.FireStoreClass
import com.sumiappcompany.shopadvisor.models.CartItem
import com.sumiappcompany.shopadvisor.ui.activities.CartListActivity
import com.sumiappcompany.shopadvisor.utils.Constants
import com.sumiappcompany.shopadvisor.utils.GlideLoader


open class CartAdapter(
    private val context: Context,
    private val list: ArrayList<CartItem>,
    private val updatecartitem: Boolean
) : RecyclerView.Adapter<CartAdapter.MyViewHolder>() {


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById(R.id.iv_cart_item_image)
        var pprice: TextView = view.findViewById(R.id.tv_cart_item_price)
        var pname: TextView = view.findViewById(R.id.tv_cart_item_title)
        val pquantity: TextView = view.findViewById(R.id.tv_cart_quantity)
        val remove: ImageButton = view.findViewById(R.id.ib_remove_cart_item)
        val add: ImageButton = view.findViewById(R.id.ib_add_cart_item)
        val delete: ImageButton = view.findViewById(R.id.ib_delete_cart_item)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.item_cart_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            GlideLoader(context).loadProductPicture(model.image, holder.image)

        }


        holder.pname.text = model.title
        holder.pprice.text = "INR ${model.price}"
        holder.pquantity.text = model.cart_quantity
        //check if the product is available or not
        if (model.cart_quantity == "0") {

            holder.remove.visibility = View.GONE
            holder.add.visibility = View.GONE

            if (updatecartitem) {
                holder.delete.visibility = View.VISIBLE
            } else {
                holder.delete.visibility = View.GONE
            }

            holder.pquantity.text = "OutOFStock"

            holder.pquantity.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorSnackBarError
                )
            )
        } else {
            if (updatecartitem) {
                holder.remove.visibility = View.VISIBLE
                holder.add.visibility = View.VISIBLE
                holder.delete.visibility = View.VISIBLE
            } else {
                holder.remove.visibility = View.GONE
                holder.add.visibility = View.GONE
                holder.delete.visibility = View.GONE
            }
            holder.pquantity.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorSecondaryText
                )
            )
        }
        //delete the item from the cart
        holder.delete.setOnClickListener {
            when (context) {
                is CartListActivity -> {
                    context.showProgressDialog(context)
                }
            }

            FireStoreClass().removeItemFromCart(context, model.id)
        }

        //increase the cart item
        holder.add.setOnClickListener {
            val cartQuantity: Int = model.cart_quantity.toInt()

            if (cartQuantity < model.stock_quantity.toInt()) {

                val itemHashMap = HashMap<String, Any>()

                itemHashMap[Constants.CARTQUANTITY] = (cartQuantity + 1).toString()

                // Show the progress dialog.
                if (context is CartListActivity) {
                    context.showProgressDialog(context)
                }

                FireStoreClass().updateMyCart(context, model.id, itemHashMap)
            } else {
                if (context is CartListActivity) {
                    context.showErrorMessage(
                        "You are adding more than the stock Quantity",
                        true
                    )
                }
            }

        }


        //decreasing the cart item
        holder.remove.setOnClickListener {
            if (model.cart_quantity == "1") {
                FireStoreClass().removeItemFromCart(context, model.id)
            } else {
                val cartQuantity: Int = model.cart_quantity.toInt()

                val itemHashMap = HashMap<String, Any>()

                itemHashMap[Constants.CARTQUANTITY] = (cartQuantity - 1).toString()

                // Show the progress dialog.

                if (context is CartListActivity) {
                    context.showProgressDialog(context)
                }

                FireStoreClass().updateMyCart(context, model.id, itemHashMap)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


}









