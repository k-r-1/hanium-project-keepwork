package com.example.a23_hf069

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class JobManagementFragment : Fragment() {

    // 사용자 ID를 저장할 변수
    private lateinit var userCompanyName: String

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_job_management, container, false)

        val tabLayout: TabLayout = rootView.findViewById(R.id.tabLayout_jobManagement) // TabLayout ID
        val viewPager: ViewPager2 = rootView.findViewById(R.id.viewPager_jobManagement) // ViewPager2 ID

        // Argument로부터 전달받은 사용자 ID를 변수에 저장
        if (arguments != null) {
            userCompanyName = arguments?.getString("userCompanyName", "") ?: ""
        }

        val fragmentList = listOf(
            JobManagementPostFragment(),
            JobManagementSaveFragment(),
            JobManagementEndFragment()
        )

        val adapter = TabPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "게재 (0)"
                1 -> tab.text = "임시저장 (0)"
                2 -> tab.text = "마감 (0)"
                else -> tab.text = ""
            }
        }.attach()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Argument로부터 전달받은 사용자 ID를 변수에 저장
        if (arguments != null) {
            userCompanyName = arguments?.getString("userCompanyName", "") ?: ""
        }

        val floatbtnJobPost: FloatingActionButton = view.findViewById(R.id.floatbtnJobPost)
        floatbtnJobPost.setOnClickListener {
            val intent = Intent(requireContext(), JobPostingActivity::class.java)
            startActivity(intent)
        }
    }

}

