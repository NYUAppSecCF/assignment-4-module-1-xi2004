package com.example.giftcardsite

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giftcardsite.api.model.*
import com.example.giftcardsite.api.service.CardInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CardScrollingActivity : AppCompatActivity() {
    private var loggedInUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(findViewById(R.id.toolbar))
        loggedInUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("User", User::class.java)
        } else {
            intent.getParcelableExtra<User>("User")
        }
        val viewProductsBtn : Button = findViewById<Button>(R.id.view_cards_button)
        viewProductsBtn.text = "View Products"
        viewProductsBtn.setOnClickListener {
            val navIntent = Intent(this, ProductScrollingActivity::class.java).apply{
                putExtra("User", loggedInUser)
            }
            startActivity(navIntent)
        }
        val retrofitBuilder: Retrofit.Builder = Retrofit.Builder().baseUrl("https://appsec.moyix.net").addConverterFactory(
            GsonConverterFactory.create())
        val retrofit: Retrofit = retrofitBuilder.build()
        val cardClient: CardInterface = retrofit.create(CardInterface::class.java)
        val activityContext = this
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val authToken = "Token ${loggedInUser?.token}"
        recyclerView.layoutManager = layoutManager
        cardClient.getCards(authToken)?.enqueue(object :
            Callback<List<Card?>?> {
            override fun onFailure(call: Call<List<Card?>?>, t: Throwable) {
                Log.d("Cards", "Failed to retrieve cards")
                Log.d("Cards", t.message.toString())

            }

            override fun onResponse(call: Call<List<Card?>?>, response: Response<List<Card?>?>) {
                if (!response.isSuccessful) {
                    Log.d("Cards", "Card retrieval unsuccessful")
                }
                val cards = response.body()
                Log.d("Cards", "Cards loaded successfully")
                if (cards == null) {
                    Log.d("Cards", "Received empty card list")
                    Log.d("Cards", response.toString())
                } else {
                    recyclerView.adapter = CardRecyclerViewAdapter(activityContext, cards, loggedInUser)
                }
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
