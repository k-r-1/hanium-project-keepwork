package com.example.a23_hf069

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TalentManagementFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_talent_management, container, false)

//        // 합격 버튼 참조 및 클릭 리스너 설정
//        val passButton = rootView.findViewById<Button>(R.id.passBtn)
//        passButton.setOnClickListener {
//            // 합격 탭에 이력서 추가
//            // adapter.addItem("새로운 합격 아이템")
//            // 2번 탭(합격 탭)으로 이동
//            viewPager.currentItem = 2
//        }
//        // 불합격 버튼 참조 및 클릭 리스너 설정
//        val failButton = rootView.findViewById<Button>(R.id.failBtn)
//        failButton.setOnClickListener {
//            // 불합격 탭에 이력서 추가
//            // adapter.addItem("새로운 불합격 아이템")
//            // 3번 탭(불합격 탭)으로 이동
//            viewPager.currentItem = 3
//        }
//        // 검토중 버튼 참조 및 클릭 리스너 설정
//        val onHoldButton = rootView.findViewById<Button>(R.id.onHoldBtn)
//        passButton.setOnClickListener {
//            // 검토중 탭에 이력서 추가
//            // adapter.addItem("검토중 아이템")
//            // 1번 탭(검토중 탭)으로 이동
//            viewPager.currentItem = 1
//        }

        // ViewPager2 어댑터 연결
        viewPager = rootView.findViewById(R.id.ViewPager2)
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        // TabLayout과 ViewPager2 연동
        tabLayout = rootView.findViewById<TabLayout>(R.id.tablayout01)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "전체"
                1 -> "검토중"
                2 -> "합격"
                3 -> "불합격"
                else -> null
            }
        }.attach()



        return rootView
    }

}