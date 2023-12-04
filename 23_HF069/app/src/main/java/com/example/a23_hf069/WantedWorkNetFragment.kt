package com.example.a23_hf069

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.example.a23_hf069.databinding.FragmentWantedWorkNetBinding
import com.example.a23_hf069.databinding.ActivityJobDetailBinding
import com.example.a23_hf069.databinding.JobItemBinding
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class WantedWorkNetFragment : Fragment() {

    private lateinit var binding: FragmentWantedWorkNetBinding
    private lateinit var jobList: List<Job>
    private lateinit var jobListView: ListView  // jobListView 변수 선언
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private var currentPage = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWantedWorkNetBinding.inflate(inflater, container, false)
        val view = binding.root

        // UI 요소 초기화
        jobListView = binding.jobListView  // jobListView 초기화
        prevButton = binding.prevButton
        nextButton = binding.nextButton

        // 이전 페이지 버튼 클릭 이벤트 처리
        prevButton.setOnClickListener {
            if (currentPage > 1) {
                currentPage -= 1
                fetchJobData()
            }
        }

        // 다음 페이지 버튼 클릭 이벤트 처리
        nextButton.setOnClickListener {
            currentPage += 1
            fetchJobData()
        }

        // API 호출
        fetchJobData()

        return view
    }


    private fun fetchJobData() {
        val url =
            "http://openapi.work.go.kr/opi/opi/opia/wantedApi.do?authKey=WNLJYZLM2VZXTT2TZA9XR2VR1HK&callTp=L&returnType=XML&startPage=$currentPage&display=10"
        FetchJobData().execute(url)
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
            jobList = result
            showJobList()
        }
    }

    private fun showJobList() {
        val adapter = CustomAdapter(jobList, requireContext())
        binding.jobListView.adapter = adapter

        binding.jobListView.setOnItemClickListener { _, _, position, _ ->
            val job = jobList[position]
            val intent = JobDetailActivity.newIntent(requireContext(), job)
            startActivity(intent)
        }
    }
}

class CustomAdapter(private val jobList: List<Job>, private val fragmentContext: Context) :
    ArrayAdapter<Job>(fragmentContext, R.layout.job_item, jobList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.let { JobItemBinding.bind(it) }
            ?: JobItemBinding.inflate(LayoutInflater.from(fragmentContext), parent, false)

        val job = jobList[position]

        binding.titleTextView.text = job.title
        binding.companyTextView.text = job.company
        binding.regionContTextView.text = job.region

        return binding.root
    }
}

class JobDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobDetailBinding

    companion object {
        private const val JOB_EXTRA = "job"

        fun newIntent(context: Context, job: Job): Intent {
            return Intent(context, JobDetailActivity::class.java).apply {
                putExtra(JOB_EXTRA, job)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 기본 툴바 숨기기
        supportActionBar?.hide()

        val job = intent.getParcelableExtra<Job>(JOB_EXTRA)

        binding.company.text = job?.company
        binding.title.text = job?.title
        binding.salTpNm.text = job?.salTpNm
        binding.sal.text = job?.sal
        binding.region.text = job?.region
        binding.holidayTpNm.text = job?.holidayTpNm
        binding.minEdubg.text = job?.minEdubg
        binding.career.text = job?.career
        binding.closeDt.text = job?.closeDt
        binding.wantedMobileInfoUrl.text = job?.wantedMobileInfoUrl
        binding.jobsCd.text = job?.jobsCd
        binding.infoSvc.text = job?.infoSvc

        // 뒤로가기 버튼 클릭 시 액티비티 종료
        binding.backButton.setOnClickListener {
            finish()
        }

    }
}

data class Job(
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(company)
        parcel.writeString(title)
        parcel.writeString(salTpNm)
        parcel.writeString(sal)
        parcel.writeString(region)
        parcel.writeString(holidayTpNm)
        parcel.writeString(minEdubg)
        parcel.writeString(career)
        parcel.writeString(closeDt)
        parcel.writeString(wantedMobileInfoUrl)
        parcel.writeString(jobsCd)
        parcel.writeString(infoSvc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Job> {
        override fun createFromParcel(parcel: Parcel): Job {
            return Job(parcel)
        }

        override fun newArray(size: Int): Array<Job?> {
            return arrayOfNulls(size)
        }
    }
}
