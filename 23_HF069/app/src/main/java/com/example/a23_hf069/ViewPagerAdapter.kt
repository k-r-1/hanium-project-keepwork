package com.example.a23_hf069
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter(fragmentActivity: TalentManagementFragment) : FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(
        WholeApplicantsFragment(), // 전체 탭에 연결될 Fragment
        OnHold(), // 검토중 탭에 연결될 Fragment
        PassResumeFragment(), // 합격 탭에 연결될 Fragment
        FailResumeFragment()  // 불합격 탭에 연결될 Fragment
    )
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
