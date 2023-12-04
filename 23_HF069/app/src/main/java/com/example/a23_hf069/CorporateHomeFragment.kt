package com.example.a23_hf069

import JobPosting
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.a23_hf069.R
import com.example.a23_hf069.RetrofitInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CorporateHomeFragment : Fragment() {

    private lateinit var userCompanyName: String
    private lateinit var postingTitleTextView: TextView
    private lateinit var deadlineTextView: TextView
    private lateinit var retrofit: Retrofit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_corporate_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 사용자 이름 가져오기 (이 부분은 SharedPreferences를 통해 사용자 이름을 얻는 방식으로 진행)
        val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        userCompanyName = sharedPreferences.getString("userName", null) ?: ""
        postingTitleTextView = view.findViewById(R.id.postingTitleTextView)
        deadlineTextView = view.findViewById(R.id.deadlineTextView)

        retrofit = Retrofit.Builder()
            .baseUrl(RetrofitInterface.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fetchJobPostings()
    }

    private fun fetchJobPostings() {
        val retrofitInterface = retrofit.create(RetrofitInterface::class.java)

        // userCompanyName과 userCompanyName이 같을 때만 해당 공고를 불러오도록 요청
        val call = retrofitInterface.getJobPostingData(userCompanyName)

        call.enqueue(object : Callback<List<JobPosting>> {
            override fun onResponse(
                call: Call<List<JobPosting>>,
                response: Response<List<JobPosting>>
            ) {
                if (response.isSuccessful) {
                    val jobPostingList = response.body() ?: emptyList()

                    val latestJobPosting = findLatestJobPosting(jobPostingList)

                    if (latestJobPosting != null) {
                        postingTitleTextView.text = latestJobPosting.job_title
                        deadlineTextView.text = latestJobPosting.job_deadline
                    }
                } else {
                    // 서버로부터 데이터를 가져오지 못한 경우 처리
                }
            }

            override fun onFailure(call: Call<List<JobPosting>>, t: Throwable) {
                // 네트워크 오류 처리
            }
        })
    }

    private fun findLatestJobPosting(jobPostings: List<JobPosting>?): JobPosting? {
        if (jobPostings.isNullOrEmpty()) {
            return null
        }

        // 가장 마지막에 등록된 공고를 찾는 로직
        var latestJobPosting: JobPosting? = null
        for (jobPosting in jobPostings) {
            if (latestJobPosting == null || jobPosting.job_listnum > latestJobPosting.job_listnum) {
                latestJobPosting = jobPosting
            }
        }

        return latestJobPosting
    }
}
