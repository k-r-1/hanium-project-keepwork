package com.example.a23_hf069

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResumeAdapter(private val resumeList: List<ResumeModel>) : RecyclerView.Adapter<ResumeAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvResumeTitle: Button = itemView.findViewById(R.id.tvResumeTitle)
        val tvWriteStatus: TextView = itemView.findViewById(R.id.tvWriteStatus)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.resume_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = resumeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resume = resumeList[position]
        holder.tvResumeTitle.text = resume.resume_title
        holder.tvWriteStatus.text = resume.resume_complete
        // 리스트 아이템 클릭 이벤트 처리
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ResumeClickActivity::class.java)

            // 선택한 항목의 정보를 인텐트에 추가합니다.
            intent.putExtra("userName", resume.personal_id)
            intent.putExtra("resumeTitle", resume.resume_title) // 회사 이름을 추가

            // Activity를 시작합니다.
            context.startActivity(intent)
        }
    }
}