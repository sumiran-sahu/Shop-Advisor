package com.sumiappcompany.shopadvisor.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.models.Product
import com.sumiappcompany.shopadvisor.ui.activities.ProductDetailsActivity
import com.sumiappcompany.shopadvisor.utils.Constants
import com.sumiappcompany.shopadvisor.utils.GlideLoader


open class MyProductDashBoardAdapter(
    private val context: Context,
    private val list: ArrayList<Product>
) : RecyclerView.Adapter<MyProductDashBoardAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view =
            LayoutInflater.from(context).inflate(R.layout.list_itemfordashboard, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        GlideLoader(context).loadProductPicture(model.image, holder.image)
        holder.pname.text = model.title
        holder.pprice.text = "INR ${model.price}"

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
            intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById(R.id.dashProductImage)
        var pprice: TextView = view.findViewById(R.id.dashproductname)
        var pname: TextView = view.findViewById(R.id.dashproductPrice)

    }


}

