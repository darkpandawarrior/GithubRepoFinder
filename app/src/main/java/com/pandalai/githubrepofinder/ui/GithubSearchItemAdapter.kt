package com.pandalai.githubrepofinder.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pandalai.githubrepofinder.R
import com.pandalai.githubrepofinder.databinding.DialogGithubSearchCardBinding
import com.pandalai.githubrepofinder.models.GithubSearchItem
import com.pandalai.githubrepofinder.databinding.ItemGithubSearchCardBinding
import com.pandalai.githubrepofinder.models.RepoModel
import com.squareup.picasso.Picasso

class GithubSearchItemAdapter(recyclerView: RecyclerView, val context: Context) : RecyclerView.Adapter<GithubSearchItemAdapter.ViewHolder>(){
    private var mRepositories = mutableListOf<GithubSearchItem>()
    private lateinit var dialogBinding : DialogGithubSearchCardBinding
    private lateinit var repoModel : RepoModel

    private var loading: Boolean = false

    lateinit var onLoadMoreListener: OnLoadMoreListener
    inner class ViewHolder(val binding: ItemGithubSearchCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGithubSearchCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        dialogBinding = DialogGithubSearchCardBinding.inflate(LayoutInflater.from(parent.context))
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
            cvItemSearch.setOnClickListener {
                if(dialogBinding.root.parent != null){
                    (dialogBinding.root.parent as ViewGroup).removeAllViews()
                }
                val alert = setDialogBox(mRepositories[position], root.context)
                alert.show()
            }
        }
    }

    private fun setDialogBox(githubSearchItem: GithubSearchItem, context: Context): AlertDialog {
        repoModel = RepoModel(githubSearchItem.fullName, githubSearchItem.owner?.login, githubSearchItem.stargazersCount, githubSearchItem.description, githubSearchItem.language, githubSearchItem.owner?.avatarUrl)
        val builder = AlertDialog.Builder(context)
        val alert = builder.create()
        val view = dialogBinding
        view.tvGithubRepoName.text =  githubSearchItem.fullName
        view.tvGithubUserName.text = context.getString(R.string.repoOwnerName, repoModel.repoFullName)
        view.tvGithubStars.text =  repoModel.stars.toString()
        view.tvGithubRepoDescription.text = context.getString(R.string.repoDescription, repoModel.repoDescription)
        view.tvGithubRepoLanguages.text = context.getString(R.string.repoLanguages, repoModel.languages)
        Picasso.get()
            .load(repoModel.userImage)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .into(view.ivProfile)
//        val dialogText = ("Repo Name: ").plus(repoModel.repoFullName).plus("\n")
//            .plus(this.context.getString(R.string.repoOwnerName, repoModel.userName)).plus("\n")
//            .plus("Star Count: "+repoModel.stars).plus("\n")
//            .plus(this.context.getString(R.string.repoDescription, repoModel.repoDescription)).plus("\n")
//            .plus(this.context.getString(R.string.repoLanguages, repoModel.languages)).plus("\n")
        alert.setView(dialogBinding.root)
        alert.setCanceledOnTouchOutside(false)
        view.tvClose.setOnClickListener {
            alert.cancel()
        }
        return alert
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