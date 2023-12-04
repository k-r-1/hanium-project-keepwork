package com.example.a23_hf069

import C_loginFragment
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.a23_hf069.databinding.ActivityLoginBinding

class MainActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        val loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        // 기본 툴바 숨기기
        supportActionBar?.hide()

        val adapter = PagerAdapter(supportFragmentManager)
        adapter.addFragment(P_loginFragment(), "개인회원")
        adapter.addFragment(C_loginFragment(), "기업회원")

        loginBinding.viewpager01.adapter = adapter
        loginBinding.tablayout01.setupWithViewPager(loginBinding.viewpager01)
    }

}
