package com.example.test.user

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import coil.load
import com.example.test.MainActivity
import com.example.test.R
import com.example.test.databinding.FragmentUserDetailBinding
import com.example.test.db.HomeDbHelper
import com.example.test.util.OnlineDbUtil
import com.example.test.login.LoginUtil
import com.example.test.module.User


class UserDetailFragment : Fragment() {
    private var _binding: FragmentUserDetailBinding? = null
    private val binding: FragmentUserDetailBinding get() = _binding!!

    //pock media request
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var imageUri: Uri? = null

    private lateinit var mDbHelper: HomeDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Media picker listener
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) loadImage(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        //返回按鈕
        binding.userCodeImageBack.greyBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        //Launch media picker
        binding.userDetailIcon.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.userDetailSave.setOnClickListener { saveUserData() }
    }

    //Load User Data (from companion object)
    private fun initView() {
        binding.userDetailAccount.text = MainActivity.USER_DATA.userName
        binding.userDetailEmail.text =
            getString(R.string.user_detail_email, MainActivity.USER_DATA.email)
        binding.userDetailNo.text =
            getString(R.string.user_no_data, MainActivity.USER_DATA.userId)
        binding.userDetailFirstNameEd.setText(MainActivity.USER_DATA.firstName)
        binding.userDetailLastNameEd.setText(MainActivity.USER_DATA.lastName)
        binding.userDetailAddressEd.setText(MainActivity.USER_DATA.address)
        //show default when icon is null
        if (MainActivity.USER_DATA.icon == null)
            binding.userDetailIcon.load(R.drawable.user_icon_default)
        else binding.userDetailIcon.setImageBitmap(MainActivity.USER_DATA.icon)
    }

    private fun loadImage(uri: Uri) {
        imageUri = uri
        binding.userDetailIcon.setImageURI(imageUri)
    }

    //Save User Data (to database)
    private fun saveUserData() {
        //Change companion object data
        val firstName = binding.userDetailFirstNameEd.text.toString()
        val lastName = binding.userDetailLastNameEd.text.toString()
        val address = binding.userDetailAddressEd.text.toString()
        //get drawable from image view and turn it to bitmap
        val icon = (binding.userDetailIcon.drawable as BitmapDrawable).bitmap
        val iconString = LoginUtil.getBytes(icon)
        val user = User(false, "", "", "", firstName, lastName, address, "", null, iconString, "")
        //Update in db
        if (OnlineDbUtil.updateUserData(user) == LoginUtil.SUCCESS) {
            Toast.makeText(
                requireContext(),
                getString(R.string.user_detail_change_success),
                Toast.LENGTH_LONG
            ).show()
            MainActivity.USER_DATA.firstName = firstName
            MainActivity.USER_DATA.lastName = lastName
            MainActivity.USER_DATA.address = address
            MainActivity.USER_DATA.icon = icon
            MainActivity.USER_DATA.iconByteArray = iconString

            getParentFragmentManager().setFragmentResult("requestKey", Bundle().apply { putInt("Done", 1) });
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.cart_delete_error),
                Toast.LENGTH_LONG
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
        fun newInstance() = UserDetailFragment()
    }
}