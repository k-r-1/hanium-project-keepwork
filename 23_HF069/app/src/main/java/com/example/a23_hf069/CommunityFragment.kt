package com.example.a23_hf069

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class CommunityFragment : Fragment(), TabLayout.OnTabSelectedListener {

    private lateinit var searchContent: EditText
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_community, container, false)

        val adapter = PagerAdapter(childFragmentManager)
        adapter.addFragment(CommunityBoardFragment(), "커뮤니티")
        adapter.addFragment(PublicityBoardFragment(), "홍보 게시판")

        viewPager = view.findViewById<ViewPager>(R.id.viewpager01)
        viewPager.adapter = adapter

        tabLayout = view.findViewById<TabLayout>(R.id.tablayout01)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(this)

        searchContent = view.findViewById<EditText>(R.id.searchContent)
        searchContent.setOnTouchListener { _, _ ->
            val currentTab = tabLayout.selectedTabPosition
            if (currentTab == 1) {
                val PublicityBoardSearchFragment = PublicityBoardSearchFragment()
                FragmentManagerHelper.replaceFragment(
                    requireActivity().supportFragmentManager,
                    R.id.fl_container,
                    PublicityBoardSearchFragment
                )
                searchContent.hint = "홍보 게시판 검색"
            } else if (currentTab == 0) {
                val CommunityBoardSearchFragment = CommunityBoardSearchFragment()
                FragmentManagerHelper.replaceFragment(
                    requireActivity().supportFragmentManager,
                    R.id.fl_container,
                    CommunityBoardSearchFragment
                )
                searchContent.hint = "커뮤니티 검색"
            }
            true
        }


        return view
    }

    private fun hideKeyboard() {
        // 키보드 숨김 처리를 수행
        val imm =
            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
        view?.clearFocus()
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        val currentTab = tab?.position
        if (currentTab == 1) {
            searchContent.hint = "홍보 게시판 검색"
        } else if (currentTab == 0) {
            searchContent.hint = "커뮤니티 검색"
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        // Do nothing
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        // Do nothing
    }

}