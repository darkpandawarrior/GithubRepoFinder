package com.pandalai.githubrepofinder.api

import com.pandalai.githubrepofinder.models.GithubSearchItem
import com.pandalai.githubrepofinder.models.GithubSearchModel
import com.pandalai.githubrepofinder.models.Owner
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Api endpoints are defined inside of this interface using
 * retrofit annotations to encode details about the parameters and request method.
 */

interface ApiService {
    @GET(DataConstants.GET_REPOS)
    suspend fun getReposByQuery(@Query("q") q: String,
                 @Query("sort") sort: String,
                 @Query("order") order: String,
                 @Query("per_page") per_page: Int,
                 @Query("page") page: Int): Response<GithubSearchModel>

}