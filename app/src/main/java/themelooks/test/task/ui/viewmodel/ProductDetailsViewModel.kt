package themelooks.test.task.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import themelooks.test.task.data.model.ProductModel
import themelooks.test.task.data.repository.SingleProductRepository
import themelooks.test.task.util.Task

class ProductDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private var repository = SingleProductRepository()
    private val _productDetails = MutableLiveData<Task<ProductModel>>()

    fun getProductDetails(key:String){
        repository.getProductDetails(key,_productDetails)
    }

    val productDetails: LiveData<Task<ProductModel>>
        get() = _productDetails

}