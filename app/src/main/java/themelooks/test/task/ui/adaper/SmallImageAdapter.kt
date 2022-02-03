package themelooks.test.task.ui.adaper

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import themelooks.test.task.data.model.ImageModel
import themelooks.test.task.databinding.ItemImageGalleryBinding
import themelooks.test.task.util.RecyclerViewItemClickInterface

class SmallImageAdapter(
    private val context: Context,
    private val images: List<ImageModel>,
    private val clickInterface: RecyclerViewItemClickInterface
) : RecyclerView.Adapter<SmallImageViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallImageViewHolder {
        val binding = ItemImageGalleryBinding.inflate(LayoutInflater.from(context), parent, false)
        return SmallImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SmallImageViewHolder, position: Int) {
        Glide.with(context).load(images[position].url).into(holder.binding.imageView)
        holder.itemView.setOnClickListener {
            clickInterface.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }
}

class SmallImageViewHolder(val binding: ItemImageGalleryBinding) :
    RecyclerView.ViewHolder(binding.root) {
}
