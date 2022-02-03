package themelooks.test.task.data.repository

import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.internal.service.Common
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import themelooks.test.task.data.model.ProductModel
import themelooks.test.task.util.Constants
import themelooks.test.task.util.Task
import java.util.*

class AddProductRepository(private val context: Application) {
    private val storageRef: StorageReference = FirebaseStorage.getInstance().reference

    // this function will take
    fun uploadImages(images: List<Uri>, links: MutableLiveData<Task<List<String>>>) {
        links.postValue(Task.Loading())
        var k = 0
        val tempLinks = ArrayList<String>()
        images.forEach { uri ->
            val name = System.currentTimeMillis().toString() + Random().nextInt().toString() + Constants.getFileName(context, uri)
            val uploadTask = storageRef.child("product_images/$name")
            uploadTask.putFile(uri).addOnSuccessListener {
                uploadTask.downloadUrl.addOnSuccessListener {
                    tempLinks.add(it.toString())
                    k += 1
                    if (k == images.size) {
                        links.postValue(Task.Success(tempLinks))
                    }
                }
            }.addOnFailureListener {
                links.postValue(Task.Failed(it.message))
            }
        }
    }

    fun saveToDatabase(product: ProductModel, status: MutableLiveData<Task<Boolean>>) {
        status.postValue(Task.Loading())
        val id = Constants.dbRef.push().key
        product.id=id.toString()
        Constants.dbRef.child(id.toString()).setValue(product).addOnSuccessListener {
            status.postValue(Task.Success(true))
        }.addOnFailureListener {
            status.postValue(Task.Failed(it.message))

        }
    }
}