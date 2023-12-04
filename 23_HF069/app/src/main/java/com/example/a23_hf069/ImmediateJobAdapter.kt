package com.example.a23_hf069

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ImmediateJobAdapter(private val jobList: List<ImmediateJob>) : RecyclerView.Adapter<ImmediateJobAdapter.JobViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.job_item, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        holder.bind(job)
    }

    override fun getItemCount() = jobList.size

    class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val companyTextView: TextView = itemView.findViewById(R.id.companyTextView)
        private val regionTextView: TextView = itemView.findViewById(R.id.regionContTextView)

        fun bind(job: ImmediateJob) {
            titleTextView.text = job.title
            companyTextView.text = job.company
            regionTextView.text = job.region
        }
    }
}
data class ImmediateJob(
    val title: String,
    val company: String,
    val region: String
)