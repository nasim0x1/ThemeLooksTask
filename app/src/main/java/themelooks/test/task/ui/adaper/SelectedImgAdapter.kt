package themelooks.test.task.ui.adaper

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import themelooks.test.task.databinding.ItemImageGalleryBinding

class UploadImageViewAdapter(private val context: Context, private val images: List<Uri>) :
    RecyclerView.Adapter<AdminImageUpVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminImageUpVH {
        val binding = ItemImageGalleryBinding.inflate(LayoutInflater.from(context), parent, false)
        return AdminImageUpVH(binding)
    }

    override fun onBindViewHolder(holder: AdminImageUpVH, position: Int) {
        holder.binding.imageView.setImageURI(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }


}

class AdminImageUpVH(val binding: ItemImageGalleryBinding) : RecyclerView.ViewHolder(binding.root) {

}