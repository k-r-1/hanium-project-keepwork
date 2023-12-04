package com.example.a23_hf069

import JobPosting
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ResumeCompleteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataAdapter: ResumeFragment.DataAdapter
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_resume_complete, container, false)
        // userId 가져오기
        userId = arguments?.getString("userId")

        // RecyclerView 설정
        recyclerView = view.findViewById(R.id.recyclerviewResumeComplete)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Adapter 설정
        dataAdapter = ResumeFragment.DataAdapter(emptyList(), userId ?: "")
        recyclerView.adapter = dataAdapter

        // 작성 완료된 이력서 데이터 가져오기
        fetchDataFromServer()

        return view
    }

    private fun fetchDataFromServer() {
        val retrofit = Retrofit.Builder()
            .baseUrl(RetrofitInterface.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(RetrofitInterface::class.java)

        // userId를 사용하여 ResumeModel을 가져옵니다.
        apiService.getResumeData(userId, null, null, null, null, null, null, null, null, null)
            .enqueue(object : Callback<List<ResumeModel>> {
                override fun onResponse(
                    call: Call<List<ResumeModel>>,
                    response: Response<List<ResumeModel>>
                ) {
                    if (response.isSuccessful) {
                        val resumeList = response.body()
                        dataAdapter.dataList = resumeList ?: emptyList()
                        dataAdapter.notifyDataSetChanged()
                    }
                else {
                    // 오류 처리
                }
            }

                    override fun onFailure(call: Call<List<ResumeModel>>, t: Throwable) {
                // 네트워크 오류 처리
            }
    })
}
}