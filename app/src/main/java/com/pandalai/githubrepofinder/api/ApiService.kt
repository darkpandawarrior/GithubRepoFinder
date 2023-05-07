package com.pandalai.githubrepofinder.api

import com.pandalai.githubrepofinder.models.GithubSearchItem
import com.pandalai.githubrepofinder.models.GithubSearchModel
import com.pandalai.githubrepofinder.models.Owner
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Api endpoints are defined inside of this interface using
 * retrofit annotations to encode details about the parameters and request method.
 */

interface ApiService {
    @GET(DataConstants.GET_REPOS)
    suspend fun searchRepositories(@Query("q") query: String): GithubSearchModel

    @GET(DataConstants.GET_REPOS)
    fun getRepos(@Query("q") q: String,
                 @Query("sort") sort: String,
                 @Query("order") order: String,
                 @Query("per_page") per_page: Int,
                 @Query("page") page: Int): Call<GithubSearchModel>

    @GET(DataConstants.GET_CONTRIBUTORS)
    fun getContributors(@Path("ownerName") ownerName: String, @Path("repoName") repoName: String): Call<List<Owner>>

    @GET(DataConstants.GET_REPOS_BY_NAME)
    fun getReposByName(@Path("ownerName") ownerName: String): Call<List<GithubSearchItem>>
}