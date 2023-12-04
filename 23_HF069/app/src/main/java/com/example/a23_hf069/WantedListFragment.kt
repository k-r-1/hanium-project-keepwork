package com.example.a23_hf069

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout


class WantedListFragment : Fragment(), TabLayout.OnTabSelectedListener {
    private lateinit var filter: Button
    private lateinit var searchContent: EditText
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wanted_list, container, false)

        val adapter = PagerAdapter(childFragmentManager)
        adapter.addFragment(WantedRequestingFragment(), "즉시지원")
        adapter.addFragment(WantedWorkNetFragment(), "워크넷 채용공고")

        viewPager = view.findViewById<ViewPager>(R.id.viewpager01)
        viewPager.adapter = adapter

        tabLayout = view.findViewById<TabLayout>(R.id.tablayout01)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(this)

        filter = view.findViewById<Button>(R.id.conditionButton)
        filter.setOnClickListener() {
            val WantedFilteringFragment = WantedFilteringFragment()
            FragmentManagerHelper.replaceFragment(
                requireActivity().supportFragmentManager,
                R.id.fl_container,
                WantedFilteringFragment
            )
        }

        searchContent = view.findViewById<EditText>(R.id.searchContent)
        searchContent.setOnTouchListener { _, _ ->
            val currentTab = tabLayout.selectedTabPosition
            if (currentTab == 1) {
                val WantedWorkNetSearchFragment = WantedWorkNetSearchFragment()
                FragmentManagerHelper.replaceFragment(
                    requireActivity().supportFragmentManager,
                    R.id.fl_container,
                    WantedWorkNetSearchFragment
                )
                searchContent.hint = "워크넷 채용공고 검색"
            } else if (currentTab == 0) {
                val WantedRequestingSearchFragment = WantedRequestingSearchFragment()
                FragmentManagerHelper.replaceFragment(
                    requireActivity().supportFragmentManager,
                    R.id.fl_container,
                    WantedRequestingSearchFragment
                )
                searchContent.hint = "즉시지원 가능한 일자리 검색"
            }
            true
        }

        return view
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        val currentTab = tab?.position
        if (currentTab == 1) {
            searchContent.hint = "워크넷 채용공고 검색"
        } else if (currentTab == 0) {
            searchContent.hint = "즉시지원 가능한 일자리 검색"
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        // Do nothing
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        // Do nothing
    }
}
