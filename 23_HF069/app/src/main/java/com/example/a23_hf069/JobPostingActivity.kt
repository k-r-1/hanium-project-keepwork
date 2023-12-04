package com.example.a23_hf069

import JobPosting
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar

class JobPostingActivity : AppCompatActivity() {
    private var calendar = Calendar.getInstance()
    private var postingYear = calendar.get(Calendar.YEAR)
    private var postingMonth = calendar.get(Calendar.MONTH)
    private var postingDay = calendar.get(Calendar.DAY_OF_MONTH)
    lateinit var postingCalButton: Button
    lateinit var edtpostingYear: EditText
    lateinit var edtpostingMonth: EditText
    lateinit var edtpostingDay: EditText
    lateinit var careerSpinner: Spinner
    lateinit var educationSpinner: Spinner

    // 뷰 요소들을 선언
    private lateinit var titleEditText: TextInputEditText // 공고 제목
    private lateinit var periodEditText: TextInputEditText // 기간
    private lateinit var dayEditText: TextInputEditText // 요일
    private lateinit var timeEditText: TextInputEditText // 시간
    private lateinit var payEditText: TextInputEditText // 급여
    private lateinit var positionEditText: TextInputEditText // 직책
    private lateinit var occupationEditText: TextInputEditText // 직군
    private lateinit var detailedEditText: EditText // 상세 요강
    private lateinit var contactEditText: TextInputEditText // 인사 담당자 연락처
    private lateinit var emailEditText: TextInputEditText // 이메일
    private lateinit var registerButton: Button // 등록 버튼
    private var deadline: String = "" //  마김일

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(RetrofitInterface.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: RetrofitInterface = retrofit.create(RetrofitInterface::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_posting)

        // 기본 툴바 숨기기
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.hide()
        }

        // backButton 클릭 시 뒤로가기 처리
        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed() // 뒤로가기 처리
        }


        titleEditText = findViewById(R.id.titleEditText) // 공고 제목
        periodEditText = findViewById(R.id.periodEditText) // 기간
        dayEditText = findViewById(R.id.dayEditText) // 요일
        timeEditText = findViewById(R.id.timeEditText) // 시간
        payEditText = findViewById(R.id.payEditText) // 급여
        positionEditText = findViewById(R.id.positionEditText) // 직책
        occupationEditText = findViewById(R.id.occupationEditText) // 직군
        detailedEditText = findViewById(R.id.detailedEditText) // 상세 요강
        contactEditText = findViewById(R.id.contactEditText) // 인사 담당자 연락처
        emailEditText = findViewById(R.id.emailEditText) // 이메일
        registerButton = findViewById(R.id.registerButton) // 등록 버튼

        postingCalButton = findViewById(R.id.posting_calendar)
        edtpostingYear = findViewById(R.id.edt_posting_year)
        edtpostingMonth = findViewById(R.id.edt_posting_month)
        edtpostingDay = findViewById(R.id.edt_posting_day)

        postingCalButton.setOnClickListener {
            val datePickerDialog1 = DatePickerDialog(
                this,
                { _, year, month, day ->
                    edtpostingYear.setText(year.toString())
                    edtpostingMonth.setText((month + 1).toString())
                    edtpostingDay.setText(day.toString())

                    // 선택한 날짜를 "yyyy-MM-dd" 형식으로 포맷하고 "deadline" 변수에 저장합니다.
                    deadline = String.format(
                        "%04d%02d%02d",
                        year,
                        month + 1, // 월은 0부터 시작하므로 1을 더합니다.
                        day
                    )
                },
                postingYear,
                postingMonth,
                postingDay
            )
            datePickerDialog1.show()
        }

        val careerList = listOf(
            "경력무관",
            "신입",
            "경력",
        )

        val adapter1 =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, careerList)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        careerSpinner = findViewById(R.id.careerSpinner)
        careerSpinner.adapter = adapter1

        val educationList = listOf(
            "학력무관",
            "고등학교 졸업 이상",
            "전문학사 이상",
            "학사 이상",
            "석사 이상",
            "박사 이상"
        )

        val adapter2 =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, educationList)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        educationSpinner = findViewById(R.id.educationSpinner)
        educationSpinner.adapter = adapter2


        registerButton.setOnClickListener {
            val dialog = AlertDialog.Builder(this@JobPostingActivity)
                .setMessage("공고를 등록하시겠습니까?")
                .setPositiveButton("예") { _, _ ->
                    // 사용자가 '예'를 선택한 경우, 공고 등록 로직 실행

                    // 사용자 이름 가져오기 (이 부분은 SharedPreferences를 통해 사용자 이름을 얻는 방식으로 진행)
                    val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                    val companyName = sharedPreferences.getString("userName", null) ?: ""

                    // EditText 및 Spinner에서 값을 가져옵니다.
                    val jobTitle = titleEditText.text.toString()
                    val jobCareer = careerSpinner.selectedItem.toString()
                    val jobEducation = educationSpinner.selectedItem.toString()
                    val jobPeriod = periodEditText.text.toString()
                    val jobDayOfWeek = dayEditText.text.toString()
                    val jobTime = timeEditText.text.toString()
                    val jobPay = payEditText.text.toString()
                    val jobPosition = positionEditText.text.toString()
                    val jobOccupation = occupationEditText.text.toString()
                    val jobRequirements = detailedEditText.text.toString()
                    val jobContactNumber = contactEditText.text.toString()
                    val jobEmail = emailEditText.text.toString()
                    val job_deadline = deadline

                    // JobPosting 객체를 생성하고 데이터를 할당합니다.
                    val jobPosting = JobPosting(
                        job_listnum = 0,
                        company_name = companyName,
                        job_title = jobTitle,
                        job_experience_required = jobCareer,
                        job_education_required = jobEducation,
                        job_period = jobPeriod,
                        job_days_of_week = jobDayOfWeek,
                        job_working_hours = jobTime,
                        job_salary = jobPay,
                        job_position = jobPosition,
                        job_category = jobOccupation,
                        job_requirements = jobRequirements,
                        job_contact_number = jobContactNumber,
                        job_email = jobEmail,
                        job_deadline = job_deadline
                    )

                    // Retrofit을 사용하여 서버에 jobPosting 객체를 전송
                    val call = apiService.postJob(jobPosting)
                    call.enqueue(object : Callback<JobPosting> {
                        override fun onResponse(call: Call<JobPosting>, response: Response<JobPosting>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@JobPostingActivity, "공고 등록 성공", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@JobPostingActivity, "오류 발생: ${response.errorBody()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<JobPosting>, t: Throwable) {
                            Toast.makeText(this@JobPostingActivity, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .setNegativeButton("아니오", null)  // 사용자가 '아니오'를 선택한 경우, 아무런 동작을 하지 않음.
                .create()

            dialog.show()

         }

    }
}
