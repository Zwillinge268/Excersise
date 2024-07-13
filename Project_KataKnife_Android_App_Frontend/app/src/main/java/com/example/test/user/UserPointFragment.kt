package com.example.test.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.test.MainActivity
import com.example.test.MainVPAdapter
import com.example.test.R
import com.example.test.databinding.FragmentUserPointBinding

class UserPointFragment : Fragment() {
    private var _binding: FragmentUserPointBinding? = null
    private val binding: FragmentUserPointBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserPointBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //返回按鈕
        binding.userCodeImageBack.greyBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.userPointPoint.text =
            getString(R.string.user_point_data, MainActivity.USER_DATA.point)
        binding.userPointShopping.setOnClickListener {
            val activity = requireActivity()
            //Set activity element in fragment
            activity.findViewById<ViewPager2>(R.id.main_viewpager).currentItem =
                MainVPAdapter.HOME
            activity.supportFragmentManager.popBackStack()
        }
        binding.userPointDescribe.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.end_of_simulation),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //釋放記憶體
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserPointFragment()
    }
}