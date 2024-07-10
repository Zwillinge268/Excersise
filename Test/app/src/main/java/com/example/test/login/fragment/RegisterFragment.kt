package com.example.test.login.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.test.R
import com.example.test.databinding.FragmentRegisterBinding
import com.example.test.db.HomeDbHelper
import com.example.test.dbUtil.OnlineDbUtil
import com.example.test.login.LoginUtil.Companion.REGISTER_EMAIL_EXIST
import com.example.test.login.LoginUtil.Companion.REGISTER_USERNAME_EXIST

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding get() = _binding!!
    private lateinit var mDbHelper: HomeDbHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDbHelper = HomeDbHelper.getInstance(requireContext())!!

        //Button listener
        binding.registerRegisterBtn.setOnClickListener { checkRule() }
        binding.registerLoginBtn.setOnClickListener { backLoginPage() }

        //返回按鈕
        binding.userCodeImageBack.greyBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    //返回Login Fragment
    private fun backLoginPage() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun checkRule() {
        //關閉鍵盤
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )

        val userName = binding.registerIdRt.text.toString()
        val password = binding.registerPasswordRt.text.toString()
        val email = binding.registerEmailRt.text.toString()

        //設定用戶名及密碼規則
        var message = ""
        //if (userName.length in 3..20) message += getString(R.string.register_name_length)
        //if (password.length in 6..20) message += getString(R.string.register_password_length)
//        if (!password.contains(Regex("[a-z]")) || !password.contains(Regex("[A-Z]"))
//            || !password.contains(Regex("[0-9]"))
//        ) message += getString(R.string.register_complexity_require)
        if (binding.registerPasswordRt.text.toString() != binding.registerConfirmRt.text.toString())
            message += getString(R.string.register_password_wrong_input)

        if (message.isBlank()) checkAccount(userName, password, email)
        else binding.registerMessage.text = message
    }

    private fun checkAccount(userName: String, password: String, email: String) {
        //檢查賬號資料是否存在
        when (OnlineDbUtil.checkUserExist(userName, email)) {
            REGISTER_USERNAME_EXIST -> binding.registerMessage.text =
                getString(R.string.register_user_exist)

            REGISTER_EMAIL_EXIST -> binding.registerMessage.text =
                getString(R.string.register_email_exist)

            else -> {
                //檢查賬號資料是否儲存成功
                if (mDbHelper.register(userName, password, email) != -1L) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.register_success),
                        Toast.LENGTH_LONG
                    ).show()
                    backLoginPage()
                } else binding.registerMessage.text = getString(R.string.register_fail)
            }
        }
    }

    //釋放記憶體
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegisterFragment()
    }
}