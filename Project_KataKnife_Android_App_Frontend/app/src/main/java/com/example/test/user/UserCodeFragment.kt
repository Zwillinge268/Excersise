package com.example.test.user

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.ImageView
import androidx.fragment.app.Fragment
import coil.load
import com.example.test.MainActivity
import com.example.test.R
import com.example.test.databinding.FragmentUserCodeBinding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class UserCodeFragment : Fragment() {
    private var _binding: FragmentUserCodeBinding? = null
    private val binding: FragmentUserCodeBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserCodeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //返回按鈕
        binding.userCodeImageBack.greyBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        initUserData()
        initUserCode()
    }

    //Load user data
    private fun initUserData() {
        binding.userCodeUserId.text =
            getString(R.string.user_no_data, MainActivity.USER_DATA.userId)
        binding.userCodeUserPoint.text =
            getString(R.string.user_point_data, MainActivity.USER_DATA.point)
    }

    //Create barcode (from user id)
    private fun initUserCode() {
        val data = MainActivity.USER_DATA.userId

        //Get screen size
        val size = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (API 30) up
            //獲取當前視窗指標
            val metrics = requireActivity().windowManager.currentWindowMetrics

            //獲取視窗非顯示部分
            val windowInsets = metrics.windowInsets
            val insets = windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars()
                        or WindowInsets.Type.displayCutout()
            )
            val insetsWidth = insets.left + insets.right
            val insetsHeight = insets.top + insets.bottom

            //獲取視窗整體大小
            val bounds = metrics.bounds
            val boundsWidth = bounds.width()
            val boundsHeight = bounds.height()

            //相減以上數值獲得實際可顯示部分
            Pair(boundsWidth - insetsWidth, boundsHeight - insetsHeight)
        } else {
            //Under needed version
            val display = requireActivity().windowManager.defaultDisplay
            Pair(display.width, display.height)
        }

        //使用 BarcodeEncoder
        val barcodeEncoder = BarcodeEncoder()
        val bitmap =
            barcodeEncoder.encodeBitmap(data, BarcodeFormat.CODE_128, size.first, (size.second / 7))

        //Image View Setting
        val img = ImageView(requireContext()).apply {
            ViewGroup.LayoutParams(size.first, (size.second / 7))
            scaleType = ImageView.ScaleType.FIT_XY
            load(bitmap)
        }

        binding.userCodeFrame.addView(img)
    }

    //釋放記憶體
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserCodeFragment()
    }
}