package com.example.a23_hf069

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import retrofit2.Call
import retrofit2.Response

class NoticeActivity : AppCompatActivity() {
    private lateinit var backButton_notice: ImageButton

    // 데이터를 담을 리스트
    private var dataList: List<NoticeItem> = emptyList()

    // 공지사항 목록을 표시할 RecyclerView 변수
    private lateinit var recyclerViewNotice: RecyclerView

    // 어댑터를 RecyclerView에 설정
    private lateinit var dataAdapterNotice: RecyclerView.Adapter<NoticeActivity.ViewHolder>

    data class NoticeItem(val noticeListNum: Int, val noticeTitle: String, val noticeDate: String)
    data class DataListContainer(val noticeList: List<NoticeItem>)

    // View holder class for DataAdapter
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: Button = itemView.findViewById(R.id.tvNotice_title)
        val dateTextView: TextView = itemView.findViewById(R.id.tvNotice_date)
    }

    // 어댑터 클래스
    inner class DataAdapterNotice : RecyclerView.Adapter<ViewHolder>() {
        // Inflate the layout for each item and return a ViewHolder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.notice_item, parent, false)
            return ViewHolder(itemView)

        }

        // Bind data to each item in the RecyclerView
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = dataList[position]
            holder.titleTextView.setText(item.noticeTitle)
            holder.dateTextView.text = item.noticeDate

            // 공지사항 제목 버튼 클릭 리스너 설정
            holder.titleTextView.setOnClickListener {
                val intent = Intent(holder.itemView.context, NoticeContentActivity::class.java)
                intent.putExtra("noticeListNum", item.noticeListNum)
                holder.itemView.context.startActivity(intent)
            }
        }

        // Get the number of items in the list
        override fun getItemCount(): Int {
            return dataList.size
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_notice)

        // RecyclerView 초기화 후 어댑터 설정
        recyclerViewNotice = findViewById(R.id.recyclerviewNotice)
        recyclerViewNotice.layoutManager = LinearLayoutManager(this)

        // 어댑터 생성 및 RecyclerView에 설정
        dataAdapterNotice = DataAdapterNotice()
        recyclerViewNotice.adapter = dataAdapterNotice

        // 사용자의 작성중 이력서 개수와 작성완료 이력서 개수를 업데이트
        fetchDataFromServer()

        backButton_notice = findViewById(R.id.backButton)

        backButton_notice.setOnClickListener {
            finish()
        }
    }

    // Retrofit 인스턴스 생성
    private val retrofit = Retrofit.Builder()
        .baseUrl(RetrofitInterface.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(RetrofitInterface::class.java)

    // 서버로부터 데이터를 가져오는 메서드
    private fun fetchDataFromServer() {
        val call = service.getNoticeData(null, null, null, null)
        call.enqueue(object : retrofit2.Callback<List<NoticeModel>> {
            override fun onResponse(call: Call<List<NoticeModel>>, response: Response<List<NoticeModel>>) {
                response.body()?.let { noticeList ->
                    // NoticeModel을 NoticeItem으로 변환
                    dataList = noticeList.map {
                        NoticeItem(it.notice_listnum, it.notice_title, it.notice_date)
                    }.reversed()

                    dataAdapterNotice.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<NoticeModel>>, t: Throwable) {
                Log.e("ServerResponse", "Error fetching data", t)
                Toast.makeText(this@NoticeActivity, "서버로부터 응답이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
