package com.example.a23_hf069

import JobPosting
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResumeClickActivity : AppCompatActivity() {
    private var userId: String = "" // User ID
    private var resumeListNum: Int = -1
    private lateinit var editResumeTitle: TextView
    private lateinit var editTextAcademic: TextView
    private lateinit var editTextCareer: TextView
    private lateinit var editTextIntroduction: TextView
    private lateinit var editTextCertificate: TextView
    private lateinit var editTextEducation: TextView
    private lateinit var editTextDesire: TextView

    private lateinit var backButton_click: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_resume_click)

        // Get user ID
        resumeListNum = intent.getIntExtra("resumeListNum", -1)
//        userId = intent.getStringExtra("userId") ?: ""

        val textID = findViewById<TextView>(R.id.tvClick_ID)
        textID.text = userId

        editTextAcademic = findViewById(R.id.edtClick_academic)
        editResumeTitle = findViewById(R.id.edtClick_title)
        editTextCareer = findViewById(R.id.edtClick_career)
        editTextIntroduction = findViewById(R.id.edtClick_introduction)
        editTextCertificate = findViewById(R.id.edtClick_certificate)
        editTextEducation = findViewById(R.id.edtClick_education)
        editTextDesire = findViewById(R.id.edtClick_desire)

        backButton_click = findViewById(R.id.backButton_click)

        // 이력서 아이템 데이터 불러오기
        getResumeItemData(resumeListNum)

        backButton_click.setOnClickListener {
            onBackPressed()
        }
    }

    // 이력서 아이템 데이터 불러오기
    private fun getResumeItemData(resumeListNum: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(RetrofitInterface.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(RetrofitInterface::class.java)

        apiService.getResumeData(
            null,
            resumeListNum,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
            .enqueue(object : Callback<List<ResumeModel>> {
                override fun onResponse(
                    call: Call<List<ResumeModel>>,
                    response: Response<List<ResumeModel>>
                ) {
                    if (response.isSuccessful) {
                        val resumeData = response.body()
                        handleResumeItemData(resumeData)
                        userId = resumeData?.getOrNull(0)?.personal_id ?: ""
                        // personal_id를 userId에 설정합니다.
                    }
                }

                override fun onFailure(call: Call<List<ResumeModel>>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(
                        this@ResumeClickActivity,
                        "통신 오류: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // 이력서 아이템 데이터를 처리하는 함수 추가
    private fun handleResumeItemData(resumeData: List<ResumeModel>?) {
        resumeData?.let { dataList ->
            val targetResume =
                dataList.find { it.resume_listnum == this.resumeListNum }

            if (targetResume != null) {
                    userId = targetResume.personal_id
                    editResumeTitle.text = targetResume.resume_title
                    editTextAcademic.text = targetResume.resume_academic
                    editTextCareer.text = targetResume.resume_career
                    editTextIntroduction.text = targetResume.resume_introduction
                    editTextCertificate.text = targetResume.resume_certificate
                    editTextEducation.text = targetResume.resume_learning
                    editTextDesire.text = targetResume.resume_desire
            }
        } ?: run {
            // Optional: Handle the case where resumeData is null or empty.
            Toast.makeText(this, "이력서 데이터가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}


//class ResumeClickActivity : AppCompatActivity() {
//    private var IP_ADDRESS = "54.180.186.168" // Replace with your IP address.
//    private var userId: String = "" // User ID
//    private var resumeListNum: Int = -1
//    private lateinit var editResumeTitle: TextView
//    private lateinit var editTextAcademic: TextView
//    private lateinit var editTextCareer: TextView
//    private lateinit var editTextIntroduction: TextView
//    private lateinit var editTextCertificate: TextView
//    private lateinit var editTextEducation: TextView
//    private lateinit var editTextDesire: TextView
//
//    private lateinit var backButton_click: ImageButton
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        supportActionBar?.hide()
//        setContentView(R.layout.activity_resume_click)
//
//        // Get user ID
//        resumeListNum = intent.getIntExtra("resumeListNum", -1)
//        userId = intent.getStringExtra("userId") ?: ""
//
//        val textID = findViewById<TextView>(R.id.tvClick_ID)
//        textID.text = userId
//
//        editTextAcademic = findViewById(R.id.edtClick_academic)
//        editResumeTitle = findViewById(R.id.edtClick_title)
//        editTextCareer = findViewById(R.id.edtClick_career)
//        editTextIntroduction = findViewById(R.id.edtClick_introduction)
//        editTextCertificate = findViewById(R.id.edtClick_certificate)
//        editTextEducation = findViewById(R.id.edtClick_education)
//        editTextDesire = findViewById(R.id.edtClick_desire)
//
//        backButton_click = findViewById(R.id.backButton_click)
//
//        // 이력서 아이템 데이터 불러오기
//        getResumeItemData(resumeListNum)
//
//        backButton_click.setOnClickListener {
//            onBackPressed()
//        }
//    }
//
//    // 이력서 아이템 데이터 불러오기
//    private fun getResumeItemData(resumeListNum: Int) {
//        val url =
//            "http://$IP_ADDRESS/android_resume_change.php?resume_listnum=$resumeListNum" // 데이터를 불러올 PHP 스크립트의 주소
//
//        val client = OkHttpClient()
//
//        val request = Request.Builder()
//            .url(url)
//            .get() // GET 방식으로 요청 변경
//            .build()
//
//        client.newCall(request).enqueue(object : okhttp3.Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                // Handle request failure
//                e.printStackTrace()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                val responseData = response.body?.string()
//                // 이력서 아이템 데이터를 파싱하여 UI 업데이트
//                runOnUiThread {
//                    handleResumeItemData(responseData)
//                }
//            }
//        })
//    }
//
//    // 이력서 아이템 데이터를 처리하는 함수 추가
//    private fun handleResumeItemData(responseData: String?) {
//        try {
//            val jsonObject = JSONObject(responseData) // JSONObject로 파싱
//
//            // 이력서 아이템 데이터가 존재하는 경우에만 UI 업데이트
//            if (jsonObject.length() > 0) {
//                // 여기서 이력서 아이템 데이터를 파싱하여 UI에 표시하는 작업을 수행하면 됩니다.
//                // 예를 들어, 다음과 같이 각 EditText에 데이터를 설정할 수 있습니다.
//                editResumeTitle.setText(jsonObject.optString("resumeTitle", ""))
//                editTextAcademic.setText(jsonObject.optString("resumeAcademic", ""))
//                editTextCareer.setText(jsonObject.optString("resumeCareer", ""))
//                editTextIntroduction.setText(jsonObject.optString("resumeIntroduction", ""))
//                editTextCertificate.setText(jsonObject.optString("resumeCertificate", ""))
//                editTextEducation.setText(jsonObject.optString("resumeLearning", ""))
//                editTextDesire.setText(jsonObject.optString("resumeDesire", ""))
//            }
//        } catch (e: JSONException) {
//            // JSON 파싱 오류 처리
//            e.printStackTrace()
//        }
//    }
//}

