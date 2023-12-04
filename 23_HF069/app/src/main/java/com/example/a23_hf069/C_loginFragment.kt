import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.a23_hf069.C_MemberModel
import com.example.a23_hf069.CorporateHomeActivity
//import com.example.a23_hf069.CorporateSignUpActivity
import com.example.a23_hf069.FindCorporateIdActivity
import com.example.a23_hf069.R
import com.example.a23_hf069.RetrofitInterface
import com.example.a23_hf069.UserViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class C_loginFragment : Fragment() {
    private lateinit var userViewModel: UserViewModel
    lateinit var login: Button
    lateinit var signUp: Button
    lateinit var btnFindId: Button
    private lateinit var id: String
    private lateinit var id_text_input_edit_text: EditText
    private lateinit var password_text_input_edit_text: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_c_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UserViewModel 초기화
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)


        login = view.findViewById<Button>(R.id.login_btn)
        signUp = view.findViewById<Button>(R.id.signUp_btn)
        btnFindId = view.findViewById<Button>(R.id.findID_btn)

        id_text_input_edit_text = view.findViewById<EditText>(R.id.id_text)
        password_text_input_edit_text = view.findViewById<EditText>(R.id.pw_text)

        login.setOnClickListener() {
            id = id_text_input_edit_text.text.toString().trim()
            val password = password_text_input_edit_text.text.toString().trim()

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val retrofit = Retrofit.Builder()
                    .baseUrl(RetrofitInterface.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(RetrofitInterface::class.java)

                apiService.getCorporateData(id).enqueue(object : Callback<C_MemberModel> {
                    override fun onResponse(call: Call<C_MemberModel>, response: Response<C_MemberModel>) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            var isLoginSuccessful = false // 로그인 성공 여부를 체크하기 위한 변수

                            if (result != null) {
                                isLoginSuccessful = true // 아이디와 비밀번호가 일치할 때 플래그를 true로 설정
                                // 데이터 저장
                                val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("userName", result.company_name)
                                editor.apply()
                                val intent = Intent(requireActivity(), CorporateHomeActivity::class.java)
                                /*intent.putExtra("userCompanyName", data.company_name)*/
                                /*intent.putExtra("userCompanyId", data.company_id)*/
                                startActivity(intent)
                            }

                            if (!isLoginSuccessful) {
                                // 로그인 성공 여부에 따라 메시지를 표시
                                Toast.makeText(requireContext(), "아이디 또는 비밀번호를 잘못 입력했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<C_MemberModel>, t: Throwable) {
                        Toast.makeText(
                            requireContext(),
                            "통신 오류: " + t.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

            }
        }

        signUp.setOnClickListener() {
//            val intent = Intent(getActivity(), CorporateSignUpActivity::class.java)
//            startActivity(intent)
        }

        btnFindId.setOnClickListener {
            val intent = Intent(getActivity(), FindCorporateIdActivity::class.java)
            startActivity(intent)
        }
    }
}
