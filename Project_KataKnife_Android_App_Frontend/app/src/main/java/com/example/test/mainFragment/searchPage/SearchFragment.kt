package com.example.test.mainFragment.searchPage

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import com.example.test.R
import com.example.test.databinding.FragmentSearchBinding
import com.example.test.db.HomeDbHelper
import com.example.test.util.OnlineDbUtil

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding get() = _binding!!
    private lateinit var mDbHelper: HomeDbHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        binding.searchGo.setOnClickListener {
            //關閉鍵盤
            (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                requireActivity().currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )

            val keyWord = binding.searchInput.text.toString()
            val type = binding.searchType.selectedItem.toString()
            val min = binding.searchMin.text.toString()
            val max = binding.searchMax.text.toString()

            requireActivity().supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                setReorderingAllowed(true)
                add(
                    R.id.main_fragment_view,
                    SearchResultFragment.newInstance(keyWord, type, min, max)
                )
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun initView() {
        //Get product type for spinner
        var dataList = OnlineDbUtil.getProductType()
        //Null = Connection failed , Empty if online db data missing
        if (dataList.isNullOrEmpty()) dataList = mutableListOf()
        //Add hint
        dataList.add(0,getString(R.string.search_type_all))
        val adapter = ArrayAdapter(requireContext(), R.layout.search_spinner_item, dataList)
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.searchType.adapter = adapter
    }

    //釋放記憶體
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}