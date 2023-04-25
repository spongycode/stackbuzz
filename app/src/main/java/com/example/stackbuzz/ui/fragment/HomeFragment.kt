package com.example.stackbuzz.ui.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stackbuzz.R
import com.example.stackbuzz.data.api.ApiRepository
import com.example.stackbuzz.data.model.Question
import com.example.stackbuzz.databinding.FragmentHomeBinding
import com.example.stackbuzz.util.HelperFunctions.getTimeAgo
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(ApiRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        observeQuestions()

        return binding.root
    }

    private fun observeQuestions() {
        val questionAdapter = HomeRecyclerAdapter(requireContext())
        binding.rvQuestion.adapter = questionAdapter
        viewModel.questions.observe(viewLifecycleOwner) { result ->
            questionAdapter.mainDiffer.submitList(result.data)
        }
    }

    inner class HomeRecyclerAdapter(
        private val context: Context
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val ITEM_VIEW_TYPE_QUESTION = 0
        private val ITEM_VIEW_TYPE_AD = 1

        private val mainDiffUtil = object : DiffUtil.ItemCallback<Question>() {
            override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
                return oldItem.title == newItem.title
            }
        }

        val mainDiffer = AsyncListDiffer(this, mainDiffUtil)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == ITEM_VIEW_TYPE_QUESTION) {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.question_item,
                    parent,
                    false
                )
                ViewHolder(view)
            } else {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.ad_item,
                    parent,
                    false
                )
                AdViewHolder(view)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (getItemViewType(position) == ITEM_VIEW_TYPE_QUESTION) {
                val question: Question = mainDiffer.currentList[position - (position / 6)]
                val questionHolder = holder as ViewHolder
                questionHolder.title.text = question.title
                questionHolder.username.text = question.owner!!.display_name + " · "
                questionHolder.score.text = question.score.toString()
                questionHolder.answerCount.text = question.answer_count.toString()
                questionHolder.viewCount.text = question.view_count.toString()
                Glide
                    .with(context)
                    .load(question.owner.profile_image)
                    .centerCrop()
                    .placeholder(R.drawable.profile_pic)
                    .into(questionHolder.image)

                var tags = ""
                for (tag in question.tags!!) {
                    tags += "#${tag} "
                }

                questionHolder.tagHolder.text = tags

                questionHolder.timesAgo.text = getTimeAgo(question.creation_date!!)

                questionHolder.mainContainer.setOnClickListener {
                    activity!!
                        .supportFragmentManager
                        .beginTransaction().apply {
                            replace(
                                R.id.fragmentContainerView,
                                WebViewFragment.newInstance(question.link.toString())
                            )
                            addToBackStack(null)
                            commit()
                        }
                    val view = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
                    if (view != null) {
                        view.visibility = GONE
                    }
                }
            } else {
                val adHolder = holder as AdViewHolder
                val adRequest = AdRequest.Builder().build()
                adHolder.adView.loadAd(adRequest)
            }
        }

        override fun getItemCount() =
            mainDiffer.currentList.size + (mainDiffer.currentList.size / 5)

        override fun getItemViewType(position: Int): Int {
            return if ((position + 1) % 6 == 0) {
                ITEM_VIEW_TYPE_AD
            } else {
                ITEM_VIEW_TYPE_QUESTION
            }
        }

        inner class ViewHolder internal constructor(view: View) :
            RecyclerView.ViewHolder(view) {
            internal val title: TextView = view.findViewById(R.id.title_question)
            internal val image: ImageView = view.findViewById(R.id.imageview_question)
            internal val username: TextView = view.findViewById(R.id.username_question)
            internal val score: TextView = view.findViewById(R.id.score_question)
            internal val answerCount: TextView = view.findViewById(R.id.answer_count_question)
            internal val viewCount: TextView = view.findViewById(R.id.view_count_question)
            internal val tagHolder: TextView = view.findViewById(R.id.textview_tag_holder)
            internal val timesAgo: TextView = view.findViewById(R.id.textview_times_ago_question)
            internal val mainContainer: CardView = view.findViewById(R.id.cv_main_holder)
        }

        inner class AdViewHolder internal constructor(view: View) :
            RecyclerView.ViewHolder(view) {
            val adView: AdView = view.findViewById(R.id.adView)

            init {
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
            }
        }
    }

}