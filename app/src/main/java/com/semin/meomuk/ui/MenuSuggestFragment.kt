package com.semin.meomuk.ui

import CommentBottomSheetDialogFragment
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.toObject
import com.semin.meomuk.R
import com.semin.meomuk.databinding.FragmentMenuSuggestBinding
import kotlin.random.Random
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.semin.meomuk.Comment
import com.semin.meomuk.Food
import com.semin.meomuk.MainActivity

class MenuSuggestFragment : Fragment() {
    private val db = Firebase.firestore
    private var _binding: FragmentMenuSuggestBinding? = null
    private val binding get() = _binding!!
    private var foods: List<Food>? = null
    private val tag = "MenuSuggest"
    private var nowFood: Food? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuSuggestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textMenuName.text = ""
        binding.textMenuDesc.text = ""
        binding.imageMenu.setImageDrawable(null)
        nowFood = null

        binding.buttonMenuRecipe.setOnClickListener {
            if (nowFood != null) {
                val content = nowFood!!.recipeContent.replace("\\n", System.getProperty("line.separator"));
                val bottomSheet = BottomSheetFragment(content)
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
            else {
                Toast.makeText(requireContext(),
                    getString(R.string.click_pickup_first), Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonMenuHistory.setOnClickListener {
            if (nowFood != null) {
                val content = nowFood!!.historyContent.replace("\\n", System.getProperty("line.separator"));
                val bottomSheet = BottomSheetFragment(content)
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
            else {
                Toast.makeText(requireContext(), getString(R.string.click_pickup_first), Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonMenuComments.setOnClickListener {
            if (nowFood != null) {
                val bottomSheet = CommentBottomSheetDialogFragment(nowFood!!)
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
            else {
                Toast.makeText(requireContext(), getString(R.string.click_pickup_first), Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonMenuSuggest.setOnClickListener {
            foods = (activity as? MainActivity)?.getFoods()
            if (foods != null && foods!!.isNotEmpty()) {
                nowFood = foods?.get(Random.nextInt(foods!!.size))
                if (nowFood != null) {
                    binding.textMenuName.fadeText(nowFood!!.name)
                    binding.textMenuDesc.fadeText(nowFood!!.desc)
                    Glide.with(this).load(nowFood!!.imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).transition(
                        DrawableTransitionOptions.withCrossFade()).into(binding.imageMenu)
                }
            }
            else {
                Toast.makeText(requireContext(), getString(R.string.error_occurred, "DB002"), Toast.LENGTH_LONG).show()
                Log.e(tag, "Not Ready")
            }
        }
    }

    private fun View.fadeText(newText: String) {
        animate().alpha(0f).setDuration(0).withEndAction {
            if (this is android.widget.TextView) {
                text = newText
            }
            animate().alpha(1f).setDuration(200).start()
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}