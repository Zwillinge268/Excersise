package com.example.test.mainFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.MainActivity
import com.example.test.R
import com.example.test.mainFragment.adapter.HomeAdapter
import com.example.test.databinding.FragmentHomeBinding
import com.example.test.db.HomeDbHelper
import com.example.test.mainFragment.searchPage.SearchFragment
import com.example.test.module.Product
import com.example.test.dbUtil.OnlineDbUtil
import java.sql.Connection

class HomeFragment : Fragment() {
    //View Binding
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    private var mProduct: MutableList<Product> = mutableListOf()
    private lateinit var mAdapter: HomeAdapter
    private lateinit var mDbHelper: HomeDbHelper

    //onCreate -> onCreateView -> onViewCreated
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        //mDbHelper.insertData()
        //先載入及顯示所有資料
        searchProduct()
        OnlineDbUtil.register("{\"userName\":\"Test\",\"userPassword\":\"123\"},\"email\":\"test@test.com\"},\"point\":\"0\"},\"permission\":\"0\"}")
    }

    private fun initEvent() {
        mDbHelper = HomeDbHelper.getInstance(requireContext())!!

        //Get post
        val postTemp = OnlineDbUtil.getPost()
        if (postTemp == null) {
            Toast.makeText(requireContext(), getString(R.string.connection_fail), Toast.LENGTH_LONG)
                .show()
        } else MainActivity.POST_DATA = postTemp

        //Recycler View init
        mAdapter = HomeAdapter(this.requireContext(), mProduct, lifecycle)
        binding.homeRv.layoutManager = LinearLayoutManager(this.requireContext())
        binding.homeRv.addItemDecoration(
            DividerItemDecoration(
                this.requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        binding.homeRv.adapter = mAdapter

        //Scroll to top of the page
        binding.homeFloatButton.setOnClickListener {
            binding.homeRv.smoothScrollToPosition(0)
        }

        binding.homeSearch.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                setReorderingAllowed(true)
                add(R.id.main_fragment_view, SearchFragment.newInstance())
                addToBackStack(null)
                commit()
            }
        }
    }

    //從Product DB取出相關資料
    private fun searchProduct() {
        val queryProduct = OnlineDbUtil.queryProduct()
        if (queryProduct == null) {
            Toast.makeText(requireContext(), getString(R.string.connection_fail), Toast.LENGTH_LONG)
                .show()
        } else {
            mProduct = queryProduct
            mAdapter.refresh(mProduct)
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
        fun newInstance() = HomeFragment()
    }
}