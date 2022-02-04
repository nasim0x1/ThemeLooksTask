package themelooks.test.task.ui.view.admin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import themelooks.test.task.R
import themelooks.test.task.data.model.ImageModel
import themelooks.test.task.data.model.ProductModel
import themelooks.test.task.data.model.ProductVariant
import themelooks.test.task.databinding.ActivityAdminHomeBinding
import themelooks.test.task.ui.adapter.UploadImageViewAdapter
import themelooks.test.task.ui.viewmodel.AddProductViewModel
import themelooks.test.task.util.Task
import kotlin.collections.ArrayList


class AdminHome : AppCompatActivity() {

    private lateinit var viewModel: AddProductViewModel
    private val IMAGE_SELECT = 101;

    private var viewVariants: ArrayList<View> = ArrayList()
    private var productImages = ArrayList<Uri>()
    private var productVariant = ArrayList<ProductVariant>()

    private lateinit var progresDialog: ProgressDialog

    private lateinit var binding: ActivityAdminHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Add your product"

        progresDialog = ProgressDialog(this)
        progresDialog.setMessage("Product image uploading")
        progresDialog.setCancelable(false)

        viewModel = ViewModelProvider(this).get(AddProductViewModel::class.java)

        // it will observe with image download links after product images upload into storage
        viewModel.upLoadedImageLinks.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Task.Loading -> {
                    progresDialog.show()
                }
                is Task.Failed -> {
                    progresDialog.dismiss()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is Task.Success -> {
                    progresDialog.dismiss()

                    // after upload success
                    // here will make image models using uploaded images download links
                    val images = ArrayList<ImageModel>()
                    it.data!!.forEach {
                        images.add(ImageModel(it))
                    }
                    // product model
                    val product = ProductModel(
                        "",
                        binding.productTitle.text.toString(),
                        binding.productDescription.text.toString(),
                        productVariant,
                        images
                    )
                    // finally viewmodel function call to save product into database
                    viewModel.saveDB(product)
                }
            }
        })

        // after product save successfully or failed it will observe
        viewModel.productInfoSaveStatus.observe(this, Observer {
            when (it) {
                is Task.Loading -> {
                    progresDialog.setMessage("Product saving in database.")
                    progresDialog.show()
                }
                is Task.Failed -> {
                    progresDialog.dismiss()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is Task.Success -> {
                    progresDialog.dismiss()
                    Toast.makeText(this, "Product saved!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        })

        //but default one variant section will add
        addViewVariant()

        binding.addImages.setOnClickListener { selectImages() }

        binding.addVariantBtn.setOnClickListener {
            // after checking last variant it will add new variant section into view
            if (checkLastVariantView()) {
                addViewVariant()
            } else {
                Toast.makeText(this, "First fill all field of product variant", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (validator()) {
            progresDialog.show()
            var flag = true
            viewVariants.forEach {
                if (!extractVariantView(it)) {
                    flag = false
                    progresDialog.dismiss()
                    return@forEach
                }
            }
            if (flag) {
                // all field data valid then will call viewmodel function to upload images
                viewModel.uploadImage(productImages)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // it will check all field is valid or not ( without variants section)
    private fun validator(): Boolean {
        when {
            binding.productTitle.text?.length == 0 -> {
                binding.productTitle.requestFocus()
                binding.productTitle.error = "Product title can not be blank"
                return false
            }
            binding.productDescription.text?.length == 0 -> {
                binding.productDescription.requestFocus()
                binding.productDescription.error = "Product description can not be blank"
                return false
            }
            viewVariants.size == 0 -> {
                val section = addViewVariant()
                section.requestFocus()
                Toast.makeText(this, "Minimum one variant required!", Toast.LENGTH_SHORT).show()
                return false
            }
            productImages.size == 0 -> {
                Toast.makeText(this, "Select latest one image!", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> {
                viewVariants.forEach {

                }
            }
        }
        return true
    }

    // for adding new variant section in view
    private fun addViewVariant(): View {
        val variantSection = layoutInflater.inflate(R.layout.item_variant_section, null, false)
        variantSection.findViewById<ImageView>(R.id.remove_variant).setOnClickListener {
            if (viewVariants.size > 1) {
                viewVariants.remove(variantSection)
                binding.variantFieldLayout.removeView(variantSection)
            } else {
                Toast.makeText(
                    variantSection.context,
                    "Minimum one variant required.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewVariants.add(variantSection)
        binding.variantFieldLayout.addView(variantSection)
        return variantSection
    }

    private fun checkLastVariantView(): Boolean {
        // it will check if last added variant section is empty or not
        if (viewVariants.size != 0) {
            val view = viewVariants[viewVariants.size - 1]
            val color = view.findViewById<EditText>(R.id.product_color)
            val price = view.findViewById<EditText>(R.id.product_price)
            val size = view.findViewById<EditText>(R.id.product_size)

            if (color.text.isEmpty() || price.text.isEmpty() || size.text.isEmpty()) {
                return false
            }
            return true
        }
        return true
    }

    // it will make variant model form variant view
    private fun extractVariantView(view: View): Boolean {
        val color = view.findViewById<EditText>(R.id.product_color)
        val price = view.findViewById<EditText>(R.id.product_price)
        val size = view.findViewById<EditText>(R.id.product_size)
        if (color.text.isEmpty() || price.text.isEmpty() || size.text.isEmpty()) {
            view.requestFocus()
            Toast.makeText(
                view.context,
                "Fill with valid details or remove variant section",
                Toast.LENGTH_SHORT
            ).show()
            productVariant.clear()
            return false

        }
        productVariant.add(
            ProductVariant(
                color.text.toString(),
                size.text.toString(),
                price.text.toString().toFloat()
            )
        )

        return true
    }

    // to start image picker intent
    private fun selectImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select product images"), IMAGE_SELECT)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                productImages.clear()
                if (data!!.clipData != null) {
                    for (i in 0 until data.clipData!!.itemCount) {
                        productImages.add(data.clipData!!.getItemAt(i).uri)
                    }
                    binding.selectedImageView.adapter = UploadImageViewAdapter(this, productImages)
                }
            }
        }
    }
}