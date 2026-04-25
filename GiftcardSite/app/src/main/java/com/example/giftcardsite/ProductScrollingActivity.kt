package com.example.giftcardsite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import com.google.android.material.appbar.CollapsingToolbarLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giftcardsite.api.model.*
import com.example.giftcardsite.api.service.ProductInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductScrollingActivity : AppCompatActivity() {
    var loggedInUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title
        loggedInUser = intent.getParcelableExtra<User>("User")
        findViewById<Button>(R.id.view_cards_button).setOnClickListener{
            val navIntent = Intent(this, CardScrollingActivity::class.java).apply {
                putExtra("User", loggedInUser)
            }
            startActivity(navIntent)
        }
        val retrofitBuilder: Retrofit.Builder = Retrofit.Builder().baseUrl("https://appsec.moyix.net").addConverterFactory(
                GsonConverterFactory.create())
        val retrofit: Retrofit = retrofitBuilder.build()
        val productClient: ProductInterface = retrofit.create(ProductInterface::class.java)
        val activityContext = this
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        productClient.getAllProducts()?.enqueue(object :
                Callback<List<Product?>?> {
            override fun onFailure(call: Call<List<Product?>?>, t: Throwable) {
                Log.d("Products", "Could not fetch products")
                Log.d("Products", t.message.toString())

            }

            override fun onResponse(call: Call<List<Product?>?>, response: Response<List<Product?>?>) {
                if (!response.isSuccessful) {
                    Log.d("Products", "Product fetch unsuccessful")
                }
                val products = response.body()
                Log.d("Products", "Products loaded")
                if (products == null) {
                    Log.d("Products", "Received empty product list")
                    Log.d("Products", response.toString())
                } else {
                    recyclerView.adapter = RecyclerViewAdapter(activityContext, products, loggedInUser)
                }
            }
        })

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
