package com.pandalai.githubrepofinder.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * API Error response model class
 */
@Parcelize
data class GithubErrorResponseModel(

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("documentation_url")
    val documentationUrl: String? = null,

    @field:SerializedName("errors")
    val errors: List<GithubErrorItem?>? = null
) : Parcelable

@Parcelize
data class GithubErrorItem(

    @field:SerializedName("code")
    val code: String? = null,

    @field:SerializedName("field")
    val field: String? = null,

    @field:SerializedName("resource")
    val resource: String? = null,

    @field:SerializedName("message")
    val message: String? = null
) : Parcelable
