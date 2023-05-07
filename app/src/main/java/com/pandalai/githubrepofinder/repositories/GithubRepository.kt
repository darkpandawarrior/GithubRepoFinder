package com.pandalai.githubrepofinder.repositories

import com.pandalai.githubrepofinder.api.ApiService
import com.pandalai.githubrepofinder.models.GithubSearchItem
import com.pandalai.githubrepofinder.models.GithubSearchModel
import com.pandalai.githubrepofinder.models.Owner
import retrofit2.Call
import retrofit2.http.Path
import retrofit2.http.Query

class GithubRepository constructor(private val apiService: ApiService) {
    suspend fun fetchRepoByQuery(queryText: String): GithubSearchModel {
        return apiService.searchRepositories(queryText)
    }
    fun getRepos(queryText: String, sort: String, order: String, per_page: Int, page: Int): Call<GithubSearchModel> {
        return apiService.getRepos(queryText, sort, order, per_page, page)
    }
    fun getContributors(ownerName: String, repoName: String): Call<List<Owner>> {
        return apiService.getContributors(ownerName, repoName)
    }
    fun getReposByName(ownerName: String): Call<List<GithubSearchItem>> {
        return apiService.getReposByName(ownerName)
    }

}