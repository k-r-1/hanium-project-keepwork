package com.example.a23_hf069

import JobPosting
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JobRequestingAdapter : RecyclerView.Adapter<JobRequestingAdapter.JobViewHolder>() {
    private var jobPostings: List<JobPosting> = emptyList()

    fun setData(data: List<JobPosting>) {
        jobPostings = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.job_requesting_item, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobPostings[position]
        // ViewHolder의 뷰에 데이터를 바인딩하는 코드를 추가하세요.
        holder.jobTitleTextView.text = job.job_title
        holder.endDateTextView.text = "~${job.job_deadline}"
        holder.tvJobExperienceRequirede.text = job.job_experience_required
        holder.tvJobEducationRequirede.text = job.job_education_required
        holder.tvJobCompanyName.text = job.company_name


        // 리스트 아이템 클릭 이벤트 처리
        holder.itemView.setOnClickListener {
            // JobPostingDetailActivity로 이동하고 선택한 항목의 정보를 전달합니다.
            val context = holder.itemView.context
            val intent = Intent(context, JobRequestingDetailActivity::class.java)

            // 선택한 항목의 정보를 인텐트에 추가합니다.
            intent.putExtra("jobPosting", job)
            intent.putExtra("companyName", job.company_name)

            // Activity를 시작합니다.
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return jobPostings.size
    }

    inner class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder의 뷰 요소를 초기화하는 코드를 추가하세요.
        val tvJobCompanyName: TextView = itemView.findViewById(R.id.tvJobCompanyName)
        val tvJobExperienceRequirede: TextView = itemView.findViewById(R.id.tvJobExperienceRequirede)
        val tvJobEducationRequirede: TextView = itemView.findViewById(R.id.tvJobEducationRequirede)
        /*val tvJobAdress: TextView = itemView.findViewById(R.id.tvJobAdress)*/

        val jobTitleTextView: TextView = itemView.findViewById(R.id.tvJobManagementTitle)
        val endDateTextView: TextView = itemView.findViewById(R.id.tvJobManagementEndDate)
    }
}