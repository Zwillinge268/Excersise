package com.example.test.payment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.test.MainActivity
import com.example.test.R
import com.example.test.databinding.FragmentAddressBinding

class AddressFragment : Fragment() {
    private var _binding: FragmentAddressBinding? = null
    private val binding: FragmentAddressBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initEvent()
    }

    private fun initData() {
        //載入默認地址資料
        binding.addressRbDefault.text = getString(
            R.string.checkout_address_string,
            MainActivity.USER_DATA.lastName + MainActivity.USER_DATA.firstName,
            MainActivity.USER_DATA.address,
            MainActivity.USER_DATA.email
        )
        binding.addressOtherAddress1.setText(CheckoutPageActivity.address1)
        binding.addressOtherAddress2.setText(CheckoutPageActivity.address2)

        //設定默認選項
        if (CheckoutPageActivity.addressChoose.value == CheckoutPageActivity.DEFAULT)
            binding.addressRbDefault.isChecked = true
        else binding.addressRbNew.isChecked = true
    }

    private fun initEvent() {
        //返回按鈕
        binding.addressImageBack.greyBackButton.setOnClickListener { popThis() }

        binding.addressSave.setOnClickListener {
            //使用默認地址
            if (binding.addressRbDefault.isChecked) {
                CheckoutPageActivity.addressChoose.value = CheckoutPageActivity.DEFAULT
                popThis()
            }
            //使用其他地址(地址行非空)
            else if (binding.addressRbNew.isChecked && binding.addressOtherAddress1.text.toString()
                    .isNotBlank() && binding.addressOtherAddress2.text.toString().isNotBlank()
            ) {
                CheckoutPageActivity.address1 = binding.addressOtherAddress1.text.toString()
                CheckoutPageActivity.address2 = binding.addressOtherAddress2.text.toString()
                CheckoutPageActivity.addressChoose.value = CheckoutPageActivity.OTHER
                popThis()
            //使用其他地址(地址行為空)
            } else {
                CheckoutPageActivity.addressChoose.value = CheckoutPageActivity.DEFAULT
                Toast.makeText(
                    requireContext(),
                    getString(R.string.checkout_address_null), Toast.LENGTH_LONG
                ).show()
            }
            //關閉鍵盤
            (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                requireActivity().currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    private fun popThis() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    //釋放記憶體
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddressFragment()
    }
}