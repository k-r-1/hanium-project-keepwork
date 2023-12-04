package com.example.a23_hf069

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeContentActivity : AppCompatActivity() {
    private var noticeListNum: Int = -1
    private lateinit var noticeTitle: TextView
    private lateinit var noticeContent: TextView
    private lateinit var noticeDate: TextView

    private lateinit var backButton_click: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_notice_content)

        // Get user ID
        noticeListNum = intent.getIntExtra("noticeListNum", -1)

        noticeTitle = findViewById(R.id.notice_title)
        noticeContent = findViewById(R.id.notice_content)
        noticeDate = findViewById(R.id.notice_date)

        backButton_click = findViewById(R.id.backButton_click)

        // 공지사항 아이템 데이터 불러오기
        getNoticeItemData(noticeListNum)

        backButton_click.setOnClickListener {
            onBackPressed()
        }
    }

    // 공지사항 아이템 데이터 불러오기
    private fun getNoticeItemData(noticeListNum: Int) {
        // Retrofit 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl(RetrofitInterface.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // RetrofitInterface 인터페이스 구현
        val apiService = retrofit.create(RetrofitInterface::class.java)

        apiService.getNoticeData(noticeListNum, null, null, null).enqueue(object : Callback<List<NoticeModel>> {
            override fun onResponse(call: Call<List<NoticeModel>>, response: Response<List<NoticeModel>>) {
                if (response.isSuccessful) {
                    val noticeData = response.body()
                    handleNoticeItemData(noticeData)
                }
            }

            override fun onFailure(call: Call<List<NoticeModel>>, t: Throwable) {
                // 통신 실패 처리
                t.printStackTrace()
                Toast.makeText(
                    this@NoticeContentActivity,
                    "통신 오류: " + t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // 공지사항 아이템 데이터를 처리하는 함수 추가
    private fun handleNoticeItemData(noticeData: List<NoticeModel>?) {
        noticeData?.let { dataList ->
            val targetNotice = dataList.find { it.notice_listnum == this.noticeListNum }
            targetNotice?.let {
                noticeTitle.text = it.notice_title
                noticeContent.text = it.notice_content
                noticeDate.text = it.notice_date
            }
        }
    }
}