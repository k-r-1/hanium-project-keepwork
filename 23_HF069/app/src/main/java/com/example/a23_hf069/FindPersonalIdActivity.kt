package com.example.a23_hf069

import android.app.Activity
import android.content.Intent
import android.graphics.pdf.PdfDocument.Page
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.example.a23_hf069.databinding.ActivityFindPersonalIdBinding

class FindPersonalIdActivity : AppCompatActivity() {
    private lateinit var findPersonalIdBinding: ActivityFindPersonalIdBinding

    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findPersonalIdBinding = ActivityFindPersonalIdBinding.inflate(layoutInflater)
        setContentView(findPersonalIdBinding.root)
        val findPersonalIdBinding = ActivityFindPersonalIdBinding.inflate(layoutInflater)
        setContentView(findPersonalIdBinding.root)

        //기본 툴바 숨기기
        supportActionBar?.hide()

        val adapter = PagerAdapter(supportFragmentManager)
        adapter.addFragment(FindPersonalIdPhoneFragment(), "전화번호로 찾기")
        adapter.addFragment(FindPersonalIdEmailFragment(), "이메일로 찾기")

        findPersonalIdBinding.viewpagerFindPid.adapter = adapter
        findPersonalIdBinding.tablayoutFindPid.setupWithViewPager(findPersonalIdBinding.viewpagerFindPid)

        backButton = findViewById(R.id.backButton_findPid)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}