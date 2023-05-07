package com.pandalai.githubrepofinder.presenter

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import com.pandalai.githubrepofinder.R
import com.pandalai.githubrepofinder.api.RestClient
import com.pandalai.githubrepofinder.api.RestClient2
import com.pandalai.githubrepofinder.models.GithubSearchItem
import com.pandalai.githubrepofinder.models.GithubSearchModel
import com.pandalai.githubrepofinder.repositories.GithubRepository
import com.pandalai.githubrepofinder.ui.MainActivity
import com.pandalai.githubrepofinder.ui.MainView
import com.pandalai.githubrepofinder.utils.KeyboardUtils
import com.pandalai.githubrepofinder.utils.MiscUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainPresenterImpl (private var context: Context) : MainPresenter {

    private var mainView: MainView = context as MainView

    private var searchText: String = ""
    private var sortBy: String = ""
    private var orderBy: String = ""
    private var pageNum: Int = 0
    private var pageLength: Int = 10
    private var itemsCount: Int = 0
    private var totalCount: Int = 0

    private var isFilterApplied: Boolean = false

    private fun resetData() {
        mainView.clearList()
        pageLength = 10
        pageNum = 1
        totalCount = 0
        itemsCount = 0
    }

    private fun clearFilters() {
        orderBy = ""
        sortBy = ""

        mainView.resetFilters()
    }

    private fun onSearching() {
        mainView.changeMessage(context.getString(R.string.fetching))
    }

    private fun showNoReposFound() {
        if (itemsCount == 0) mainView.changeMessage(context.getString(R.string.repoNotFound))
    }

    private fun manageResponse(response: Response<GithubSearchModel>) {
        if(response?.body() == null || response.body()!!.items == null){
            Toast.makeText(context, context.getString(R.string.no_more_results), Toast.LENGTH_SHORT).show()
            return
        }
        var filteredResponse : ArrayList<GithubSearchItem> = response.body()!!.items as ArrayList<GithubSearchItem>
        showNoReposFound()
        when(sortBy){
            MiscUtils.SortBy.created.name ->{
                if(orderBy == MiscUtils.OrderBy.asc.name){
                    filteredResponse.sortBy {
                        it.createdAt
                    }
                } else {
                    filteredResponse.sortByDescending {
                        it.createdAt
                    }
                }
            }
            MiscUtils.SortBy.repoName.name ->{
                if(orderBy == MiscUtils.OrderBy.asc.name){
                    filteredResponse.sortBy {
                        it.name
                    }
                } else {
                    filteredResponse.sortByDescending {
                        it.name
                    }
                }
            }
            MiscUtils.SortBy.score.name ->{
                if(orderBy == MiscUtils.OrderBy.asc.name){
                    filteredResponse.sortBy {
                        it.score
                    }
                } else {
                    filteredResponse.sortByDescending {
                        it.score
                    }
                }
            }
            MiscUtils.SortBy.watchers.name ->{
                if(orderBy == MiscUtils.OrderBy.asc.name){
                    filteredResponse.sortBy {
                        it.watchersCount
                    }
                } else {
                    filteredResponse.sortByDescending {
                        it.watchersCount
                    }
                }
            }
            else ->{}
        }
        totalCount = response.body()!!.totalCount!!
        itemsCount += filteredResponse.size
        if (itemsCount != 0) {
            mainView.updateList(filteredResponse.toList())
        }
    }

    private fun getReposService() {
        val sortByParam = if(sortBy == MiscUtils.SortBy.stars.name || sortBy == MiscUtils.SortBy.updated.name) sortBy else ""
        val orderByParam = if(sortBy == MiscUtils.SortBy.stars.name || sortBy == MiscUtils.SortBy.updated.name) orderBy else ""
        val retrofitService = RestClient2.service
        val mainRepository = GithubRepository(retrofitService)
        (context as MainActivity).binding.progress.visibility = View.VISIBLE
        mainRepository.getRepos(searchText, sortByParam, orderBy, pageLength, pageNum)
            .enqueue(object : Callback<GithubSearchModel> {
                override fun onFailure(call: Call<GithubSearchModel>, t: Throwable) {
                    (context as MainActivity).binding.progress.visibility = View.GONE
                    showNoReposFound()
                }

                override fun onResponse(call: Call<GithubSearchModel>, response: Response<GithubSearchModel>) {
                    (context as MainActivity).binding.progress.visibility = View.GONE
                    manageResponse(response)
                }

            })
    }

    override fun searchRepos(searchText: String, sortBy: String, orderBy: String) {

        KeyboardUtils.hideKeyboard(context as Activity)

        if (this.searchText != searchText) {
            resetData()
        }

        this.searchText = searchText
        this.sortBy = sortBy
        this.orderBy = orderBy

        if (pageNum == 1) {
            onSearching()
        }

        getReposService()
    }

    override fun onDialogCancel() {
        if (isFilterApplied) {
            getReposService()
        }
    }

    override fun onFilterApply() {
        isFilterApplied = true
        resetData()
    }

    override fun onFilterClear() {
        clearFilters()
        resetData()
        getReposService()
    }

    override fun onLoadMore() {
        if (itemsCount != 0 && totalCount != itemsCount) {
            pageNum++
            getReposService()
        }
    }
}