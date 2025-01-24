package com.sumiappcompany.shopadvisor.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sumiappcompany.shopadvisor.models.*
import com.sumiappcompany.shopadvisor.ui.activities.*
import com.sumiappcompany.shopadvisor.ui.fragments.DashboardFragment
import com.sumiappcompany.shopadvisor.ui.fragments.OrdersFragment
import com.sumiappcompany.shopadvisor.ui.fragments.ProductsFragment
import com.sumiappcompany.shopadvisor.ui.fragments.SoldProductFragment
import com.sumiappcompany.shopadvisor.utils.Constants

class FireStoreClass {


    private val mFireStore = FirebaseFirestore.getInstance()

    //user data
    //send the data to firebase and store it
    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)

            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistationSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while registering the user")
            }
    }

    //get the current user id
    fun getCurrentUserID(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID

    }

    //get the current user details
    fun getCurrentDetails(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { documents ->

                val user = documents.toObject(User::class.java)!!

                //shared preference to store the user name in key value pair
                val sharedPreferences = activity.getSharedPreferences(
                    Constants.ShopAdvisor_Preferences,
                    Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"

                )
                editor.apply()



                when (activity) {
                    is LogInActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingActivity -> {

                        activity.userDetailsSuccess(user)
                    }
                }

            }
            .addOnFailureListener {
                when (activity) {

                    is LogInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingActivity -> {
                        activity.hideProgressDialog()
                    }

                }


            }


    }

    //updating user details later
    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {

        mFireStore.collection(Constants.USERS).document(getCurrentUserID()).update(userHashMap)
            .addOnCompleteListener {
                when (activity) {
                    is UserProfileActivity -> {

                        activity.userProfileSuccessfullyUpdated()

                    }
                }


            }
            .addOnFailureListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

            }
    }


    //upload image to cloud storage for both product and user
    fun uploadImagetoCloudStorage(activity: Activity, imagefileURl: Uri?, imagetype: String) {
        val mref: StorageReference = FirebaseStorage.getInstance().reference.child(
            imagetype + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(activity, imagefileURl)
        )


        mref.putFile(imagefileURl!!).addOnSuccessListener { taskSnapsort ->


            //get the downloadble url from the snapsort
            taskSnapsort.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.successfullyuploadedImage(uri.toString())
                        }
                        is AddProductActivity -> {
                            activity.successfullyuploadedImage(uri.toString())
                        }
                    }

                }

        }.addOnFailureListener { exception ->
            when (activity) {
                is UserProfileActivity -> {
                    activity.hideProgressDialog()
                }
                is AddProductActivity -> {
                    activity.hideProgressDialog()
                }
            }

        }
    }


    //Product
    //upload product details to database
    fun UploadProductsDetails(activity: AddProductActivity, productinfo: Product) {
        mFireStore.collection(Constants.PRODUCTS).document()
            .set(productinfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()

            }.addOnFailureListener {
                activity.hideProgressDialog()

            }

    }

    //product of a user perticular
    fun getProductsList(fragment: Fragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get().addOnSuccessListener {
                val productList: ArrayList<Product> = ArrayList()
                for (i in it.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productList.add(product)
                }


                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductListFromFireStore(productList)
                    }
                }

            }.addOnFailureListener {
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.hideProgressDialog()
                    }
                }
            }

    }

    //deleteProduct
    fun deleteAProduct(fragment: ProductsFragment, productid: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productid).delete().addOnSuccessListener {
                fragment.productdeleteSuccess()
            }.addOnFailureListener {

                Toast.makeText(fragment.activity, "something error happen ", Toast.LENGTH_SHORT)
                    .show()
            }

    }

    //all products
    fun allProducts(fragment: DashboardFragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .get().addOnSuccessListener {


                val productList: ArrayList<Product> = ArrayList()
                for (i in it.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productList.add(product)
                }
                fragment.successDashboardItemlist(productList)


            }.addOnFailureListener {
                fragment.hideProgressDialog()
            }
    }

    //getting a product details
    fun gettingaProductDetails(activity: ProductDetailsActivity, productId: String) {
        mFireStore.collection(Constants.PRODUCTS).document(productId)
            .get().addOnSuccessListener {
                val product = it.toObject(Product::class.java)
                if (product != null) {
                    activity.productDetailsSuccess(product)
                }

            }.addOnFailureListener {
                activity.hideProgressDialog()

            }
    }


    //cart data
    fun addCartItems(activity: ProductDetailsActivity, addtocart: CartItem) {
        mFireStore.collection(Constants.CARTITEM).document()
            .set(addtocart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addtoCartSuccess()
            }.addOnFailureListener { }
        activity.hideProgressDialog()

    }

    //is the product present in the cart or not
    fun checkIfProductPresentInCart(activity: ProductDetailsActivity, productId: String) {
        mFireStore.collection(Constants.CARTITEM)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get().addOnSuccessListener {
                if (it.documents.size > 0) {
                    activity.ProductExistInCart()
                } else {
                    activity.hideProgressDialog()
                }

            }.addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

    //get the products
    fun getCartLists(activity: Activity) {
        mFireStore.collection(Constants.CARTITEM)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get().addOnSuccessListener {
                val Cartlist: ArrayList<CartItem> = ArrayList()
                for (i in it.documents) {
                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id
                    Cartlist.add(cartItem)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemList(Cartlist)
                    }
                    is CheckOutActivity -> {
                        activity.successCartItemsList(Cartlist)
                    }
                }


            }.addOnFailureListener {
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }

                    is CheckOutActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun getAllProductListforcart(activity: Activity) {
        mFireStore.collection(Constants.CARTITEM)
            .get().addOnSuccessListener {
                val productsList: ArrayList<Product> = ArrayList()
                for (i in it.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                when (activity) {

                    is CartListActivity -> {

                        activity.successproductListFromFireStore(productsList)

                    }
                    is CheckOutActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }

                }
            }.addOnFailureListener {


                when (activity) {

                    is CartListActivity -> {

                        activity.hideProgressDialog()

                    }
                    is CheckOutActivity -> {
                        activity.hideProgressDialog()
                    }


                }

            }
    }


    // A function to remove the cart item from the cloud fire store.
    fun removeItemFromCart(context: Context, cart_id: String) {

        // Cart items collection name
        mFireStore.collection(Constants.CARTITEM)
            .document(cart_id) // cart id
            .delete()
            .addOnSuccessListener {

                // Notify the success result of the removed cart item from the list to the base class.
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    e
                )
            }
    }

    // A function to update the cart item in the cloud firestore.

    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {

        // Cart items collection name
        mFireStore.collection(Constants.CARTITEM)
            .document(cart_id) // cart id
            .update(itemHashMap) // A HashMap of fields which are to be updated.
            .addOnSuccessListener {

                // Notify the success result of the updated cart items list to the base class.
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }

                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
                    e
                )
            }
    }


    //Manipulation on address
    //add address
    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {


        mFireStore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address.",
                    e
                )
            }
    }

    // A function to get the list of address from the cloud firestore.

    fun getAddressesList(activity: AddressListActivity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                val addressList: ArrayList<Address> = ArrayList()
                for (i in document.documents) {
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id
                    addressList.add(address)
                }

                activity.successAddressListFromFirestore(addressList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
            }
    }

    // A function to update the existing address to the cloud firestore.
//
    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

            }
    }

    //     A function to delete the existing address from the cloud firestore.
    fun deleteAddress(activity: AddressListActivity, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

            }
    }


    fun placeOrder(activity: CheckOutActivity, order: Order) {

        mFireStore.collection(Constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {

                activity.orderPlacedSuccess()
            }
            .addOnFailureListener {

                activity.hideProgressDialog()

            }
    }

    fun updateAllDetails(activity: CheckOutActivity, cartList: ArrayList<CartItem>, order: Order) {
        val writeBatch = mFireStore.batch()
        for (cart in cartList) {
            val soldProduct = SoldProduct(
                cart.product_owner_id,
                cart.title,
                cart.price,
                cart.cart_quantity,
                cart.image,
                order.title,
                order.order_datetime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address
            )
            val documentReference = mFireStore.collection(Constants.SOLD_PRODUCTS)
                .document()
            writeBatch.set(documentReference, soldProduct)
        }


        for (cart in cartList) {

            val productHashMap = HashMap<String, Any>()

            productHashMap[Constants.STOCK_QUANTITY] =
                (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()

            val documentReference = mFireStore.collection(Constants.PRODUCTS)
                .document(cart.product_id)

            writeBatch.update(documentReference, productHashMap)
        }

        // Delete the list of cart items
        for (cart in cartList) {

            val documentReference = mFireStore.collection(Constants.CARTITEM)
                .document(cart.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {

            activity.allDetailsUpdatedSuccessfully()

        }.addOnFailureListener {
            activity.hideProgressDialog()
        }
    }


    fun getMyOrdersList(fragment: OrdersFragment) {
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                val list: ArrayList<Order> = ArrayList()

                for (i in document.documents) {

                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id

                    list.add(orderItem)
                }

                fragment.populateOrdersListInUI(list)
            }
            .addOnFailureListener {


                fragment.hideProgressDialog()

            }
    }

    fun getSoldProductsList(fragment: SoldProductFragment) {
        mFireStore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<SoldProduct> = ArrayList()

                for (i in document.documents) {

                    val soldProduct = i.toObject(SoldProduct::class.java)!!
                    soldProduct.id = i.id
                    list.add(soldProduct)
                }
                fragment.successSoldProductsList(list)
            }
            .addOnFailureListener {
                fragment.hideProgressDialog()

            }
    }

}
