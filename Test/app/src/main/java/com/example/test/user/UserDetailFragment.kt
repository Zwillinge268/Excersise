package com.example.test.user

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import com.example.test.MainActivity
import com.example.test.R
import com.example.test.databinding.FragmentUserDetailBinding
import com.example.test.db.HomeDbHelper

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
            getString(R.string.user_no_data, MainActivity.USER_DATA.userID)
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
        MainActivity.USER_DATA.firstName = binding.userDetailFirstNameEd.text.toString()
        MainActivity.USER_DATA.lastName = binding.userDetailLastNameEd.text.toString()
        MainActivity.USER_DATA.address = binding.userDetailAddressEd.text.toString()
        //get drawable from image view and turn it to bitmap
        MainActivity.USER_DATA.icon = (binding.userDetailIcon.drawable as BitmapDrawable).bitmap

        //Update in db
        mDbHelper = HomeDbHelper.getInstance(requireContext())!!
        if (mDbHelper.userDataChange() == 1) {
            Toast.makeText(
                requireContext(),
                getString(R.string.user_detail_change_success),
                Toast.LENGTH_LONG
            ).show()
            requireActivity().supportFragmentManager.popBackStack()
        } else Toast.makeText(
            requireContext(),
            getString(R.string.cart_delete_error),
            Toast.LENGTH_LONG
        ).show()
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