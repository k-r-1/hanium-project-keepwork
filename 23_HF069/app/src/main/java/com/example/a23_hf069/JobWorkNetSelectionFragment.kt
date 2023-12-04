package com.example.a23_hf069

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Xml
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.a23_hf069.databinding.FragmentJobWorkNetSelectionBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.w3c.dom.Element
import org.xml.sax.InputSource
import org.xmlpull.v1.XmlPullParser
import java.io.IOException
import java.io.StringReader
import java.util.Collections.addAll
import javax.xml.parsers.DocumentBuilderFactory

class JobWorkNetSelectionFragment : Fragment() {

    private lateinit var binding: FragmentJobWorkNetSelectionBinding
    private lateinit var jobAdapter: ArrayAdapter<String>
    private lateinit var jobCodeList: MutableList<String> //직종코드를 담을 리스트
    private lateinit var selectedJobList: MutableList<String> // 여러 개의 직종을 저장할 리스트

    private lateinit var selectedJobCodeList: MutableList<String> // 여러 개의 직종코드를 저장할 리스트
    private lateinit var combinedList: MutableList<String>

    private lateinit var jobAdapter1: ArrayAdapter<String> // 대분류 지역 리스트뷰에 대한 어댑터
    private lateinit var jobAdapter2: ArrayAdapter<String> // 중분류 지역 리스트뷰에 대한 어댑터

    private lateinit var joblistView1: ListView // 대분류 직종 리스트뷰
    private lateinit var joblistView2: ListView // 중분류 직종 리스트뷰

    private val jobList1: MutableList<String> = mutableListOf() // 대분류 직종을 담을 리스트
    private val jobList2: MutableList<String> = mutableListOf() // 중분류 직종을 담을 리스트

    private var selectedMajorCode: String? = null
    lateinit var selectedJob: String

    // 클래스 내에 맵을 선언합니다.
    private val majorToMiddleMap: MutableMap<String, List<String>> = mutableMapOf()


    // ViewModel 생성
    private val sharedSelectionViewModel: SharedSelectionViewModel by activityViewModels()

    // 직업 목록을 불러오는 API의 기본 URL을 설정
    private val baseUrl =
        "http://openapi.work.go.kr/opi/commonCode/commonCode.do?returnType=XML&target=CMCD&authKey=WNLJYZLM2VZXTT2TZA9XR2VR1HK&dtlGb=2"
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJobWorkNetSelectionBinding.inflate(inflater, container, false)
        val rootView = binding.root

        // View 초기화
        val searchEditText = binding.tvSelectJob
        //val jobListView = binding.listviewMajorCategory
        val jobSelectButton = binding.btnJobSelectComplete

        joblistView1 = binding.listviewMajorCategory
        joblistView2 = binding.listviewMiddleCategory

        selectedJobList = mutableListOf()
        jobCodeList = mutableListOf()
        selectedJobCodeList = mutableListOf()
        combinedList = mutableListOf()

        // ListView 초기화
        jobAdapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, jobList1)
        jobAdapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, jobList2)
        joblistView1.adapter = jobAdapter1
        joblistView2.adapter = jobAdapter2

        selectedJob = sharedSelectionViewModel.selectedJob.toString() // ViewModel에서 선택된 직종 정보를 가져와서 TextView에 설정
        binding.tvSelectedJob.text = selectedJob //화면에 textView 나타내기

        fetchJobList() // 직업 목록 API 호출 및 결과 처리 함수를 호출


        binding.btnMajorCategory.setOnClickListener {
            selectedMajorCode = null // 선택된 대분류 초기화
            joblistView1.visibility = View.VISIBLE
            joblistView2.visibility = View.VISIBLE
            binding.btnMajorCategory.setBackgroundColor(Color.parseColor("#35B891"))
            // 중분류 버튼을 안 보이도록 설정
            binding.btnMiddleCategory.visibility = View.GONE

            // 중분류 목록 초기화 및 갱신
            jobList2.clear()
            jobAdapter2.clear() // 중분류 어댑터에도 데이터를 클리어해야 함
            jobAdapter2.addAll(jobList2) // 초기 중분류 목록을 추가
            jobAdapter2.notifyDataSetChanged()

        }


        binding.btnMiddleCategory.setOnClickListener {
            joblistView1.visibility = View.GONE
            joblistView2.visibility = View.VISIBLE
        }

        // EditText을 클릭하면 검색 화면 JobWorkNetSelectionSearchFragment로 전환
        searchEditText.setOnFocusChangeListener { _, hasFocus -> // EditText의 포커스 변화를 감지하는 리스너를 설정
            if (hasFocus) {
                val JobWorkNetSelectionSearchFragment = JobWorkNetSelectionSearchFragment()
                FragmentManagerHelper.replaceFragment(
                    requireActivity().supportFragmentManager,
                    R.id.fl_container,
                    JobWorkNetSelectionSearchFragment
                )
                hideKeyboard() // 키보드 숨김 처리
            }
        }

        joblistView1.setOnItemClickListener { _, _, position, _ ->
            val selectedJob = jobList1[position]
            val parts = selectedJob.split("-")
            if (parts.size == 2) {
                selectedMajorCode = parts[1]
                updateMiddleJobList(selectedJob) // 중분류 목록 업데이트
                // 중분류 버튼 자동 클릭
                binding.btnMiddleCategory.performClick()
                // 중분류 버튼을 보이도록 설정
                binding.btnMiddleCategory.visibility = View.VISIBLE
                // 대분류 버튼 색상 변경
                binding.btnMajorCategory.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
            }
        }

        // 중분류 직종 선택 시
        joblistView2.setOnItemClickListener { _, _, position, _ ->
            val selectedJob = jobList2[position]
            if (selectedJob.contains("전체")) {
                handleAllMiddleJobs() // "전체" 선택 시 모든 중분류 아이템 처리
            } else {
                handleJobItemClick(selectedJob) // 일반적인 아이템 선택 시 처리
            }
        }

        jobSelectButton.setOnClickListener {
            val selectedJobs = selectedJobList.joinToString(", \n")
            sharedSelectionViewModel.selectedJob = selectedJobs // 선택된 직종 정보를 ViewModel에 저장
            val selectedJobCodes = selectedJobCodeList.joinToString(",")
            sharedSelectionViewModel.selectedJobCode = selectedJobCodes //선택된 직종코드 정보를 viewModel에 저장
            //채용공고는 소분류로 이루어져있음
            //필터링시에는 superCd에 해당하는 채용공고들을 호출

            // Bundle을 생성하여 데이터 추가
            val bundle = Bundle()
            bundle.putString("selectedJobCode", selectedJobCodes)

            // 다음 프래그먼트 생성 및 데이터 전달
            val WantedFilteringFragment = WantedFilteringFragment()
            FragmentManagerHelper.replaceFragment(
                requireActivity().supportFragmentManager,
                R.id.fl_container,
                WantedFilteringFragment
            )
            WantedFilteringFragment.arguments = bundle //뷰모델 사용x, bundle로 값넘겨줄때 필요한 코드
        }

        return rootView
    }

    // 선택된 직업 아이템을 처리하는 함수
    private fun handleJobItemClick(selectedJobItem: String) {
        val parts = selectedJobItem.split("-")
        if (parts.size == 2) {
            val jobName = parts[0]
            val jobCode = parts[1]

            if (!selectedJobList.contains(jobName)) {
                selectedJobList.add(jobName)
                updateSelectedJobTextView()

                selectedJobCodeList.add(jobCode)
            }
        }
    }

    private fun handleAllMiddleJobs() {
        val selectedMajorName = jobList1.find { it.split("-")[1] == selectedMajorCode }?.split("-")?.get(0) ?: ""
        val majorCode = selectedMajorCode ?: ""

        val allOption = "${selectedMajorName} 전체"

        if (!selectedJobList.contains(allOption)) {
            selectedJobList.add(allOption)
            selectedJobCodeList.add(majorCode) // 선택된 대분류 직종 코드 추가
            updateSelectedJobTextView()
        }
    }


    // TextView 업데이트 함수 추가
    private fun updateSelectedJobTextView() {
        val selectedJobs = selectedJobList.joinToString(", \n") // selectedJobList의 모든 항목을 하나의 문자열로 합침
        binding.tvSelectedJob.text = selectedJobs // selectedJobs에서 만들어진 문자열을 tvSelectedJob의 텍스트로 설정하여 선택된 직종들을 화면에 표시
    }

    // 직업 목록을 서버로부터 가져오는 함수
    private fun fetchJobList() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(baseUrl)
            .build()

        // 비동기적으로 API 요청을 수행하고 결과를 처리하는 콜백 등록
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 네트워크 요청 실패 시 에러 토스트 메시지를 보여줌
                showErrorToast()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // 응답이 성공적으로 도착한 경우, XML 형태의 데이터를 문자열로 변환하여 직업 목록 파싱
                    val xmlString = response.body?.string()
                    parseJobList(xmlString)
                } else {
                    // 응답이 실패한 경우 에러 토스트 메시지를 보여줌
                    showErrorToast()
                }
            }
        })
    }

    private fun parseJobList(xmlString: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            val jobList1Temp = mutableListOf<String>()
            val jobList2Temp = mutableListOf<String>()

            val xmlPullParser: XmlPullParser = Xml.newPullParser()
            xmlPullParser.setInput(StringReader(xmlString))

            var eventType = xmlPullParser.eventType
            var isOneDepth = false
            var isTwoDepth = false
            var isJobsNm = false
            var jobsName = ""
            var jobsCode = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (xmlPullParser.name) {
                            "oneDepth" -> {
                                isOneDepth = true
                                isTwoDepth = false
                            }
                            "twoDepth" -> {
                                isOneDepth = false
                                isTwoDepth = true
                            }
                            "jobsNm" -> {
                                isJobsNm = true
                            }
                            "jobsCd" -> {
                                jobsCode = xmlPullParser.nextText().trim()
                            }
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if ((isOneDepth || isTwoDepth) && isJobsNm && xmlPullParser.text.trim().isNotEmpty()) {
                            jobsName = xmlPullParser.text.trim().replace("-", "/")
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (isTwoDepth) {
                            if (jobsName.isNotEmpty() && jobsCode.isNotEmpty()) {
                                jobList2Temp.add("$jobsName-$jobsCode")
                            }
                            isTwoDepth = false
                        } else if (isOneDepth) {
                            if (jobsName.isNotEmpty() && jobsCode.isNotEmpty()) {
                                jobList1Temp.add("$jobsName-$jobsCode")
                            }
                            isOneDepth = false
                        }

                        jobsName = ""
                        jobsCode = ""
                    }
                }

                eventType = xmlPullParser.next()
            }

            // 중분류 목록을 majorToMiddleMap에 저장합니다.
            majorToMiddleMap.clear()
            jobList1Temp.forEach { majorJob ->
                val majorCode = majorJob.split("-")[1]
                val middleJobs = jobList2Temp.filter { it.contains("-$majorCode") }
                majorToMiddleMap[majorCode] = middleJobs
            }

            // 백그라운드 작업 결과를 UI 업데이트를 위한 메인 스레드 블록으로 전달
            withContext(Dispatchers.Main) {
                jobList1.clear()
                jobList2.clear()
                jobList1.addAll(jobList1Temp)
                jobList2.addAll(jobList2Temp)
                jobAdapter1.notifyDataSetChanged()
                jobAdapter2.notifyDataSetChanged()
            }
        }
    }


    private fun updateMiddleJobList(selectedJob: String) {
        val parts = selectedJob.split("-")
        if (parts.size == 2) {
            val selectedMajorCode = parts[1]

            // 선택된 대분류 직종과 "전체" 옵션을 만듭니다.
            val allOption = "${parts[0]} 전체-$selectedMajorCode"

            // 선택된 대분류 직종과 관련된 중분류 직종 리스트를 가져옵니다.
            val filteredMiddleJobs = majorToMiddleMap[selectedMajorCode] ?: emptyList()

            // "전체" 옵션을 중분류 직종 리스트 맨 위에 추가하여 새로운 리스트를 생성합니다.
            val updatedMiddleJobs = mutableListOf<String>().apply {
                add(allOption)
                addAll(filteredMiddleJobs)
            }

            // 중분류 리스트 어댑터를 갱신하여 변경된 리스트를 화면에 표시합니다.
            jobAdapter2.clear()
            jobAdapter2.addAll(updatedMiddleJobs)
            jobAdapter2.notifyDataSetChanged()
        }
    }

    private fun hideKeyboard() {
        // 키보드 숨김 처리를 수행
        val imm =
            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
        view?.clearFocus()
    }

    // API 요청 실패 시 에러 메시지를 보여주는 함수
    private fun showErrorToast() {
        Toast.makeText(requireContext(), "Failed to fetch job list.", Toast.LENGTH_SHORT).show()
    }
}