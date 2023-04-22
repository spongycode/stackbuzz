package com.example.stackbuzz.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stackbuzz.R
import com.example.stackbuzz.data.api.ApiRepository
import com.example.stackbuzz.data.model.Question
import com.example.stackbuzz.databinding.FragmentSearchBinding
import com.example.stackbuzz.util.HelperFunctions
import com.google.android.material.bottomnavigation.BottomNavigationView


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val questionList = mutableListOf<Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.searchView
            .editText
            .setOnEditorActionListener { v, actionId, event ->
                binding.searchBar.text = binding.searchView.text
                observeSearchResults(binding.searchBar.text.toString(), requireContext())
                binding.searchView.hide()
                false
            }

        return binding.root
    }

    private fun observeSearchResults(queryText: String, context: Context) {
        val questionAdapter = SearchRecyclerAdapter(requireContext())
        ApiRepository(context).getSearchResults(queryText).observe(viewLifecycleOwner) {
            questionList.clear()
            for (question in it.body()!!.items!!) {
                questionList.add(question)
            }
            Log.d("QUESTIONS", it.body().toString())
            binding.rvSearch.adapter = questionAdapter
            questionAdapter.mainDiffer.submitList(questionList)
            binding.rvSearch.adapter?.notifyDataSetChanged()
        }
    }

    inner class SearchRecyclerAdapter(
        private val context: Context
    ) :
        RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>() {

        private val mainDiffUtil = object : DiffUtil.ItemCallback<Question>() {
            override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
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
            val question: Question = mainDiffer.currentList[position]
            holder.title.text = question.title
            holder.username.text = question.owner!!.display_name + " · "
            holder.score.text = question.score.toString()
            holder.answerCount.text = question.answer_count.toString()
            holder.viewCount.text = question.view_count.toString()
            Glide
                .with(context)
                .load(question.owner.profile_image)
                .centerCrop()
                .placeholder(R.drawable.profile_pic)
                .into(holder.image)

            var tags = ""
            for (tag in question.tags!!) {
                tags += "#${tag} "
            }

            holder.tagHolder.text = tags

            holder.timesAgo.text = HelperFunctions.getTimeAgo(question.creation_date!!)

            holder.mainContainer.setOnClickListener {
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
                    view.visibility = View.GONE
                }
            }
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
            internal val tagHolder: TextView = view.findViewById(R.id.textview_tag_holder)
            internal val timesAgo: TextView = view.findViewById(R.id.textview_times_ago_question)
            internal val mainContainer: CardView = view.findViewById(R.id.cv_main_holder)
        }
    }


}