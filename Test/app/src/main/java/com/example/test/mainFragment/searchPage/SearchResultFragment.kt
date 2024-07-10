package com.example.test.mainFragment.searchPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.R
import com.example.test.databinding.FragmentSearchResultBinding
import com.example.test.db.HomeDbHelper
import com.example.test.module.Product
import com.example.test.dbUtil.OnlineDbUtil

private const val KEYWORD = "keyword"
private const val TYPE = "type"
private const val MIN_PRICE = "minPrice"
private const val MAX_PRICE = "maxPrice"

class SearchResultFragment : Fragment() {
    private var _binding: FragmentSearchResultBinding? = null
    private val binding: FragmentSearchResultBinding get() = _binding!!

    private var mProduct: MutableList<Product> = mutableListOf()
    private lateinit var mAdapter: SearchResultAdapter
    private lateinit var mDbHelper: HomeDbHelper

    private var keyWord: String? = null
    private var type: String? = null
    private var minPrice: String? = null
    private var maxPrice: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            keyWord = it.getString(KEYWORD)
            type = it.getString(TYPE)
            minPrice = it.getString(MIN_PRICE)
            maxPrice = it.getString(MAX_PRICE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initEvent()
    }

    private fun initView() {
        mDbHelper = HomeDbHelper.getInstance(requireContext())!!

        //Recycler View init
        mAdapter = SearchResultAdapter(this.requireContext(), mProduct)
        binding.searchResultRv.layoutManager = LinearLayoutManager(this.requireContext())
        binding.searchResultRv.addItemDecoration(
            DividerItemDecoration(
                this.requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        binding.searchResultRv.adapter = mAdapter
        searchProduct()  //Do search

        //Show what user search
        val searchBoxHint =
            getString(R.string.search_result_title) + getString(R.string.string_resource, keyWord)
        binding.searchResultTitle.text = searchBoxHint
    }

    private fun initEvent() {
        //Back button
        binding.searchResultBack.greyBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun searchProduct() {
        if (type == getString(R.string.search_type_all)) type = null
        val queryProduct = OnlineDbUtil.queryProduct(keyWord, type, minPrice, maxPrice)
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
        fun newInstance(search: String, type: String, minPrice: String, maxPrice: String) =
            SearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString(KEYWORD, search)
                    putString(TYPE, type)
                    putString(MIN_PRICE, minPrice)
                    putString(MAX_PRICE, maxPrice)
                }
            }
    }
}