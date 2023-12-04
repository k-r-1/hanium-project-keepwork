package com.example.a23_hf069

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FAQAdapter(private var faqList: List<FAQItem>) : RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)

        fun bind(faqItem: FAQItem) {
            titleTextView.text = faqItem.title
            contentTextView.text = faqItem.content

            // 아이템을 클릭했을 때 내용이 확장/축소되도록 설정
            titleTextView.setOnClickListener {
                if (contentTextView.visibility == View.VISIBLE) {
                    contentTextView.visibility = View.GONE
                } else {
                    contentTextView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.faq_item_layout, parent, false)
        return FAQViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val currentItem = faqList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = faqList.size

    // 새로운 데이터로 RecyclerView를 업데이트하는 함수
    fun updateData(newData: List<FAQItem>) {
        faqList = newData
        notifyDataSetChanged()
    }
}
