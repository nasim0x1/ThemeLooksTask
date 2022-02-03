package themelooks.test.task.ui.view.user

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import themelooks.test.task.R
import themelooks.test.task.data.model.ImageModel
import themelooks.test.task.data.model.ProductModel
import themelooks.test.task.data.model.ProductVariant
import themelooks.test.task.databinding.ActivityProductDetailsBinding
import themelooks.test.task.ui.adaper.SmallImageAdapter
import themelooks.test.task.ui.viewmodel.ProductDetailsViewModel
import themelooks.test.task.util.Constants
import themelooks.test.task.util.RecyclerViewItemClickInterface
import themelooks.test.task.util.Task

class ProductDetails : AppCompatActivity(), RecyclerViewItemClickInterface {

    lateinit var viewModel: ProductDetailsViewModel
    lateinit var binding: ActivityProductDetailsBinding
    private val productsImages = ArrayList<ImageModel>()
    lateinit var smallImageAdapter: SmallImageAdapter
    private var currentImagePos = 0

    private var colorMap: MutableMap<String, MutableList<String>> = HashMap()
    private var sizeMap: MutableMap<String, MutableList<String>> = HashMap()
    private var priceMap: MutableMap<String, Float> = HashMap()

    private var selectedSize = ""
    private var selectedColor = ""
    private var selectedVariantPrice = 0f
    private var currentQty = 1
    private var priceRange = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        viewModel = ViewModelProvider(this)[ProductDetailsViewModel::class.java]

        //  after fetching data from database it will observe
        viewModel.productDetails.observe(this, Observer { task ->
            when (task) {
                is Task.Loading -> {
                    binding.shimmerLayout.startShimmer()

                }
                is Task.Failed -> {

                }
                is Task.Success -> {
                    setView(task.data)
                }
            }
        })


        // small images recyclerview buttons to change top imageview
        binding.imageNext.setOnClickListener {
            currentImagePos += 1
            updateImageButtons()
        }
        binding.imagePre.setOnClickListener { v ->
            currentImagePos -= 1
            updateImageButtons()
        }


        binding.sizeM.setOnClickListener {
            binding.sizeM.setBackgroundResource(R.drawable.text_button_clicked)
            binding.sizeL.setBackgroundResource(R.drawable.text_button)

            if (selectedSize != "" && selectedSize != "M") {
                clearColorsButton()
            }
            selectedSize = "M"
            buttonUpdater()
        }
        binding.sizeL.setOnClickListener {
            binding.sizeL.setBackgroundResource(R.drawable.text_button_clicked)
            binding.sizeM.setBackgroundResource(R.drawable.text_button)

            if (selectedSize != "" && selectedSize != "L"){
                clearColorsButton()
            }
            selectedSize = "L"
            buttonUpdater()
        }

        binding.colorWihte.setOnClickListener {
            if (selectedSize != "") {
                selectedColor = "White"
                setPrice()
                binding.colorWihte.setBackgroundResource(R.drawable.text_button_clicked)
                binding.colorBlack.setBackgroundResource(R.drawable.text_button)
            } else {
                Toast.makeText(this, "First select size!", Toast.LENGTH_SHORT).show()
            }

        }
        binding.colorBlack.setOnClickListener {
            if (selectedSize != "") {
                selectedColor = "Black"
                setPrice()
                binding.colorBlack.setBackgroundResource(R.drawable.text_button_clicked)
                binding.colorWihte.setBackgroundResource(R.drawable.text_button)
            } else {
                Toast.makeText(this, "First select size!", Toast.LENGTH_SHORT).show()
            }

        }
        binding.qDecrease.isEnabled = false
        binding.qIncrease.isEnabled = false
        binding.qIncrease.setOnClickListener {

            currentQty += 1
            binding.qDecrease.isEnabled = true
            binding.pQuantity.text = currentQty.toString()
            binding.qDecrease.setImageResource(R.drawable.ic_minus_black_24)
            Toast.makeText(
                this,
                "Total price ${currentQty * selectedVariantPrice} BDT",
                Toast.LENGTH_SHORT
            ).show()


        }
        binding.qDecrease.setOnClickListener {
            currentQty -= 1
            if (currentQty > 1) {
                binding.qDecrease.setImageResource(R.drawable.ic_minus_black_24)
            } else {
                binding.qDecrease.isEnabled = false
                binding.qDecrease.setImageResource(R.drawable.ic_minus_24)
            }
            binding.pQuantity.text = currentQty.toString()
            Toast.makeText(
                this,
                "Total price ${currentQty * selectedVariantPrice} BDT",
                Toast.LENGTH_SHORT
            ).show()

        }

    }

    private fun clearColorsButton() {

        binding.price.text = priceRange
        binding.actPrice.visibility = View.GONE

        binding.colorWihte.setBackgroundResource(R.drawable.text_button)
        binding.colorBlack.setBackgroundResource(R.drawable.text_button)
        selectedColor = ""
    }

    @SuppressLint("SetTextI18n")
    private fun setPrice() {
        val key = selectedColor + selectedSize
        binding.price.text = "৳${priceMap[key]}"
        binding.actPrice.paintFlags = binding.actPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        binding.actPrice.text = "৳" + (priceMap[key]!! + 50).toString()
        binding.actPrice.visibility = View.VISIBLE
        selectedVariantPrice = priceMap[key]!!.toFloat()
        binding.qIncrease.isEnabled = true
        binding.qIncrease.setImageResource(R.drawable.ic_add_black_24)
    }

    override fun onStart() {
        super.onStart()
        val key: String = intent.getStringExtra("key").toString()
        if (key.equals(null)) {
            Toast.makeText(this, "Product not found!", Toast.LENGTH_SHORT).show()
            finish()
        }
        viewModel.getProductDetails(key)
    }

    private fun setView(productModel: ProductModel?) {
        val images = productModel?.images
        binding.title.text = productModel!!.title
        binding.description.text = productModel.description

        variantFormatter(productModel.variants!!)

        images.let { imgList ->
            productsImages.addAll(imgList!!)
            Glide.with(this).load(imgList[0].url).into(binding.selectedPhoto)
            smallImageAdapter = SmallImageAdapter(this, imgList, this)
            binding.productImageList.adapter = smallImageAdapter
        }
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
        binding.detailsView.visibility = View.VISIBLE
    }

    override fun onClick(position: Int) {
        currentImagePos = position
        Glide.with(this).load(productsImages[position].url).into(binding.selectedPhoto)
        binding.productImageList.scrollToPosition(position)
        updateImageButtons()
    }

    private fun updateImageButtons() {
        if (currentImagePos == 0) {
            binding.imagePre.isClickable = false
            binding.imagePre.setBackgroundResource(R.drawable.disable_circle)
        } else {
            binding.imagePre.isClickable = true
            binding.imagePre.setBackgroundResource(R.drawable.circle)
        }

        if (currentImagePos == productsImages.size - 1) {
            binding.imageNext.isClickable = false
            binding.imageNext.setBackgroundResource(R.drawable.disable_circle)
        } else {
            binding.imageNext.isClickable = true
            binding.imageNext.setBackgroundResource(R.drawable.circle)
        }

        Glide.with(this).load(productsImages[currentImagePos].url).into(binding.selectedPhoto)
        binding.productImageList.scrollToPosition(currentImagePos)
    }

    private fun variantFormatter(variant: List<ProductVariant>) {
        priceRange = Constants.getPriceRange(variant)
        binding.price.text = priceRange
        variant.forEach {
            // mapping  colors
            if (colorMap.containsKey(it.color.toString())) {
                colorMap[it.color.toString()]?.add(it.size.toString())
            } else {
                colorMap[it.color.toString()] = mutableListOf(it.color.toString())
            }
            // mapping sizes
            if (sizeMap.containsKey(it.size.toString())) {
                sizeMap[it.size.toString()]?.add(it.color.toString())
            } else {
                sizeMap[it.size.toString()] = mutableListOf(it.color.toString())
            }
            // mapping price
            priceMap[it.color + it.size] = it.price!!.toFloat()

        }
        if ("M" !in sizeMap.keys) {
            binding.sizeM.visibility = View.GONE
        }
        if ("L" !in sizeMap.keys) {
            binding.sizeL.visibility = View.GONE
        }
        if ("White" !in colorMap.keys) {
            binding.colorWihte.visibility = View.GONE
        }
        if ("Black" !in colorMap.keys) {
            binding.colorBlack.visibility = View.GONE
        }
    }

    private fun buttonUpdater() {
        if (selectedSize != "") {
            if (sizeMap[selectedSize]!!.contains("Black")) {
                binding.colorBlack.visibility = View.VISIBLE
            } else {
                binding.colorBlack.visibility = View.GONE
            }
            if (sizeMap[selectedSize]!!.contains("White")) {
                binding.colorWihte.visibility = View.VISIBLE
            } else {
                binding.colorWihte.visibility = View.GONE

            }
        }

    }


}
