package com.example.a23_hf069

import android.os.Bundle
import android.util.Xml
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.a23_hf069.databinding.FragmentRegionSelectionBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException
import java.io.StringReader
import org.xmlpull.v1.XmlPullParser


class RegionSelectionFragment : Fragment() {
    private lateinit var binding: FragmentRegionSelectionBinding
    private lateinit var regionAdapter1: ArrayAdapter<String> // 대분류 지역 리스트뷰에 대한 어댑터
    private lateinit var regionAdapter2: ArrayAdapter<String> // 중분류 지역 리스트뷰에 대한 어댑터
    private lateinit var regionListView1: ListView // 우측의 대분류 지역 리스트뷰
    private lateinit var regionListView2: ListView // 좌측의 중분류 지역 리스트뷰
    private val regionList1: MutableList<String> = mutableListOf() // 대분류 지역명을 담을 리스트
    private val regionList2: MutableList<String> = mutableListOf() // 중분류 지역명을 담을 리스트

    // 전체 카테고리에 표시될 지역 이름 리스트
    private val wholeRegionList: List<String> = listOf("서울 전체", "부산 전체", "대구 전체","인천 전체", "광주 전체", "대전 전체", "울산 전체", "세종 전체", "경기 전체", "충북 전체", "충남 전체", "전북 전체", "전남 전체", "경북 전체", "경남 전체", "제주 전체", "강원 전체" )
    private var selectedOneDepthRegion: String? = null// 선택한 oneDepth 지역명을 저장할 변수
    private var selectedRegionList: MutableList<String> =  mutableListOf() // 선택된 지역들을 저장할 리스트


    // ViewModel 생성
    private val sharedSelectionViewModel: SharedSelectionViewModel by activityViewModels()


    // 지역 목록을 불러오는 API의 기본 URL을 설정
    private val baseUrl =
        "http://openapi.work.go.kr/opi/commonCode/commonCode.do?returnType=XML&target=CMCD&authKey=WNLJYZLM2VZXTT2TZA9XR2VR1HK&dtlGb=1"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegionSelectionBinding.inflate(inflater, container, false)
        val rootView = binding.root

        // View 초기화
        val searchEditText = binding.tvSelectRegion
        regionListView1 = binding.lvSuperRegion
        regionListView2 = binding.lvMiddleRegion
        val regionSelcetButton = binding.btnRegionSelectComplete

        // ListView 초기화
        regionAdapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, regionList1)
        regionAdapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, regionList2)
        regionListView1.adapter = regionAdapter1
        regionListView2.adapter = regionAdapter2


        // 지역 목록 API 호출하여 regionList1,2에 결과 담기
        fetchRegionList()

        // regionListView1에서 대분류 지역 선택 시 이벤트 처리
        regionListView1.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedOneDepthRegion = regionAdapter1.getItem(position) ?: "" // 클릭된 아이템의 위치(position)을 기반으로 해당 아이템의 지역명을 가져옴
                updateRegionListView2() // 대분류 지역에 해당하는 중분류 지역을 보여주도록 ui 업데이트
            }

        // regionListView2에서 중분류 지역 선택 시 이벤트 처리
        regionListView2.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedRegion = regionAdapter2.getItem(position) // 선택한 지역 키워드
                if (selectedRegion != null) {
                    // 선택된 지역이 리스트에 이미 포함되어 있지 않은 경우에만 추가
                    if (!selectedRegionList.contains(selectedRegion)) {
                        selectedRegionList.add(selectedRegion)
                        updateSelectedRegionTextView() // 선택된 지역 목록을 보여주는 TextView를 업데이트
                    }
                }


            }

        // EditText에서 검색어 입력 시 이벤트 처리
        searchEditText.setOnEditorActionListener { _, _, _ ->
            val searchText = searchEditText.text.toString() // EditText에 입력된 텍스트를 가져와서 문자열로 변환
            filterRegionList(searchText) // 가져온 검색어를 사용하여 지역 목록을 필터링하고, 결과를 ListView에 반영
            true // 가져온 검색어를 사용하여 지역 목록을 필터링하고, 결과를 ListView에 반영
        }

        // drawableRight(검색 아이콘) 클릭 시 검색 이벤트 처리
        searchEditText.setOnTouchListener { _, event ->
            val drawableRight = 2 // Index of the drawableRight icon
            if (event.action == MotionEvent.ACTION_UP &&
                event.rawX >= (searchEditText.right - searchEditText.compoundDrawables[drawableRight].bounds.width())
            ) {
                val searchText = searchEditText.text.toString()
                filterRegionList(searchText)
                true
            } else {
                false
            }
        }

        regionSelcetButton.setOnClickListener {
            val selectedRegions = selectedRegionList.joinToString(", \n")
            sharedSelectionViewModel.selectedRegion = selectedRegions // 선택된 지역 정보를 ViewModel에 저장
            // &keyword= 로 검색할 지역들을 |로 연결
            val regionKeyword = selectedRegionList.joinToString ("|")
            sharedSelectionViewModel.keywordRegions = regionKeyword

            val WantedFilteringFragment = WantedFilteringFragment()
            FragmentManagerHelper.replaceFragment(
                requireActivity().supportFragmentManager,
                R.id.fl_container,
                WantedFilteringFragment
            )
        }

        return rootView
    } // onCreateView 함수 종료


    // 워크넷 api로부터 지역 정보를 가져오는 함수
    private fun fetchRegionList() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(baseUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showErrorToast()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val xmlString = response.body?.string()
                    parseRegionList(xmlString) // parsing하기
                } else {
                    showErrorToast()
                }
            }
        })
    }

    // xml에서 읽어온 지역정보를 parsing하여 regionList1,2에 저장
    private fun parseRegionList(xmlString: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.Default) {
                val xmlPullParser: XmlPullParser = Xml.newPullParser()
                xmlPullParser.setInput(StringReader(xmlString))

                var eventType = xmlPullParser.eventType
                var isOneDepth = false
                var isTwoDepth = false
                var isregionNm = false
                var oneDepthRegionName = ""
                var twoDepthRegionName = ""

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            when (xmlPullParser.name) {
                                // oneDepth를 시작할 때 twoDepth를 종료
                                "oneDepth" -> {
                                    isOneDepth = true
                                    isTwoDepth = false

                                }
                                // twoDepth를 시작할 때 oneDepth를 종료합니다.
                                "twoDepth" -> {
                                    isOneDepth = false
                                    isTwoDepth = true
                                }
                                "regionNm" -> {
                                    isregionNm = true
                                }
                            }
                        }
                        XmlPullParser.TEXT -> {
                            if (isOneDepth && isregionNm && xmlPullParser.text.trim().isNotEmpty()) {
                                oneDepthRegionName = xmlPullParser.text.trim()
                            }
                            if (isTwoDepth && isregionNm && xmlPullParser.text.trim().isNotEmpty()) {
                                twoDepthRegionName = xmlPullParser.text.trim()
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            when (xmlPullParser.name) {
                                "oneDepth" -> {
                                    if (oneDepthRegionName.isNotEmpty()) {
                                        regionList1.add(oneDepthRegionName)
                                        oneDepthRegionName = ""
                                        isOneDepth=false
                                    }
                                }
                                "twoDepth" -> {
                                    if (twoDepthRegionName.isNotEmpty()) {
                                        regionList2.add(twoDepthRegionName)
                                        twoDepthRegionName = ""
                                        isTwoDepth=false
                                    }
                                }
                                "regionNm" -> {
                                    isregionNm=false
                                }
                            }
                        }
                    }

                    eventType = xmlPullParser.next()
                }
            }

            regionAdapter1.notifyDataSetChanged()
            regionList1.set(0, "전체")
        }
    }

    // 중분류 지역 리스트뷰 UI 업데이트
    private fun updateRegionListView2() {
        val selectedRegion = selectedOneDepthRegion
        // regionListView1에서 아무것도 선택되지 않았을 때, regionAdapter2를 비우고 리턴 -> 널체크
        if (selectedRegion == null) {
            regionAdapter2.clear()
            regionAdapter2.notifyDataSetChanged()
            return
        }

        // 선택한 oneDepth 지역명에 해당하는 twoDepth 지역명들을 필터링하여 가져오기
        val filteredTwoDepthRegions = regionList2.filter { region ->
            val regionWords = region.split(" ") // 띄어쓰기 등으로 문자열 분리
            val selectedWords = selectedRegion.split(" ")
            regionWords.firstOrNull() == selectedWords.firstOrNull() // 앞 단어 기준으로 포함 (ex) 경기 광주 -> 광주가 아닌 경기가 기준
        }.distinct()

        // 대분류에서 전체를 누른 경우 해당하는 중분류 보여주기
        if(selectedRegion.equals("전체")){
            regionAdapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, wholeRegionList)
            regionListView2.adapter = regionAdapter2

        }
        else{ // 대분류에서 전체를 제외한 나머지 지역을 누른 경우
            // regionAdapter2를 새로운 리스트로 갱신합니다. **** cf**** filter 한 뒤에 notifychange를 하게되면 regionList2에 filter 내용이 적용되어 리스트가 비게됨
            regionAdapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, filteredTwoDepthRegions)
            regionListView2.adapter = regionAdapter2 // 해당 지역에 속하는 중분류 지역 보여주기
        }


    }

    // fragment_region_selection에서 선택한 지역 키워드를 화면에 표출
    private fun updateSelectedRegionTextView(){
        val selectedRegions = selectedRegionList.joinToString(", \n") // selectedRegionList의 모든 항목을 하나의 문자열로 합침
        binding.tvSelectedRegion.text = selectedRegions // selectedRegions에서 만들어진 문자열을 tvSelectedRegion의 텍스트로 설정하여 선택된 지역들을 화면에 표시

    }

    // ArrayAdapter에서 제공하는 filter를 이용하여 입력된 검색어를 포함하는 지역만 표시
    private fun filterRegionList(searchText: String) {
        regionAdapter1.filter.filter(searchText)

    }

    private fun showErrorToast() {
        Toast.makeText(requireContext(), "Failed to fetch region list.", Toast.LENGTH_SHORT).show()
    }
}