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
import com.semin.meomuk.databinding.FragmentRandomAnswerBinding
import java.util.UUID
import kotlin.random.Random

class RandomAnswerFragment : Fragment() {
    private var _binding: FragmentRandomAnswerBinding? = null
    private val binding get() = _binding!!
    private val tag = "RandomAnswer"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRandomAnswerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val responses = arrayOf(
            getString(R.string.random_ans1),
            getString(R.string.random_ans2),
            getString(R.string.random_ans3),
            getString(R.string.random_ans4),
            getString(R.string.random_ans5),
            getString(R.string.random_ans6),
            getString(R.string.random_ans7),
            getString(R.string.random_ans8),
            getString(R.string.random_ans9)
        )
        binding.buttonAnswer.setOnClickListener {
            binding.textViewAnswer.fadeText(responses[Random.nextInt(responses.size)])
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun View.fadeText(newText: String) {
        animate().alpha(0f).setDuration(0).withEndAction {
            if (this is android.widget.TextView) {
                text = newText
            }
            animate().alpha(1f).setDuration(200).start()
        }.start()
    }
}