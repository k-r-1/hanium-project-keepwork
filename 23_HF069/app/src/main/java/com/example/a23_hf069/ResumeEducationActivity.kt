package com.example.a23_hf069

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.a23_hf069.databinding.ActivityResumeEducationBinding
import java.util.Calendar

class ResumeEducationActivity : AppCompatActivity() {

    private var calendar = Calendar.getInstance()
    private var admissionYear = calendar.get(Calendar.YEAR)
    private var admissionMonth = calendar.get(Calendar.MONTH)
    private var admissionDay = calendar.get(Calendar.DAY_OF_MONTH)
    private var graduateYear = calendar.get(Calendar.YEAR)
    private var graduateMonth = calendar.get(Calendar.MONTH)
    private var graduateDay = calendar.get(Calendar.DAY_OF_MONTH)

    lateinit var graduateSpinner: Spinner
    lateinit var completeButton : Button
    lateinit var binding : ActivityResumeEducationBinding
    lateinit var admissionCalButton : Button
    lateinit var graduateCalButton : Button
    lateinit var edtAdmissionYear : EditText
    lateinit var edtAdmissionMonth : EditText
    lateinit var edtAdmissionDay : EditText
    lateinit var edtGraduateYear : EditText
    lateinit var edtGraduateMonth : EditText
    lateinit var edtGraduateDay : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResumeEducationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        completeButton = findViewById(R.id.complete_button)
        graduateSpinner = findViewById(R.id.graduate_spinner)

        admissionCalButton = findViewById(R.id.admission_calendar)
        edtAdmissionYear = findViewById(R.id.edt_admission_year)
        edtAdmissionMonth = findViewById(R.id.edt_admission_month)
        edtAdmissionDay = findViewById(R.id.edt_admission_day)

        graduateCalButton = findViewById(R.id.graduate_calendar)
        edtGraduateYear = findViewById(R.id.edt_graduate_year)
        edtGraduateMonth = findViewById(R.id.edt_graduate_month)
        edtGraduateDay = findViewById(R.id.edt_graduate_day)

        admissionCalButton.setOnClickListener{
            val datePickerDialog1 = DatePickerDialog(this, { _, year, month, day ->
                edtAdmissionYear.setText(year.toString())
                edtAdmissionMonth.setText((month + 1).toString())
                edtAdmissionDay.setText(day.toString())
            }, admissionYear, admissionMonth, admissionDay)
            datePickerDialog1.show()
        }

        graduateCalButton.setOnClickListener{
            val datePickerDialog2 = DatePickerDialog(this, { _, year, month, day ->
                edtGraduateYear.setText(year.toString())
                edtGraduateMonth.setText((month + 1).toString())
                edtGraduateDay.setText(day.toString())
            }, graduateYear, graduateMonth, graduateDay)
            datePickerDialog2.show()
        }

        val graduateList = listOf(
            "재학",
            "졸업 예정",
            "졸업"
        )

        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, graduateList)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.graduateSpinner.adapter = adapter1
    }
}