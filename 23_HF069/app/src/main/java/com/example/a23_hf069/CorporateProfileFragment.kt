package com.example.a23_hf069

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment

class CorporateProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_corporate_profile.xml 레이아웃을 프래그먼트의 뷰로 인플레이트.
        return inflater.inflate(R.layout.fragment_corporate_profile, container, false)
    }

}