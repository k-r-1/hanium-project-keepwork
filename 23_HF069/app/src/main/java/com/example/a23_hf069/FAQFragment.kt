package com.example.a23_hf069

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class FAQFragment : Fragment(), TabLayout.OnTabSelectedListener {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_f_a_q, container, false)

        val adapter = PagerAdapter(childFragmentManager)
        adapter.addFragment(FAQPersonalFragment(), "개인회원")
        adapter.addFragment(FAQCorporateFragment(), "기업회원")

        viewPager = view.findViewById<ViewPager>(R.id.viewpager01)
        viewPager.adapter = adapter

        tabLayout = view.findViewById<TabLayout>(R.id.tablayout01)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(this)

        val closeButton = view.findViewById<ImageButton>(R.id.backButton)
        closeButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        // Do nothing
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        // Do nothing
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        // Do nothing
    }

}