package com.pandalai.githubrepofinder.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pandalai.githubrepofinder.R
import com.pandalai.githubrepofinder.models.GithubSearchItem
import com.pandalai.githubrepofinder.databinding.ItemGithubSearchCardBinding
import com.squareup.picasso.Picasso

class GithubSearchItemAdapter(recyclerView: RecyclerView) : RecyclerView.Adapter<GithubSearchItemAdapter.ViewHolder>(){
    private var mRepositories = mutableListOf<GithubSearchItem>()

    private var loading: Boolean = false

    lateinit var onLoadMoreListener: OnLoadMoreListener
    inner class ViewHolder(val binding: ItemGithubSearchCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGithubSearchCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            val mGithubSearchItem = mRepositories[position]
            tvGithubRepoName.text =  mGithubSearchItem.fullName
            tvGithubUserName.text = root.context.getString(R.string.repoOwnerName, mGithubSearchItem.owner?.login)
            tvGithubStars.text =  mGithubSearchItem.stargazersCount.toString()
            tvGithubRepoDescription.text = root.context.getString(R.string.repoDescription, mGithubSearchItem.description)
            tvGithubRepoLanguages.text = root.context.getString(R.string.repoLanguages, mGithubSearchItem.language)
            Picasso.get()
                .load(mGithubSearchItem.owner?.avatarUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(ivProfile)
        }
    }

    init {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val totalItemCount = linearLayoutManager.itemCount
                    val lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()

                    if (!loading && totalItemCount - 1 <= lastVisibleItem && lastVisibleItem > mRepositories.size - 5) {
                        onLoadMoreListener.onLoadMore()
                        loading = true
                    }
                }
            })
        }
    }

    fun clearItems() {
        mRepositories.clear()
        notifyDataSetChanged()
    }

    fun setLoaded() {
        loading = false
    }
    fun updateData(newData: List<GithubSearchItem>) {
//        mRepositories.clear()
        mRepositories.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mRepositories.size
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }
}