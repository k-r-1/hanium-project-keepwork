package com.example.a23_hf069

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class P_loginFragment : Fragment() { //개인로그인

    private lateinit var id_text_input_edit_text: EditText // id
    private lateinit var password_text_input_edit_text: EditText // password
    private lateinit var id: String // 사용자 아이디
    lateinit var btnlogin: Button // 로그인 버튼
    lateinit var btnFindId: Button // 아이디 찾기 버튼
    lateinit var btnSignUp: Button // 회원가입 버튼

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_p_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnlogin = view.findViewById<Button>(R.id.login_btn)
        btnFindId = view.findViewById<Button>(R.id.findID_btn)
        btnSignUp = view.findViewById<Button>(R.id.signUp_btn)

        id_text_input_edit_text = view.findViewById<EditText>(R.id.id_text)
        password_text_input_edit_text = view.findViewById<EditText>(R.id.pw_text)

        btnlogin.setOnClickListener() {
            id = id_text_input_edit_text.text.toString().trim()
            val password = password_text_input_edit_text.text.toString().trim()

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // Retrofit 객체 생성
                val retrofit = Retrofit.Builder()
                    .baseUrl(RetrofitInterface.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                // RetrofitInterface 인터페이스 구현
                val apiService = retrofit.create(RetrofitInterface::class.java)

                apiService.getData(id).enqueue(object : Callback<P_MemberModel> {
                    override fun onResponse(call: Call<P_MemberModel>, response: Response<P_MemberModel>) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                // 로그인 성공
                                Toast.makeText(requireContext(), "로그인 성공", Toast.LENGTH_SHORT).show()

                                // 뷰 모델에 저장
                                val userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
                                userViewModel.setUserId(id) // userId 설정

                                // 로그인 성공 시 homeactivity로 전환
                                val intent = Intent(requireActivity(), HomeActivity::class.java)
                                intent.putExtra("userId", id)
                                startActivity(intent)
                            }
                        }
                    }

                    override fun onFailure(call: Call<P_MemberModel>, t: Throwable) {
                        // 통신 실패 처리
                        Toast.makeText(
                            requireContext(),
                            "통신 오류: " + t.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }

        btnSignUp.setOnClickListener() {
            // 회원 가입 버튼을 눌렀을 때 회원 가입 화면으로 이동
//            val intent = Intent(getActivity(), PersonalSignUpActivity::class.java)
//            startActivity(intent)
        }

        btnFindId.setOnClickListener {
            // findId 버튼을 클릭하면 FindPersonalIdActivity로 전환
            val intent = Intent(getActivity(), FindPersonalIdActivity::class.java)
            startActivity(intent)
        }
    }
}


//package com.example.a23_hf069
//
//import android.app.ProgressDialog
//import android.content.Intent
//import android.os.AsyncTask
//import android.os.Bundle
//import android.text.method.ScrollingMovementMethod
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.fragment.app.Fragment
//import java.io.BufferedReader
//import java.io.InputStream
//import java.io.InputStreamReader
//import java.net.HttpURLConnection
//import java.net.URL
//
//
//class P_loginFragment : Fragment() { //개인로그인
//    private var IP_ADDRESS = "54.180.186.168" // 본인 IP주소를 넣으세요.
//
//    private var TAG = "phptest" // phptest log 찍으려는 용도
//    private lateinit var id_text_input_edit_text: EditText // id
//    private lateinit var password_text_input_edit_text: EditText // password
//    private lateinit var id: String // 사용자 아이디
//    lateinit var btnlogin : Button // 로그인 버튼
//    lateinit var btnFindId : Button // 아이디 찾기 버튼
//    lateinit var btnSignUp : Button // 회원가입 버튼
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View?
//    {
//        return inflater.inflate(R.layout.fragment_p_login, container, false)
//    }
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        btnlogin = view.findViewById<Button>(R.id.login_btn)
//        btnFindId = view.findViewById<Button>(R.id.findID_btn)
//        btnSignUp = view.findViewById<Button>(R.id.signUp_btn)
//
//        id_text_input_edit_text = view.findViewById<EditText>(R.id.id_text)
//        password_text_input_edit_text = view.findViewById<EditText>(R.id.pw_text)
//
//        btnlogin.setOnClickListener() {
//
//            id = id_text_input_edit_text.text.toString().trim()
//            val password = password_text_input_edit_text.text.toString().trim()
//
//            if (id.isEmpty() || password.isEmpty()) {
//                Toast.makeText(requireContext(), "정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
//            } else {
//                val task = SelectData()
//                task.execute("http://$IP_ADDRESS/android_login_php.php", id, password)
//            }
//        }
//
//        btnFindId.setOnClickListener {
//            // findId 버튼을 클릭하면 FindPersonalIdActivity로 전환
//            val intent = Intent(getActivity(), FindPersonalIdActivity::class.java)
//            startActivity(intent)
//        }
//
//        btnSignUp.setOnClickListener() {
//            // signUp버튼을 클릭하면 CorporateSignUpActivity로 전환
//            val intent = Intent(getActivity(), PersonalSignUpActivity::class.java)
//            startActivity(intent)
//        }
//    }
//
//    inner class SelectData : AsyncTask<String, Void, String>() {
//        private var progressDialog: ProgressDialog? = null
//        override fun doInBackground(vararg params: String): String {
//            val serverURL = params[0]
//            val userid = params[1]
//            val userpw = params[2]
//
//            val postParameters = "personal_id=$userid&personal_password=$userpw"
//
//            try {
//                val url = URL(serverURL)
//                val httpURLConnection = url.openConnection() as HttpURLConnection
//
//                httpURLConnection.readTimeout = 5000
//                httpURLConnection.connectTimeout = 5000
//                httpURLConnection.requestMethod = "POST"
//                httpURLConnection.connect()
//
//                val outputStream = httpURLConnection.outputStream
//                outputStream.write(postParameters.toByteArray(charset("UTF-8")))
//                outputStream.flush()
//                outputStream.close()
//
//                val responseStatusCode = httpURLConnection.responseCode
//                Log.d(TAG, "POST response code - $responseStatusCode")
//
//                val inputStream: InputStream
//                inputStream = if (responseStatusCode == HttpURLConnection.HTTP_OK) {
//                    httpURLConnection.inputStream
//                } else {
//                    httpURLConnection.errorStream
//                }
//
//                val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
//                val bufferedReader = BufferedReader(inputStreamReader)
//                val sb = StringBuilder()
//                var line: String? = null
//
//                while (bufferedReader.readLine().also { line = it } != null) {
//                    sb.append(line)
//                }
//
//                bufferedReader.close()
//                Log.d("php 값 :", sb.toString())
//                return sb.toString()
//            } catch (e: Exception) {
//                Log.d(TAG, "SelectData: Error", e)
//                return "Error " + e.message
//            }
//        }
//
//        override fun onPostExecute(result: String) {
//            super.onPostExecute(result)
//
//            if (result == "success") {
//                // 로그인 성공
//                Toast.makeText(view?.context, "로그인 성공", Toast.LENGTH_SHORT).show()
//                // 로그인 성공 시 homeactivity로 전환
//                val intent = Intent(requireActivity(), HomeActivity::class.java)
//                intent.putExtra("userId", id) // 아이디를 Intent에 추가 (사용자 아이디를 HomeActivity로 넘김)
//                startActivity(intent)
//            } else {
//                // 로그인 실패
//                Toast.makeText(view?.context, "로그인 실패", Toast.LENGTH_SHORT).show()
//
//                // 추후 삭제할 코드
//                val intent = Intent(getActivity(), HomeActivity::class.java)
//                intent.putExtra("userId", "test") // 아이디를 Intent에 추가 (사용자 아이디를 HomeActivity로 넘김)
//                startActivity(intent)
//            }
//        }
//    }
//}