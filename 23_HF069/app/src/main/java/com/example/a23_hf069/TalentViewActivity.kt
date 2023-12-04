package com.example.a23_hf069

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class TalentViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_talent_view)
    }
}