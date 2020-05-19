package com.example.rxandroidsample.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rxandroidsample.R
import com.example.rxandroidsample.model.Post
import java.util.*


class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    private val TAG = "RecyclerAdapter"
    private var posts = ArrayList<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.flat_map_post_list_item, null)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun getPosts(): List<Post> {
        return posts
    }

    fun updatePost(post: Post) {
        posts[posts.indexOf(post)] = post;
        notifyItemChanged(posts.indexOf(post));
    }

    fun setPosts(posts: ArrayList<Post>) {
        this.posts = posts
    }

    class MyViewHolder : RecyclerView.ViewHolder {
        var title: TextView? = null
        var numComments: TextView? = null
        var progressBar: ProgressBar

        constructor(itemView: View) : super(itemView) {
            this.title = itemView.findViewById(R.id.title)
            this.numComments = itemView.findViewById(R.id.num_comments)
            this.progressBar = itemView.findViewById(R.id.flat_map_pb)
        }

        fun bind(post: Post) {
            this.title?.text = post.title
            if (post.comments != null) {
                Log.d("TAG", post.comments!![0].toString())
                showProgressBar(false)
                this.numComments?.text = post.comments!!.size.toString()
            } else {
                showProgressBar(true)
                this.numComments?.text = ""
            }
        }

        private fun showProgressBar(showProgressBar: Boolean) {
            if (showProgressBar) {
                this.progressBar.visibility = View.VISIBLE
            } else {
                this.progressBar.visibility = View.GONE
            }
        }
    }
}