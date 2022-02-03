package themelooks.test.task.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import themelooks.test.task.data.model.ProductModel
import themelooks.test.task.util.Constants
import themelooks.test.task.util.Task

class SingleProductRepository {

    fun getProductDetails(key: String, productDetails: MutableLiveData<Task<ProductModel>>) {
        Constants.dbRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.getValue(ProductModel::class.java).let {
                        productDetails.postValue(Task.Success(it))
                    }
                } else {
                    productDetails.postValue(Task.Failed("Product not found!"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                productDetails.postValue(Task.Failed(error.message))
            }
        })
    }
}