package com.example.a23_hf069

import JobPosting
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WantedRequestingFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JobRequestingAdapter
    private lateinit var retrofitInterface: RetrofitInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wanted_requesting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView와 어댑터를 초기화합니다.
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerviewJobRequesting)
        adapter = JobRequestingAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Retrofit 인스턴스를 초기화합니다.
        val retrofit = Retrofit.Builder()
            .baseUrl(RetrofitInterface.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit.create(RetrofitInterface::class.java)

        // 모든 JobList 데이터를 가져옵니다.
        fetchJobListData()
    }

    private fun fetchJobListData() {
        val call = retrofitInterface.getAllJobPostings()
        call.enqueue(object : Callback<List<JobPosting>> {
            override fun onResponse(call: Call<List<JobPosting>>, response: Response<List<JobPosting>>) {
                if (response.isSuccessful) {
                    val jobList = response.body()
                    if (jobList != null) {
                        // LinearLayoutManager를 생성하고 reverseLayout을 true로 설정
                        val layoutManager = LinearLayoutManager(requireContext())
                        layoutManager.reverseLayout = true
                        layoutManager.stackFromEnd = true
                        recyclerView.layoutManager = layoutManager

                        // 어댑터에 데이터를 설정합니다.
                        adapter.setData(jobList)
                    }
                } else {
                    // API 호출에 실패한 경우 에러 메시지를 표시합니다.
                    Toast.makeText(context, "Error fetching job postings", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<JobPosting>>, t: Throwable) {
                // 네트워크 오류 등의 이유로 API 호출에 실패한 경우 에러 메시지를 표시합니다.
                Toast.makeText(context, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
