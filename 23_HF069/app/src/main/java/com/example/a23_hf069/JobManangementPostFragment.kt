package com.example.a23_hf069

import JobPosting
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class JobManagementPostFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JobPostingAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        // 화면이 다시 활성화될 때마다 데이터를 새로고침합니다.
        fetchDataFromServer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_job_manangement_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recylcerviewJobPost)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = JobPostingAdapter(mutableListOf()) // 빈 MutableList로 초기화
        recyclerView.adapter = adapter

        // 서버에서 작업 게시 데이터 가져오기
        fetchDataFromServer()
    }

    private fun fetchDataFromServer() {
        // 사용자 이름 가져오기 (이 부분은 SharedPreferences를 통해 사용자 이름을 얻는 방식으로 진행)
        val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", null) ?: ""

        val retrofit = Retrofit.Builder()
            .baseUrl(RetrofitInterface.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RetrofitInterface::class.java)

        // Retrofit 요청을 수정하여 userName을 사용하여 해당 사용자의 공고만 가져오도록 합니다.
        val call = service.getJobPostingData(userName)

        call.enqueue(object : Callback<List<JobPosting>> {
            override fun onResponse(call: Call<List<JobPosting>>, response: Response<List<JobPosting>>) {
                if (response.isSuccessful) {
                    val jobPostingList = response.body()?.toMutableList() ?: mutableListOf()

                    // LinearLayoutManager를 생성하고 reverseLayout을 true로 설정
                    val layoutManager = LinearLayoutManager(requireContext())
                    layoutManager.reverseLayout = true
                    layoutManager.stackFromEnd = true
                    recyclerView.layoutManager = layoutManager

                    // 가져온 공고 데이터를 RecyclerView에 설정하여 화면에 표시
                    adapter = JobPostingAdapter(jobPostingList)
                    recyclerView.adapter = adapter
                } else {
                    // 처리 중 오류 발생
                    // 오류 처리 로직을 추가하세요.
                }
            }

            override fun onFailure(call: Call<List<JobPosting>>, t: Throwable) {
                // 네트워크 오류 등으로 요청 실패
                // 오류 처리 로직 추가
            }
        })
    }
}