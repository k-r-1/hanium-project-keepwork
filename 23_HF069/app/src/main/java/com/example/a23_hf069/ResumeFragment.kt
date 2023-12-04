import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.a23_hf069.FragmentManagerHelper
import com.example.a23_hf069.R
import com.example.a23_hf069.ResumeChangeActivity
import com.example.a23_hf069.ResumeClickActivity
import com.example.a23_hf069.ResumeCompleteFragment
import com.example.a23_hf069.ResumeModel
import com.example.a23_hf069.ResumeTemporaryFragment
import com.example.a23_hf069.ResumeWriteFragment
import com.example.a23_hf069.RetrofitInterface
import com.example.a23_hf069.TabPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ResumeFragment : Fragment() {
    // 사용자 ID를 저장할 변수
    private lateinit var personal_id: String

    // 사용자의 작성중 이력서 개수를 표시할 TextView 변수
    private lateinit var tvResume_temporary_count: TextView

    // 사용자의 작성완료 이력서 개수를 표시할 TextView 변수
    private lateinit var tvResume_complete_count: TextView

    // 이력서 추가 버튼을 나타낼 Button 변수
    private lateinit var buttonAddResume: FloatingActionButton

    // 이력서 목록을 표시할 RecyclerView 변수
    private lateinit var recyclerView: RecyclerView

    // 이력서 목록을 표시하는 어댑터를 담을 변수
    private lateinit var dataAdapter: DataAdapter

    // 이력서 목록을 업데이트하는 주기를 지정하는 변수 (2초로 설정)
//    private val updateIntervalMillis: Long = 2000

    // 업데이트를 위해 사용할 핸들러 객체
//    private val handler = Handler()

    // Handler 동작 여부를 나타내는 변수
//    private var isHandlerRunning = false

    // Fragment가 화면에 보일 때 호출되는 메서드
//    override fun onStart() {
//        super.onStart()
//        // Handler 시작 (주기적인 작업 시작)
//        startHandler()
//    }

    // Fragment가 화면에서 사라질 때 호출되는 메서드
//    override fun onStop() {
//        super.onStop()
//        // Handler 중지 (주기적인 작업 중지)
//        stopHandler()
//    }

    // Handler 시작 메서드
//    private fun startHandler() {
//        if (!isHandlerRunning) {
//            handler.postDelayed(updateDataRunnable, updateIntervalMillis)
//            isHandlerRunning = true
//        }
//    }

    // Handler 중지 메서드
//    private fun stopHandler() {
//        handler.removeCallbacks(updateDataRunnable)
//        isHandlerRunning = false
//    }

    // 서버로부터 데이터를 가져오는 작업을 반복할 Runnable 객체
//    private val updateDataRunnable = object : Runnable {
//        override fun run() {
//            fetchDataFromServer()
//            handler.postDelayed(this, updateIntervalMillis) // 일정 간격 후 다시 호출
//        }
//    }

    // Fragment의 뷰를 생성하는 메서드
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 뷰를 생성하여 반환하는 코드
        val view = inflater.inflate(R.layout.fragment_resume, container, false)

        // Argument로부터 전달받은 사용자 ID를 변수에 저장
        if (arguments != null) {
            personal_id = arguments?.getString("userId", "") ?: ""
        }

        // 사용자 ID를 표시할 TextView 초기화
        val textID = view.findViewById<TextView>(R.id.tvID1)
        textID.text = personal_id

        // RecyclerView 초기화 후 빈 어댑터 설정
        recyclerView = view.findViewById(R.id.recyclerviewResume)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 빈 어댑터 생성 및 RecyclerView에 설정
        dataAdapter = DataAdapter(emptyList(), personal_id)
        recyclerView.adapter = dataAdapter

        // 작성중 이력서 개수와 작성완료 이력서 개수를 표시할 TextView 초기화
        tvResume_temporary_count = view.findViewById(R.id.tvResume_temporary_count)
        tvResume_complete_count = view.findViewById(R.id.tvResume_complete_count)

        // 서버로 사용자 아이디를 전송하여 이력서 데이터를 가져오도록 요청
        fetchDataFromServer()

        // 이력서 추가 버튼 클릭 리스너 설정
        buttonAddResume = view.findViewById(R.id.btnAddResume)
        buttonAddResume.setOnClickListener {
            // 백 스택에 추가하지 않고 ResumeWriteFragment로 이동
            FragmentManagerHelper.replaceFragment(
                requireActivity().supportFragmentManager,
                R.id.fl_container,
                ResumeWriteFragment().apply {
                    arguments = Bundle().apply {
                        putString("userId", personal_id)
                    }
                },
                addToBackStack = false
            )
        }

        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout_resume) // TabLayout ID
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager_resume) // ViewPager2 ID

        val fragmentList = listOf(
            ResumeCompleteFragment(),
            ResumeTemporaryFragment()
        )

        val adapter = TabPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "작성중 (1)"
                1 -> tab.text = "작성완료 (0)"
                else -> tab.text = ""
            }
        }.attach()

        // 생성한 뷰 반환
        return view

    }

    // Fragment의 뷰가 생성되었을 때 호출되는 메서드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 이력서 삭제 버튼 클릭 리스너 설정
        dataAdapter.setOnDeleteClickListener(object : DataAdapter.OnDeleteClickListener {
            override fun onDeleteClick(resumeData: ResumeModel) {
                // 서버에서 이력서 데이터 삭제 요청
                deleteResumeFromServer(resumeData.resume_listnum)
            }
        })

        // 사용자의 작성중 이력서 개수와 작성완료 이력서 개수를 업데이트
        fetchDataFromServer()

        // 일정 간격으로 서버에서 데이터를 가져오고 UI를 업데이트하는 작업 시작
//        handler.postDelayed(updateDataRunnable, updateIntervalMillis)

        // 이력서 삭제 버튼 클릭 리스너 설정
        setupDeleteButtonClickListener()
    }

    // 서버로부터 데이터를 가져오는 메서드
    private fun fetchDataFromServer() {
        // 서버로 사용자 아이디를 전송하여 이력서 데이터를 가져오도록 요청
        val retrofit = Retrofit.Builder()
            .baseUrl(RetrofitInterface.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(RetrofitInterface::class.java)
        apiService.getResumeData(personal_id, null, null, null, null, null, null, null, null, null)
            .enqueue(object : Callback<List<ResumeModel>> { // 사용하는 Response 클래스 타입으로 변경
            override fun onResponse(call: Call<List<ResumeModel>>, response: Response<List<ResumeModel>>) {
                if (response.isSuccessful) {
                    val dataList = response.body()
                    // 응답 내용을 로그로 출력(테스트용)
                    Log.d("FetchDataFromServer", "Response: $dataList")

                    requireActivity().runOnUiThread {
                        if (dataList != null) {
                            dataAdapter.setData(dataList)
                            // println("이력서갯수:"+dataList.size)

                            // resume_complete 값을 가진 이력서의 개수 구하기
                            val cnt1 = dataList.count { it.resume_complete == "작성중"}
                            //cnt1은 작성완료 탭이니까 나중에 바꾸기

                            // resume_complete 값을 가지지 않은 이력서의 개수 구하기
                            val cnt2 = dataList.size - cnt1

                            if (cnt1 != null && cnt2 != null) {
                                tvResume_temporary_count.text = cnt1.toString()
                                tvResume_complete_count.text = cnt2.toString()
                            } else {
                                Toast.makeText(view?.context, "이력서 개수를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(view?.context, "서버로부터 이력서 데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(view?.context, "서버로부터 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ResumeModel>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(view?.context, "통신 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 이력서 삭제 버튼 클릭 리스너 설정
    private fun setupDeleteButtonClickListener() {
        dataAdapter.setOnDeleteClickListener(object : DataAdapter.OnDeleteClickListener {
            override fun onDeleteClick(resumeData: ResumeModel) {
                // 서버에서 이력서 데이터 삭제 요청
                deleteResumeFromServer(resumeData.resume_listnum)
            }
        })
    }

    // 서버로 이력서 삭제 요청
    private fun deleteResumeFromServer(resumeListNum: Int?) {
        // resumeListNum이 null인 경우에 대비하여 처리
        if (resumeListNum == null) {
            // null인 경우, 서버 요청을 보낼 수 없으므로 오류 처리
            requireActivity().runOnUiThread {
                Toast.makeText(view?.context, "잘못된 이력서 번호입니다.", Toast.LENGTH_SHORT).show()
            }
            return
        }

        // 다이얼로그를 생성하고 설정
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setMessage("해당 이력서를\n삭제하시겠습니까?")
        alertDialogBuilder.setPositiveButton("네") { _, _ ->
            // "네"를 눌렀을 때 서버에서 이력서 데이터 삭제 요청

            val retrofit = Retrofit.Builder()
                .baseUrl(RetrofitInterface.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(RetrofitInterface::class.java)

            apiService.deleteResumeFromServer(personal_id, resumeListNum)
                .enqueue(object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful && response.body()?.contains("Record deleted successfully") == true) {
                        requireActivity().runOnUiThread {
                            // RecyclerView에서 아이템 삭제
                            dataAdapter.removeDataByListNum(resumeListNum)
                            // 작성중 이력서 개수와 작성완료 이력서 개수 업데이트
                            fetchDataFromServer()
                            // 삭제 성공 메시지 출력
                            Toast.makeText(view?.context, "이력서를 성공적으로 삭제했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        requireActivity().runOnUiThread {
                            Toast.makeText(view?.context, "이력서 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    t.printStackTrace()
                    requireActivity().runOnUiThread {
                        Toast.makeText(view?.context, "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        alertDialogBuilder.setNegativeButton("아니오") { dialog, _ ->
            // "아니오"를 눌렀을 때 다이얼로그를 닫고 아무 작업도 하지 않음
            dialog.dismiss()
        }

        // 다이얼로그를 표시
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

    // 이력서 목록을 표시하는 어댑터 클래스
    class DataAdapter(
        var dataList: List<ResumeModel>,
        private val personal_id: String
    ) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

        // 각각의 뷰를 보유하는 뷰홀더 클래스
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewTitle: Button = itemView.findViewById(R.id.tvResumeTitle)
            val textViewStatus: TextView = itemView.findViewById(R.id.tvWriteStatus)
            val buttonRemove: Button = itemView.findViewById(R.id.buttonRemove)
            val buttonChange: Button = itemView.findViewById(R.id.buttonEdit)
        }

        // 뷰홀더 생성
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.resume_item, parent, false)
            return ViewHolder(view)
        }

        // 삭제 버튼 클릭 리스너 인터페이스
        interface OnDeleteClickListener {
            fun onDeleteClick(resumeData: ResumeModel)
        }

        private var deleteClickListener: OnDeleteClickListener? = null

        // 삭제 버튼 클릭 리스너 설정
        fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
            deleteClickListener = listener
        }

        // 아이템 삭제 메서드
        fun removeDataByListNum(resumeListNum: Int) {
            val updatedList = dataList.toMutableList()
            val position = updatedList.indexOfFirst { it.resume_listnum == resumeListNum }
            if (position != -1) {
                updatedList.removeAt(position)
                dataList = updatedList
                notifyItemRemoved(position)
            }
        }


        // 뷰홀더의 뷰에 데이터를 바인딩
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = dataList[position]
            holder.textViewTitle.text = data.resume_title
            holder.textViewStatus.text = data.resume_complete

            // 삭제 버튼 클릭 리스너 설정
            holder.buttonRemove.setOnClickListener {
                deleteClickListener?.onDeleteClick(data)
            }

            // 수정 버튼 클릭 리스너 설정
            holder.buttonChange.setOnClickListener {
                val intent = Intent(holder.itemView.context, ResumeChangeActivity::class.java)
                intent.putExtra("resumeListNum", data.resume_listnum)
                intent.putExtra("userId", personal_id)
                holder.itemView.context.startActivity(intent)
            }

            // 이력서 제목 버튼 클릭 리스너 설정
            holder.textViewTitle.setOnClickListener {
                val intent = Intent(holder.itemView.context, ResumeClickActivity::class.java)
                intent.putExtra("resumeListNum", data.resume_listnum)
                intent.putExtra("userId", personal_id)
                holder.itemView.context.startActivity(intent)
            }
        }

        // 전체 아이템 개수 반환
        override fun getItemCount(): Int {
            return dataList.size
        }

        // 외부에서 데이터를 설정할 수 있도록 setData() 함수 추가
        fun setData(newDataList: List<ResumeModel>) {
            dataList = newDataList
            notifyDataSetChanged()
        }
    }
}

//class ResumeFragment : Fragment() {
//    // 서버의 IP 주소를 저장할 변수
//    private var IP_ADDRESS = "54.180.186.168"
//
//    // 사용자 ID를 저장할 변수
//    private lateinit var userId: String
//
//    // 사용자의 작성중 이력서 개수를 표시할 TextView 변수
//    private lateinit var tvResume_temporary_count: TextView
//
//    // 사용자의 작성완료 이력서 개수를 표시할 TextView 변수
//    private lateinit var tvResume_complete_count: TextView
//
//    // 이력서 추가 버튼을 나타낼 Button 변수
//    private lateinit var buttonAddResume: Button
//
//    // 이력서 목록을 표시할 RecyclerView 변수
//    private lateinit var recyclerView: RecyclerView
//
//    // 이력서 목록을 표시하는 어댑터를 담을 변수
//    private lateinit var dataAdapter: ResumeFragment.DataAdapter
//    // Fragment의 뷰를 생성하는 메서드
//    @SuppressLint("MissingInflatedId")
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // 뷰를 생성하여 반환하는 코드
//        val view = inflater.inflate(R.layout.fragment_resume, container, false)
//
//        // Argument로부터 전달받은 사용자 ID를 변수에 저장
//        if (arguments != null) {
//            userId = arguments?.getString("userId", "") ?: ""
//        }
//
//        // 사용자 ID를 표시할 TextView 초기화
//        val textID = view.findViewById<TextView>(R.id.tvID1)
//        textID.text = userId
//
//        // RecyclerView 초기화 후 빈 어댑터 설정
//        recyclerView = view.findViewById(R.id.recyclerviewResume)
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//
//        // 빈 어댑터 생성 및 RecyclerView에 설정
//        dataAdapter = ResumeFragment.DataAdapter(emptyList(), userId)
//        recyclerView.adapter = dataAdapter
//
//        // 작성중 이력서 개수와 작성완료 이력서 개수를 표시할 TextView 초기화
//        tvResume_temporary_count = view.findViewById(R.id.tvResume_temporary_count)
//        tvResume_complete_count = view.findViewById(R.id.tvResume_complete_count)
//
//        // 서버로 사용자 아이디를 전송하여 이력서 데이터를 가져오도록 요청
//        fetchDataFromServer()
//        // 이력서 추가 버튼 클릭 리스너 설정
//        buttonAddResume = view.findViewById<Button>(R.id.btnAddResume)
//        buttonAddResume.setOnClickListener {
//            val intent = Intent(requireContext(), ResumeWriteActivity::class.java)
//            intent.putExtra("userId", userId)
//            startActivity(intent)
//        }
//
//        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout_resume) // TabLayout ID
//        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager_resume) // ViewPager2 ID
//
//        val fragmentList = listOf(
//            ResumeCompleteFragment(),
//            ResumeTemporaryFragment()
//        )
//
//        val adapter = TabPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
//        viewPager.adapter = adapter
//
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            when (position) {
//                0 -> tab.text = "작성완료 (0)"
//                1 -> tab.text = "작성중 (0)"
//                else -> tab.text = ""
//            }
//        }.attach()
//
//        // 생성한 뷰 반환
//        return view
//
//    }
//
//    // Fragment의 뷰가 생성되었을 때 호출되는 메서드
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // 이력서 삭제 버튼 클릭 리스너 설정
//        dataAdapter.setOnDeleteClickListener(object : ResumeFragment.DataAdapter.OnDeleteClickListener {
//            override fun onDeleteClick(resumeData: ResumeData) {
//                // 서버에서 이력서 데이터 삭제 요청
//                deleteResumeFromServer(resumeData.resumeListNum)
//            }
//        })
//
//        // 사용자의 작성중 이력서 개수와 작성완료 이력서 개수를 업데이트
//        fetchDataFromServer()
//
//        // 일정 간격으로 서버에서 데이터를 가져오고 UI를 업데이트하는 작업 시작
////        handler.postDelayed(updateDataRunnable, updateIntervalMillis)
//
//        // 이력서 삭제 버튼 클릭 리스너 설정
//        setupDeleteButtonClickListener()
//    }// 서버로부터 데이터를 가져오는 메서드
//    private fun fetchDataFromServer() {
//        // 서버로 사용자 아이디를 전송하여 이력서 데이터를 가져오도록 요청
//        val phpUrl = "http://$IP_ADDRESS/android_resume_fragment.php"
//        val requestBody = FormBody.Builder()
//            .add("personal_id", userId)
//            .build()
//        val request = Request.Builder()
//            .url(phpUrl)
//            .post(requestBody)
//            .build()
//
//        val client = OkHttpClient()
//        client.newCall(request).enqueue(object : Callback {
//            override fun onResponse(call: Call, response: Response) {
//                // 서버로부터 응답을 받았을 때 호출되는 콜백 메서드
//                val responseData = response.body?.string()
//                Log.d("ServerResponse", responseData ?: "No response data")
//                if (responseData != null) {
//                    try {
//                        // JSON 파싱을 위해 Gson 객체 생성
//                        val gson = Gson()
//
//                        // 서버 응답 데이터를 담는 컨테이너 클래스로 파싱
//                        val dataListContainer = gson.fromJson(responseData, DataListContainer::class.java)
//
//                        // UI 업데이트는 메인 스레드에서 실행되어야 함
//                        requireActivity().runOnUiThread {
//                            // 가져온 이력서 데이터 리스트를 어댑터에 설정하여 RecyclerView 업데이트
//                            val dataList = dataListContainer?.resumeList
//                            if (dataList != null) {
//                                dataAdapter.setData(dataList)
//
//                                // 이력서 개수 설정
//                                val cnt1 = dataListContainer.cnt1
//                                val cnt2 = dataListContainer.cnt2
//
//                                // cnt1, cnt2가 null이 아닌지 확인하여 텍스트뷰에 업데이트
//                                if (cnt1 != null && cnt2 != null) {
//                                    tvResume_temporary_count.text = cnt1.toString()
//                                    tvResume_complete_count.text = cnt2.toString()
//                                } else {
//                                    // cnt1 또는 cnt2가 null인 경우에 대한 처리를 여기에 추가
//                                    Toast.makeText(view?.context, "이력서 개수를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
//                                }
//                            } else {
//                                // dataList가 null인 경우에 대한 처리를 여기에 추가
//                                Toast.makeText(view?.context, "서버로부터 이력서 데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    } catch (e: JSONException) {
//                        // JSON 파싱 오류 발생한 경우
//                        Log.e("JSONParsingError", "Invalid JSON format: $responseData")
//                    }
//                } else {
//                    // responseData가 null인 경우에 대한 처리를 여기에 추가
//                    Log.e("ServerResponse", "Response data is null")
//                    Toast.makeText(view?.context, "서버로부터 응답이 없습니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call, e: IOException) {
//                // 요청 실패 처리
//                e.printStackTrace()
//            }
//        })
//    }
//    // 이력서 삭제 버튼 클릭 리스너 설정
//    private fun setupDeleteButtonClickListener() {
//        dataAdapter.setOnDeleteClickListener(object : DataAdapter.OnDeleteClickListener {
//            override fun onDeleteClick(resumeData: ResumeData) {
//                // 서버에서 이력서 데이터 삭제 요청
//                deleteResumeFromServer(resumeData.resumeListNum)
//            }
//        })
//    }
//
//    // 서버로 이력서 삭제 요청
//    private fun deleteResumeFromServer(resumeListNum: Int?) {
//        // resumeListNum이 null인 경우에 대비하여 처리
//        if (resumeListNum == null) {
//            // null인 경우, 서버 요청을 보낼 수 없으므로 오류 처리
//            requireActivity().runOnUiThread {
//                Toast.makeText(view?.context, "잘못된 이력서 번호입니다.", Toast.LENGTH_SHORT).show()
//            }
//            return
//        }
//
//        // 다이얼로그를 생성하고 설정
//        val alertDialogBuilder = AlertDialog.Builder(requireContext())
//        alertDialogBuilder.setMessage("해당 이력서를\n삭제하시겠습니까?")
//        alertDialogBuilder.setPositiveButton("네") { _, _ ->
//            // "네"를 눌렀을 때 서버에서 이력서 데이터 삭제 요청
//            val phpUrl = "http://$IP_ADDRESS/android_resume_delete.php"
//            val requestBody = FormBody.Builder()
//                .add("personal_id", userId)
//                .add("resume_listnum", resumeListNum.toString())
//                .build()
//            val request = Request.Builder()
//                .url(phpUrl)
//                .post(requestBody)
//                .build()
//
//            val client = OkHttpClient()
//            client.newCall(request).enqueue(object : Callback {
//                override fun onResponse(call: Call, response: Response) {
//                    // 서버로부터 응답을 받았을 때 호출되는 콜백 메서드
//                    val responseData = response.body?.string()
//                    Log.d("DeleteResponse", responseData ?: "No response data")
//                    if (responseData != null && responseData.contains("Record deleted successfully")) {
//                        // 삭제 성공
//                        requireActivity().runOnUiThread {
//                            // RecyclerView에서 아이템 삭제
//                            dataAdapter.removeDataByListNum(resumeListNum)
//                            // 작성중 이력서 개수와 작성완료 이력서 개수 업데이트
//                            fetchDataFromServer()
//                            // 삭제 성공 메시지 출력
//                            Toast.makeText(view?.context, "이력서를 성공적으로 삭제했습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    } else {
//                        // 삭제 실패 또는 응답 데이터 오류
//                        requireActivity().runOnUiThread {
//                            Toast.makeText(view?.context, "이력서를 삭제했습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call, e: IOException) {
//                    // 요청 실패 처리
//                    e.printStackTrace()
//                    requireActivity().runOnUiThread {
//                        Toast.makeText(view?.context, "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            })
//        }
//        alertDialogBuilder.setNegativeButton("아니오") { dialog, _ ->
//            // "아니오"를 눌렀을 때 다이얼로그를 닫고 아무 작업도 하지 않음
//            dialog.dismiss()
//        }
//
//        // 다이얼로그를 표시
//        val alertDialog = alertDialogBuilder.create()
//        alertDialog.show()
//
//    }
//    // 이력서 데이터 클래스
//    data class ResumeData(val resumeListNum: Int, val resumeTitle: String, val writeStatus: String)
//
//    // 이력서 데이터 리스트 컨테이너 클래스
//    data class DataListContainer(
//        val resumeList: List<ResumeData> = emptyList(),
//        val cnt1: Int = 0,
//        val cnt2: Int = 0
//    )
//
//    // 이력서 목록을 표시하는 어댑터 클래스
//    class DataAdapter(private var dataList: List<ResumeData>,
//                      private val userId: String
//    ) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {
//
//        // 각각의 뷰를 보유하는 뷰홀더 클래스
//        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            val textViewTitle: Button = itemView.findViewById(R.id.tvResumeTitle)
//            val textViewStatus: TextView = itemView.findViewById(R.id.tvWriteStatus)
//            val buttonRemove: Button = itemView.findViewById(R.id.buttonRemove)
//            val buttonChange: Button = itemView.findViewById(R.id.buttonEdit)
//        }
//
//        // 뷰홀더 생성
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.resume_item, parent, false)
//            return ViewHolder(view)
//        }
//
//        // 삭제 버튼 클릭 리스너 인터페이스
//        interface OnDeleteClickListener {
//            fun onDeleteClick(resumeData: ResumeData)
//        }
//
//        private var deleteClickListener: OnDeleteClickListener? = null
//
//        // 삭제 버튼 클릭 리스너 설정
//        fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
//            deleteClickListener = listener
//        }
//
//        // 아이템 삭제 메서드
//        fun removeDataByListNum(resumeListNum: Int) {
//            val updatedList = dataList.toMutableList()
//            val position = updatedList.indexOfFirst { it.resumeListNum == resumeListNum }
//            if (position != -1) {
//                updatedList.removeAt(position)
//                dataList = updatedList
//                notifyItemRemoved(position)
//            }
//        }
//
//
//        // 뷰홀더의 뷰에 데이터를 바인딩
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            val data = dataList[position]
//            holder.textViewTitle.setText(data.resumeTitle)
//            holder.textViewStatus.text = data.writeStatus
//
//            // 삭제 버튼 클릭 리스너 설정
//            holder.buttonRemove.setOnClickListener {
//                deleteClickListener?.onDeleteClick(data)
//            }
//
//            // 수정 버튼 클릭 리스너 설정
//            holder.buttonChange.setOnClickListener {
//                val intent = Intent(holder.itemView.context, ResumeChangeActivity::class.java)
//                intent.putExtra("resumeListNum", data.resumeListNum)
//                intent.putExtra("userId", userId)
//                holder.itemView.context.startActivity(intent)
//            }
//
//            // 이력서 제목 버튼 클릭 리스너 설정
//            holder.textViewTitle.setOnClickListener {
//                val intent = Intent(holder.itemView.context, ResumeClickActivity::class.java)
//                intent.putExtra("resumeListNum", data.resumeListNum)
//                intent.putExtra("userId", userId)
//                holder.itemView.context.startActivity(intent)
//            }
//        }
//
//        // 전체 아이템 개수 반환
//        override fun getItemCount(): Int {
//            return dataList.size
//        }
//
//        // 외부에서 데이터를 설정할 수 있도록 setData() 함수 추가
//        fun setData(newDataList: List<ResumeData>) {
//            dataList = newDataList
//            notifyDataSetChanged()
//        }
//    }
//}