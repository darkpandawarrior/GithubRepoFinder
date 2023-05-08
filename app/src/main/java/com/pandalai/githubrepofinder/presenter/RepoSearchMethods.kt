package com.pandalai.githubrepofinder.presenter

interface RepoSearchMethods {

    fun searchRepos(searchText: String, sortBy: String, orderBy: String);

    fun onDialogCancel()

    fun onFilterApply()

    fun onFilterClear()

    fun onLoadMore()
}