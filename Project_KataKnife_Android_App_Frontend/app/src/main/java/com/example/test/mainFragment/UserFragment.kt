package com.example.test.mainFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import coil.load
import com.example.test.MainActivity
import com.example.test.R
import com.example.test.databinding.FragmentUserBinding
import com.example.test.login.fragment.LoginFragment
import com.example.test.module.User
import com.example.test.user.UserAboutUsFragment
import com.example.test.user.UserCodeFragment
import com.example.test.user.UserDetailFragment
import com.example.test.user.UserPointFragment


class UserFragment : Fragment(), LoginFragment.UpdatePage {
    //View Binding
    private var _binding: FragmentUserBinding? = null
    private val binding: FragmentUserBinding get() = _binding!!

    //onCreate -> onCreateView -> onViewCreated
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        doUpdate()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() {
        //To Login page or to user detail page (if already login)
        binding.userName.setOnClickListener {
            toMemberFunction(UserDetailFragment::class.java)
        }

        // 會員功能
        binding.userCode.setOnClickListener {
            toMemberFunction(UserCodeFragment::class.java)
        }
        binding.userPoint.setOnClickListener {
            toMemberFunction(UserPointFragment::class.java)
        }
        binding.userAboutUs.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.end_of_simulation),
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.userChangeData.setOnClickListener {
            toMemberFunction(UserDetailFragment::class.java)
        }
        binding.userLogout.setOnClickListener { logout() }
        binding.userOurWeb.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.end_of_simulation),
                Toast.LENGTH_SHORT
            ).show()
        }

        requireActivity().supportFragmentManager.setFragmentResultListener("requestKey", this
        ) { _, result -> // 當Fragment1返回結果時，執行這個方法
            if (result.getInt("Done", 0) == 1) doUpdate()
        }
    }

    private fun toMemberFunction(fragment: Class<out Fragment>) {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            setReorderingAllowed(true)
            if (!MainActivity.USER_DATA.isLogin) {
                add(R.id.main_fragment_view, LoginFragment.newInstance(this@UserFragment), "login")
            } else add(R.id.main_fragment_view, fragment.newInstance())
            addToBackStack(null)
            commit()
        }
    }


    private fun logout() {
        MainActivity.USER_DATA = User()
        doUpdate()
        Toast.makeText(requireContext(), getString(R.string.user_logout_success), Toast.LENGTH_LONG)
            .show()

        //Stop auto login
        val sharedPreferences = requireContext().getSharedPreferences(
            "com.example.test.REMEMBER",
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit().clear().apply()
    }

    //Page element init / setting (only run in created & after login & logout)
    override fun doUpdate() {
        if (MainActivity.USER_DATA.isLogin) {
            binding.userName.text = MainActivity.USER_DATA.userName
            binding.userNo.text = getString(R.string.user_no_data, MainActivity.USER_DATA.userId)
            binding.userLogout.visibility = View.VISIBLE
            if (MainActivity.USER_DATA.icon == null)
                binding.userIcon.load(R.drawable.user_icon_default)
            else binding.userIcon.setImageBitmap(MainActivity.USER_DATA.icon)
        } else {
            binding.userName.text = getString(R.string.user_click_login)
            binding.userNo.text = ""
            binding.userLogout.visibility = View.INVISIBLE
            binding.userIcon.load(R.drawable.user_icon_default)
        }
    }

    //釋放記憶體
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        //創建函數
        fun newInstance() = UserFragment()
    }
}