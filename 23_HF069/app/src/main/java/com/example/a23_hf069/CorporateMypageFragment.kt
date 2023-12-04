package com.example.a23_hf069

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

// CorporateMypageFragment 클래스 정의. Fragment를 상속받음.
class CorporateMypageFragment : Fragment() {

    // 사용자 ID를 저장할 변수
    private lateinit var userCompanyName: String

    // 이미지 선택 요청 코드. startActivityForResult 호출 시 사용됨.
    private val REQUEST_IMAGE_PICK = 1
    lateinit var notificationButton : Button
    lateinit var noticeButton: Button
    lateinit var faqButton : Button
    lateinit var logoutButton: Button

    // 프래그먼트 뷰가 생성될 때 호출되는 메서드.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_corporate_mypage.xml 레이아웃을 프래그먼트의 뷰로 인플레이트.
        return inflater.inflate(R.layout.fragment_corporate_mypage, container, false)
    }

    // 프래그먼트의 뷰가 완전히 생성된 후 호출되는 메서드.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Argument로부터 전달받은 사용자 ID를 변수에 저장
        if (arguments != null) {
            userCompanyName = arguments?.getString("userCompanyName", "") ?: ""
        }
        // 사용자 userCompanyName를 표시할 TextView 초기화
        val textName = view.findViewById<TextView>(R.id.tv_user_name)
        textName.text = userCompanyName

        // profileImageView라는 ID를 가진 ImageView를 찾아서 변수에 할당.
        val profileImageView = view.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profileImageView)

        val profileName = view.findViewById<LinearLayout>(R.id.profile_name_btn)

        // 회사정보수정화면으로 전환
        profileName.setOnClickListener{
            val fragment1 = CorporateProfileFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fl_container, fragment1)
                .addToBackStack(null)
                .commit()
        }
        // profileImageView를 클릭하면 실행될 클릭 리스너 설정.
        profileImageView.setOnClickListener {
            Log.d("permission","클릭됨")
            // 갤러리 열기
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)

        }
        notificationButton = view.findViewById<Button>(R.id.notificationButton)
        notificationButton.setOnClickListener(){// 알림 설정 프래그먼트로 전환
            val fragment_notification_settings = NotificationSettingsFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fl_container, fragment_notification_settings) // 프래그먼트 교체
                .addToBackStack(null) // 이전 프래그먼트로 돌아가기
                .commit()
        }
        noticeButton = view.findViewById<Button>(R.id.noticeButton)
        noticeButton.setOnClickListener {
            // NoticeActivity로 이동하는 코드
            val intent = Intent(requireContext(), NoticeActivity::class.java)
            startActivity(intent)
        }

        faqButton = view.findViewById<Button>(R.id.faqButton)
        faqButton.setOnClickListener(){// 자주 묻는 질문 프래그먼트로 전환
            val fragment_faq = FAQFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fl_container, fragment_faq) // 프래그먼트 교체
                .addToBackStack(null) // 이전 프래그먼트로 돌아가기
                .commit()
        }

        logoutButton = view.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener(){// C_login프래그먼트로 전환
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }

    // startActivityForResult로 시작된 액티비티의 결과를 받는 메서드.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 이미지 선택 요청 코드와 결과 코드가 OK인 경우.
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            // 선택된 이미지의 URI를 가져옴.
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImageUri)
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 160, 160, true)
                // profileImageView에 선택된 이미지를 설정.
                view?.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profileImageView)?.setImageBitmap(resizedBitmap)
            }

        }
    }
}
