package themelooks.test.task.ui.adapter.productDes

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import themelooks.test.task.R
import themelooks.test.task.databinding.ItemColorSizeBinding
import themelooks.test.task.util.interfaces.ColorInterface

class ColorAdapter(
    private val context: Context,
    private val colors: MutableList<String>,
    private val clickInterface: ColorInterface

) : RecyclerView.Adapter<ColorViewHolder>() {
    private var rowIndex = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(
            ItemColorSizeBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.binding.text.text = colors[position]
        holder.binding.content.setOnClickListener {
            clickInterface.onColorClicked(colors[position],position)
        }
        if (rowIndex == position) {
            holder.binding.text.setBackgroundResource(R.drawable.text_button_clicked)
        } else {
            holder.binding.text.setBackgroundResource(R.drawable.text_button)

        }

    }

    override fun getItemCount() = colors.size
    // update colors base on size click
    fun updateList( newColors: MutableList<String>){
        colors.clear()
        colors.addAll(newColors)
        notifyDataSetChanged()
    }
    // update button background after click
    fun updateFrame(position: Int){
        this.rowIndex = position
        notifyDataSetChanged()
    }
}

class ColorViewHolder(val binding: ItemColorSizeBinding) : RecyclerView.ViewHolder(binding.root) {

}