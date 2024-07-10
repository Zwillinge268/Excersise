package com.example.test.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.test.databinding.FragmentPaymentBinding

class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding: FragmentPaymentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set default method
        if (CheckoutPageActivity.payMethod.value == CheckoutPageActivity.CASH)
            binding.paymentRadioCash.isChecked = true
        else binding.paymentRadioCredit.isChecked = true

        //Listener
        binding.paymentRadioCash.setOnClickListener { choose(CheckoutPageActivity.CASH) }
        binding.paymentRadioCredit.setOnClickListener { choose(CheckoutPageActivity.CREDIT) }
        binding.paymentLayout.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
    }

    private fun choose(method: Int) {
        CheckoutPageActivity.payMethod.value = method
        requireActivity().supportFragmentManager.popBackStack()
    }

    //釋放記憶體
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = PaymentFragment()
    }
}