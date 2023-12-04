package com.example.a23_hf069

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MemberInfoEditActivity : AppCompatActivity() {
    private var IP_ADDRESS = "54.180.186.168" // Replace with your IP address.
    private var userId: String = "" // User ID

    private lateinit var edtID: TextView
    private lateinit var edtName: TextView
    private lateinit var edtBirth: EditText
    private lateinit var edtEmail: EditText
    private lateinit var btnVerify: Button
    private lateinit var edtVerification: EditText
    private lateinit var btnOk: Button
    private lateinit var edtPhone: EditText
    private lateinit var edtAddress: EditText
    private lateinit var edtAddressDetail: EditText
    private lateinit var btnSubmit: Button

    private lateinit var backButton_edit: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_member_info_edit)

        edtID = findViewById(R.id.edtEdit_memberinfo_id)
        edtName = findViewById(R.id.edtEdit_memberinfo_name)
        edtBirth = findViewById(R.id.edtEdit_memberinfo_birth)
        edtEmail = findViewById(R.id.edtEdit_memberinfo_email)
        btnVerify = findViewById(R.id.btnEdit_memberinfo_verify)
        edtVerification = findViewById(R.id.edtEdit_memberinfo_verification)
        btnOk = findViewById(R.id.btnEdit_memberinfo_ok)
        edtPhone = findViewById(R.id.edtEdit_memberinfo_phone)
        edtAddress = findViewById(R.id.edtEdit_memberinfo_address_serach)
        edtAddressDetail = findViewById(R.id.edtEdit_memberinfo_address_detail)
        btnSubmit = findViewById(R.id.buttonSubmit_complete_edit)

        backButton_edit = findViewById(R.id.backButton)

        backButton_edit.setOnClickListener {
            onBackPressed()
        }

        userId = intent.getStringExtra("userId") ?: ""
        edtID.text = userId

        btnSubmit.setOnClickListener {
            val personal_id = edtID.text.toString()
            val personal_birth = edtBirth.text.toString()
            val personal_email = edtEmail.text.toString()
            val personal_phonenum = edtPhone.text.toString()
            val personal_address = edtAddress.text.toString()
            val personal_address_detail = edtAddressDetail.text.toString()

            if (personal_birth.isNotBlank() &&
                personal_email.isNotBlank() &&
                personal_phonenum.isNotBlank() &&
                personal_address.isNotBlank() &&
                personal_address_detail.isNotBlank()
            ){
                updatePersonalMemberinfoData(
                    personal_id,
                    personal_birth,
                    personal_email,
                    personal_phonenum,
                    personal_address,
                    personal_address_detail
                )
                onBackPressed()
            }
            else {
                Toast.makeText(this, "모든 항목을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        // 기존 저장 회원정보 불러오기
        getPersonalMemberinfoData(userId)
    }

    private fun updatePersonalMemberinfoData(
        personal_id: String,
        personal_birth: String,
        personal_email: String,
        personal_phonenum: String,
        personal_address: String,
        personal_address_detail: String
    ) {
        val url =
            "http://$IP_ADDRESS/android_edit_memberinfo_save.php" // URL of the hosting server with PHP script

        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("personal_id", personal_id) // Id
            .add("personal_birth", personal_birth) // Birthday
            .add("personal_email", personal_email) // Email
            .add("personal_phonenum", personal_phonenum) // Phone number
            .add("personal_address", personal_address) // Address
            .add("personal_address_detail", personal_address_detail) // Detail Address
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
                    Toast.makeText(this@MemberInfoEditActivity, responseData, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // 기존에 저장되어 있던 회원정보 불러오기
    private fun getPersonalMemberinfoData(userId: String) {
        val url =
            "http://$IP_ADDRESS/android_edit_memberinfo_call.php?personal_id=$userId" // 데이터를 불러올 PHP 스크립트의 주소

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle request failure
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                // 개인회원 정보 데이터를 파싱하여 UI 업데이트
                runOnUiThread {
                    handlePersonalMemberinfoData(responseData)
                }
            }
        })
    }

    // 이력서 아이템 데이터를 처리하는 함수 추가
    private fun handlePersonalMemberinfoData(responseData: String?) {
        try {
            val jsonObject = JSONObject(responseData) // JSONObject로 파싱

            // 이력서 아이템 데이터가 존재하는 경우에만 UI 업데이트
            if (jsonObject.length() > 0) {
                // 여기서 이력서 아이템 데이터를 파싱하여 UI에 표시하는 작업을 수행하면 됩니다.
                // 예를 들어, 다음과 같이 각 EditText에 데이터를 설정할 수 있습니다.
                edtName.setText(jsonObject.optString("personal_name", ""))
                edtBirth.setText(jsonObject.optString("personal_birth", ""))
                edtEmail.setText(jsonObject.optString("personal_email", ""))
                edtPhone.setText(jsonObject.optString("personal_phonenum", ""))
                edtAddress.setText(jsonObject.optString("personal_address", ""))
                edtAddressDetail.setText(jsonObject.optString("personal_address_detail", ""))
            }
        } catch (e: JSONException) {
            // JSON 파싱 오류 처리
            e.printStackTrace()
        }
    }
}