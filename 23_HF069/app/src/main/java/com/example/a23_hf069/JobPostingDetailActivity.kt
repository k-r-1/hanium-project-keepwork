package com.example.a23_hf069

import JobPosting
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBar

class JobPostingDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_posting_detail)

        // 기본 툴바 숨기기
        supportActionBar?.hide()

        val closeButton = findViewById<ImageButton>(R.id.backButton)
        closeButton.setOnClickListener {
            finish()
        }

        // JobPostingDetailActivity에서 데이터 추출
        val intent = intent
        val jobPosting = intent.getParcelableExtra<JobPosting>("jobPosting")
        val companyName = intent.getStringExtra("companyName")

        // companyName을 사용하여 필요한 작업을 수행

        // XML 레이아웃의 TextView 등을 찾아서 데이터 표시
        val company = findViewById<TextView>(R.id.company)
        val jobTitleTextView = findViewById<TextView>(R.id.tvJobTitle)
        val experienceRequiredTextView = findViewById<TextView>(R.id.job_experience_required)
        val educationRequiredTextView = findViewById<TextView>(R.id.job_education_required)
        val periodTextView = findViewById<TextView>(R.id.job_period)
        val daysOfWeekTextView = findViewById<TextView>(R.id.job_days_of_week)
        val workingHoursTextView = findViewById<TextView>(R.id.job_working_hours)
        val salaryTextView = findViewById<TextView>(R.id.job_salary)
        val positionTextView = findViewById<TextView>(R.id.job_position)
        val categoryTextView = findViewById<TextView>(R.id.job_category) // 직군
        val requirementsTextView = findViewById<TextView>(R.id.job_requirements)
        val contactNumberTextView = findViewById<TextView>(R.id.job_contact_number)
        val emailTextView = findViewById<TextView>(R.id.job_email)
        val deadlineTextView = findViewById<TextView>(R.id.job_deadline)

        // 가져온 데이터를 레이아웃에 표시
        company.text = companyName
        jobTitleTextView.text = jobPosting?.job_title
        experienceRequiredTextView.text = jobPosting?.job_experience_required
        educationRequiredTextView.text = jobPosting?.job_education_required
        periodTextView.text = jobPosting?.job_period
        daysOfWeekTextView.text = jobPosting?.job_days_of_week
        workingHoursTextView.text = jobPosting?.job_working_hours
        salaryTextView.text = jobPosting?.job_salary
        positionTextView.text = jobPosting?.job_position
        categoryTextView.text = jobPosting?.job_category
        requirementsTextView.text = jobPosting?.job_requirements
        contactNumberTextView.text = jobPosting?.job_contact_number
        emailTextView.text = jobPosting?.job_email
        deadlineTextView.text = jobPosting?.job_deadline
    }
}