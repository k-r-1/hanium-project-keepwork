package com.example.a23_hf069

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CommunityBoardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommunityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_community_board, container, false)

        val community = listOf(
            CommunityBoard("보험총무사무원 양성과정", "2023-06-27 ~ 2023-09-19", "고양새일센터"),
            CommunityBoard("오피스 실무 사무원 양성과정", "2023-07-25 ~ 2023-10-12", "광명새일센터"),
            CommunityBoard("사회복지 사무원 양성과정", "2023-06-13 ~ 2023-08-22", "광명새일센터"),
            CommunityBoard("산모 영유아 돌봄 전문가 양성과정", "2023-08-28 ~ 2023-10-27", "김포새일센터" ),
            CommunityBoard("쇼핑몰창업&프리마켓셀러 양성과정", "2023-09-04 ~ 2023-11-30", "성남새일센터"),
            CommunityBoard("세무회계사무원 양성과정", "2023-07-03 ~ 2023-09-26", "영통새일센터"),
            CommunityBoard("노인맞춤돌봄 생활지원사", "2023-09-04 ~ 2023-11-10", "영통새일센터"),
            CommunityBoard("학교행정사무원 양성과정", "2023-09-11 ~ 2023-11-23","오산새일센터")
        )
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = CommunityAdapter(community)
        // RecyclerView 아이템 간의 구분선을 추가합니다.
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL)
        val dividerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        dividerItemDecoration.setDrawable(dividerDrawable!!)
        recyclerView.addItemDecoration(dividerItemDecoration)

        recyclerView.adapter = adapter

        return view
    }

}