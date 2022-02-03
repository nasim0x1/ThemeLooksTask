package themelooks.test.task.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import themelooks.test.task.databinding.ActivityMainBinding
import themelooks.test.task.ui.view.admin.AdminHome
import themelooks.test.task.ui.view.user.UserHome

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.addProducts.setOnClickListener {
            startActivity(Intent(this, AdminHome::class.java))
        }

        binding.viewProducts.setOnClickListener {
            startActivity(Intent(this, UserHome::class.java))
        }


    }
}