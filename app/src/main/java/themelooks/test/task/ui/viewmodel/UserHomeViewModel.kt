package themelooks.test.task.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import themelooks.test.task.data.model.ProductModel
import themelooks.test.task.data.repository.ViewProductRepository
import themelooks.test.task.util.Task

class UserHomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ViewProductRepository()
    private var _productList = MutableLiveData<Task<List<ProductModel>>>()

    fun loadProduct() {
        repository.getProductsFromDatabase(_productList)
    }

    val productList: LiveData<Task<List<ProductModel>>>
        get() = _productList
}