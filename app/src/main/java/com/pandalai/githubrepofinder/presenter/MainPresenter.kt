package com.pandalai.githubrepofinder.presenter

interface MainPresenter {

    fun searchRepos(searchText: String, sortBy: String, orderBy: String);

    fun onDialogCancel()

    fun onFilterApply()

    fun onFilterClear()

    fun onLoadMore()
}