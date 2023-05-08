package com.pandalai.githubrepofinder.repositories

import com.pandalai.githubrepofinder.api.ApiService

class GithubRepository constructor(private val apiService: ApiService) {
    suspend fun getReposByQuery(queryText: String, sort: String, order: String, per_page: Int, page: Int) = apiService.getReposByQuery(queryText, sort, order, per_page, page)

}