package com.example.a23_hf069

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class FindPersonalIdPhoneFragment : Fragment() {

    private var IP_ADDRESS = "54.180.186.168" // 실행중인 인스턴스의 IP 주소

    private lateinit var tiedtName: TextInputEditText
    private lateinit var tiedtPhone: TextInputEditText
    private lateinit var btnFindId: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_find_personal_id_phone, container, false)

        tiedtName = rootView.findViewById(R.id.tiedtName_findpid_phone)
        tiedtPhone = rootView.findViewById(R.id.tiedtPhone_findpid)
        btnFindId = rootView.findViewById(R.id.btnFindPid_phone)

        btnFindId.setOnClickListener {
            val personal_name = tiedtName.text.toString().trim()
            val personal_phonenum = tiedtPhone.text.toString().trim()

            val serverUrl = "http://$IP_ADDRESS/android_find_personal_id_phone.php"

            val params = hashMapOf(
                "personal_name" to personal_name,
                "personal_phonenum" to personal_phonenum
            )

            GlobalScope.launch(Dispatchers.IO) {
                val response = sendPostRequest(serverUrl, params)
                withContext(Dispatchers.Main) {
                    if (personal_name.isEmpty() || personal_phonenum.isEmpty()) {
                        Toast.makeText(view?.context, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
                    } else {
                        if (response.isNotEmpty()) {
                            showDialogWithId(response)
                        } else {
                            showNomatchingIdDialog()
                        }
                    }
                }
                // 예시: 통신 시작 로그
                Log.d("Communication", "Sending request to server...")

                // 예시: 서버 응답 로그
                Log.d("Communication", "Server response received: $response")
            }
        }
        return rootView
    }

    private suspend fun sendPostRequest(url: String, params: HashMap<String, String>): String {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val requestBody = FormBody.Builder()
            for((key, value) in params) {
                requestBody.add(key, value)
            }
            val request = Request.Builder()
                .url(url)
                .post(requestBody.build())
                .build()

            val response = client.newCall(request).execute()
            response.body?.string() ?: ""

        }
    }

    private fun showDialogWithId(id: String) {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("<아이디 찾기 성공>")
        dialog.setMessage("아이디는  \" $id \"  입니다")
        dialog.setPositiveButton("확인") {_, _ ->
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
        dialog.show()
    }

    private fun showNomatchingIdDialog() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("<아이디 찾기 실패>")
        dialog.setMessage("입력한 정보와 일치하는 아이디가 \n존재하지 않습니다.")
        dialog.setPositiveButton("확인", null)
        dialog.show()
    }
}