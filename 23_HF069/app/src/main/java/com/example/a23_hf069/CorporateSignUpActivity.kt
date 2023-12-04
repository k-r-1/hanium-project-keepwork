//package com.example.a23_hf069
//
//import android.app.DatePickerDialog
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.AdapterView
//import androidx.appcompat.app.ActionBar
//import android.app.ProgressDialog
//import android.content.ContentValues.TAG
//import android.content.Intent
//import android.os.AsyncTask
//import android.text.method.ScrollingMovementMethod
//import android.util.Log
//import android.view.View
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
//import android.widget.Spinner
//import android.widget.TextView
//import android.widget.Toast
//import okhttp3.FormBody
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import java.io.BufferedReader
//import java.io.InputStream
//import java.io.InputStreamReader
//import java.net.HttpURLConnection
//import java.net.URL
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import com.amazonaws.auth.BasicAWSCredentials
//import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient
//import com.amazonaws.services.simpleemail.model.*
//import java.util.UUID
//import androidx.lifecycle.ViewModel
//import com.example.a23_hf069.databinding.ActivityCorporateSignUpBinding
//import com.example.a23_hf069.databinding.ActivityResumeEducationBinding
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.util.Calendar
//
//
//class CorporateSignUpActivity : AppCompatActivity() {
//
//    private var checkID = false
//
//    // 뷰 요소들을 선언
//    private lateinit var backButton: ImageButton // go back to prev page
//    private lateinit var id_text_input_edit_text: EditText // id
//    private lateinit var idcheck_button: Button // id duplicate check
//    private lateinit var password_text_input_edit_text: EditText // password
//    private lateinit var password_recheck_text_input_edit_text: EditText // password recheck
//    private lateinit var name_textview_input_edit_text: EditText // name
//    private lateinit var email_textview_input_edit_text: EditText // email
//    private lateinit var phoneNumber_textview_input_edit_text: EditText // phone number
//    private lateinit var registrationNum_textview_input_edit_text: EditText // address
//    private lateinit var companyName_textview_input_layout: EditText // company name
//    private lateinit var ceoName_textview_input_edit_text: EditText
//    private lateinit var year_textview_input_edit_text: EditText
//    private lateinit var worker_textview_input_edit_text: EditText
//    private lateinit var companyAddress_textview_input_edit_text: EditText
//    private lateinit var signUp_button: Button // sign up button
//    lateinit var companySpinner: Spinner
//    lateinit var binding : ActivityCorporateSignUpBinding
//    lateinit var company_type: String
//
//    // TextView 요소인 mTextViewResult 선언
//    private lateinit var mTextViewResult: TextView
//
//    private val retrofit: Retrofit = Retrofit.Builder()
//        .baseUrl(RetrofitInterface.API_URL)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    private val apiService: RetrofitInterface= retrofit.create(RetrofitInterface::class.java)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityCorporateSignUpBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        companySpinner = findViewById(R.id.company_spinner)
//
//        val companyList = listOf(
//            "대기업",
//            "중견기업",
//            "중소기업",
//            "스타트업",
//            "공기업",
//            "개인사업자",
//        )
//
//        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, companyList)
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.companySpinner.adapter = adapter1
//
//        binding.companySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                // 이곳에 선택된 아이템을 처리하는 코드를 추가하세요.
//                company_type = binding.companySpinner.selectedItem.toString()
//                // 선택된 아이템에 대한 처리를 여기에 추가하세요.
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                // 아무것도 선택되지 않았을 때 처리할 코드를 추가하세요.
//            }
//        }
//
//        // 기본 툴바 숨기기
//        val actionBar: ActionBar? = supportActionBar
//        if (actionBar != null) {
//            actionBar.hide()
//        }
//
//        // 각 뷰의 요소들과 레이아웃 파일에서의 아이디 연결
//        backButton = findViewById(R.id.backButton)  // go back to prev page
//        id_text_input_edit_text = findViewById(R.id.id_text_input_edit_text) // company id
//        idcheck_button = findViewById(R.id.idCheck_button) // id duplicate check
//        password_text_input_edit_text = findViewById(R.id.password_text_input_edit_text) // company password
//        password_recheck_text_input_edit_text = findViewById(R.id.password_recheck_text_input_edit_text) // password recheck
//        name_textview_input_edit_text = findViewById(R.id.name_textview_input_edit_text) // company name
//        email_textview_input_edit_text = findViewById(R.id.email_textview_input_edit_text) // company email
//        phoneNumber_textview_input_edit_text = findViewById(R.id.phoneNumber_textview_input_edit_text) // personal phone_num
//        registrationNum_textview_input_edit_text = findViewById(R.id.registrationNum_textview_input_edit_text) // registration_num
//        companyName_textview_input_layout = findViewById(R.id.companyName_textview_input_edit_text) // company name
//        ceoName_textview_input_edit_text = findViewById(R.id.ceoName_textview_input_edit_text) // ceo name
//        year_textview_input_edit_text = findViewById(R.id.year_textview_input_edit_text) // year
//        worker_textview_input_edit_text = findViewById(R.id.worker_textview_input_edit_text) // worker
//        companyAddress_textview_input_edit_text = findViewById(R.id.companyAddress_textview_input_edit_text) // company address
//        signUp_button = findViewById(R.id.signUp_button)
//
//        // mTextViewResult를 스크롤 가능하도록 설정
//        mTextViewResult = findViewById(R.id.textView_main_result)
//        mTextViewResult.movementMethod = ScrollingMovementMethod()
//
//        // 클릭 시 현재 액티비티 종료
//        backButton.setOnClickListener {
//            finish()
//        }
//
//        // 버튼 클릭 시 아이디 중복 확인 과정 수행
//        idcheck_button.setOnClickListener {
//            val id = id_text_input_edit_text.text.toString().trim()
//
//            // 아이디가 비어있는지 확인
//            if (id.isEmpty()) {
//                Toast.makeText(this@CorporateSignUpActivity, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
//            } else {
//                apiService.getCorporateData(id).enqueue(object : Callback<List<C_MemberModel>> {
//                    override fun onResponse(call: Call<List<C_MemberModel>>, response: Response<List<C_MemberModel>>) {
//                        if (response.isSuccessful) {
//                            val result = response.body()
//                            if (result != null && result.isNotEmpty()) {
//                                var cnt = 0
//                                for (data in result) {
//                                    if (data.company_id == id) {
//                                        // 아이디 중복
//                                        cnt = 1
//                                        id_text_input_edit_text.setText("")
//                                        Toast.makeText(this@CorporateSignUpActivity, "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                                if (cnt == 0) {
//                                    Toast.makeText(this@CorporateSignUpActivity, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
//                                    checkID = true
//                                }
//                            }
//                        } else {
//                            Toast.makeText(this@CorporateSignUpActivity, "서버 응답 오류", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<List<C_MemberModel>>, t: Throwable) {
//                        // 네트워크 오류 처리
//                        Toast.makeText(this@CorporateSignUpActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
//                    }
//                })
//            }
//        }
//
//        // 버튼 클릭 시 회원가입 과정 수행
//        signUp_button.setOnClickListener {
//            val id = id_text_input_edit_text.text.toString().trim()
//            val password = password_text_input_edit_text.text.toString().trim()
//            val password_recheck = password_recheck_text_input_edit_text.text.toString().trim()
//            val name = name_textview_input_edit_text.text.toString().trim()
//            val email = email_textview_input_edit_text.text.toString().trim()
//            val phonenum = phoneNumber_textview_input_edit_text.text.toString().trim()
//            val registnum = registrationNum_textview_input_edit_text.text.toString().trim()
//            val company_name = companyName_textview_input_layout.text.toString().trim()
//            val ceo_name = ceoName_textview_input_edit_text.text.toString().trim()
//            val company_address = companyAddress_textview_input_edit_text.text.toString().trim()
//            val establishment = year_textview_input_edit_text.text.toString().trim()
//            val employees = worker_textview_input_edit_text.text.toString().trim()
//
//            if (id.isEmpty() || password.isEmpty() || password_recheck.isEmpty() || name.isEmpty() || email.isEmpty() || phonenum.isEmpty() || registnum.isEmpty() || company_name.isEmpty() || ceo_name.isEmpty() || company_address.isEmpty() || establishment.isEmpty() || employees.isEmpty()) {
//                Toast.makeText(this@CorporateSignUpActivity, "정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
//            } else {
//                if (password == password_recheck) {
//                    if (password.length <= 5) {
//                        Toast.makeText(this@CorporateSignUpActivity, "비밀번호를 6자리 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
//                    } else if (!email.contains("@")) {
//                        Toast.makeText(this@CorporateSignUpActivity, "이메일에 @를 포함시키세요.", Toast.LENGTH_SHORT).show()
//                    } else if (phonenum.contains("-")) {
//                        Toast.makeText(this@CorporateSignUpActivity, "올바른 전화번호 형식으로 입력해주세요.", Toast.LENGTH_SHORT).show()
//                    } else if (checkID == false) {
//                        Toast.makeText(this@CorporateSignUpActivity, "아이디를 중복확인을 진행하세요.", Toast.LENGTH_SHORT).show()
//                    } else {
//                        // 사용자 데이터를 포함하는 C_MemberModel 객체를 생성합니다.
//                        val memberModel = C_MemberModel(id, password, name, email, phonenum, registnum, company_name, ceo_name, company_address, establishment, employees, company_type)
//
//                        // Retrofit을 사용하여 서버에 사용자 데이터를 보냅니다.
//                        apiService.postCorporateData(memberModel).enqueue(object : Callback<C_MemberModel> {
//                            override fun onResponse(call: Call<C_MemberModel>, response: Response<C_MemberModel>) {
//                                if (response.message().equals("Created")) {
//                                    Toast.makeText(this@CorporateSignUpActivity, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show()
//                                    checkID = false
//                                    finish()
//                                } else {
//                                    Toast.makeText(this@CorporateSignUpActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//
//                            override fun onFailure(call: Call<C_MemberModel>, t: Throwable) {
//                                // 네트워크 오류 처리
//                                Toast.makeText(this@CorporateSignUpActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
//                            }
//                        })
//                    }
//                } else {
//                    Toast.makeText(this@CorporateSignUpActivity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//}
