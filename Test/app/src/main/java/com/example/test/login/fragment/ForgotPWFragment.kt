package com.example.test.login.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.test.databinding.FragmentForgotPWBinding

class ForgotPWFragment : Fragment() {

    private var _binding: FragmentForgotPWBinding? = null
    private val binding: FragmentForgotPWBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPWBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //返回按鈕
        binding.userCodeImageBack.greyBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.forgotLoginBtn.setOnClickListener {
            Toast.makeText(requireContext(), "End of simulation", Toast.LENGTH_LONG).show()
        }
    }

    //釋放記憶體
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ForgotPWFragment()
    }
}