package com.example.a23_hf069

import JobPosting
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JobPostingAdapter(
    private val jobPostingList: MutableList<JobPosting>,
    private val companyData: List<C_MemberModel> = emptyList() // 기본 값 설정
) : RecyclerView.Adapter<JobPostingAdapter.JobPostingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobPostingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.jobmanagement_item, parent, false)
        return JobPostingViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobPostingViewHolder, position: Int) {
        val jobPosting = jobPostingList[position]

        // jobmanagement_item.xml 뷰에 데이터를 바인딩합니다.
        holder.jobTitleTextView.text = jobPosting.job_title
        holder.endDateTextView.text = "~${jobPosting.job_deadline}"
        holder.tvJobExperienceRequirede.text = jobPosting.job_experience_required
        holder.tvJobEducationRequirede.text = jobPosting.job_education_required

        // C_MemberModel에서 필요한 데이터를 가져와서 사용합니다.
        val companyInfo = companyData.find { it.company_name == jobPosting.company_name }
        if (companyInfo != null) {
          //  holder.tvJobCompanyName.text = companyInfo.company_name
          //  holder.tvJobAdress.text = companyInfo.company_address

        }

        // 버튼 클릭 이벤트를 처리합니다.
        holder.modifyButton.setOnClickListener {
            // 수정 처리를 여기에 추가하세요.
            // JobPostingModifyActivity로 이동하고 선택한 항목의 정보를 전달합니다.
            val context = holder.itemView.context
            val intent = Intent(context, JobPostingModifyActivity::class.java)

            // 선택한 항목의 정보를 인텐트에 추가합니다.
            intent.putExtra("jobPosting", jobPosting)
            intent.putExtra("companyName", companyInfo?.company_name) // 회사 이름을 추가

            // Activity를 시작합니다.
            context.startActivity(intent)
        }

        holder.repostButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(holder.itemView.context)
            dialogBuilder.setMessage("정말 재등록하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예") { dialog, id ->
                    // 예 버튼을 클릭한 경우
                    dialog.dismiss() // 다이얼로그를 닫습니다.

                    // 현재 항목의 공고 정보를 가져와서 재등록합니다.
                    val repostJobPosting = jobPostingList[position]
                    repostJobToServer(repostJobPosting)
                }
                .setNegativeButton("아니요") { dialog, id ->
                    // 아니요 버튼을 클릭한 경우
                    dialog.dismiss() // 다이얼로그를 닫습니다.
                }
            val alertDialog = dialogBuilder.create()
            alertDialog.show()
        }


        holder.endButton.setOnClickListener {
            // 마감 처리를 여기에 추가하세요.
        }

        holder.deleteButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(holder.itemView.context)
            dialogBuilder.setMessage("정말 삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예") { dialog, id ->
                    // 예 버튼을 클릭한 경우
                    dialog.dismiss() // 다이얼로그를 닫습니다.
                    // 서버에 삭제 요청을 보내고 항목을 삭제합니다.
                    val jobPostingToDelete = jobPostingList[position]
                    deleteJobPostingFromServer(jobPostingToDelete, position)
                }
                .setNegativeButton("아니요") { dialog, id ->
                    // 아니요 버튼을 클릭한 경우
                    dialog.dismiss() // 다이얼로그를 닫습니다.
                }
            val alertDialog = dialogBuilder.create()
            alertDialog.show()
        }

        // 리스트 아이템 클릭 이벤트 처리
        holder.itemView.setOnClickListener {
            // JobPostingDetailActivity로 이동하고 선택한 항목의 정보를 전달합니다.
            val context = holder.itemView.context
            val intent = Intent(context, JobPostingDetailActivity::class.java)

            // 선택한 항목의 정보를 인텐트에 추가합니다.
            intent.putExtra("jobPosting", jobPosting)
            intent.putExtra("companyName", companyInfo?.company_name) // 회사 이름을 추가

            // Activity를 시작합니다.
            context.startActivity(intent)
        }
    }

    private fun deleteJobPostingFromServer(jobPosting: JobPosting, position: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(RetrofitInterface.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RetrofitInterface::class.java)

        val call = service.deleteJobPosting(jobPosting.job_listnum)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 서버에서 삭제 성공한 경우
                    // 로컬 리스트에서도 해당 항목을 제거
                    jobPostingList.removeAt(position)
                    notifyItemRemoved(position)
                } else {
                    // 서버 에러 처리
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 네트워크 오류 처리
            }
        })
    }

    private fun repostJobToServer(jobPosting: JobPosting) {
        val retrofit = Retrofit.Builder()
            .baseUrl(RetrofitInterface.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RetrofitInterface::class.java)

        val call = service.postJob(jobPosting)
        call.enqueue(object : Callback<JobPosting> {
            override fun onResponse(call: Call<JobPosting>, response: Response<JobPosting>) {
                if (response.isSuccessful) {
                    // 서버에 재등록 성공한 경우
                    // 로컬 리스트에 항목 추가
                    jobPostingList.add(response.body()!!)
                    notifyItemInserted(jobPostingList.size - 1)
                } else {
                    // 서버 에러 처리
                }
            }

            override fun onFailure(call: Call<JobPosting>, t: Throwable) {
                // 네트워크 오류 처리
            }
        })
    }

    override fun getItemCount(): Int {
        return jobPostingList.size
    }

    inner class JobPostingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // val tvJobCompanyName: TextView = itemView.findViewById(R.id.tvJobCompanyName)
        val tvJobExperienceRequirede: TextView = itemView.findViewById(R.id.tvJobExperienceRequirede)
        val tvJobEducationRequirede: TextView = itemView.findViewById(R.id.tvJobEducationRequirede)
       // val tvJobAdress: TextView = itemView.findViewById(R.id.tvJobAdress)

        val jobTitleTextView: TextView = itemView.findViewById(R.id.tvJobManagementTitle)
        val endDateTextView: TextView = itemView.findViewById(R.id.tvJobManagementEndDate)
        val modifyButton: Button = itemView.findViewById(R.id.btnJobManagement_modify)
        val repostButton: Button = itemView.findViewById(R.id.btnJobManagement_repost)
        val endButton: Button = itemView.findViewById(R.id.btnJobManagement_end)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }
}
