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
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
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
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.util.Calendar
//
//
//class PersonalSignUpActivity : AppCompatActivity() {
//
//    private var checkID = false
//    private var calendar = Calendar.getInstance()
//    private var birthYear = calendar.get(Calendar.YEAR)
//    private var birthMonth = calendar.get(Calendar.MONTH)
//    private var birthDay = calendar.get(Calendar.DAY_OF_MONTH)
//    lateinit var btnCalendar : ImageButton
//    lateinit var edtBirthYear : EditText
//    lateinit var edtBirthMonth : EditText
//    lateinit var edtBirthDay : EditText
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
//    private lateinit var address_textview_input_edit_text: EditText // address
//    private lateinit var signUp_button: Button // sign up button
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
//        setContentView(R.layout.activity_individual_sign_up)
//
//        // 기본 툴바 숨기기
//        val actionBar: ActionBar? = supportActionBar
//        if (actionBar != null) {
//            actionBar.hide()
//        }
//
//        // 각 뷰의 요소들과 레이아웃 파일에서의 아이디 연결
//        backButton = findViewById(R.id.backButton)  // go back to prev page
//        id_text_input_edit_text = findViewById(R.id.id_text_input_edit_text) // personal id
//        idcheck_button = findViewById(R.id.idCheck_button) // id duplicate check
//        password_text_input_edit_text = findViewById(R.id.password_text_input_edit_text) // personal password
//        password_recheck_text_input_edit_text = findViewById(R.id.password_recheck_text_input_edit_text) // password recheck
//        name_textview_input_edit_text = findViewById(R.id.name_textview_input_edit_text) // personal name
//        email_textview_input_edit_text = findViewById(R.id.email_textview_input_edit_text) // personal email
//        phoneNumber_textview_input_edit_text = findViewById(R.id.phoneNumber_textview_input_edit_text) // personal phonenum
//        address_textview_input_edit_text = findViewById(R.id.address_textview_input_edit_text)
//
//        signUp_button = findViewById(R.id.signUp_button)
//
//        btnCalendar = findViewById(R.id.btn_calendar)
//        edtBirthYear = findViewById(R.id.edt_birthyear)
//        edtBirthMonth = findViewById(R.id.edt_birthmonth)
//        edtBirthDay = findViewById(R.id.edt_birthday)
//        btnCalendar.setOnClickListener{
//            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
//                edtBirthYear.setText(year.toString())
//                if (month < 9) {
//                    edtBirthMonth.setText("0" + (month + 1).toString())
//                }
//                else {
//                    edtBirthMonth.setText((month + 1).toString())
//                }
//                if (day < 10) {
//                    edtBirthDay.setText("0" + day.toString())
//                }
//                else {
//                    edtBirthDay.setText(day.toString())
//                }
//            }, birthYear, birthMonth, birthDay)
//            datePickerDialog.show()
//        }
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
//                Toast.makeText(this@PersonalSignUpActivity, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
//            } else {
//                apiService.getData(id).enqueue(object : Callback<List<P_MemberModel>> {
//                    override fun onResponse(call: Call<List<P_MemberModel>>, response: Response<List<P_MemberModel>>) {
//                        if (response.isSuccessful) {
//                            val result = response.body()
//                            if (result != null && result.isNotEmpty()) {
//                                var cnt = 0
//                                for (data in result) {
//                                    if (data.personal_id == id) {
//                                        // 아이디 중복
//                                        cnt = 1
//                                        id_text_input_edit_text.setText("")
//                                        Toast.makeText(this@PersonalSignUpActivity, "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                                if (cnt == 0) {
//                                    Toast.makeText(this@PersonalSignUpActivity, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
//                                    checkID = true
//                                }
//                            }
//                        } else {
//                            Toast.makeText(this@PersonalSignUpActivity, "서버 응답 오류", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<List<P_MemberModel>>, t: Throwable) {
//                        // 네트워크 오류 처리
//                        Toast.makeText(this@PersonalSignUpActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
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
//            val birth = edtBirthYear.text.toString().trim() + edtBirthMonth.text.toString().trim() + edtBirthDay.text.toString().trim()
//            val email = email_textview_input_edit_text.text.toString().trim()
//            val phonenum = phoneNumber_textview_input_edit_text.text.toString().trim()
//            val address = address_textview_input_edit_text.text.toString().trim()
//            // val phoneNumberCheck = phoneNumberCheck_textview_input_edit_text.toString().trim()
//
//            if (id.isEmpty() || password.isEmpty() || password_recheck.isEmpty() || name.isEmpty() || birth.isEmpty() || email.isEmpty() || phonenum.isEmpty() || address.isEmpty()) {
//                Toast.makeText(this@PersonalSignUpActivity, "정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
//            } else {
//                if (password == password_recheck) {
//                    if (password.length <= 5) {
//                        Toast.makeText(this@PersonalSignUpActivity, "비밀번호를 6자리 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
//                    } else if (!email.contains("@")) {
//                        Toast.makeText(this@PersonalSignUpActivity, "이메일에 @를 포함시키세요.", Toast.LENGTH_SHORT).show()
//                    } else if (checkID == false) {
//                        Toast.makeText(this@PersonalSignUpActivity, "아이디를 중복확인을 진행하세요.", Toast.LENGTH_SHORT).show()
//                    } else {
//                        // 사용자 데이터를 포함하는 P_MemberModel 객체를 생성합니다.
//                        val memberModel = P_MemberModel(id, password, name, birth, email, phonenum, address)
//
//                        // Retrofit을 사용하여 서버에 사용자 데이터를 보냅니다.
//                        apiService.postData(memberModel).enqueue(object : Callback<P_MemberModel> {
//                            override fun onResponse(call: Call<P_MemberModel>, response: Response<P_MemberModel>) {
//                                if (response.message().equals("Created")) {
//                                    Toast.makeText(this@PersonalSignUpActivity, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show()
//                                    checkID = false
//                                    finish()
//                                } else {
//                                    Toast.makeText(this@PersonalSignUpActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//
//                            override fun onFailure(call: Call<P_MemberModel>, t: Throwable) {
//                                // 네트워크 오류 처리
//                                Toast.makeText(this@PersonalSignUpActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
//                            }
//                        })
//                    }
//                } else {
//                    Toast.makeText(this@PersonalSignUpActivity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//}
//
////package com.example.a23_hf069
////
////import androidx.appcompat.app.AppCompatActivity
////import android.os.Bundle
////import android.widget.AdapterView
////import androidx.appcompat.app.ActionBar
////import android.app.ProgressDialog
////import android.os.AsyncTask
////import android.text.method.ScrollingMovementMethod
////import android.util.Log
////import android.view.View
////import android.widget.Button
////import android.widget.EditText
////import android.widget.ImageButton
////import android.widget.TextView
////import android.widget.Toast
////import okhttp3.FormBody
////import okhttp3.OkHttpClient
////import okhttp3.Request
////import java.io.BufferedReader
////import java.io.InputStream
////import java.io.InputStreamReader
////import java.net.HttpURLConnection
////import java.net.URL
////import retrofit2.Call
////import retrofit2.Callback
////import retrofit2.Response
////import retrofit2.Retrofit
////import retrofit2.converter.gson.GsonConverterFactory
////import com.amazonaws.auth.BasicAWSCredentials
////import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient
////import com.amazonaws.services.simpleemail.model.*
////import java.util.UUID
////import androidx.lifecycle.ViewModel
////import kotlinx.coroutines.Dispatchers
////import kotlinx.coroutines.withContext
////
////
////class PersonalSignUpActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
////
////    // IP 주소와 태그를 초기화
////    private var IP_ADDRESS = "54.180.186.168" // 본인 IP주소를 넣으세요.
////    private var TAG = "phptest" // phptest log 찍으려는 용도
////    private var checkID = false
////
////    // 뷰 요소들을 선언
////    private lateinit var backButton: ImageButton // go back to prev page
////    private lateinit var id_text_input_edit_text: EditText // id
////    private lateinit var idcheck_button: Button // id duplicate check
////    private lateinit var password_text_input_edit_text: EditText // password
////    private lateinit var password_recheck_text_input_edit_text: EditText // password recheck
////    private lateinit var name_textview_input_edit_text: EditText // name
////    private lateinit var email_textview_input_edit_text: EditText // email
////    private lateinit var phoneNumber_textview_input_edit_text: EditText // phone number
////    private lateinit var btnEmailCertify: Button // email certification button
////    private lateinit var signUp_button: Button // sign up button
////
////    // TextView 요소인 mTextViewResult 선언
////    private lateinit var mTextViewResult: TextView
////
////    private lateinit var emailViewModel: EmailViewModel
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_individual_sign_up)
////
////        // 기본 툴바 숨기기
////        val actionBar: ActionBar? = supportActionBar
////        if (actionBar != null) {
////            actionBar.hide()
////        }
////
////        // 각 뷰의 요소들과 레이아웃 파일에서의 아이디 연결
////        backButton = findViewById(R.id.backButton)  // go back to prev page
////        id_text_input_edit_text = findViewById(R.id.id_text_input_edit_text) // personal id
////        idcheck_button = findViewById(R.id.idCheck_button) // id duplicate check
////        password_text_input_edit_text = findViewById(R.id.password_text_input_edit_text) // personal password
////        password_recheck_text_input_edit_text = findViewById(R.id.password_recheck_text_input_edit_text) // password recheck
////        name_textview_input_edit_text = findViewById(R.id.name_textview_input_edit_text) // personal name
////        email_textview_input_edit_text = findViewById(R.id.email_textview_input_edit_text) // personal email
////        phoneNumber_textview_input_edit_text = findViewById(R.id.phoneNumber_textview_input_edit_text) // personal phonenum
////        btnEmailCertify = findViewById(R.id.btnEmailCertify)
////        signUp_button = findViewById(R.id.signUp_button)
////
////        // mTextViewResult를 스크롤 가능하도록 설정
////        mTextViewResult = findViewById(R.id.textView_main_result)
////        mTextViewResult.movementMethod = ScrollingMovementMethod()
////
////        // 클릭 시 현재 액티비티 종료
////        backButton.setOnClickListener {
////            finish()
////        }
////
////        // 버튼 클릭 시 아이디 중복 확인 과정 수행
////        idcheck_button.setOnClickListener {
////            val id = id_text_input_edit_text.text.toString().trim()
////
////            // 아이디가 비어있는지 확인
////            if (id.isEmpty()) {
////                Toast.makeText(this@PersonalSignUpActivity, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
////            } else {
////                // 'CheckIdDuplicate' 클래스의 인스턴스인 'task'를 생성
////                val task = CheckIdDuplicate()
////
////                // 'task'의 'execute' 메서드를 호출해 백그라운드에서 아이디 중복 여부 확인
////                task.execute("http://$IP_ADDRESS/android_id_check.php", id)
////            }
////        }
////
////        // 버튼 클릭시 이메일 인증과정 진행
////        btnEmailCertify.setOnClickListener {
////            val emailAddress = email_textview_input_edit_text.text.toString()
////            if (isValidEmail(emailAddress)) {
////                // Amazon SES 인증 정보 설정
////                val credentials = BasicAWSCredentials("AKIA3TFEOQMXM7LIFFWS", "abiuY2bRi7iBXvuuDLvYkQ4bhMtmAAIMCvQOR35/")
////                val sesClient = AmazonSimpleEmailServiceClient(credentials)
////
////                // 이메일 전송 작업 실행
////                SendEmailTask(sesClient, emailAddress).execute()
////            } else {
////                Toast.makeText(this@PersonalSignUpActivity, "유효한 이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
////            }
////        }
////
////        // 버튼 클릭 시 회원가입 과정 수행
////        signUp_button.setOnClickListener {
////            val id = id_text_input_edit_text.text.toString().trim()
////            val password = password_text_input_edit_text.text.toString().trim()
////            val password_recheck = password_recheck_text_input_edit_text.text.toString().trim()
////            val name = name_textview_input_edit_text.text.toString().trim()
////            val email = email_textview_input_edit_text.text.toString().trim()
////            val phoneNumber = phoneNumber_textview_input_edit_text.text.toString().trim()
////            // val phoneNumberCheck = phoneNumberCheck_textview_input_edit_text.toString().trim()
////
////            if (id.isEmpty() || password.isEmpty() || password_recheck.isEmpty() || name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
////                Toast.makeText(this@PersonalSignUpActivity, "정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
////            } else {
////                if (password == password_recheck) {
////                    if (password.length <= 5) {
////                        Toast.makeText(this@PersonalSignUpActivity, "비밀번호를 6자리 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
////                    } else if (!email.contains("@")) {
////                        Toast.makeText(this@PersonalSignUpActivity, "이메일에 @를 포함시키세요.", Toast.LENGTH_SHORT).show()
////                    } else if (phoneNumber.contains("-") || !(phoneNumber[1] == '1')) {
////                        Toast.makeText(this@PersonalSignUpActivity, "올바른 전화번호 형식으로 입력해주세요.", Toast.LENGTH_SHORT).show()
////                    } else if (checkID == false) {
////                        Toast.makeText(this@PersonalSignUpActivity, "아이디를 중복확인을 진행하세요.", Toast.LENGTH_SHORT).show()
////                    } else {
////                        // 'InsertData' 클래스의 인스턴스인 'task'를 생성
////                        val task = InsertData()
////
////                        // 'task'의 'execute'메서드를 호출해 백그라운드에서 데이터를 삽입
////                        task.execute(
////                            // 'execute' 메서드에 서버 URL과 회원가입에 필요한 개인정보를 전달
////                            "http://$IP_ADDRESS/android_log_insert_php.php",
////                            id,
////                            password,
////                            password_recheck,
////                            name,
////                            email,
////                            phoneNumber
////                        )
////                        Toast.makeText(this@PersonalSignUpActivity, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show()
////                        checkID = false
////                        finish()
////                    }
////                } else {
////                    Toast.makeText(this@PersonalSignUpActivity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
////                }
////            }
////        }
////    }
////
////    // AsyncTask를 상속받고, 서버로 데이터를 전송
////    inner class InsertData : AsyncTask<String, Void, String>() {
////        private var progressDialog: ProgressDialog? = null
////
////        // 백그라운드 작업 실행 전 실행, 프로그레스 다이얼로그 표시
////        override fun onPreExecute() {
////            super.onPreExecute()
////            progressDialog = ProgressDialog.show(
////                this@PersonalSignUpActivity,
////                "Please Wait",
////                null,
////                true,
////                true
////            )
////        }
////
////        // 백그라운드 작업 완료 후 실행, 결과를 처리하고 프로그레스 다이얼로그 종료
////        override fun onPostExecute(result: String) {
////            super.onPostExecute(result)
////            progressDialog?.dismiss()
////            mTextViewResult.text = result
////            Log.d(TAG, "POST response  - $result")
////        }
////
////        // 백그라운드에서 수행될 작업 정의, 서버로 데이터 전송 & 응답을 받아 처리
////        // AsyncTask의 Params 매개변수로 가변 인자를 받아 String을 반환
////        override fun doInBackground(vararg params: String): String {
////
////            // param 배열에서 서버 URL과 각각의 개인정보 추출
////            val serverURL = params[0]
////            val personal_id = params[1]
////            val personal_password = params[2]
////            val personal_password_chk = params[3]
////            val personal_name = params[4]
////            val personal_email = params[5]
////            val personal_phonenum = params[6]
////
////            // POST 요청으로 전송할 파라미터 문자열 구성
////            val postParameters =
////                "personal_id=$personal_id&personal_password=$personal_password&personal_password_chk=$personal_password_chk&personal_name=$personal_name&personal_email=$personal_email&personal_phonenum=$personal_phonenum"
////
////            // 'serverURL'을 기반으로 URL 객체 생성, 'openConnection'메서드를 사용해 HttpURLconnection 객체 얻음
////            try {
////                val url = URL(serverURL)
////                val httpURLConnection = url.openConnection() as HttpURLConnection
////
////                // 연결과 읽기 타임아웃 설정
////                httpURLConnection.readTimeout = 5000
////                httpURLConnection.connectTimeout = 5000
////
////                // 요청 메서드를 POST로 설정정
////                httpURLConnection.requestMethod = "POST"
////
////                // 서버에 연결
////                httpURLConnection.connect()
////
////                // 연결에 대한 출력 스트림을 얻고, 파라미터를 'UTF-8'로 인코딩하여 전송
////                val outputStream = httpURLConnection.outputStream
////                outputStream.write(postParameters.toByteArray(charset("UTF-8")))
////                outputStream.flush()
////                outputStream.close()
////
////                // 서버로부터 응답 상태 코드 얻음
////                val responseStatusCode = httpURLConnection.responseCode
////                Log.d(TAG, "POST response code - $responseStatusCode")
////
////                // 응답 상태 코드가 'HTTP_OK(200)'인 경우, 'inputStream'을 얻고, 아닌 경우 'errorStream'을 얻음
////                val inputStream: InputStream
////                inputStream = if (responseStatusCode == HttpURLConnection.HTTP_OK) {
////                    httpURLConnection.inputStream
////                } else {
////                    httpURLConnection.errorStream
////                }
////
////                // 'inputStream'을 'UTF-8'로 읽기 위해 'InputStreadReader'와 'BufferedReader'를 생성
////                val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
////                val bufferedReader = BufferedReader(inputStreamReader)
////
////                // 'StringBuilder'를 사용해 응답 데이터를 한 줄씩 읽어 연결
////                val sb = StringBuilder()
////                var line: String? = null
////
////                while (bufferedReader.readLine().also { line = it } != null) {
////                    sb.append(line)
////                }
////
////                // 'bufferReader' 닫기
////                bufferedReader.close()
////                Log.d("php 값 :", sb.toString())
////
////                // 'sb.toString()'을 반환하여 응답 데이터를 반환
////                return sb.toString()
////            } catch (e: Exception) {
////                Log.d(TAG, "InsertData: Error", e)
////                return "Error " + e.message
////            }
////        }
////    }
////
////    // 아이템이 선택되었을 때 호출
////    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
////
////    // 아무것도 선택되지 않았을 때 호출
////    override fun onNothingSelected(parent: AdapterView<*>?) {}
////
////    // AsyncTask를 상속받고, 서버로 아이디 중복 여부 확인을 위한 데이터를 전송
////    // CheckIdDuplicate 클래스에서 중복 여부를 받아와 처리하는 부분
////    inner class CheckIdDuplicate : AsyncTask<String, Void, String>() {
////        override fun doInBackground(vararg params: String?): String {
////            val url = params[0]?: ""
////            val id = params[1]?: ""
////
////            val client = OkHttpClient()
////
////            val formBody = FormBody.Builder()
////                .add("personal_id", id)
////                .build()
////
////            val request = Request.Builder()
////                .url(url)
////                .post(formBody)
////                .build()
////
////            val response = client.newCall(request).execute()
////            return response.body?.string() ?: ""
////        }
////
////        override fun onPostExecute(result: String?) {
////            super.onPostExecute(result)
////
////            // 중복 여부에 따라 처리
////            if (result == "duplicate") {
////                // 중복된 아이디가 존재하는 경우, 아이디 칸을 빈칸으로 만들기
////                id_text_input_edit_text.setText("")
////                Toast.makeText(this@PersonalSignUpActivity, "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show()
////            } else if (result == "not_duplicate") {
////                Toast.makeText(this@PersonalSignUpActivity, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
////                checkID = true
////            } else {
////                // 오류 처리
////                Toast.makeText(this@PersonalSignUpActivity, "서버 응답 오류", Toast.LENGTH_SHORT).show()
////            }
////        }
////    }
////
////    private fun initRetrofit(): ApiService {
////        val retrofit = Retrofit.Builder()
////            .baseUrl("http://54.180.186.168/") // 탄력적 IP 주소를 여기에 입력
////            .addConverterFactory(GsonConverterFactory.create())
////            .build()
////
////        return retrofit.create(ApiService::class.java)
////    }
////
////    private fun requestEmailVerification(email: String) {
////        val apiService = initRetrofit()
////        val call = apiService.requestEmailVerification(email)
////
////        call.enqueue(object : Callback<ApiResponse> {
////            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
////                if (response.isSuccessful && response.body()?.success == true) {
////                    // 인증 요청 성공
////                    btnEmailCertify.text = "인증완료"
////                    btnEmailCertify.isEnabled = false
////                } else {
////                    // 인증 요청 실패
////                }
////            }
////
////            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
////                // 통신 실패
////            }
////        })
////    }
////
////    private inner class SendEmailTask(val sesClient: AmazonSimpleEmailServiceClient, val toEmail: String) :
////        AsyncTask<Void, Void, Boolean>() {
////
////        override fun doInBackground(vararg params: Void?): Boolean {
////            val subject = "이메일 인증을 완료해주세요"
////            // UUID 생성 예시
////            val verificationToken = UUID.randomUUID().toString()
////            val message = "이메일 인증을 완료하려면 다음 링크를 클릭하세요: http://54.180.186.168/android_email_certify_complete.php?token=$verificationToken"
////
////            val sendEmailRequest = SendEmailRequest()
////                .withSource("yelly0104@naver.com") // 발신자 이메일 주소
////                .withDestination(Destination().withToAddresses(toEmail)) // 수신자 이메일 주소
////                .withMessage(Message().withSubject(Content().withCharset("UTF-8").withData(subject))
////                    .withBody(Body().withText(Content().withCharset("UTF-8").withData(message))))
////
////            try {
////                sesClient.sendEmail(sendEmailRequest)
////                return true
////            } catch (e: Exception) {
////                e.printStackTrace()
////                return false
////            }
////        }
////
////        override fun onPostExecute(result: Boolean) {
////            super.onPostExecute(result)
////            if (result) {
////                Toast.makeText(this@PersonalSignUpActivity, "이메일이 전송되었습니다.", Toast.LENGTH_SHORT).show()
////                btnEmailCertify.text = "인증완료"
////                btnEmailCertify.isEnabled = false
////            } else {
////                Toast.makeText(this@PersonalSignUpActivity, "이메일 전송 실패", Toast.LENGTH_SHORT).show()
////                Log.d("email fail", "email send fail")
////            }
////        }
////    }
////
////    private fun isValidEmail(email: String): Boolean {
////        // 이메일 주소 유효성 검사 로직 추가
////        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
////    }
////
////    class EmailViewModel : ViewModel() {
////        suspend fun sendEmail(sesClient: AmazonSimpleEmailServiceClient, toEmail: String): Boolean {
////            return withContext(Dispatchers.IO) {
////                val subject = "이메일 인증을 완료해주세요"
////                val verificationToken = UUID.randomUUID().toString()
////                val message = "이메일 인증을 완료하려면 다음 링크를 클릭하세요: http://54.180.186.168/android_email_certify_complete.php?token=$verificationToken"
////
////                val sendEmailRequest = SendEmailRequest()
////                    .withSource("yelly0104@naver.com")
////                    .withDestination(Destination().withToAddresses(toEmail))
////                    .withMessage(Message().withSubject(Content().withCharset("UTF-8").withData(subject))
////                        .withBody(Body().withText(Content().withCharset("UTF-8").withData(message))))
////
////                try {
////                    sesClient.sendEmail(sendEmailRequest)
////                    true
////                } catch (e: Exception) {
////                    e.printStackTrace()
////                    false
////                }
////            }
////        }
////    }
////}
////
