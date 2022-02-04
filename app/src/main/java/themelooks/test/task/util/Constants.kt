package themelooks.test.task.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import themelooks.test.task.data.model.ProductVariant
import java.io.File


class Constants {
    companion object {
        // Database reference
        val dbRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("products")

        // extracting file name from file URI
        @SuppressLint("Range", "Recycle")
        fun getFileName(applicationContext: Application, uri: Uri?): String {
            var result: String? = null
            if (uri!!.scheme == "content") {
                val cursor = applicationContext.contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor!!.close()
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result!!.lastIndexOf('/')
                if (cut != -1) {
                    result = result.substring(cut + 1)
                }
            }
            return result
        }

        // formatting price range from all variants
        fun getPriceRange(variants: List<ProductVariant>): String {
            if (variants.size == 1){
                return "৳${variants[0].price}"
            }
            var min = 0f
            var max = 0f
            if (variants.isNotEmpty() && variants.size > 1) {
                variants.forEach {
                    if (it.price!! > max) max = it.price!!
                    if (min == 0f) min = it.price!! else if (min > it.price!!) min = it.price!!
                }
            } else {
                return variants[0].price.toString()
            }
            return "৳$min-$max"
        }
    }
}