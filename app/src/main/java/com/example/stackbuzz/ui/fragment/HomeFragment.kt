package com.example.stackbuzz.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stackbuzz.R
import com.example.stackbuzz.data.api.ApiRepository
import com.example.stackbuzz.data.model.QuestionItem
import com.example.stackbuzz.databinding.FragmentHomeBinding
import com.example.stackbuzz.util.HelperFunctions.getTimeAgo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val questionList = mutableListOf<QuestionItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


//        questionList.add(QuestionItem(title = "Question 1"))
//        questionList.add(QuestionItem(title = "Question 2"))

        observeQuestions()

        return binding.root
    }

    private fun observeQuestions() {
        val questionAdapter = HomeRecyclerAdapter(requireContext())
        ApiRepository().getQuestions().observe(viewLifecycleOwner) {
            for (question in it.body()!!.items!!) {
                questionList.add(question)
            }
            Log.d("QUESTIONS", it.body().toString())
            binding.rvQuestion.adapter = questionAdapter
            questionAdapter.mainDiffer.submitList(questionList)
            binding.rvQuestion.adapter?.notifyDataSetChanged()
        }
    }


    inner class HomeRecyclerAdapter(
        private val context: Context
    ) :
        RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder>() {

        private val mainDiffUtil = object : DiffUtil.ItemCallback<QuestionItem>() {
            override fun areItemsTheSame(oldItem: QuestionItem, newItem: QuestionItem): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: QuestionItem, newItem: QuestionItem): Boolean {
                return oldItem.title == newItem.title
            }
        }

        val mainDiffer = AsyncListDiffer(this, mainDiffUtil)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.question_item,
                parent,
                false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val question: QuestionItem = mainDiffer.currentList[position]
            holder.title.text = question.title
            holder.username.text = question.owner!!.display_name
            holder.score.text = question.score.toString()
            holder.answerCount.text = question.answer_count.toString()
            holder.viewCount.text = question.view_count.toString()
            Glide
                .with(context)
                .load(question.owner.profile_image)
                .centerCrop()
                .placeholder(R.drawable.profile_pic)
                .into(holder.image)

            val chipGroup = ChipGroup(context).apply {
                id = View.generateViewId()
                chipSpacingVertical = 8
                chipSpacingHorizontal = 8
            }

            for (tag in question.tags!!) {
                val chip = Chip(context).apply {
                    text = tag
                    setChipDrawable(
                        ChipDrawable.createFromAttributes(
                            context,
                            null,
                            0,
                            com.google.android.material.R.style.Widget_Material3_Chip_Suggestion
                        )
                    )

                    textStartPadding = 0F
                    textEndPadding = 0F
                    textSize = 10F
                    id = View.generateViewId()
                    setChipStrokeColorResource(R.color.transparent)
                }
                chipGroup.addView(chip)
            }

            holder.tagHolder.removeAllViews()
            holder.tagHolder.addView(chipGroup)

            holder.timesAgo.text = getTimeAgo(question.creation_date!!)
        }

        override fun getItemCount() = mainDiffer.currentList.size

        inner class ViewHolder internal constructor(view: View) :
            RecyclerView.ViewHolder(view) {
            internal val title: TextView = view.findViewById(R.id.title_question)
            internal val image: ImageView = view.findViewById(R.id.imageview_question)
            internal val username: TextView = view.findViewById(R.id.username_question)
            internal val score: TextView = view.findViewById(R.id.score_question)
            internal val answerCount: TextView = view.findViewById(R.id.answer_count_question)
            internal val viewCount: TextView = view.findViewById(R.id.view_count_question)
            internal val tagHolder: LinearLayout = view.findViewById(R.id.chip_tag_holder)
            internal val timesAgo: TextView = view.findViewById(R.id.textview_times_ago_question)
        }
    }
}