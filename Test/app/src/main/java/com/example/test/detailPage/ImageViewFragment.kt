package com.example.test.detailPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.test.R
import com.example.test.databinding.FragmentImageViewBinding
import com.example.test.detailPage.adapter.ProductDetailImageAdapter
import com.zhpan.indicator.enums.IndicatorSlideMode

//Get data from parent (0)
private const val POSITION = "position"
private const val DATA = "data"

class ImageViewFragment : Fragment() {
    //(0)
    private var position: Int? = null
    private var data: ArrayList<String>? = null

    private var _binding: FragmentImageViewBinding? = null
    private val binding: FragmentImageViewBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //(0)
        arguments?.let {
            position = it.getInt(POSITION)
            data = it.getStringArrayList(DATA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageViewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //返回按鈕
        binding.detailImageBack.whiteBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        //Banner Setting
        binding.detailImageVp.apply {
            this.adapter = ProductDetailImageAdapter()
            currentItem = position!!
            registerLifecycleObserver(lifecycle)
            setCanLoop(false)
            setAutoPlay(false)
            setIndicatorSlideMode(IndicatorSlideMode.SCALE)
            setIndicatorSliderColor(
                requireContext().getColor(R.color.dark_gray),
                requireContext().getColor(R.color.beige_300)
            )
        }.create(data!!.toList())
    }

    //釋放記憶體
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int, data: List<String>) =
            ImageViewFragment().apply {
                //(0)
                arguments = Bundle().apply {
                    putInt(POSITION, position)
                    putStringArrayList(DATA, data as ArrayList<String>)
                }
            }
    }
}