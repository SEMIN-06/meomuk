package com.semin.meomuk.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.semin.meomuk.Food
import com.semin.meomuk.MainActivity
import com.semin.meomuk.R
import com.semin.meomuk.databinding.FragmentMenuAddBinding
import java.util.UUID

class MenuAddFragment : Fragment() {
    private val db = Firebase.firestore
    private val storage = Firebase.storage

    private var _binding: FragmentMenuAddBinding? = null
    private val binding get() = _binding!!
    private val tag = "MenuAdd"
    private var uploadImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuAddBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonUploadImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1000, 1000)
                .createIntent { intent -> startForProfileImageResult.launch(intent) }
        }

        binding.buttonAddMenu.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()
            val desc = binding.editTextDesc.text.toString().trim()
            val recipe = binding.editTextRecipe.text.toString().trim()
            val history = binding.editTextHistory.text.toString().trim()

            if (name.isEmpty() || desc.isEmpty() || recipe.isEmpty() || history.isEmpty() || uploadImageUri == null) {
                Toast.makeText(requireContext(),
                    getString(R.string.fill_all_inputs), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadImageUri?.let {
                val fileName = UUID.randomUUID().toString() + ".png"
                val storageRef = storage.reference.child("foodImages/$fileName")

                storageRef.putFile(it)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val food = Food(
                                name,
                                desc,
                                uri.toString(),
                                recipe,
                                history
                            )
                            db.collection("foods")
                                .add(food)
                                .addOnSuccessListener { res ->
                                    binding.editTextName.setText("")
                                    binding.editTextDesc.setText("")
                                    binding.editTextRecipe.setText("")
                                    binding.editTextHistory.setText("")
                                    food.id = res.id
                                    (activity as? MainActivity)?.addFood(food)
                                    Glide.with(this).load(food.imageUrl).diskCacheStrategy(
                                        DiskCacheStrategy.ALL).preload()
                                    Toast.makeText(requireContext(),
                                        getString(R.string.menu_add_success), Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener{ e ->
                                    Toast.makeText(requireContext(), getString(R.string.error_occurred, "DB004"), Toast.LENGTH_LONG).show()
                                    Log.e(tag, e.toString())
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), getString(R.string.error_occurred, "DB005"), Toast.LENGTH_LONG).show()
                        Log.e(tag, it.toString())
                    }
            } ?: Toast.makeText(requireContext(),
                getString(R.string.select_image_required), Toast.LENGTH_SHORT).show()
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                uploadImageUri = data?.data!!
                binding.imageView.setImageURI(uploadImageUri)
                binding.imageView.visibility = View.VISIBLE
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.image_upload_cancled), Toast.LENGTH_SHORT).show()
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}