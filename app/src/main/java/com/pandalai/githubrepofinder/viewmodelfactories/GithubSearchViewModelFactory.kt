package com.pandalai.githubrepofinder.viewmodelfactories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pandalai.githubrepofinder.repositories.GithubRepository
import com.pandalai.githubrepofinder.viewmodels.GithubSearchViewModel

class GithubSearchViewModelFactory constructor(private val context: Context, private val repository: GithubRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(GithubSearchViewModel::class.java)) {
            GithubSearchViewModel(context, this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}

