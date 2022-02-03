package themelooks.test.task.ui.adaper

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.util.Pair
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import themelooks.test.task.data.model.ProductModel
import themelooks.test.task.databinding.ItemProductBinding
import themelooks.test.task.ui.view.user.ProductDetails
import themelooks.test.task.util.Constants

class ProductListAdapter(private val context: Context, private val pList: List<ProductModel>) :
    RecyclerView.Adapter<ProductItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ProductItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductItemViewHolder, position: Int) {
        holder.binding.title.text = pList[position].title
        holder.binding.price.text = Constants.getPriceRange(pList[position].variants!!)
        Glide.with(context).load(pList[position].images!![0].url).into(holder.binding.photo)

        holder.itemView.setOnClickListener {
            val options = ActivityOptions.makeSceneTransitionAnimation(
                context as Activity,
                Pair<View, String>(holder.binding.photo, "photoToAnimate")
            )
            val intent = Intent(context, ProductDetails::class.java)
            intent.putExtra("key",pList[position].id)
            context.startActivity(intent,options.toBundle())
        }

    }

    override fun getItemCount(): Int {
        return pList.size
    }

}

class ProductItemViewHolder(val binding: ItemProductBinding) :
    RecyclerView.ViewHolder(binding.root) {}
