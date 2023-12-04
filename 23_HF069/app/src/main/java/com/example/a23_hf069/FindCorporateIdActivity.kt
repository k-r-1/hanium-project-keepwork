package com.example.a23_hf069

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.example.a23_hf069.databinding.ActivityFindCorporateIdBinding
import com.example.a23_hf069.databinding.ActivityFindPersonalIdBinding


class FindCorporateIdActivity : AppCompatActivity(){
    private lateinit var findCoporateIdBinding: ActivityFindCorporateIdBinding

    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findCoporateIdBinding = ActivityFindCorporateIdBinding.inflate(layoutInflater)
        setContentView(findCoporateIdBinding.root)
        val findCorporateIdBinding = ActivityFindPersonalIdBinding.inflate(layoutInflater)
        setContentView(findCorporateIdBinding.root)

        //기본 툴바 숨기기
        supportActionBar?.hide()

        val adapter = PagerAdapter(supportFragmentManager)
        adapter.addFragment(FindCorporatePhoneFragment(), "전화번호로 찾기")
        adapter.addFragment(FindCorporateEmailFragment(), "이메일로 찾기")

        findCorporateIdBinding.viewpagerFindPid.adapter = adapter
        findCorporateIdBinding.tablayoutFindPid.setupWithViewPager(findCorporateIdBinding.viewpagerFindPid)

        backButton = findViewById(R.id.backButton_findPid)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}