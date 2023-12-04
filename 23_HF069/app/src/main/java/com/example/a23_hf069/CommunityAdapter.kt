package com.example.a23_hf069

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommunityAdapter(private val communityList: List<CommunityBoard>) : RecyclerView.Adapter<CommunityAdapter.CommunityBoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityBoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.community_board_item, parent, false)
        return CommunityBoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommunityBoardViewHolder, position: Int) {
        val community = communityList[position]
        holder.bind(community)
    }

    override fun getItemCount() = communityList.size

    class CommunityBoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView2)
        private val companyTextView: TextView = itemView.findViewById(R.id.companyTextView2)
        private val regionTextView: TextView = itemView.findViewById(R.id.regionContTextView2)

        fun bind(cb: CommunityBoard) {
            titleTextView.text = cb.title
            companyTextView.text = cb.company
            regionTextView.text = cb.region
        }
    }
}
data class CommunityBoard(
    val title: String,
    val company: String,
    val region: String
)