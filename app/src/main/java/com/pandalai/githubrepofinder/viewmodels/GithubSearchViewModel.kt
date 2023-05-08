package com.pandalai.githubrepofinder.viewmodels

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandalai.githubrepofinder.R
import com.pandalai.githubrepofinder.models.GithubSearchItem
import com.pandalai.githubrepofinder.models.GithubSearchModel
import com.pandalai.githubrepofinder.presenter.RepoSearchMethods
import com.pandalai.githubrepofinder.repositories.GithubRepository
import com.pandalai.githubrepofinder.ui.MainActivity
import com.pandalai.githubrepofinder.utils.KeyboardUtils
import com.pandalai.githubrepofinder.utils.MiscUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response


/**
 * ViewModel class which is responsible for making the network call and then handing over the
 * response to the fragment.
 */
class GithubSearchViewModel constructor(private val context: Context, private val repository: GithubRepository): ViewModel(), RepoSearchMethods{

    val errorMessage = MutableLiveData<String>()
    val getRepoResponse = MutableLiveData<Response<GithubSearchModel>>()
    val mainActivity = context as MainActivity
    private var debouncePeriod: Long = 500
    private var searchJob: Job? = null

    private var searchText: String = ""
    private var sortBy: String = ""
    private var orderBy: String = ""
    private var pageNum: Int = 0
    private var pageLength: Int = 10
    private var itemsCount: Int = 0
    private var totalCount: Int = 0

    private var isFilterApplied: Boolean = false

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun resetData() {
        mainActivity.clearList()
        pageLength = 10
        pageNum = 1
        totalCount = 0
        itemsCount = 0
    }

    fun clearFilters() {
        orderBy = ""
        sortBy = ""

        mainActivity.resetFilters()
    }

    fun onSearching() {
        mainActivity.changeMessage(mainActivity.getString(R.string.fetching))
    }

    fun showNoReposFound() {
        if (itemsCount == 0) mainActivity.changeMessage(context.getString(R.string.repoNotFound))
    }

    fun manageResponse(response: Response<GithubSearchModel>) {
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
            mainActivity.updateList(filteredResponse.toList())
        }
    }
    fun getReposService() {
        val sortByParam = if(sortBy == MiscUtils.SortBy.stars.name || sortBy == MiscUtils.SortBy.updated.name) sortBy else ""
        val orderByParam = if(sortBy == MiscUtils.SortBy.stars.name || sortBy == MiscUtils.SortBy.updated.name) orderBy else ""
        mainActivity.binding.progress.visibility = View.VISIBLE
        getRepos(searchText, sortByParam, orderBy, pageLength, pageNum)
    }
    override fun searchRepos(searchText: String, sortBy: String, orderBy: String) {
        KeyboardUtils.hideKeyboard(mainActivity)
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
    }

    override fun onLoadMore() {
        if (itemsCount != 0 && totalCount != itemsCount) {
            pageNum++
            getReposService()
        }
    }
    fun getRepos(queryText: String, sort: String, order: String, per_page: Int, page: Int) {
        if(!checkIfOnline()){
            onError(context.resources.getString(R.string.no_internet_connection))
            return
        }
        val liveData = MutableLiveData<Call<GithubSearchModel>>()
        searchJob = viewModelScope.launch(Dispatchers.IO + exceptionHandler){
            val response = repository.getReposByQuery(queryText, sort, order, per_page, page)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    mainActivity.binding.progress.visibility = View.GONE
                    getRepoResponse.postValue(response)
                    loading.value = false
                } else {
                    mainActivity.binding.progress.visibility = View.GONE
                    if(response?.body() == null || response.body()!!.items == null){
                        onError("${context.getString(R.string.no_more_results)} ")
                    }
                    showNoReposFound()
                }
            }
        }
    }

    fun checkIfOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm != null) {
            val n = cm.activeNetwork
            if (n != null) {
                val nc = cm.getNetworkCapabilities(n)
                return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || nc.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
            }
        }
        return false
    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }

}
