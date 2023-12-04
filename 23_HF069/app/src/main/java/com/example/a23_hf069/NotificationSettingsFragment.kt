package com.example.a23_hf069

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

class NotificationSettingsFragment : Fragment() {
    // 데이터 클래스로 알림 아이템 정의
    public data class NotificationItem(val title: String, var isNotificationOn: Boolean = false) {
        // 아이템 클릭 이벤트를 처리하기 위한 인터페이스 정의
        interface OnClickListener {
            fun onNotificationItemClick(position: Int, item: NotificationItem)
        }

        // 클릭 이벤트 처리를 위한 리스너 변수
        var onClickListener: OnClickListener? = null
    }

    // 알림 아이템 리스트 생성
    private val notificationList = mutableListOf(
        NotificationItem("일자리 추천 매칭 공고 알림"),
        NotificationItem("관심기업 공고 알림"),
        NotificationItem("이력서 열람 기업 알림"),
        NotificationItem("스크랩 공고 알림"),
        NotificationItem("입사지원 현황 알림")
        // 필요에 따라 더 많은 아이템을 추가할 수 있습니다.
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃 파일을 뷰로 변환
        val rootView = inflater.inflate(R.layout.fragment_notification_settings, container, false)

        // ListView를 찾아서 어댑터를 설정
        val listView: ListView = rootView.findViewById(R.id.notification_settings_list)
        val adapter = NotificationAdapter()
        listView.adapter = adapter

        // 닫기 버튼 클릭 이벤트 설정
        val closeButton = rootView.findViewById<ImageView>(R.id.backButton)
        closeButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // 뷰 반환
        return rootView
    }

    // 알림 아이템 어댑터 클래스 정의
    inner class NotificationAdapter :
        ArrayAdapter<NotificationItem>(requireContext(), R.layout.list_item_notification, notificationList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            // 뷰 재활용 또는 새로 생성
            val itemView = convertView ?: LayoutInflater.from(context).inflate(
                R.layout.list_item_notification,
                parent,
                false
            )

            // 뷰의 요소들을 찾음
            val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
            val notificationImageView: Switch = itemView.findViewById(R.id.notificationImageView)

            // 현재 포지션의 아이템 가져오기
            val item = notificationList[position]
            titleTextView.text = item.title
            notificationImageView.isChecked = item.isNotificationOn
            notificationImageView.tag = position // 클릭 이벤트를 위해 포지션을 태그로 설정

            // 알림 아이콘 변경
            notificationImageView.setOnCheckedChangeListener { _, isChecked ->
                onNotificationImageClick(position, isChecked)
            }


            // 뷰 반환
            return itemView
        }
    }

    private fun onNotificationImageClick(position: Int, isChecked: Boolean) {
        val item = notificationList[position]
        item.isNotificationOn = isChecked

        // 아이템의 클릭 이벤트 리스너가 설정되어 있다면 호출
        item.onClickListener?.onNotificationItemClick(position, item)

        // 아이템에 따라 다른 Toast 메시지 표시
        val toastMessage = if (isChecked) {
            when (position) {
                0 -> "일자리 추천 매칭 공고 알림 켜짐"
                1 -> "관심기업 공고 알림 켜짐"
                2 -> "이력서 열람 기업 알림 켜짐"
                3 -> "스크랩 공고 알림 켜짐"
                4 -> "입사지원 현황 알림 켜짐"
                else -> ""
            }
        } else {
            when (position) {
                0 -> "일자리 추천 매칭 공고 알림 꺼짐"
                1 -> "관심기업 공고 알림 꺼짐"
                2 -> "이력서 열람 기업 알림 꺼짐"
                3 -> "스크랩 공고 알림 꺼짐"
                4 -> "입사지원 현황 알림 꺼짐"
                else -> ""
            }
        }

        showToast(toastMessage)
    }

    // Toast 메시지를 표시하는 함수
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}