package com.pandalai.githubrepofinder.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class RepoModel(val repoFullName : String?, val userName : String?, val stars : Int?,  val repoDescription: String?, val languages : String?, val userImage: String?)