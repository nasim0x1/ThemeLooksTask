package themelooks.test.task.ui.adapter.productDes

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import themelooks.test.task.R
import themelooks.test.task.databinding.ItemColorSizeBinding
import themelooks.test.task.util.interfaces.SizeInterface

class SizeAdapter(

    private val context: Context,
    private val sizes: List<String>,
    private val clickInterface: SizeInterface

) : RecyclerView.Adapter<SizeViewHolder>() {

    private var rowIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        return SizeViewHolder(
            ItemColorSizeBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        holder.binding.text.text = sizes[position]
        holder.binding.content.setOnClickListener {
            clickInterface.onSizeClicked(sizes[position],position)
        }
        if (rowIndex == position) {
            holder.binding.text.setBackgroundResource(R.drawable.text_button_clicked)
        } else {
            holder.binding.text.setBackgroundResource(R.drawable.text_button)

        }
    }

    override fun getItemCount() = sizes.size

    // update button background after item clicked
    fun updateFrame(position: Int){
        this.rowIndex = position
        notifyDataSetChanged()
    }
}

class SizeViewHolder(val binding: ItemColorSizeBinding) :
    RecyclerView.ViewHolder(binding.root) {

}