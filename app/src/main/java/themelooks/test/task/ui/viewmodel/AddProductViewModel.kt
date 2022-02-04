package themelooks.test.task.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import themelooks.test.task.data.model.ProductModel
import themelooks.test.task.data.repository.AddProductRepository
import themelooks.test.task.util.Task

class AddProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AddProductRepository(application);
    private var _upLoadedImageLinks = MutableLiveData<Task<List<String>>>()
    private var _dbStatus = MutableLiveData<Task<Boolean>>()

    fun uploadImage(images: List<Uri>) {
            repository.uploadImages(images, _upLoadedImageLinks)
    }

    fun saveDB(product: ProductModel) {
        repository.saveToDatabase(product, _dbStatus)
    }


    val upLoadedImageLinks: LiveData<Task<List<String>>>
        get() = _upLoadedImageLinks
    val productInfoSaveStatus: LiveData<Task<Boolean>>
        get() = _dbStatus

}