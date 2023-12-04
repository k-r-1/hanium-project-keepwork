package com.example.a23_hf069

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WholeApplicantsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_whole_applicants, container, false)

        // "applicant" 레이아웃을 찾습니다.
        val applicantLayout = view.findViewById<LinearLayout>(R.id.applicant)

        // "applicant" 레이아웃에 클릭 리스너를 설정합니다.
        applicantLayout.setOnClickListener {
            // 클릭 시 TalentViewActivity로 전환하는 코드를 작성합니다.
            val intent = Intent(requireContext(), TalentViewActivity::class.java)

            // 필요한 데이터를 인텐트에 추가할 수 있습니다.
            // intent.putExtra("key", value)

            // TalentViewActivity를 시작합니다.
            startActivity(intent)
        }

        return view
    }
}
