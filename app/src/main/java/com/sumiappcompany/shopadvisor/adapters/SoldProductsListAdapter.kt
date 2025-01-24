package com.sumiappcompany.shopadvisor.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.models.SoldProduct
import com.sumiappcompany.shopadvisor.ui.activities.SoldProductActivity
import com.sumiappcompany.shopadvisor.utils.Constants
import com.sumiappcompany.shopadvisor.utils.GlideLoader

class SoldProductsListAdapter(
    private val context: Context,
    private var list: ArrayList<SoldProduct>
) : RecyclerView.Adapter<SoldProductsListAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var picture: ImageView = view.findViewById(R.id.productImage)
        var pname: TextView = view.findViewById(R.id.productname)
        var pprice: TextView = view.findViewById(R.id.productPrice)
        var remove: ImageButton = view.findViewById(R.id.deleteproduct)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.image,
                holder.picture
            )

            holder.pname.text = model.title
            holder.pprice.text = "$${model.price}"

            holder.remove.visibility = View.GONE

            holder.itemView.setOnClickListener {
                val intent = Intent(context, SoldProductActivity::class.java)
                intent.putExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS, model)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}