package themelooks.test.task.ui.view.user

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import themelooks.test.task.R
import themelooks.test.task.data.model.ImageModel
import themelooks.test.task.data.model.ProductModel
import themelooks.test.task.data.model.ProductVariant
import themelooks.test.task.databinding.ActivityProductDetailsBinding
import themelooks.test.task.ui.adapter.SmallImageAdapter
import themelooks.test.task.ui.adapter.productDes.ColorAdapter
import themelooks.test.task.ui.adapter.productDes.SizeAdapter
import themelooks.test.task.ui.viewmodel.ProductDetailsViewModel
import themelooks.test.task.util.Constants
import themelooks.test.task.util.interfaces.SizeInterface
import themelooks.test.task.util.interfaces.Imageinterface
import themelooks.test.task.util.Task
import themelooks.test.task.util.interfaces.ColorInterface

class ProductDetails : AppCompatActivity(), Imageinterface,
    SizeInterface, ColorInterface {

    private lateinit var viewModel: ProductDetailsViewModel
    lateinit var binding: ActivityProductDetailsBinding
    private val productsImages = ArrayList<ImageModel>()

    // adapters
    private lateinit var sizeAdapter: SizeAdapter
    lateinit var colorAdapter: ColorAdapter
    lateinit var smallImageAdapter: SmallImageAdapter

    private var currentImagePos = 0

    // helper maps
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

        // app bar back icon handler
        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        viewModel = ViewModelProvider(this)[ProductDetailsViewModel::class.java]

        //  after fetched data from database or failed it will observe
        viewModel.productDetails.observe(this, Observer { task ->
            when (task) {
                is Task.Loading -> {
                    binding.shimmerLayout.startShimmer()
                }
                is Task.Failed -> {
                    binding.shimmerLayout.stopShimmer()
                    Toast.makeText(this, task.message, Toast.LENGTH_SHORT).show()
                }
                is Task.Success -> {
                    setView(task.data)
                }
            }
        })

        // small images slider recyclerview buttons to change top imageview
        // right site small button click handler
        binding.imageNext.setOnClickListener {
            currentImagePos += 1
            updateImageButtons()
        }
        //left site small button click handler
        binding.imagePre.setOnClickListener { v ->
            currentImagePos -= 1
            updateImageButtons()
        }

        // quantity button disable cause by default color or size not selected
        binding.qDecrease.isEnabled = false
        binding.qIncrease.isEnabled = false

        //slider pre and next button disabling
        binding.imageNext.isClickable = false
        binding.imagePre.isClickable = false


        // quality + button handler
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
        // quality - button handler
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

    override fun onStart() {
        super.onStart()
        // product id
        val key: String = intent.getStringExtra("key").toString()

        if (key.equals(null)) {
            Toast.makeText(this, "Product not found!", Toast.LENGTH_SHORT).show()
            finish()
        }
        // calling viewmodel function to get data from repository
        viewModel.getProductDetails(key)
    }

    // it will call when product details model ready
    private fun setView(productModel: ProductModel?) {
        val images = productModel?.images
        binding.title.text = productModel!!.title
        binding.description.text = productModel.description

        variantFormatter(productModel.variants!!)

        images.let { imgList ->
            productsImages.addAll(imgList!!)
            Glide.with(this).load(imgList[0].url).placeholder(R.drawable.loading).into(binding.selectedPhoto)
            smallImageAdapter = SmallImageAdapter(this, imgList, this)
            binding.productImageList.adapter = smallImageAdapter
            smallImageAdapter.setSelectedFrame(0)
            if (imgList.size > 1){
                binding.imageNext.isClickable = true
                binding.imageNext.setBackgroundResource(R.drawable.circle)
            }

        }
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
        binding.detailsView.visibility = View.VISIBLE
    }

    // interface method - click SmallImageAdapter item
    override fun onImageClick(position: Int) {
        currentImagePos = position
        Glide.with(this).load(productsImages[position].url).placeholder(R.drawable.loading).into(binding.selectedPhoto)
        binding.productImageList.scrollToPosition(position)
        updateImageButtons()
    }

    // image gallery next and pre button handler
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

        Glide.with(this).load(productsImages[currentImagePos].url).placeholder(R.drawable.loading).into(binding.selectedPhoto)
        binding.productImageList.scrollToPosition(currentImagePos)
        smallImageAdapter.setSelectedFrame(currentImagePos)
    }

    // it will map all variant in helper Hashmap
    private fun variantFormatter(variant: List<ProductVariant>) {
        priceRange = Constants.getPriceRange(variant)
        binding.price.text = priceRange
        variant.forEach {
            // mapping sizes base on color(key)
            if (colorMap.containsKey(it.color.toString())) {
                colorMap[it.color.toString()]?.add(it.size.toString())
            } else {
                colorMap[it.color.toString()] = mutableListOf(it.size.toString())
            }
            // mapping color base on sizes(key)
            if (sizeMap.containsKey(it.size.toString())) {
                sizeMap[it.size.toString()]?.add(it.color.toString())
            } else {
                sizeMap[it.size.toString()] = mutableListOf(it.color.toString())
            }
            // mapping price color+size(key)
            priceMap[it.color + it.size] = it.price!!.toFloat()

        }

        sizeAdapter = SizeAdapter(this, ArrayList(sizeMap.keys), this)

        colorAdapter = ColorAdapter(this, ArrayList(colorMap.keys), this)


        binding.productColors.adapter = colorAdapter
        binding.productSizes.adapter = sizeAdapter

    }

    // interface method - click SizeAdapter item
    override fun onSizeClicked(name: String, position: Int) {
        if (selectedSize != name) {

            sizeAdapter.updateFrame(position)
            colorAdapter.updateFrame(-1)

            binding.selectedColorText.visibility =View.GONE
            selectedColor = ""
            selectedVariantPrice = 0f
            updatePrice()

            selectedSize = name

            if (sizeMap.containsKey(name)) {
                val colors = sizeMap[name]
                colorAdapter.updateList(colors!!)

            } else {
                Toast.makeText(this, "Color not found", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // interface method - click ColorAdapter item
    override fun onColorClicked(name: String, position: Int) {
        if (selectedSize == "") {
            Toast.makeText(this, "Select size first!", Toast.LENGTH_SHORT).show()
        } else {
            selectedColor = name
            binding.selectedColorText.visibility =View.VISIBLE
            binding.selectedColorText.text = name
            colorAdapter.updateFrame(position)
            updatePrice()
        }

    }

    // it will update price
    @SuppressLint("SetTextI18n")
    private fun updatePrice() {
        /*
            if color "" mean new size clicked or color not selected
            then quantity button will disable
            actual price will hide and price rang will visible
        */
        if (selectedColor == "") {
            binding.qIncrease.isEnabled = false
            binding.actPrice.visibility = View.GONE
            binding.price.text = priceRange
        } else {
            // enable quantity button
            binding.qIncrease.isEnabled = true
            // storing current variant price on global variable
            selectedVariantPrice = priceMap[selectedColor+selectedSize]!!
            //setting variant price to price text
            binding.price.text = "৳${selectedVariantPrice}"

            // make fake actual price and making it visible
            val actPrice = selectedVariantPrice + 50
            binding.actPrice.paintFlags = binding.actPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.actPrice.text = "৳$actPrice"
            binding.actPrice.visibility = View.VISIBLE
        }
    }


}
