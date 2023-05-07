package com.pandalai.githubrepofinder.ui

sealed class LoadingStates<T>(val data: T? = null, val message: String? = null) {

    class Success<T>(data: T) : LoadingStates<T>(data)

    class Error<T>(message: String?, data: T? = null) : LoadingStates<T>(data, message)

    class Loading<T> : LoadingStates<T>()

}