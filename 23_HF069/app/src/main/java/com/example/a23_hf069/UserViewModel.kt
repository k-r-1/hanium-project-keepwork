package com.example.a23_hf069

import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    private var _userId: String? = null // 사용자 ID를 저장할 변수
    private var _userName: String? = null // 사용자 이름을 저장할 변수

    val userId: String?
        get() = _userId

    val userName: String?
        get() = _userName

    // 추가: ViewModel을 초기화할 때 _userId를 초기화
    init {
        _userId = ""
    }

    // 사용자 이름 설정 함수
    fun setUserName(name: String) {
        _userName = name
    }

    fun setUserId(id: String) {
        _userId = id
    }
}
