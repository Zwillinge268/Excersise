package com.example.test.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.test.databinding.FragmentUserAboutUsBinding
import com.example.test.databinding.FragmentUserPointBinding

class UserAboutUsFragment : Fragment() {

    private var _binding: FragmentUserAboutUsBinding? = null
    private val binding: FragmentUserAboutUsBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserAboutUsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserAboutUsFragment()
    }
}