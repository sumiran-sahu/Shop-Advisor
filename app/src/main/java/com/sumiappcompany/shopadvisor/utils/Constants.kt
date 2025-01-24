package com.sumiappcompany.shopadvisor.utils

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap

object Constants {
    //firebase database data sets
    const val USERS: String = "users"
    const val PRODUCTS: String = "products"
    const val CARTITEM: String = "cartitem"
    const val ADDRESSES: String = "addresses"
    const val ORDERS:String="orders"
    const val SOLD_PRODUCTS:String="sold_products"


    const val ShopAdvisor_Preferences: String = "ShopAdvisorPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"


    const val ExtraUserDetails: String = "ExtraUserDetails"

    const val READ_STORAGE_PERMISSION_CODE: Int = 2

    //firebase data base user
    const val MALE: String = "male"
    const val FEMALE: String = "female"
    const val FIRESTNAME: String = "firstname"
    const val LASTNAME: String = "Lastname"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val IMAGE: String = "image"
    const val COMPLETE_PROFILE: String = "profileCompleted"
    const val USER_PROFILE_IMAGE: String = "User_Profile_Image"


    //firebase product
    const val PRODUCT_IMAGE: String = "productimage"


    const val USER_ID: String = "user_id"
    const val PRODUCT_ID: String = "product_id"


    const val EXTRA_PRODUCT_ID: String = "extra_product_id"

    const val EXTRA_PRODUCT_OWNER_ID: String = "extra_product_owner_id"
    const val DEFAULT_CART_QUANTITY: String = "1"

    const val CARTQUANTITY: String = "cart_quantity"


    //for address type
    const val HOME: String = "Home"
    const val OFFICE: String = "office"
    const val OTHER: String = "Other"

    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"


    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121


    const val EXTRA_SELECTED_ADDRESS:String="extra_selected_address"
    const val STOCK_QUANTITY:String="stock_quantity"

    const val EXTRA_MY_ORDER_DETAILS:String="extra_my_order_details"

    const val EXTRA_SOLD_PRODUCT_DETAILS:String="extra_sold_product_details"








    fun getFileExtension(activity: Activity, uri: Uri?): String? {


        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}