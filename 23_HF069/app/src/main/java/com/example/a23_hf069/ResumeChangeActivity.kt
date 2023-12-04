package com.example.a23_hf069

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ResumeChangeActivity : AppCompatActivity() {
    private var IP_ADDRESS = "54.180.186.168" // Replace with your IP address.
    private var userId: String = "" // User ID
    private var resumeListNum: Int = -1
    private lateinit var editResumeTitle: EditText
    private lateinit var editTextAcademic: EditText
    private lateinit var editTextCareer: EditText
    private lateinit var editTextIntroduction: EditText
    private lateinit var editTextCertificate: EditText
    private lateinit var editTextEducation: EditText
    private lateinit var editTextDesire: EditText
    private lateinit var buttonSubmit1: Button
    private lateinit var buttonSubmit2: Button

    private lateinit var backButton_change: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_resume_change)

        // Get user ID
        resumeListNum = intent.getIntExtra("resumeListNum", -1)
        userId = intent.getStringExtra("userId") ?: ""

        val textID = findViewById<TextView>(R.id.tvChange_ID)
        textID.text = userId

        editTextAcademic = findViewById(R.id.edtChange_academic)
        editResumeTitle = findViewById(R.id.edtChange_title)
        editTextCareer = findViewById(R.id.edtChange_career)
        editTextIntroduction = findViewById(R.id.edtChange_introduction)
        editTextCertificate = findViewById(R.id.edtChange_certificate)
        editTextEducation = findViewById(R.id.edtChange_education)
        editTextDesire = findViewById(R.id.edtChange_desire)
        buttonSubmit1 = findViewById(R.id.buttonSubmit_temporary_change)
        buttonSubmit2 = findViewById(R.id.buttonSubmit_complete_change)

        backButton_change = findViewById(R.id.backButton_change)

        backButton_change.setOnClickListener {
            onBackPressed()
        }

        buttonSubmit1.setOnClickListener { // 임시 저장
            val resume_listnum = resumeListNum.toString()
            val resume_title = editResumeTitle.text.toString()
            val resume_academic = editTextAcademic.text.toString()
            val resume_career = editTextCareer.text.toString()
            val resume_introduction = editTextIntroduction.text.toString()
            val resume_certificate = editTextCertificate.text.toString()
            val resume_learning = editTextEducation.text.toString()
            val resume_desire = editTextDesire.text.toString()
            val resume_complete = "작성 중"

            updateResumeData(
                resume_listnum,
                resume_title,
                resume_academic,
                resume_career,
                resume_introduction,
                resume_certificate,
                resume_learning,
                resume_desire,
                resume_complete
            )

            Toast.makeText(this, "이력서가 임시저장되었습니다", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }

        buttonSubmit2.setOnClickListener { // 작성완료
            val resume_listnum = resumeListNum.toString()
            val resume_title = editResumeTitle.text.toString()
            val resume_academic = editTextAcademic.text.toString()
            val resume_career = editTextCareer.text.toString()
            val resume_introduction = editTextIntroduction.text.toString()
            val resume_certificate = editTextCertificate.text.toString()
            val resume_learning = editTextEducation.text.toString()
            val resume_desire = editTextDesire.text.toString()
            val resume_complete = "작성 완료"

            updateResumeData(
                resume_listnum,
                resume_title,
                resume_academic,
                resume_career,
                resume_introduction,
                resume_certificate,
                resume_learning,
                resume_desire,
                resume_complete
            )

            Toast.makeText(this, "이력서가 작성완료되었습니다", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }

        // 이력서 아이템 데이터 불러오기
        getResumeItemData(resumeListNum)
    }

    private fun updateResumeData(
        resume_listnum: String,
        resume_title: String,
        resume_academic: String,
        resume_career: String,
        resume_introduction: String,
        resume_certificate: String,
        resume_learning: String,
        resume_desire: String,
        resume_complete: String
    ) {
        val url =
            "http://$IP_ADDRESS/android_resume_update.php" // URL of the hosting server with PHP script

        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("resume_listnum", resume_listnum) // ListNum
            .add("resume_title", resume_title) // Title
            .add("resume_academic", resume_academic) // Education
            .add("resume_career", resume_career) // Career
            .add("resume_introduction", resume_introduction) // Introduction
            .add("resume_certificate", resume_certificate) // Certification
            .add("resume_learning", resume_learning) // Education history
            .add("resume_desire", resume_desire) // Desired job position
            .add("resume_complete", resume_complete)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle request failure
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle request success
                val responseData = response.body?.string()

                // UI 업데이트를 위한 runOnUiThread 호출
                runOnUiThread {
                    Toast.makeText(this@ResumeChangeActivity, responseData, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // 이력서 아이템 데이터 불러오기
    private fun getResumeItemData(resumeListNum: Int) {
        val url =
            "http://$IP_ADDRESS/android_resume_change.php?resume_listnum=$resumeListNum" // 데이터를 불러올 PHP 스크립트의 주소

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .get() // GET 방식으로 요청 변경
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle request failure
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                // 이력서 아이템 데이터를 파싱하여 UI 업데이트
                runOnUiThread {
                    handleResumeItemData(responseData)
                }
            }
        })
    }

    // 이력서 아이템 데이터를 처리하는 함수 추가
    private fun handleResumeItemData(responseData: String?) {
        try {
            val jsonObject = JSONObject(responseData) // JSONObject로 파싱

            // 이력서 아이템 데이터가 존재하는 경우에만 UI 업데이트
            if (jsonObject.length() > 0) {
                // 여기서 이력서 아이템 데이터를 파싱하여 UI에 표시하는 작업을 수행하면 됩니다.
                // 예를 들어, 다음과 같이 각 EditText에 데이터를 설정할 수 있습니다.
                editResumeTitle.setText(jsonObject.optString("resumeTitle", ""))
                editTextAcademic.setText(jsonObject.optString("resumeAcademic", ""))
                editTextCareer.setText(jsonObject.optString("resumeCareer", ""))
                editTextIntroduction.setText(jsonObject.optString("resumeIntroduction", ""))
                editTextCertificate.setText(jsonObject.optString("resumeCertificate", ""))
                editTextEducation.setText(jsonObject.optString("resumeLearning", ""))
                editTextDesire.setText(jsonObject.optString("resumeDesire", ""))
            }
        } catch (e: JSONException) {
            // JSON 파싱 오류 처리
            e.printStackTrace()
        }
    }
}
