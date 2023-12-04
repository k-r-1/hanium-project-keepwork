package com.example.a23_hf069

import HomeFragment
import ResumeFragment
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val userId = intent.getStringExtra("userId") // Intent에서 사용자 아이디를 받아옴

        // 기본 툴바 숨기기
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.hide()
        }

        // 하단 탭이 눌렸을 때 화면을 전환하기 위해선 이벤트 처리하기 위해 BottomNavigationView 객체 생성
        var bnv_main = findViewById(R.id.bnv_main) as BottomNavigationView

        // OnNavigationItemSelectedListener를 통해 탭 아이템 선택 시 이벤트를 처리
        // navi_menu.xml 에서 설정했던 각 아이템들의 id를 통해 알맞은 프래그먼트로 변경하게 한다.
        bnv_main.run { setOnNavigationItemSelectedListener {
            when(it.itemId) { // 홈
                R.id.homeMenu -> {
                    // 다른 프래그먼트 화면으로 이동하는 기능
                    val homeFragment = HomeFragment()

                    // 사용자 아이디를 Bundle에 추가해서 전달
                    val bundle = Bundle()
                    bundle.putString("userId", userId)
                    homeFragment.arguments = bundle

                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, homeFragment).commit()
                }
                R.id.jobMenu -> { // 채용공고
                    val jobFragment = WantedListFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, jobFragment).commit()
                }
                R.id.resumeMenu -> { // 이력서
                    val resumeFragment = ResumeFragment()
                    // 사용자 아이디를 Bundle에 추가해서 전달
                    val bundle = Bundle()
                    bundle.putString("userId", userId)
                    resumeFragment.arguments = bundle
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, resumeFragment).commit()
                }
                R.id.communityMenu -> { // 커뮤니티
                    val communityFragment = CommunityFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, communityFragment).commit()
                }
                R.id.mypageMenu -> { // 마이페이지
                    val mypageFragment = MypageFragment()
                    // 사용자 아이디를 Bundle에 추가해서 전달
                    val bundle = Bundle()
                    bundle.putString("userId", userId)
                    mypageFragment.arguments = bundle
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, mypageFragment).commit()
                }
            }
            true
        }
            bnv_main.selectedItemId = R.id.homeMenu
        }
    }
}