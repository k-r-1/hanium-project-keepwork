package com.example.a23_hf069

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ResumeSelectionActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var requestingButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resume_selection)

        // 기본 툴바 숨기기
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.hide()
        }

        requestingButton = findViewById(R.id.requesting_button)
        requestingButton.setOnClickListener {
            val intent = Intent(this@ResumeSelectionActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        val closeButton = findViewById<ImageButton>(R.id.backButton)
        closeButton.setOnClickListener {
            finish()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // RecyclerView 어댑터 설정 (어댑터 클래스를 만들어야 함)
        val adapter = ResumeSelectionAdapter() // MyAdapter는 RecyclerView.Adapter를 상속한 사용자 정의 어댑터 클래스입니다.
        recyclerView.adapter = adapter

        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        val userId = userViewModel.userId

        // Retrieve resumes for the userId
        if (userId != null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(RetrofitInterface.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(RetrofitInterface::class.java)
            val call = service.getResumeData(userId)

            call.enqueue(object : Callback<List<ResumeModel>> {
                override fun onResponse(
                    call: Call<List<ResumeModel>>,
                    response: Response<List<ResumeModel>>
                ) {
                    if (response.isSuccessful) {
                        val adapter = ResumeSelectionAdapter()
                        recyclerView.adapter = adapter

                        val resumeList = response.body()
                        if (resumeList != null) {
                            for (resume in resumeList) {
                                adapter.addItem(resume.resume_title)
                            }
                        }
                    } else {
                        // Handle the error
                    }
                }

                override fun onFailure(call: Call<List<ResumeModel>>, t: Throwable) {
                    // Handle the network error
                }
            })
        }



    }
}