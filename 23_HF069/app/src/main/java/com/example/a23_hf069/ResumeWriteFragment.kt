package com.example.a23_hf069

import JobPosting
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import okhttp3.*

class ResumeWriteFragment : Fragment() {
    private var userId: String = "" // User ID
    private lateinit var backButton: ImageButton
    private lateinit var viewUserId : EditText
    private lateinit var editResumeTitle: EditText
    private lateinit var editTextAcademic: EditText
    private lateinit var editTextCareer: EditText
    private lateinit var editTextIntroduction: EditText
    private lateinit var editTextCertificate: EditText
    private lateinit var editTextEducation: EditText
    private lateinit var editTextDesire: EditText
    private lateinit var buttonSubmit_temporary: Button
    private lateinit var buttonSubmit_complete: Button
    private lateinit var educationPlus: ImageButton
    private lateinit var careerPlus: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_resume_write, container, false)
// Get user ID
//        userId = intent.getStringExtra("userId") ?: ""

//        backButton = findViewById(R.id.backButton)
//        editTextAcademic = findViewById(R.id.edtWrite_academic)

        editResumeTitle = view.findViewById(R.id.edtWrite_title)
        editTextAcademic = view.findViewById(R.id.edtWrite_academic)
        editTextCareer = view.findViewById(R.id.edtWrite_career)
        editTextIntroduction = view.findViewById(R.id.edtWrite_introduction)
        editTextCertificate = view.findViewById(R.id.edtWrite_certificate)
        editTextEducation = view.findViewById(R.id.edtWrite_education)
        editTextDesire = view.findViewById(R.id.edtWrite_desire)
        buttonSubmit_temporary = view.findViewById(R.id.buttonSubmit_temporary_edit) // 임시 저장 버튼
        buttonSubmit_complete = view.findViewById(R.id.buttonSubmit_complete_edit) // 작성 완료 버튼
//        educationPlus = findViewById(R.id.education_plus)
//        careerPlus = findViewById(R.id.career_plus)

//        educationPlus = view.findViewById(R.id.education_plus)
//        careerPlus = view.findViewById(R.id.career_plus)
//
//        educationPlus.setOnClickListener {
//            val intent = Intent(requireContext(), ResumeEducationActivity::class.java)
//            startActivity(intent)
//        }
//
//        careerPlus.setOnClickListener {
//            val intent = Intent(requireContext(), ResumeCareerActivity::class.java)
//            startActivity(intent)
//        }

//        backButton.setOnClickListener {
//            onBackPressed()
//        }

        buttonSubmit_temporary.setOnClickListener { // 임시 저장
//            val personal_id = userId
//            val resume_title = editResumeTitle.text.toString()
//            val resume_academic = editTextAcademic.text.toString()
//            val resume_career = editTextCareer.text.toString()
//            val resume_introduction = editTextIntroduction.text.toString()
//            val resume_certificate = editTextCertificate.text.toString()
//            val resume_learning = editTextEducation.text.toString()
//            val resume_desire = editTextDesire.text.toString()
//            val resume_complete = "작성 중"
//
//            sendResumeData(
//                personal_id,
//                resume_title,
//                resume_academic,
//                resume_career,
//                resume_introduction,
//                resume_certificate,
//                resume_learning,
//                resume_desire,
//                resume_complete
//            )
//
//            Toast.makeText(this, "이력서가 임시저장되었습니다", Toast.LENGTH_SHORT).show()

//            onBackPressed()
        }
//
        buttonSubmit_complete.setOnClickListener { // 작성완료
//            val personal_id = userId
//            val resume_title = editResumeTitle.text.toString()
//            val resume_academic = editTextAcademic.text.toString()
//            val resume_career = editTextCareer.text.toString()
//            val resume_introduction = editTextIntroduction.text.toString()
//            val resume_certificate = editTextCertificate.text.toString()
//            val resume_learning = editTextEducation.text.toString()
//            val resume_desire = editTextDesire.text.toString()
//            val resume_complete = "작성 완료"
//
//            sendResumeData(
//                personal_id,
//                resume_title,
//                resume_academic,
//                resume_career,
//                resume_introduction,
//                resume_certificate,
//                resume_learning,
//                resume_desire,
//                resume_complete
//            )
//
//            Toast.makeText(this, "이력서가 작성완료되었습니다", Toast.LENGTH_SHORT).show()
//
//            onBackPressed()
        }
        return view
    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//    }
//
//
//    private fun sendResumeData(
//        personal_id: String,
//        resume_title: String,
//        resume_academic: String,
//        resume_career: String,
//        resume_introduction: String,
//        resume_certificate: String,
//        resume_learning: String,
//        resume_desire: String,
//        resume_complete: String
//    ) {
//        val url = "http://$IP_ADDRESS/android_resume_write_php.php" // URL of the hosting server with PHP script
//
//        val client = OkHttpClient()
//
//        val formBody = FormBody.Builder()
//            .add("personal_id", personal_id) // ID
//            .add("resume_title", resume_title) // Title
//            .add("resume_academic", resume_academic) // Education
//            .add("resume_career", resume_career) // Career
//            .add("resume_introduction", resume_introduction) // Introduction
//            .add("resume_certificate", resume_certificate) // Certification
//            .add("resume_learning", resume_learning) // Education history
//            .add("resume_desire", resume_desire) // Desired job position
//            .add("resume_complete", resume_complete)
//            .build()
//
//        val request = Request.Builder()
//            .url(url)
//            .post(formBody)
//            .build()
//
//        client.newCall(request).enqueue(object : okhttp3.Callback {
//            override fun onFailure(call: Call, e: IOException) {
//// Handle request failure
//                e.printStackTrace()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//// Handle request success
//                val responseData = response.body?.string()
//            }
//        })
//    }
}