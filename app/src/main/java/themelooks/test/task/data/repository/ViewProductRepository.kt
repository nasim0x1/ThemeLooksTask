package themelooks.test.task.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import themelooks.test.task.data.model.ProductModel
import themelooks.test.task.util.Constants
import themelooks.test.task.util.Task


class ViewProductRepository {
    fun getProductsFromDatabase(productsList: MutableLiveData<Task<List<ProductModel>>>) {

        productsList.postValue(Task.Loading())

        val temp = ArrayList<ProductModel>()
        Constants.dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    ds.getValue(ProductModel::class.java)?.let { temp.add(it) }
                }
                productsList.postValue(Task.Success(temp))
                Constants.dbRef.removeEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
            override fun onCancelled(error: DatabaseError) {
                productsList.postValue(Task.Failed(error.message))
            }
        })

    }
}