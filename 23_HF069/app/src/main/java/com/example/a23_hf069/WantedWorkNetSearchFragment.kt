package com.example.a23_hf069

import android.os.AsyncTask
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class WantedWorkNetSearchFragment : Fragment() {
    private lateinit var searchContent: EditText
    private lateinit var searchButton: Button
    private lateinit var searchListView: ListView
    private lateinit var jobList: List<Job>
    private var currentPage = 1
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_wanted_work_net_search, container, false)

        searchContent = rootView.findViewById(R.id.searchContent)
        searchListView = rootView.findViewById(R.id.searchListView)
        prevButton = rootView.findViewById(R.id.prevButton)
        nextButton = rootView.findViewById(R.id.nextButton)

        searchContent.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                fetchJobDataWithSearch()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        // EditText drawableRight 이미지 클릭 이벤트 처리
        searchContent.setOnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (searchContent.right - searchContent.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    // drawableRight를 클릭한 경우
                    fetchJobDataWithSearch()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        val closeButton = rootView.findViewById<ImageButton>(R.id.backButton)
        closeButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        prevButton.visibility = View.GONE
        nextButton.visibility = View.GONE

        // 이전 페이지 버튼 클릭 이벤트 처리
        prevButton.setOnClickListener {
            if (currentPage > 1) {
                currentPage -= 1
                fetchJobDataWithSearch()
            }
        }

        // 다음 페이지 버튼 클릭 이벤트 처리
        nextButton.setOnClickListener {
            currentPage += 1
            fetchJobDataWithSearch()
        }


        return rootView
    }

    private fun fetchJobDataWithSearch() {
        val searchKeyword = searchContent.text.toString().trim()

        if (searchKeyword.isNotEmpty()) {
            val encodedKeyword = URLEncoder.encode(searchKeyword, "UTF-8")
            val url = "http://openapi.work.go.kr/opi/opi/opia/wantedApi.do?authKey=WNLJYZLM2VZXTT2TZA9XR2VR1HK&callTp=L&returnType=XML&startPage=$currentPage&display=10&keyword=$encodedKeyword"
            FetchJobData().execute(url)
        } else {
            // 검색어가 비어있을 때 처리할 내용
        }
    }


    private inner class FetchJobData : AsyncTask<String, Void, List<Job>>() {
        override fun doInBackground(vararg urls: String): List<Job> {
            val urlString = urls[0]
            var result: List<Job> = emptyList()
            var connection: HttpURLConnection? = null

            try {
                val url = URL(urlString)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.readTimeout = 15 * 1000
                connection.connectTimeout = 15 * 1000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    result = parseXml(inputStream)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
            }

            return result
        }

        private fun parseXml(inputStream: InputStream): List<Job> {
            val jobList = mutableListOf<Job>()
            val factory = XmlPullParserFactory.newInstance()
            val xpp = factory.newPullParser()
            xpp.setInput(inputStream, null)

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
                                    jobList.add(
                                        Job(
                                            c, t, salTpNm, sal, region, holidayTpNm,
                                            minEdubg, career, closeDt, wantedMobileInfoUrl, jobsCd, infoSvc
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
            }

            return jobList
        }

        override fun onPostExecute(result: List<Job>) {
            if (currentPage > 1 && result.isEmpty()) {
                // nextButton을 누른 후에 빈 리스트가 반환된 경우, "마지막 페이지입니다" 토스트 메시지를 표시합니다.
                Toast.makeText(requireContext(), "마지막 페이지입니다", Toast.LENGTH_SHORT).show()
                currentPage -= 1 // 이전 페이지로 돌아갑니다.
                nextButton.isEnabled = false // nextButton을 비활성화합니다.
            } else {
                jobList = result
                showJobList()

                if (searchContent.text.toString().trim().isEmpty()) {
                    // EditText에 입력값이 없을 때
                    prevButton.visibility = View.GONE
                    nextButton.visibility = View.GONE
                } else {
                    // EditText에 입력값이 있을 때
                    if (currentPage > 1 || jobList.size >= 10) {
                        prevButton.visibility = View.VISIBLE
                    } else {
                        prevButton.visibility = View.GONE
                    }
                    nextButton.visibility = View.VISIBLE
                }

                nextButton.isEnabled = true // nextButton을 활성화합니다.
            }
        }
    }

    private fun showJobList() {
        val adapter = CustomAdapter(jobList, requireContext())
        searchListView.adapter = adapter

        searchListView.setOnItemClickListener { _, _, position, _ ->
            val job = jobList[position]
            val intent = JobDetailActivity.newIntent(requireContext(), job)
            startActivity(intent)
        }
    }

}