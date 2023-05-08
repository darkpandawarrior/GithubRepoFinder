package com.pandalai.githubrepofinder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pandalai.githubrepofinder.R
import com.pandalai.githubrepofinder.api.RestClient
import com.pandalai.githubrepofinder.databinding.ActivityMainBinding
import com.pandalai.githubrepofinder.databinding.LayoutBottomSheetFilterBinding
import com.pandalai.githubrepofinder.models.GithubSearchItem
import com.pandalai.githubrepofinder.repositories.GithubRepository
import com.pandalai.githubrepofinder.utils.MiscUtils
import com.pandalai.githubrepofinder.utils.viewBinding
import com.pandalai.githubrepofinder.viewmodelfactories.GithubSearchViewModelFactory
import com.pandalai.githubrepofinder.viewmodels.GithubSearchViewModel

class MainActivity  : AppCompatActivity(), SearchItemListMethods, View.OnClickListener, GithubSearchItemAdapter.OnLoadMoreListener {
    val binding by viewBinding(ActivityMainBinding::inflate)
    private lateinit var bottomSheetBinding : LayoutBottomSheetFilterBinding
    private lateinit var githubSearchItemAdapter: GithubSearchItemAdapter
    private lateinit var viewModel: GithubSearchViewModel

    private lateinit var dialog: BottomSheetDialog

    private var orderBy: String = ""
    private var sortBy: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val retrofitService = RestClient.service
        val mainRepository = GithubRepository(retrofitService)
        viewModel = ViewModelProvider(this, GithubSearchViewModelFactory(this, mainRepository))[GithubSearchViewModel::class.java]
        binding.ivSearch.setOnClickListener{
            if(binding.etSearch.text.isNullOrEmpty()){
                Toast.makeText(this, resources.getString(R.string.search_field_not_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.searchRepos(binding.etSearch.text.toString(), sortBy, orderBy)
        }
        binding.ivFilter.setOnClickListener{
            dialog.show()
        }
        setObservers()
        setRecyclerView()
        setBottomSheet()

    }

    private fun setObservers(){
        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.getRepoResponse.observe(this) {
            viewModel.manageResponse(it)
        }

        viewModel.loading.observe(this) {
            if(it){
                binding.progress.visibility = View.VISIBLE
            } else{
                binding.progress.visibility = View.GONE
            }
        }
    }
    private fun setRecyclerView() {
        binding.rvSearchResult.itemAnimator = DefaultItemAnimator()
        binding.rvSearchResult.layoutManager = LinearLayoutManager(this)

        githubSearchItemAdapter = GithubSearchItemAdapter(binding.rvSearchResult)
        githubSearchItemAdapter.onLoadMoreListener = this

        binding.rvSearchResult.adapter = githubSearchItemAdapter
    }

    private fun setBottomSheet() {
        val inflater = LayoutInflater.from(this)
        bottomSheetBinding = LayoutBottomSheetFilterBinding.inflate(inflater, null, false)

        bottomSheetBinding.tvStars.setOnClickListener(this)
        bottomSheetBinding.tvWatcherCount.setOnClickListener(this)
        bottomSheetBinding.tvScore.setOnClickListener(this)
        bottomSheetBinding.tvName.setOnClickListener(this)
        bottomSheetBinding.tvCreatedAt.setOnClickListener(this)
        bottomSheetBinding.tvUpdated.setOnClickListener(this)
        bottomSheetBinding.tvAscending.setOnClickListener(this)
        bottomSheetBinding.tvDescending.setOnClickListener(this)
        bottomSheetBinding.tvApply.setOnClickListener(this)
        bottomSheetBinding.tvClear.setOnClickListener(this)

        dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetBinding.root)
        dialog.setOnCancelListener {
            viewModel.onDialogCancel()
        }
    }

    private fun clearSortFilters() {
        bottomSheetBinding.tvStars.isSelected = false
        bottomSheetBinding.tvWatcherCount.isSelected = false
        bottomSheetBinding.tvScore.isSelected = false
        bottomSheetBinding.tvName.isSelected = false
        bottomSheetBinding.tvCreatedAt.isSelected = false
        bottomSheetBinding.tvUpdated.isSelected = false
    }

    private fun clearOrderByFilters() {
        bottomSheetBinding.tvAscending.isSelected = false
        bottomSheetBinding.tvDescending.isSelected = false
    }

    private fun manageSortFilters() {
        clearSortFilters()
        when (sortBy) {
            MiscUtils.SortBy.stars.name -> bottomSheetBinding.tvStars.isSelected = true
            MiscUtils.SortBy.watchers.name -> bottomSheetBinding.tvWatcherCount.isSelected = true
            MiscUtils.SortBy.score.name -> bottomSheetBinding.tvScore.isSelected = true
            MiscUtils.SortBy.repoName.name -> bottomSheetBinding.tvName.isSelected = true
            MiscUtils.SortBy.created.name -> bottomSheetBinding.tvCreatedAt.isSelected = true
            MiscUtils.SortBy.updated.name -> bottomSheetBinding.tvUpdated.isSelected = true
        }
    }

    private fun manageOrderByFilters() {
        clearOrderByFilters()
        when (orderBy) {
            MiscUtils.OrderBy.asc.name -> bottomSheetBinding.tvAscending.isSelected = true
            MiscUtils.OrderBy.desc.name -> bottomSheetBinding.tvDescending.isSelected = true
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvStars -> {
                sortBy = MiscUtils.SortBy.stars.name
                manageSortFilters()
            }
            R.id.tvWatcherCount -> {
                sortBy = MiscUtils.SortBy.watchers.name
                manageSortFilters()
            }
            R.id.tvScore -> {
                sortBy = MiscUtils.SortBy.score.name
                manageSortFilters()
            }
            R.id.tvName -> {
                sortBy = MiscUtils.SortBy.repoName.name
                manageSortFilters()
            }
            R.id.tvCreatedAt -> {
                sortBy = MiscUtils.SortBy.created.name
                manageSortFilters()
            }
            R.id.tvUpdated -> {
                sortBy = MiscUtils.SortBy.updated.name
                manageSortFilters()
            }
            R.id.tvAscending -> {
                orderBy = MiscUtils.OrderBy.asc.name
                manageOrderByFilters()
            }
            R.id.tvDescending -> {
                orderBy = MiscUtils.OrderBy.desc.name
                manageOrderByFilters()
            }
            R.id.tvApply -> {
                if(binding.etSearch.text.isNullOrEmpty()){
                    Toast.makeText(this, resources.getString(R.string.search_field_not_empty), Toast.LENGTH_SHORT).show()
                    return
                }
                dialog.dismiss()
                viewModel.onFilterApply()
                binding.ivSearch.performClick()
            }
            R.id.tvClear -> {
                dialog.dismiss()
                viewModel.onFilterClear()
                binding.ivSearch.performClick()
            }
        }
    }

    override fun onLoadMore() {
        viewModel.onLoadMore()
    }


    override fun changeMessage(message: String) {
        binding.tvEmptyScreenMessage.text = message
        showMessage()
    }

    override fun showMessage() {
        hideList()
        binding.tvEmptyScreenMessage.visibility = View.VISIBLE
    }

    override fun hideMessage() {
        showList()
        binding.tvEmptyScreenMessage.visibility = View.GONE
    }

    override fun updateList(items: List<GithubSearchItem>) {
        hideMessage()
        githubSearchItemAdapter.setLoaded()
        githubSearchItemAdapter.updateData(items)
    }

    override fun clearList() {
        githubSearchItemAdapter.clearItems()
    }

    override fun showList() {
        binding.rvSearchResult.visibility = View.VISIBLE
    }

    override fun hideList() {
        binding.rvSearchResult.visibility = View.GONE
    }

    override fun resetFilters() {
        clearOrderByFilters()
        clearSortFilters()
    }

}
