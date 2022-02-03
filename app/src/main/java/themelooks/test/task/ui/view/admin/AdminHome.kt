package themelooks.test.task.ui.view.admin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import themelooks.test.task.R
import themelooks.test.task.data.model.ImageModel
import themelooks.test.task.data.model.ProductModel
import themelooks.test.task.data.model.ProductVariant
import themelooks.test.task.databinding.ActivityAdminHomeBinding
import themelooks.test.task.ui.adaper.UploadImageViewAdapter
import themelooks.test.task.ui.viewmodel.AddProductViewModel
import themelooks.test.task.util.Task
import kotlin.collections.ArrayList


class AdminHome : AppCompatActivity() {

    private lateinit var viewModel: AddProductViewModel
    private val IMAGE_SELECT = 101;

    private var productImages = ArrayList<Uri>()

    private var productVariant = ArrayList<ProductVariant>()

    private lateinit var dialog: ProgressDialog

    private lateinit var binding: ActivityAdminHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Add your product"

        dialog = ProgressDialog(this)
        dialog.setMessage("Product images uploading")
        dialog.setCancelable(false)

        viewModel = ViewModelProvider(this).get(AddProductViewModel::class.java)

        // viewmodel livedata variable to observe storage(image uploading) operations
        viewModel.upLoadedImageLinks.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Task.Loading -> {
                    dialog.show()
                }
                is Task.Failed -> {
                    dialog.dismiss()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is Task.Success -> {
                    dialog.dismiss()
                    val images = ArrayList<ImageModel>()
                    it.data!!.forEach {
                        images.add(ImageModel(it))
                    }
                    val product = ProductModel(
                        "",
                        binding.productTitle.text.toString(),
                        binding.productDescription.text.toString(),
                        productVariant,
                        images,
                    )
                    viewModel.saveDB(product)
                }
            }
        })

        // viewmodel livedata variable to observe database operations
        viewModel.status.observe(this, Observer {
            when (it) {
                is Task.Loading -> {
                    dialog.setMessage("Product saving in database.")
                    dialog.show()
                }
                is Task.Failed -> {
                    dialog.dismiss()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is Task.Success -> {
                    dialog.dismiss()
                    Toast.makeText(this, "Product saved!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        })


        // check box handling and disabling print field if unchecked
        binding.v1.setOnClickListener {
            binding.productPriceV1.isEnabled = binding.v1.isChecked

        }
        binding.v2.setOnClickListener {
            binding.productPriceV2.isEnabled = binding.v2.isChecked

        }
        binding.v3.setOnClickListener {
            binding.productPriceV3.isEnabled = binding.v3.isChecked

        }
        binding.v4.setOnClickListener {
            binding.productPriceV4.isEnabled = binding.v4.isChecked

        }
        // to open image picker/selector
        binding.addImages.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(
                Intent.createChooser(intent, "Select product images"),
                IMAGE_SELECT
            )
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (validator() && variantValidator() ) {
           viewModel.uploadImage(productImages)
        }
        return super.onOptionsItemSelected(item)
    }
    // to check validation for title and description
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
            productImages.size == 0 -> {
                Toast.makeText(this, "Minimum one image required", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    // to check 4 variant file and create variant models
    private fun variantValidator(): Boolean {
        productVariant.clear()
        if (binding.v1.isChecked) {
            if (binding.productPriceV1.text!!.isEmpty()) {
                binding.productPriceV1.requestFocus()
                binding.productPriceV1.error = "Amount required"
                return false
            } else {
                val v1 = ProductVariant(
                    "White",
                    "L",
                    binding.productPriceV1.text.toString().toFloat()
                )
                productVariant.add(v1)
            }
        }
        if (binding.v2.isChecked) {
            if (binding.productPriceV2.text!!.isEmpty()) {
                binding.productPriceV2.requestFocus()
                binding.productPriceV2.error = "Amount required"
                return false
            } else {
                val v2 = ProductVariant(
                    "Black",
                    "L",
                    binding.productPriceV2.text.toString().toFloat()
                )
                productVariant.add(v2)
            }
        }
        if (binding.v3.isChecked) {
            if (binding.productPriceV3.text!!.isEmpty()) {
                binding.productPriceV3.requestFocus()
                binding.productPriceV3.error = "Amount required"
                return false
            } else {
                val v3 = ProductVariant(
                    "White",
                    "M",
                    binding.productPriceV3.text.toString().toFloat()
                )
                productVariant.add(v3)
            }
        }
        if (binding.v4.isChecked) {
            if (binding.productPriceV4.text!!.isEmpty()) {
                binding.productPriceV4.requestFocus()
                binding.productPriceV4.error = "Amount required"
                return false
            } else {
                val v4 = ProductVariant(
                    "Black",
                    "M",
                    binding.productPriceV4.text.toString().toFloat()
                )
                productVariant.add(v4)
            }
        }
        if (productVariant.size == 0) {
            Toast.makeText(this, "Minimum one variant required", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
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