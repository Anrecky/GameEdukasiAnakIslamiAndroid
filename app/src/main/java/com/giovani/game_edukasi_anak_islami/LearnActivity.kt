package com.giovani.game_edukasi_anak_islami

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.giovani.game_edukasi_anak_islami.data.Question
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class LearnActivity : AppCompatActivity(), RecyclerViewClickListener {

    private var learnType: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var learnAdapter: LearnAdapter
    private val client: OkHttpClient = OkHttpClient()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)

        learnType = intent.getStringExtra("learnType").toString()
        val learnTitle = findViewById<TextView>(R.id.learnTitle)
        learnTitle.text = when(learnType){
            "Angka" -> getString(R.string.learn_number_title)
            else -> getString(R.string.learn_letter_title)
        }

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = if (learnType == "Angka") {
            GridLayoutManager(applicationContext, 2)
        } else {
            GridLayoutManager(applicationContext, 4)
        }

        learnAdapter = LearnAdapter(applicationContext)
        recyclerView.adapter = learnAdapter
        learnAdapter.listener = this

        lifecycleScope.launch(Dispatchers.IO) {
            val categoryId = when (learnType) {
                "Angka" -> 1
                else -> 2
            }
            try {
                // Build request
                val request = Request.Builder()
                    .url("http://${getString(R.string.ip_address)}/admin/api/read.php?id_kategori=$categoryId")
                    .build()
                // Execute request
                @Suppress("BlockingMethodInNonBlockingContext")
                val res = client.newCall(request).execute().body?.string()

                if (res != null) {
                    try {
                        // Parse result string JSON to data class
                            if(categoryId == 1){
                                learnAdapter.setDataList(
                                    Json.decodeFromString<List<Question>>(res).sortedBy { it.answer.toInt() })
                            }else{
                                learnAdapter.setDataList(
                                    Json.decodeFromString<List<Question>>(res).sortedBy { it.answer })
                            }

                        withContext(Dispatchers.Main) {
                            learnAdapter.notifyDataSetChanged()
                        }

                    } catch (err: Error) {
                        print("Error when parsing JSON: " + err.localizedMessage)
                    }
                } else {
                    print("Error: Get request returned no response")
                }
            } catch (err: Error) {
                print("Error when executing get request: " + err.localizedMessage)
            }
        }
    }

    override fun onItemClicked(view: View, menuItem: Question) {

        val itemDesc = (menuItem.question.replace("Yang manakah ", "")).replace("?", "")

        val intent = Intent(this, LearnDetailActivity::class.java).apply {
            putExtra("item", menuItem.answer)
            putExtra("itemDescription", itemDesc)

        }
        startActivity(intent)
    }


}