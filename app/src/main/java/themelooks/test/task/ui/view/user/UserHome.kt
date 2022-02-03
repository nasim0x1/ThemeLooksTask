package themelooks.test.task.ui.view.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import themelooks.test.task.databinding.ActivityUserHomeBinding
import themelooks.test.task.ui.adaper.ProductListAdapter
import themelooks.test.task.ui.viewmodel.UserHomeViewModel
import themelooks.test.task.util.Task

class UserHome : AppCompatActivity() {
    lateinit var viewModel: UserHomeViewModel
    lateinit var binding: ActivityUserHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.shimmerLayout.startShimmer()
        viewModel = ViewModelProvider(this)[UserHomeViewModel::class.java]
        binding.productView.setHasFixedSize(true)
//        binding.productView.layoutManager = GridLayoutManager(this,2)
        binding.productView.layoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        viewModel.productList.observe(this, Observer {
            when (it) {
                is Task.Loading -> {
                    binding.shimmerLayout.startShimmer()
                    binding.refresher.isRefreshing = true

                    binding.shimmerLayout.visibility = View.VISIBLE
                    binding.productView.visibility = View.INVISIBLE
                }
                is Task.Failed -> {
                    binding.refresher.isRefreshing = false
                    binding.shimmerLayout.stopShimmer()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is Task.Success -> {
                    binding.refresher.isRefreshing = false

                    if (it.data!!.isNotEmpty()) {
                        val adapter = ProductListAdapter(this, it.data)
                        binding.productView.adapter = adapter
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.visibility = View.GONE
                        binding.productView.visibility = View.VISIBLE


                    } else {
                        Toast.makeText(this, "No product found!", Toast.LENGTH_SHORT).show()
                    }

                }
            }

        })

        binding.refresher.setOnRefreshListener {
            viewModel.loadProduct()
        }
        viewModel.loadProduct()
    }

}