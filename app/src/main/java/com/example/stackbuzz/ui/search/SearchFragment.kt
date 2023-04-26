package com.example.stackbuzz.ui.search

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stackbuzz.R
import com.example.stackbuzz.data.api.ApiRepository
import com.example.stackbuzz.data.model.Question
import com.example.stackbuzz.databinding.FragmentSearchBinding
import com.example.stackbuzz.helpers.HelperFunctions
import com.example.stackbuzz.data.utils.Resource
import com.example.stackbuzz.ui.webview.WebViewFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(ApiRepository(requireContext()))
    }
    lateinit var dialogView: View

    private lateinit var chipGroup: ChipGroup
    private lateinit var questionAdapter: SearchRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        dialogView = layoutInflater.inflate(R.layout.dialog_tags, null)
        chipGroup = dialogView.findViewById(R.id.chip_group)

        questionAdapter = SearchRecyclerAdapter(requireContext())
        binding.rvSearch.adapter = questionAdapter

        if (viewModel.editTextValue.isNotEmpty()) binding.etSearch.setText(viewModel.editTextValue)

        observeFilteredQuestions()
        observeTags()
        addScrollListener()
        binding.goUpFab.setOnClickListener {
            scrollToTop()
        }
        binding.goUpFab.hide()

        binding.btnSearch.setOnClickListener {
            startSearch()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                startSearch()
                true
            } else {
                false
            }
        }

        binding.btnFilter.setOnClickListener {
            showTagDialog()
        }
        return binding.root
    }


    private fun showTagDialog() {
        if (dialogView.parent != null) {
            (dialogView.parent as ViewGroup).removeView(dialogView)
        }
        val builder = AlertDialog.Builder(requireContext())
        val dialog: Dialog = builder.setView(dialogView).create()
        dialogView.findViewById<Button>(R.id.dialog_ok_button).setOnClickListener {
            viewModel.filterQuery()
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogView.findViewById<Button>(R.id.dialog_cancel_button)
            .setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun startSearch() {
        viewModel.editTextValue = binding.etSearch.text.toString()
        viewModel.searchQuery(binding.etSearch.text.toString())
        HelperFunctions.hideKeyboard(requireContext(), binding.etSearch)
        scrollToTop()
    }

    fun scrollToTop() {
        binding.rvSearch.scrollToPosition(0)
    }

    private fun observeFilteredQuestions() {
        viewModel.filteredQuestions.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.loadingContainer.visibility = VISIBLE
                    binding.tvErrorMessage.visibility = GONE
                }

                is Resource.Success -> {
                    questionAdapter.mainDiffer.submitList(resource.data!!)
                    binding.rvSearch.scrollToPosition(0)
                    binding.loadingContainer.visibility = GONE
                    binding.tvErrorMessage.visibility = GONE
                    binding.rvSearch.visibility = VISIBLE
                    if (resource.data.isEmpty()) {
                        binding.tvErrorMessage.text = "No results found :("
                        binding.tvErrorMessage.visibility = VISIBLE
                    }
                }

                is Resource.Error -> {
                    binding.tvErrorMessage.text = viewModel.errorMessage.value
                    binding.tvErrorMessage.visibility = VISIBLE
                    binding.rvSearch.visibility = GONE
                    binding.loadingContainer.visibility = GONE
                }
            }
            scrollToTop()
        }
    }

    private fun showHideFilterBtn() {
        if (viewModel.tagsList.value.isNullOrEmpty()) {
            binding.btnFilter.visibility = GONE
        } else {
            binding.btnFilter.visibility = VISIBLE
        }
    }

    private fun observeTags() {
        showHideFilterBtn()
        viewModel.tagsList.observe(viewLifecycleOwner) { tag ->
            updateChipGroup(tag)
            showHideFilterBtn()
        }
    }

    private fun updateChipGroup(chipTexts: MutableList<String>?) {
        chipGroup.removeAllViews()
        if (chipTexts != null) {
            for (chipText in chipTexts) {
                val chip = createChip(chipText)
                setupChipListener(chip)
                chipGroup.addView(chip)
            }
        }
    }

    private fun addScrollListener() {
        binding.rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItemPosition > 2) {
                    binding.goUpFab.show()
                } else {
                    binding.goUpFab.hide()
                }
            }
        })
    }

    private fun setupChipListener(chip: Chip) {
        chip.setOnCheckedChangeListener { button, isChecked ->
            val chipText = button.text.toString()
            if (isChecked) {
                viewModel.selectedChips.value?.add(chipText)
            } else {
                viewModel.selectedChips.value?.remove(chipText)
            }
        }
    }

    private fun createChip(chipText: String): Chip {
        val chip = Chip(context)
        chip.text = chipText
        chip.isCheckable = true
        chip.setChipDrawable(
            ChipDrawable.createFromAttributes(
                requireContext(),
                null,
                0,
                R.style.CustomChipStyle
            )
        )
        chip.isChecked = viewModel.selectedChips.value?.contains(chipText) ?: false
        return chip
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

            override fun getChangePayload(oldItem: Question, newItem: Question): Any? {
                val diffBundle = Bundle()
                if (oldItem.title != newItem.title) {
                    diffBundle.putString("title", newItem.title)
                }
                return if (diffBundle.isEmpty) null else diffBundle
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

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val question: Question = mainDiffer.currentList[position]
            holder.title.text = question.title
            holder.username.text = question.owner!!.display_name + " · "
            holder.score.text = question.score.toString() + " votes"
            holder.answerCount.text = question.answer_count.toString() + " answers"
            holder.viewCount.text = question.view_count.toString() + " views"
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
                    view.visibility = GONE
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