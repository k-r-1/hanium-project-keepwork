package com.example.a23_hf069


import android.os.Build
import android.os.Bundle
import android.text.TextUtils.split
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.a23_hf069.*
import okhttp3.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class WantedFilteringFragment : Fragment() {
    private val baseUrl =
        "http://openapi.work.go.kr/opi/opi/opia/wantedApi.do?authKey=WNLJYZLM2VZXTT2TZA9XR2VR1HK&callTp=L&returnType=XML&display=100"
    private val jobUrl =
        "http://openapi.work.go.kr/opi/commonCode/commonCode.do?returnType=XML&target=CMCD&authKey=WNLJYZLM2VZXTT2TZA9XR2VR1HK&dtlGb=2"
    private var page = 1

    //완료 버튼
    lateinit var complete_btn: Button

    //지역,직종 선택 버튼
    lateinit var regioncl_btn: Button
    lateinit var jobcl_btn: Button

    lateinit var tv_jobcl_selected: TextView
    lateinit var tv_regioncl_selected: TextView
    lateinit var selectedJob: String
    lateinit var selectedRegion: String

    //직종 코드
    private lateinit var selectedJobCode: String

    //라디오 그룹
    lateinit var rgEdu: RadioGroup // 학력 라디오그룹
    lateinit var rgCareer: RadioGroup // 경력 라디오그룹

    private lateinit var wantedList: List<Wanted>
    private lateinit var jobList: List<Job>
    private val sharedSelectionViewModel: SharedSelectionViewModel by activityViewModels() // 필터링된 리스트를 전달하는 viewModel 객체 생성


    // 필터링 키워드
    private var keywordRegion = ""
    private var keywordRegion2 = ""
    private var keywordJob = ""
    private var keywordEdu = ""
    private var keywordCareer = ""
    private var regionList: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_wanted_filtering, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //완료 버튼 초기화
        complete_btn = view.findViewById<Button>(R.id.complete_btn1)
        //지역 선택 초기화
        regioncl_btn = view.findViewById<Button>(R.id.regioncl_btn)
        jobcl_btn = view.findViewById<Button>(R.id.jobcl_btn)


        tv_regioncl_selected =
            view.findViewById(R.id.tv_regioncl_selected) // 선택된 지역 정보를 나타낼 TextView 초기화
        tv_jobcl_selected = view.findViewById(R.id.tv_jobcl_selected) // 선택된 직종 정보를 나타낼 TextView 초기화
        selectedRegion =
            sharedSelectionViewModel.selectedRegion.toString() // ViewModel에서 선택된 지역 정보를 가져와서 TextView에 설정
        tv_regioncl_selected.text = selectedRegion //화면에 textView 나타내기
        selectedJob =
            sharedSelectionViewModel.selectedJob.toString() // ViewModel에서 선택된 직종 정보를 가져와서 TextView에 설정
        tv_jobcl_selected.text = selectedJob //화면에 textView 나타내기

        // 전달된 직종코드 데이터를 받아서 사용
        //selectedJobCode = arguments?.getString("selectedJobCode").toString()
        selectedJobCode=sharedSelectionViewModel.selectedJobCode.toString()

        // 라디오 그룹을 초기화
        rgEdu = view.findViewById(R.id.rg_edu)
        rgCareer = view.findViewById(R.id.rg_career)

        // 완료 버튼이 눌렸을 때 지역,직종 변수 및 학력,경력,마감일 라디오버튼 확인 -> 선택된 조건에 해당하는 공고목록 가져와서 UI에 업데이트
        complete_btn.setOnClickListener {
            // 해당 라디오 그룹에서 선택된 Id를 가져오기
            val checkEduId = rgEdu.checkedRadioButtonId
            val checkCareerId = rgCareer.checkedRadioButtonId

            // 선택한 지역이 있을 경우 키워드에 해당 지역이름 넣기
            if (selectedRegion != "") {
                keywordRegion = sharedSelectionViewModel.keywordRegions // ex) "서울 성동구|서울 종로구"
                keywordRegion2 = keywordRegion.replace(" ", "") // ex) "서울성동구|서울종로구"
                regionList = keywordRegion.split("|").map { it.trim() }
                    .toMutableList() //ex) [서울 성동구, 서울 종로구]

            }

            // 선택한 직종이 있을 경우 필터링하기
            if (selectedJob != "") {
                keywordJob = selectedJobCode
            }

            // 학력 라디오 그룹중 선택된 라디오 버튼이 있을때 처리
            when (checkEduId) {
                R.id.rb_e_1 -> {
                    keywordEdu = "학력무관"
                }

                R.id.rb_e_2 -> {
                    keywordEdu = "고졸"
                }

                R.id.rb_e_3 -> {
                    keywordEdu = "대졸(2~3년)"
                }

                R.id.rb_e_4 -> {
                    keywordEdu = "대졸(4년)"
                }
            }

            // 경력 라디오 그룹중 선택된 라디오 버튼이 있을때 처리
            when (checkCareerId) {
                R.id.rb_c_1 -> {
                    keywordCareer = "관계없음"
                }

                R.id.rb_c_2 -> {
                    // 고등학교 졸 라디오 버튼이 선택되었을 때
                    keywordCareer = "신입"
                }

                R.id.rb_c_3 -> {
                    // 대학(2년제) 라디오 버튼이 선택되었을 때
                    keywordCareer = "경력"
                }
            }


            // 지역 조건은 필수 선택 조건으로 무조건 선택해야 함
            if (keywordRegion == "") {
                showWarningToast1() // 지역 조건을 선택하지 않은 경우 토스트창 띄우기
            }
            // 지역조건을 선택한 경우
            else {
                // 필터링 후 UI에 반영
                fetchWantedList()

                // 화면전환
                val WantedResultFragment = WantedResultFragment()
                FragmentManagerHelper.replaceFragment(
                    requireActivity().supportFragmentManager,
                    R.id.fl_container,
                    WantedResultFragment
                )
            }

        }
        // complete_btn 리스너 종료

        // 지역선택 버튼 눌렸을 때 지역선택 화면으로 전환
        regioncl_btn.setOnClickListener {
            val RegionSelectionFragment = RegionSelectionFragment()
            FragmentManagerHelper.replaceFragment(
                requireActivity().supportFragmentManager,
                R.id.fl_container,
                RegionSelectionFragment
            )
        }

        // 직종선택 버튼 눌렸을 때 직종선택 화면으로 전환
        jobcl_btn.setOnClickListener {
            val JobWorkNetSelectionFragment = JobWorkNetSelectionFragment()
            FragmentManagerHelper.replaceFragment(
                requireActivity().supportFragmentManager,
                R.id.fl_container,
                JobWorkNetSelectionFragment
            )
        }

    }

    // 키워드에 해당하는 채용공고 가져와서 sharedSelectionViewModel의 리스트에 저장 -> UI에 반영
    private fun fetchWantedList() {
        //네트워크 연결
        val client = OkHttpClient()
        //전체
        var wholeRegion = false
        if (keywordRegion2.contains("전체")) { // ex) 서울 전체인 경우 -> 서울로 변경
            keywordRegion2 = keywordRegion2.replace("전체", "").trim()
            wholeRegion = true
        }
        val request = Request.Builder()
            .url("$baseUrl&startPage=$page&keyword=$keywordRegion2") // &keyword로 지역 1차 필터링하기 (이렇게 안하면 traffic 터져서 아무것도 안나옴)
            .build()
        var result: List<Wanted> = emptyList()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.printStackTrace())
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val xmlString = response.body?.string() // url에 있는 모든 글자 다가져오기
                    result = parseXmlResponse(xmlString) // parsing한 후 리스트화 하기
                    // 지역 1차 필터링만 한 리스트
                    wantedList = result

                    // 직종 XML 데이터 호출
                    val jobRequest = Request.Builder()
                        .url(jobUrl)
                        .build()
                    var result2: List<Job> = emptyList()

                    client.newCall(jobRequest).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            //println(e.printStackTrace())
                        }

                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onResponse(call: Call, jobResponse: Response) {
                            if (jobResponse.isSuccessful) {
                                val jobXmlString = jobResponse.body?.string()
                                result2 = parseJobXmlResponse(jobXmlString)
                                jobList=result2

                                // 지역 2차 필터링하기
                                var filteredList =
                                    wantedList.filter { it.region?.trim() in regionList }

                                val matchedSuperCds =
                                    jobList.filter { it.superCd == keywordJob }.map { it.jobsCd }
                                        .toSet()

                                // 전체 지역이 선택된 경우
                                if (wholeRegion) {
                                    filteredList = wantedList.filter {
                                        it.region != null && checkCommonPrefix(
                                            it.region,
                                            keywordRegion2
                                        )
                                    }
                                }

                                if (keywordEdu == "" && keywordCareer == "" && keywordJob =="") { // 지역만 선택
                                    sharedSelectionViewModel.updateFilteredList(filteredList)
                                } else if (keywordCareer.isNotEmpty() && keywordEdu == "") { // 경력만 선택
                                    val filteredList1 = filteredList.filter {// 경력 필터링
                                        it.career == keywordCareer
                                    }
                                    sharedSelectionViewModel.updateFilteredList(filteredList1)
                                } else if (keywordEdu.isNotEmpty() && keywordCareer == "" && keywordJob =="") { // 학력만 선택
                                    val filteredList1 = filteredList.filter { // 학력 필터링
                                        it.minEdubg == keywordEdu
                                    }
                                    sharedSelectionViewModel.updateFilteredList(filteredList1)
                                } else if (keywordEdu.isNotEmpty() && keywordCareer.isNotEmpty() && keywordJob =="") { // 경력, 학력 모두 선택
                                    val filteredList1 = filteredList.filter { // 경력, 학력 필터링
                                        it.minEdubg == keywordEdu && it.career == keywordCareer
                                    }
                                    sharedSelectionViewModel.updateFilteredList(filteredList1)
                                } else if (keywordEdu == "" && keywordCareer == "" && keywordJob.isNotEmpty()) { //지역+직종
                                    val filteredList1 = wantedList.filter {
                                        it.jobsCd in matchedSuperCds
                                    }
                                    sharedSelectionViewModel.updateFilteredList(filteredList1)
                                } else if (keywordEdu == "" && keywordCareer.isNotEmpty() && keywordJob.isNotEmpty()) { //지역+직종+경력
                                    val filteredList1 = wantedList.filter {
                                        (it.jobsCd in matchedSuperCds) && it.career == keywordCareer
                                    }
                                    sharedSelectionViewModel.updateFilteredList(filteredList1)
                                } else if (keywordEdu.isNotEmpty() && keywordCareer == "" && keywordJob.isNotEmpty()) { //지역+직종+학력
                                    val filteredList1 = wantedList.filter {
                                        it.minEdubg == keywordEdu && (it.jobsCd in matchedSuperCds)
                                    }
                                    sharedSelectionViewModel.updateFilteredList(filteredList1)
                                } else { //지역+직종+경력+학력
                                    val filteredList1 = wantedList.filter {
                                        it.minEdubg == keywordEdu && it.career == keywordCareer && (it.jobsCd in matchedSuperCds)
                                    }
                                    sharedSelectionViewModel.updateFilteredList(filteredList1)
                                }

                                // 더 많은 페이지가 있는지 확인합니다.
                                val factory = XmlPullParserFactory.newInstance()
                                val xpp = factory.newPullParser()
                                xpp.setInput(StringReader(xmlString))

                                var eventType = xpp.eventType
                                var totalItems = 0
                                var totalPages = 0

                                while (eventType != XmlPullParser.END_DOCUMENT) {
                                    if (eventType == XmlPullParser.START_TAG && xpp.name == "total") {
                                        totalItems = xpp.nextText().toInt()
                                        totalPages = totalItems / 100
                                        break
                                    }
                                    eventType = xpp.next()
                                }
                                // 더 많은 페이지가 있다면 다음 페이지를 가져옵니다.
                                while (totalPages > page) {
                                    page += 1
                                    fetchWantedList()
                                }
                            }
                        }
                    })

                }// if 응답이 성공적일때
                else {
                    showErrorToast()
                } //if 응답 실패일때
            } //onResponse 함수 종료
        }) //callback 종료
    } // fetchWantedList 함수 종료

    data class Wanted(
        val company: String,
        val title: String,
        val salTpNm: String?,
        val sal: String?,
        val region: String?,
        val holidayTpNm: String?,
        val minEdubg: String?,
        val career: String?,
        val closeDt: String?,
        val wantedMobileInfoUrl: String?,
        val jobsCd: String?,
        val infoSvc: String?
    )

    data class Job(
        val jobsCd: String?,
        val superCd: String?
    )

    private fun parseXmlResponse(xmlResponse: String?): List<Wanted> {
        val wantedList = mutableListOf<Wanted>()
        val factory = XmlPullParserFactory.newInstance()
        val xpp = factory.newPullParser()
        xpp.setInput(StringReader(xmlResponse))

        var eventType = xpp.eventType
        var company: String? = null // 회사명
        var title: String? = null // 채용제목
        var salTpNm: String? = null // 임금형태
        var sal: String? = null // 급여
        var region: String? = null // 근무지역
        var holidayTpNm: String? = null // 근무형태
        var minEdubg: String? = null // 최소학력
        var career: String? = null // 경력
        var closeDt: String? = null // 마감일자
        var wantedMobileInfoUrl: String? = null // 워크넷 모바일 채용정보 URL
        var jobsCd: String? = null // 직종코드
        var infoSvc: String? = null // 정보제공처

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (xpp.name) {
                        "company" -> company = xpp.nextText()
                        "title" -> title = xpp.nextText()
                        "salTpNm" -> salTpNm = xpp.nextText()
                        "sal" -> sal = xpp.nextText()
                        "region" -> region = xpp.nextText()
                        "holidayTpNm" -> holidayTpNm = xpp.nextText()
                        "minEdubg" -> minEdubg = xpp.nextText()
                        "career" -> career = xpp.nextText()
                        "closeDt" -> closeDt = xpp.nextText()
                        "wantedMobileInfoUrl" -> wantedMobileInfoUrl = xpp.nextText()
                        "jobsCd" -> jobsCd = xpp.nextText()
                        "infoSvc" -> infoSvc = xpp.nextText()
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (xpp.name == "wanted") {
                        company?.let { c ->
                            title?.let { t ->
                                wantedList.add(
                                    Wanted(
                                        c,
                                        t,
                                        salTpNm,
                                        sal,
                                        region,
                                        holidayTpNm,
                                        minEdubg,
                                        career,
                                        closeDt,
                                        wantedMobileInfoUrl,
                                        jobsCd,
                                        infoSvc
                                    )
                                )
                            }
                        }
                        company = null
                        title = null
                        salTpNm = null
                        sal = null
                        region = null
                        holidayTpNm = null
                        minEdubg = null
                        career = null
                        closeDt = null
                        wantedMobileInfoUrl = null
                        jobsCd = null
                        infoSvc = null
                    }
                }
            }
            eventType = xpp.next()
        } // while문 종료
        return wantedList
    }

    private fun parseJobXmlResponse(xmlResponse: String?): List<Job> {
        val jobList = mutableListOf<Job>()
        val factory = XmlPullParserFactory.newInstance()
        val xpp = factory.newPullParser()
        xpp.setInput(StringReader(xmlResponse))

        var eventType = xpp.eventType
        var jobsCd: String? = null
        var superCd: String? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (xpp.name) {
                        "jobsCd" -> jobsCd = xpp.nextText()
                        "superCd" -> superCd = xpp.nextText()
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (xpp.name == "threeDepth" || xpp.name == "twoDepth" || xpp.name == "oneDepth") {
                        jobsCd?.let { jCd ->
                            superCd?.let { sCd ->
                                jobList.add(Job(jCd, sCd))
                            }
                        }
                        jobsCd = null
                        superCd = null
                    }
                }
            }
            eventType = xpp.next()
        }
        return jobList
    }

    // 앞의 두 글자가 연속적으로 일치하는지 확인하는 함수
    fun checkCommonPrefix(str1: String, str2: String): Boolean {
        val minLength = minOf(str1.length, str2.length)
        var commonPrefix = ""

        for (i in 0 until minLength) {
            if (str1[i] == str2[i]) {
                commonPrefix += str1[i]
            } else {
                break
            }
        }

        return commonPrefix.length >= 2
    }

    private fun showErrorToast() {
        Toast.makeText(requireContext(), "Failed to fetch wanted list.", Toast.LENGTH_SHORT).show()
    }

    private fun showWarningToast1() {
        Toast.makeText(requireContext(), "지역은 필수 선택 조건입니다.", Toast.LENGTH_SHORT).show()
    }
}