package com.example.a23_hf069

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

object FragmentManagerHelper {
    // 프래그먼트를 교체하고 백 스택에 추가할지 여부를 설정하는 함수
    fun replaceFragment(
        fragmentManager: FragmentManager,  // 프래그먼트 매니저
        containerId: Int,  // 프래그먼트를 표시할 레이아웃 컨테이너의 ID
        fragment: Fragment,  // 교체할 프래그먼트
        addToBackStack: Boolean = true,  // 백 스택에 추가할지 여부 (기본값: true)
        tag: String? = null  // 프래그먼트를 식별하는 태그 (기본값: null)
    ) {
        val transaction = fragmentManager.beginTransaction()  // 프래그먼트 트랜잭션 시작
        transaction.replace(containerId, fragment, tag)  // 지정한 컨테이너에 프래그먼트를 교체
        if (addToBackStack) {
            transaction.addToBackStack(tag)  // 백 스택에 프래그먼트 추가
        }
        transaction.commit()  // 트랜잭션 적용
    }

    // 백 스택을 모두 제거하는 함수
    fun clearBackStack(fragmentManager: FragmentManager) {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
    // clearBackStack 함수 사용법
    // FragmentManagerHelper.clearBackStack(requireActivity().supportFragmentManager)
}
