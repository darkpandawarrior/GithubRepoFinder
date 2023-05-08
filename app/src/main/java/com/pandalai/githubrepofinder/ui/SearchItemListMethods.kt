package com.pandalai.githubrepofinder.ui

import com.pandalai.githubrepofinder.models.GithubSearchItem

interface SearchItemListMethods {

    fun changeMessage(message: String)

    fun showMessage()

    fun hideMessage()

    fun updateList(items: List<GithubSearchItem>)

    fun clearList()

    fun showList()

    fun hideList()

    fun resetFilters()

}