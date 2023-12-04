package com.example.a23_hf069

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class MypageFragment : Fragment() {
    // 사용자 ID를 저장할 변수
    private lateinit var userId: String

    lateinit var member_info : Button
    lateinit var notificationButton : Button
    lateinit var noticeButton: Button
    lateinit var faqButton : Button
    lateinit var logoutButton: Button
    lateinit var saeil_center_btn : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Argument로부터 전달받은 사용자 ID를 변수에 저장
        if (arguments != null) {
            userId = arguments?.getString("userId", "") ?: ""
        }
        // 사용자 ID를 표시할 TextView 초기화
        val textID = view.findViewById<TextView>(R.id.tv_user_name)
        textID.text = userId

        saeil_center_btn = view.findViewById<Button>(R.id.saeil_center_btn)
        saeil_center_btn.setOnClickListener {// 가까운 새일센터 찾기 사이트 연결
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://saeil.mogef.go.kr/hom/info/search.do"))
            startActivity(intent)
        }

        member_info = view.findViewById<Button>(R.id.edit_memberinfo_btn)
        member_info.setOnClickListener(){// 회원정보 수정 프래그먼트로 전환

            val intent = Intent(requireContext(), MemberInfoEditActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
        notificationButton = view.findViewById<Button>(R.id.notificationButton)
        notificationButton.setOnClickListener(){// 알림 설정 프래그먼트로 전환
            val NotificationSettingsFragment = NotificationSettingsFragment()
            FragmentManagerHelper.replaceFragment(
                requireActivity().supportFragmentManager,
                R.id.fl_container,
                NotificationSettingsFragment
            )
        }
        noticeButton = view.findViewById<Button>(R.id.noticeButton)
        noticeButton.setOnClickListener {
            // NoticeActivity로 이동하는 코드
            val intent = Intent(requireContext(), NoticeActivity::class.java)
            startActivity(intent)
        }

        faqButton = view.findViewById<Button>(R.id.faqButton)
        faqButton.setOnClickListener(){// 자주 묻는 질문 프래그먼트로 전환
            val FAQFragment = FAQFragment()
            FragmentManagerHelper.replaceFragment(
                requireActivity().supportFragmentManager,
                R.id.fl_container,
                FAQFragment
            )
        }

        logoutButton = view.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener(){// P_login프래그먼트로 전환
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }
}