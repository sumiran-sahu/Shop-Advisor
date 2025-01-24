package com.sumiappcompany.shopadvisor.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.models.Address
import com.sumiappcompany.shopadvisor.ui.activities.AddEditAddressActivity
import com.sumiappcompany.shopadvisor.ui.activities.CheckOutActivity
import com.sumiappcompany.shopadvisor.utils.Constants


class AddressListAdapter(
    private val context: Context,
    private var list: ArrayList<Address>,
    private val selectaddress: Boolean

) : RecyclerView.Adapter<AddressListAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var aname: TextView = view.findViewById(R.id.tv_address_full_name)
        var atype: TextView = view.findViewById(R.id.tv_address_type)
        var aaddress: TextView = view.findViewById(R.id.tv_address_details)
        var mobilenumber: TextView = view.findViewById(R.id.tv_address_mobile_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.item_address_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        if (true) {
            holder.aname.text = model.name
            holder.atype.text = model.type
            holder.aaddress.text = "${model.address}, ${model.zipCode}"
            holder.mobilenumber.text = model.mobileNumber

            if (selectaddress) {
                holder.itemView.setOnClickListener {
                    val intent = Intent(context, CheckOutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, model)
                    context.startActivity(intent)
                }
            }
        }


    }

    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)

    }

    override fun getItemCount(): Int {
        return list.size
    }


}