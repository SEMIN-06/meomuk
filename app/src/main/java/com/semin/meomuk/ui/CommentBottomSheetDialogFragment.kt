import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.semin.meomuk.Comment
import com.semin.meomuk.Food
import com.semin.meomuk.R
import com.semin.meomuk.databinding.FragmentCommentBottomSheetDialogBinding
import java.util.Date


class CommentBottomSheetDialogFragment(private val nowFood: Food) : BottomSheetDialogFragment() {
    private val db = FirebaseFirestore.getInstance()
    private var _binding: FragmentCommentBottomSheetDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var commentsRef: CollectionReference

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentBottomSheetDialogBinding.inflate(inflater, container, false)

        binding.scrollView.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        commentsRef = db.collection("foods").document(nowFood.id).collection("comments")

        binding.submitButton.setOnClickListener {
            val name = binding.nicknameInput.text.toString().trim()
            val commentText = binding.commentInput.text.toString().trim()
            val rating = binding.ratingInput.rating

            if (name.isEmpty() || commentText.isEmpty() || rating == 0f) {
                Toast.makeText(requireContext(),
                    getString(R.string.fill_all_inputs), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val comment = Comment(
                userName = name,
                text = commentText,
                rating = rating,
                timestamp = System.currentTimeMillis()
            )

            commentsRef.add(comment)
                .addOnSuccessListener {
                    binding.nicknameInput.setText("")
                    binding.commentInput.setText("")
                    binding.ratingInput.rating = 0f
                    Toast.makeText(requireContext(),
                        getString(R.string.review_success), Toast.LENGTH_SHORT).show()
                    addCommentToView(comment)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), getString(R.string.error_occurred, "DB006"), Toast.LENGTH_LONG).show()
                    Log.e(tag, "Error submitting comment", e)
                }
        }

        refreshComments()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun Date.toElapsedTime(): String {
        val current = System.currentTimeMillis()
        val timestamp = this.time
        val elapsed = current - timestamp

        val seconds = elapsed / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> getString(R.string.days_ago, days.toString())
            hours > 0 -> getString(R.string.hours_ago, hours.toString())
            minutes > 0 -> getString(R.string.minutes_ago, minutes.toString())
            else -> getString(R.string.now)
        }
    }

    private fun addCommentToView(comment: Comment) {
        val commentView = layoutInflater.inflate(R.layout.comment_item, binding.comments, false)
        val nicknameTextView = commentView.findViewById<TextView>(R.id.commentNickname)
        val timeTextView = commentView.findViewById<TextView>(R.id.commentTime)
        val commentTextView = commentView.findViewById<TextView>(R.id.commentText)
        val ratingBarView = commentView.findViewById<RatingBar>(R.id.commentRating)

        nicknameTextView.text = comment.userName
        val timestampDate = Date(comment.timestamp)
        timeTextView.text = timestampDate.toElapsedTime()
        commentTextView.text = comment.text
        ratingBarView.rating = comment.rating

        binding.comments.addView(commentView, 0)
    }

    private fun refreshComments() {
        binding.comments.removeAllViews()
        commentsRef.orderBy("timestamp", Query.Direction.ASCENDING).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val comment = document.toObject<Comment>()
                    addCommentToView(comment)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), getString(R.string.error_occurred, "DB005"), Toast.LENGTH_LONG).show()
                Log.e(tag, "Error fetching comments", e)
            }
    }
}
