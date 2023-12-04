package com.example.a23_hf069

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer


class WantedResultFragment : Fragment() {
    private lateinit var listView: ListView
    //viewModel 생성 (단, var로 선언하면 안됨)
    private val sharedSelectionViewModel: SharedSelectionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ListView를 보여줄 레이아웃 파일을 연결
        val rootView = inflater.inflate(R.layout.fragment_wanted_result, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.listView)

        // LiveData를 관찰하여 데이터 변경이 있을 때마다 UI 업데이트를 수행합니다.
        sharedSelectionViewModel._filteredList.observe(viewLifecycleOwner, Observer { filteredList ->
            val adapter = WantedListAdapter(requireContext(), filteredList)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()
            // 리스트 아이템 클릭 이벤트 처리
            listView.setOnItemClickListener { _, _, position, _ ->
                val wanted = filteredList[position]
                val job = Job(
                    wanted.company,
                    wanted.title,
                    wanted.salTpNm,
                    wanted.sal,
                    wanted.region,
                    wanted.holidayTpNm,
                    wanted.minEdubg,
                    wanted.career,
                    wanted.closeDt,
                    wanted.wantedMobileInfoUrl,
                    wanted.jobsCd,
                    wanted.infoSvc
                )
                val intent = JobDetailActivity.newIntent(requireContext(), job)
                startActivity(intent)
            }
        })
    }
    class WantedListAdapter(context: Context, private val wantedList: List<WantedFilteringFragment.Wanted>) :
        ArrayAdapter<WantedFilteringFragment.Wanted>(context, R.layout.wanted_list_item, wantedList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var itemView = convertView
            if (itemView == null) {
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                itemView = inflater.inflate(R.layout.wanted_list_item, parent, false)
            }

            val titleTextView: TextView = itemView?.findViewById(R.id.tv_title) ?: throw NullPointerException("tv_title not found in the layout")
            val companyTextView: TextView = itemView?.findViewById(R.id.tv_company) ?: throw NullPointerException("tv_company not found in the layout")
            val closeDtTextView: TextView = itemView?.findViewById(R.id.tv_closeDt) ?: throw NullPointerException("tv_any not found in the layout")

            val currentItem = wantedList[position]
            titleTextView.text = currentItem.title
            companyTextView.text = currentItem.company
            closeDtTextView.text = currentItem.closeDt

            return itemView
        }
    }
}
