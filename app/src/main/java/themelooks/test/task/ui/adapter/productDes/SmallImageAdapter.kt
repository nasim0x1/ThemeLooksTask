package themelooks.test.task.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import themelooks.test.task.R
import themelooks.test.task.data.model.ImageModel
import themelooks.test.task.databinding.ItemImageGalleryBinding
import themelooks.test.task.util.interfaces.Imageinterface

class SmallImageAdapter(

    private val context: Context,
    private val images: List<ImageModel>,
    private val clickInterface: Imageinterface

) : RecyclerView.Adapter<SmallImageAdapter.SmallImageViewHolder>() {
    private var rowIndex = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallImageViewHolder {
        val binding = ItemImageGalleryBinding.inflate(LayoutInflater.from(context), parent, false)
        return SmallImageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SmallImageViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        Glide.with(context).load(images[position].url).placeholder(R.drawable.loading).into(holder.binding.imageView)
        holder.itemView.setOnClickListener {
            clickInterface.onImageClick(position)
            rowIndex = position;
            notifyDataSetChanged();
        }
        if (rowIndex == position) {
            holder.binding.imageLayout.setBackgroundResource(R.drawable.selected_small_image_frame)
        } else {
            holder.binding.imageLayout.setBackgroundResource(R.drawable.small_image_frame)

        }

    }
    // to update image frame
    fun setSelectedFrame(position: Int) {
        rowIndex = position
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {

        return images.size
    }

    class SmallImageViewHolder(val binding: ItemImageGalleryBinding) : RecyclerView.ViewHolder(binding.root) {}


}



