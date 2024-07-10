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
import com.example.test.databinding.FragmentLoginBinding
import com.example.test.login.LoginUtil

class LoginFragment(private val updatePage: UpdatePage) : Fragment() {

    //view binding
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    private fun initEvent() {
        //返回按鈕
        binding.loginImageBack.greyBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        //btn onClick listener
        binding.loginForgotBtn.setOnClickListener { toPage(ForgotPWFragment.newInstance()) }
        binding.loginRegisterBtn.setOnClickListener { toPage(RegisterFragment.newInstance()) }
        binding.loginLoginBtn.setOnClickListener { login() }
    }

    //switch to other fragment
    private fun toPage(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            .setReorderingAllowed(true).hide(this)
            .add(R.id.main_fragment_view, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun login() {
        //關閉鍵盤
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )

        val userId = binding.loginIdRt.text.toString()
        val password = binding.loginPasswordRt.text.toString()

        //Check ac & pw in db
        when (LoginUtil(requireContext()).userLogin(userId, password)) {
            //Login success
            LoginUtil.SUCCESS -> {
                //use shared preferences to save data
                val sharedPreferences = requireContext().getSharedPreferences(
                    "com.example.test.REMEMBER",
                    Context.MODE_PRIVATE
                )
                //check if remember me
                if (binding.loginRemember.isChecked) {
                    //write into shared preferences file
                    sharedPreferences.edit().apply {
                        putString("userId", userId)
                        putString("userPassword", password)
                        putBoolean("remember", true)
                        apply()
                    }
                } else sharedPreferences.edit().clear().apply()
                toast(getString(R.string.login_welcome, userId))
                updatePage.doUpdate()
                requireActivity().supportFragmentManager.popBackStack()
            }

            //login fail
            LoginUtil.INVALID_ID -> toast(getString(R.string.login_wrong_id))
            LoginUtil.INVALID_PASSWORD -> toast(getString(R.string.login_wrong_password))
            LoginUtil.AC_DISABLE -> toast(getString(R.string.login_banned_account))
        }
    }

    interface UpdatePage {
        fun doUpdate()
    }

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    //釋放記憶體
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(updatePage: UpdatePage) = LoginFragment(updatePage)
    }
}